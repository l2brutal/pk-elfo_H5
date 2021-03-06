package pk.elfo.gameserver.events.events;

import java.util.Map;

import pk.elfo.gameserver.events.AbstractEvent;
import pk.elfo.gameserver.events.Config;
import pk.elfo.gameserver.events.container.NpcContainer;
import pk.elfo.gameserver.events.io.Out;
import pk.elfo.gameserver.events.model.EventNpc;
import pk.elfo.gameserver.events.model.EventPlayer;
import pk.elfo.gameserver.events.model.SingleEventStatus;
import javolution.util.FastList;
import javolution.util.FastMap;

public class Russian extends AbstractEvent
{
	public static boolean enabled = true;
	
	private class Core implements Runnable
	{
		
		@Override
		public void run()
		{
			try
			{
				switch (eventState)
				{
					case START:
						divideIntoTeams(1);
						teleportToTeamPos();
						preparePlayers();
						startParalize();
						spawnRussians();
						setStatus(EventState.CHOOSE);
						schedule(20000);
						break;
					
					case CHOOSE:
						if (round == 0)
						{
							unParalize();
						}
						
						round++;
						announce("Escolha uma russian!");
						setStatus(EventState.CHECK);
						schedule(Config.getInstance().getInt(getId(), "roundTime") * 1000);
						break;
					
					case CHECK:
						removeAfkers();
						killRandomRussian();
						
						if (countOfPositiveStatus() != 0)
						{
							if (russians.size() != 1)
							{
								for (EventPlayer player : getPlayersWithStatus(1))
								{
									player.setStatus(0);
									player.increaseScore();
									player.setNameColor(255, 255, 255);
									player.broadcastUserInfo();
								}
								
								for (FastList<EventPlayer> chose : choses.values())
								{
									chose.reset();
								}
								
								setStatus(EventState.CHOOSE);
								schedule(Config.getInstance().getInt(getId(), "roundTime") * 1000);
							}
							else
							{
								for (EventPlayer player : getPlayersWithStatus(1))
								{
									giveReward(player);
								}
								
								unspawnRussians();
								announce("Parabens! " + countOfPositiveStatus() + " jogadores sobreviveram ao evento!");
								eventEnded();
							}
							
						}
						else
						{
							unspawnRussians();
							announce("Infelizmente ninguem sobreviveu ao evento!");
							eventEnded();
						}
						break;
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				announce("Erro! O Evento terminou.");
				eventEnded();
			}
			
		}
	}
	
	private enum EventState
	{
		START,
		CHOOSE,
		CHECK,
		INACTIVE
	}
	
	EventState eventState;
	
	private final Core task;
	
	int round;
	
	final FastMap<Integer, EventNpc> russians;
	
	final FastMap<Integer, FastList<EventPlayer>> choses;
	
	@SuppressWarnings("synthetic-access")
	public Russian(Integer containerId)
	{
		super(containerId);
		eventId = 11;
		createNewTeam(1, "All", Config.getInstance().getColor(getId(), "All"), Config.getInstance().getPosition(getId(), "All", 1));
		task = new Core();
		round = 0;
		russians = new FastMap<>();
		choses = new FastMap<>();
	}
	
	@Override
	public boolean canAttack(EventPlayer player, EventPlayer target)
	{
		return false;
	}
	
	@Override
	protected void endEvent()
	{
		EventPlayer winner = players.head().getNext().getValue();
		giveReward(winner);
		
		unspawnRussians();
		announce("Parabens! 1 jogador sobreviveu ao evento!");
		eventEnded();
		
	}
	
	@Override
	protected String getScorebar()
	{
		return "";
	}
	
	void killRandomRussian()
	{
		FastList<Integer> ids = new FastList<>();
		for (int id : russians.keySet())
		{
			ids.add(id);
		}
		int russnum = ids.get(rnd.nextInt(ids.size()));
		EventNpc russian = russians.get(russnum);
		russian.unspawn();
		announce(getPlayerList(), "O #" + russnum + " russian morreu.");
		
		for (EventPlayer victim : choses.get(russnum))
		{
			victim.setStatus(-1);
			victim.doDieNpc(russian.getId());
			victim.sendMessage("Seu russian morreu!");
			victim.setNameColor(255, 255, 255);
		}
		russians.remove(russnum);
	}
	
	@Override
	protected void onClockZero()
	{
		
	}
	
	@Override
	public void onLogout(EventPlayer player)
	{
		super.onLogout(player);
		
		for (FastList<EventPlayer> list : choses.values())
		{
			if (list.contains(player))
			{
				list.remove(player);
			}
		}
	}
	
	@Override
	public boolean onTalkNpc(Integer npc, EventPlayer player)
	{
		
		EventNpc npci = NpcContainer.getInstance().getNpc(npc);
		
		if (npci == null)
		{
			return false;
		}
		
		if (!russians.containsValue(npci))
		{
			return false;
		}
		
		if (player.getStatus() != 0)
		{
			return true;
		}
		
		for (Map.Entry<Integer, EventNpc> russian : russians.entrySet())
		{
			if (russian.getValue().equals(npci))
			{
				choses.get(russian.getKey()).add(player);
				player.setNameColor(0, 255, 0);
				player.broadcastUserInfo();
				player.setStatus(1);
			}
		}
		
		return true;
	}
	
	void removeAfkers()
	{
		for (EventPlayer player : getPlayerList())
		{
			if (player.getStatus() == 0)
			{
				player.sendMessage("Tempo esgotado!");
				player.doDie();
				player.setStatus(-1);
			}
		}
	}
	
	@Override
	protected void reset()
	{
		super.reset();
		round = 0;
		russians.clear();
		choses.clear();
	}
	
	@Override
	protected void schedule(int time)
	{
		Out.tpmScheduleGeneral(task, time);
	}
	
	void setStatus(EventState s)
	{
		eventState = s;
	}
	
	void spawnRussians()
	{
		for (int i = 1; i <= Config.getInstance().getInt(getId(), "numberOfRussians"); i++)
		{
			int[] pos = Config.getInstance().getPosition(getId(), "Russian", i);
			russians.put(i, NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Config.getInstance().getInt(getId(), "russianNpcId"), instanceId));
			choses.put(i, new FastList<EventPlayer>());
			russians.get(i).setTitle("--" + i + "--");
		}
	}
	
	@Override
	public void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	void unspawnRussians()
	{
		for (EventNpc russian : russians.values())
		{
			russian.unspawn();
		}
	}
	
	@Override
	public void createStatus()
	{
		status = new SingleEventStatus(containerId);
	}
}