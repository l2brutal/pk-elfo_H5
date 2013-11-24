package king.server.gameserver;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import king.server.Config;
import king.server.gameserver.ai.CtrlEvent;
import king.server.gameserver.instancemanager.DayNightSpawnManager;
import king.server.gameserver.model.actor.L2Character;

public class GameTimeController
{
	protected static final Logger _log = Logger.getLogger(GameTimeController.class.getName());
	
	public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	
	protected static int _gameTicks;
	protected static long _gameStartTime;
	protected static boolean _isNight = false;
	protected static boolean _interruptRequest = false;
	
	protected static final TIntObjectHashMap<L2Character> _movingObjects = new TIntObjectHashMap<>();
	private static final ReentrantLock _lock = new ReentrantLock();
	
	protected static TimerThread _timer;
	
	/**
	 * Gets the single instance of GameTimeController.
	 * @return single instance of GameTimeController
	 */
	public static GameTimeController getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Instantiates a new game time controller.
	 */
	protected GameTimeController()
	{
		_gameStartTime = System.currentTimeMillis() - 3600000; // offset so that the server starts a day begin
		_gameTicks = 3600000 / MILLIS_IN_TICK; // offset so that the server starts a day begin
		
		_timer = new TimerThread();
		_timer.start();
		
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new BroadcastSunState(), 0, 600000);
		
	}
	
	/**
	 * Checks if is now night.
	 * @return true, if is now night
	 */
	public boolean isNowNight()
	{
		return _isNight;
	}
	
	/**
	 * Gets the game time.
	 * @return the game time
	 */
	public int getGameTime()
	{
		return (_gameTicks / (TICKS_PER_SECOND * 10));
	}
	
	/**
	 * Gets the game ticks.
	 * @return the game ticks
	 */
	public static int getGameTicks()
	{
		return _gameTicks;
	}
	
	/**
	 * Add a L2Character to movingObjects of GameTimeController.<br>
	 * All characters in movement are identified in <b>movingObjects</b> of GameTimeController.
	 * @param cha the character to add to movingObjects of GameTimeController
	 */
	public void registerMovingObject(L2Character cha)
	{
		if (cha == null)
		{
			return;
		}
		
		_lock.lock();
		try
		{
			_movingObjects.putIfAbsent(cha.getObjectId(), cha);
		}
		finally
		{
			_lock.unlock();
		}
	}
	
	/**
	 * Move all characters contained in movingObjects of GameTimeController.<br>
	 * All characters in movement are identified in <b>movingObjects</b> of GameTimeController.<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Update the position of each L2Character</li> <li>If movement is finished, the L2Character is removed from movingObjects</li> <li>Create a task to update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with
	 * EVT_ARRIVED</li>
	 */
	protected void moveObjects()
	{
		_lock.lock();
		try
		{
			_movingObjects.forEachValue(new MoveObjects());
		}
		finally
		{
			_lock.unlock();
		}
	}
	
	protected final class MoveObjects implements TObjectProcedure<L2Character>
	{
		@Override
		public final boolean execute(final L2Character ch)
		{
			if (ch.updatePosition(_gameTicks))
			{
				// If movement is finished, the L2Character is removed from
				// movingObjects and added to the ArrayList ended
				_movingObjects.remove(ch.getObjectId());
				ThreadPoolManager.getInstance().executeTask(new MovingObjectArrived(ch));
			}
			return true;
		}
	}
	
	/**
	 * Stop timer.
	 */
	public void stopTimer()
	{
		_interruptRequest = true;
		_timer.interrupt();
	}
	
	class TimerThread extends Thread
	{
		/**
		 * Instantiates a new timer thread.
		 */
		public TimerThread()
		{
			super("GameTimeController");
			setDaemon(true);
			setPriority(MAX_PRIORITY);
		}
		
		@Override
		public void run()
		{
			int oldTicks;
			long runtime;
			int sleepTime;
			
			for (;;)
			{
				try
				{
					oldTicks = _gameTicks; // save old ticks value to avoid moving objects 2x in same tick
					runtime = System.currentTimeMillis() - _gameStartTime; // from server boot to now
					
					_gameTicks = (int) (runtime / MILLIS_IN_TICK); // new ticks value (ticks now)
					
					if (oldTicks != _gameTicks)
					{
						moveObjects(); // Runs possibly too often
					}
					
					runtime = (System.currentTimeMillis() - _gameStartTime) - runtime;
					
					// calculate sleep time... time needed to next tick minus time it takes to call moveObjects()
					sleepTime = (1 + MILLIS_IN_TICK) - (((int) runtime) % MILLIS_IN_TICK);
					
					// _log.finest("TICK: "+_gameTicks);
					
					if (sleepTime > 0)
					{
						Thread.sleep(sleepTime);
					}
				}
				catch (InterruptedException ie)
				{
					if (_interruptRequest)
					{
						return;
					}
					
					_log.log(Level.WARNING, "", ie);
				}
				catch (Exception e)
				{
					_log.log(Level.WARNING, "", e);
				}
			}
		}
	}
	
	/**
	 * Update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED.
	 */
	private static class MovingObjectArrived implements Runnable
	{
		private final L2Character _ended;
		
		/**
		 * Instantiates a new moving object arrived.
		 * @param ended the ended
		 */
		MovingObjectArrived(L2Character ended)
		{
			_ended = ended;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (_ended.hasAI()) // AI could be just disabled due to region turn off
				{
					if (Config.MOVE_BASED_KNOWNLIST)
					{
						_ended.getKnownList().findObjects();
					}
					_ended.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED);
				}
			}
			catch (NullPointerException e)
			{
				_log.log(Level.WARNING, "", e);
			}
		}
	}
	
	class BroadcastSunState implements Runnable
	{
		int h;
		boolean tempIsNight;
		
		@Override
		public void run()
		{
			h = ((getGameTime() + 29) / 60) % 24; // Time in hour (+ 29 is to round 60)
			tempIsNight = (h < 6);
			
			if (tempIsNight != _isNight)
			{
				// If diff day/night state
				_isNight = tempIsNight; // Set current day/night variable to value of temp variable
				DayNightSpawnManager.getInstance().notifyChangeMode();
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final GameTimeController _instance = new GameTimeController();
	}
}