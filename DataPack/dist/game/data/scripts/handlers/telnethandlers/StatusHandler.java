package handlers.telnethandlers;

import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pk.elfo.gameserver.GameTimeController;
import pk.elfo.gameserver.LoginServerThread;
import pk.elfo.gameserver.datatables.AdminTable;
import pk.elfo.gameserver.handler.ITelnetHandler;
import pk.elfo.gameserver.model.L2Object;
import pk.elfo.gameserver.model.L2World;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.L2Summon;
import pk.elfo.gameserver.model.actor.instance.L2DoorInstance;
import pk.elfo.gameserver.model.actor.instance.L2MonsterInstance;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.items.instance.L2ItemInstance;

/**
 * Projeto PkElfo
 */

public class StatusHandler implements ITelnetHandler
{
	private final String[] _commands =
	{
		"status",
		"forcegc",
		"memusage",
		"gmlist"
	};
	
	private int uptime;
	
	@Override
	public boolean useCommand(String command, PrintWriter _print, Socket _cSocket, int _uptime)
	{
		if (command.equals("status"))
		{
			uptime = _uptime;
			_print.print(getServerStatus());
			_print.flush();
		}
		else if (command.equals("forcegc"))
		{
			System.gc();
			StringBuilder sb = new StringBuilder();
			sb.append("RAM UsedA: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576)); // 1024 * 1024 = 1048576
			_print.println(sb.toString());
		}
		else if (command.startsWith("memusage"))
		{
			double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is the upper
			// limit the jvm can use
			double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory the
			// size of the
			// current
			// allocation pool
			double nonAllocated = max - allocated; // non allocated memory till jvm limit
			double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory the
			// unused memory in
			// the allocation pool
			double used = allocated - cached; // really used memory
			double useable = max - used; // allocated, but non-used and non-allocated memory
			
			DecimalFormat df = new DecimalFormat(" (0.0000'%')");
			DecimalFormat df2 = new DecimalFormat(" # 'KB'");
			
			_print.println("+----");// ...
			_print.println("| Memoria permitida:" + df2.format(max));
			_print.println("|    |= Memoria alocada:" + df2.format(allocated) + df.format((allocated / max) * 100));
			_print.println("|    |= Memoria nao alocada:" + df2.format(nonAllocated) + df.format((nonAllocated / max) * 100));
			_print.println("| Memoria alocada:" + df2.format(allocated));
			_print.println("|    |= Memoria usada:" + df2.format(used) + df.format((used / max) * 100));
			_print.println("|    |= Memoria nao usada (cached):" + df2.format(cached) + df.format((cached / max) * 100));
			_print.println("| Memoria utilizavel:" + df2.format(useable) + df.format((useable / max) * 100)); // ...
			_print.println("+----");
		}
		else if (command.equals("gmlist"))
		{
			int igm = 0;
			String gmList = "";
			
			for (String player : AdminTable.getInstance().getAllGmNames(true))
			{
				gmList = gmList + ", " + player;
				igm++;
			}
			_print.println("Atualmente " + igm + " GM(s) online...");
			if (!gmList.isEmpty())
			{
				_print.println(gmList);
			}
		}
		return false;
	}
	
	public String getServerStatus()
	{
		int playerCount = 0, objectCount = 0;
		int max = LoginServerThread.getInstance().getMaxPlayer();
		
		playerCount = L2World.getInstance().getAllPlayersCount();
		objectCount = L2World.getInstance().getAllVisibleObjectsCount();
		
		int itemCount = 0;
		int itemVoidCount = 0;
		int monsterCount = 0;
		int minionCount = 0;
		int minionsGroupCount = 0;
		int npcCount = 0;
		int charCount = 0;
		int pcCount = 0;
		int detachedCount = 0;
		int doorCount = 0;
		int summonCount = 0;
		int AICount = 0;
		
		L2Object[] objs = L2World.getInstance().getAllVisibleObjectsArray();
		for (L2Object obj : objs)
		{
			if (obj == null)
			{
				continue;
			}
			if (obj instanceof L2Character)
			{
				if (((L2Character) obj).hasAI())
				{
					AICount++;
				}
			}
			if (obj instanceof L2ItemInstance)
			{
				if (((L2ItemInstance) obj).getLocation() == L2ItemInstance.ItemLocation.VOID)
				{
					itemVoidCount++;
				}
				else
				{
					itemCount++;
				}
			}
			else if (obj instanceof L2MonsterInstance)
			{
				monsterCount++;
				if (((L2MonsterInstance) obj).hasMinions())
				{
					minionCount += ((L2MonsterInstance) obj).getMinionList().countSpawnedMinions();
					minionsGroupCount += ((L2MonsterInstance) obj).getMinionList().lazyCountSpawnedMinionsGroups();
				}
			}
			else if (obj instanceof L2Npc)
			{
				npcCount++;
			}
			else if (obj instanceof L2PcInstance)
			{
				pcCount++;
				if ((((L2PcInstance) obj).getClient() != null) && ((L2PcInstance) obj).getClient().isDetached())
				{
					detachedCount++;
				}
			}
			else if (obj instanceof L2Summon)
			{
				summonCount++;
			}
			else if (obj instanceof L2DoorInstance)
			{
				doorCount++;
			}
			else if (obj instanceof L2Character)
			{
				charCount++;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Server Status: ");
		sb.append("\r\n  --->  Player Count: " + playerCount + "/" + max);
		sb.append("\r\n  ---> Offline Count: " + detachedCount + "/" + playerCount);
		sb.append("\r\n  +-->  Object Count: " + objectCount);
		sb.append("\r\n  +-->      AI Count: " + AICount);
		sb.append("\r\n  +.... L2Item(Void): " + itemVoidCount);
		sb.append("\r\n  +.......... L2Item: " + itemCount);
		sb.append("\r\n  +....... L2Monster: " + monsterCount);
		sb.append("\r\n  +......... Minions: " + minionCount);
		sb.append("\r\n  +.. Minions Groups: " + minionsGroupCount);
		sb.append("\r\n  +........... L2Npc: " + npcCount);
		sb.append("\r\n  +............ L2Pc: " + pcCount);
		sb.append("\r\n  +........ L2Summon: " + summonCount);
		sb.append("\r\n  +.......... L2Door: " + doorCount);
		sb.append("\r\n  +.......... L2Char: " + charCount);
		sb.append("\r\n  --->   Ingame Time: " + gameTime());
		sb.append("\r\n  ---> Server Uptime: " + getUptime(uptime));
		sb.append("\r\n  --->      GM Count: " + getOnlineGMS());
		sb.append("\r\n  --->       Threads: " + Thread.activeCount());
		sb.append("\r\n  RAM Usada: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576)); // 1024 * 1024 = 1048576
		sb.append("\r\n");
		
		return sb.toString();
	}
	
	private int getOnlineGMS()
	{
		return AdminTable.getInstance().getAllGms(true).size();
	}
	
	private String getUptime(int time)
	{
		int uptime = (int) System.currentTimeMillis() - time;
		uptime = uptime / 1000;
		int h = uptime / 3600;
		int m = (uptime - (h * 3600)) / 60;
		int s = ((uptime - (h * 3600)) - (m * 60));
		return h + "hrs " + m + "mins " + s + "secs";
	}
	
	private String gameTime()
	{
		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60;
		int m = t % 60;
		SimpleDateFormat format = new SimpleDateFormat("H:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		return format.format(cal.getTime());
	}
	
	@Override
	public String[] getCommandList()
	{
		return _commands;
	}
}