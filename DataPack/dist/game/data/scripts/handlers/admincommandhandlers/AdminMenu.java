package handlers.admincommandhandlers;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import pk.elfo.gameserver.datatables.AdminTable;
import pk.elfo.gameserver.datatables.ChampionData;
import pk.elfo.gameserver.handler.AdminCommandHandler;
import pk.elfo.gameserver.handler.IAdminCommandHandler;
import pk.elfo.gameserver.model.L2Clan;
import pk.elfo.gameserver.model.L2Object;
import pk.elfo.gameserver.model.L2World;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.stats.Stats;
import pk.elfo.gameserver.network.SystemMessageId;
 
/**
 * Projeto PkElfo
 */

public class AdminMenu implements IAdminCommandHandler
{
	private static final Logger _log = Logger.getLogger(AdminMenu.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_char_manage",
		"admin_teleport_character_to_menu",
		"admin_recall_char_menu",
		"admin_recall_party_menu",
		"admin_recall_clan_menu",
		"admin_goto_char_menu",
		"admin_kick_menu",
		"admin_kill_menu",
		"admin_ban_menu",
		"admin_unban_menu"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_char_manage"))
		{
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_teleport_character_to_menu"))
		{
			String[] data = command.split(" ");
			if (data.length == 5)
			{
				String playerName = data[1];
				L2PcInstance player = L2World.getInstance().getPlayer(playerName);
				if (player != null)
				{
					teleportCharacter(player, Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), activeChar, "Admin is teleporting you.");
				}
			}
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_recall_char_menu"))
		{
			try
			{
				String targetName = command.substring(23);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				teleportCharacter(player, activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar, "Admin is teleporting you.");
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_recall_party_menu"))
		{
			int x = activeChar.getX(), y = activeChar.getY(), z = activeChar.getZ();
			try
			{
				String targetName = command.substring(24);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return true;
				}
				if (!player.isInParty())
				{
					activeChar.sendMessage("Player is not in party.");
					teleportCharacter(player, x, y, z, activeChar, "Admin is teleporting you.");
					return true;
				}
				for (L2PcInstance pm : player.getParty().getMembers())
				{
					teleportCharacter(pm, x, y, z, activeChar, "Your party is being teleported by an Admin.");
				}
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "", e);
			}
		}
		else if (command.startsWith("admin_recall_clan_menu"))
		{
			int x = activeChar.getX(), y = activeChar.getY(), z = activeChar.getZ();
			try
			{
				String targetName = command.substring(23);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return true;
				}
				L2Clan clan = player.getClan();
				if (clan == null)
				{
					activeChar.sendMessage("Player is not in a clan.");
					teleportCharacter(player, x, y, z, activeChar, "Admin is teleporting you.");
					return true;
				}
				
				for (L2PcInstance member : clan.getOnlineMembers(0))
				{
					teleportCharacter(member, x, y, z, activeChar, "Your clan is being teleported by an Admin.");
				}
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "", e);
			}
		}
		else if (command.startsWith("admin_goto_char_menu"))
		{
			try
			{
				String targetName = command.substring(21);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				activeChar.setInstanceId(player.getInstanceId());
				teleportToCharacter(activeChar, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.equals("admin_kill_menu"))
		{
			handleKill(activeChar);
		}
		else if (command.startsWith("admin_kick_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				String text;
				if (plyr != null)
				{
					plyr.logout();
					text = "You kicked " + plyr.getName() + " from the game.";
				}
				else
				{
					text = "Player " + player + " was not found in the game.";
				}
				activeChar.sendMessage(text);
			}
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_ban_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				String subCommand = "admin_ban_char";
				if (!AdminTable.getInstance().hasAccess(subCommand, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access right to use this command!");
					_log.warning("Character " + activeChar.getName() + " tryed to use admin command " + subCommand + ", but have no access to it!");
					return false;
				}
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(subCommand);
				ach.useAdminCommand(subCommand + command.substring(14), activeChar);
			}
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_unban_menu"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				String subCommand = "admin_unban_char";
				if (!AdminTable.getInstance().hasAccess(subCommand, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access right to use this command!");
					_log.warning("Character " + activeChar.getName() + " tryed to use admin command " + subCommand + ", but have no access to it!");
					return false;
				}
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(subCommand);
				ach.useAdminCommand(subCommand + command.substring(16), activeChar);
			}
			showMainPage(activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleKill(L2PcInstance activeChar)
	{
		handleKill(activeChar, null);
	}
	
	private void handleKill(L2PcInstance activeChar, String player)
	{
		L2Object obj = activeChar.getTarget();
		L2Character target = (L2Character) obj;
		String filename = "main_menu.htm";
		if (player != null)
		{
			L2PcInstance plyr = L2World.getInstance().getPlayer(player);
			if (plyr != null)
			{
				target = plyr;
				activeChar.sendMessage("You killed " + plyr.getName());
			}
		}
		if (target != null)
		{
			if (target instanceof L2PcInstance)
			{
				target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null);
				filename = "charmanage.htm";
			}
			else if (target.isChampion())
			{
				target.reduceCurrentHp((target.getMaxHp() * ChampionData.getInstance().getMultiplier(target.getChampionType(), Stats.MAX_HP)) + 1, activeChar, null);
			}
			else
			{
				target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
		AdminHelpPage.showHelpPage(activeChar, filename);
	}
	
	private void teleportCharacter(L2PcInstance player, int x, int y, int z, L2PcInstance activeChar, String message)
	{
		if (player != null)
		{
			player.sendMessage(message);
			player.teleToLocation(x, y, z, true);
		}
		showMainPage(activeChar);
	}
	
	private void teleportToCharacter(L2PcInstance activeChar, L2Object target)
	{
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		if (player.getObjectId() == activeChar.getObjectId())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
		}
		else
		{
			activeChar.setInstanceId(player.getInstanceId());
			activeChar.teleToLocation(player.getX(), player.getY(), player.getZ(), true);
			activeChar.sendMessage("You're teleporting yourself to character " + player.getName());
		}
		showMainPage(activeChar);
	}
	
	/**
	 * @param activeChar
	 */
	private void showMainPage(L2PcInstance activeChar)
	{
		AdminHelpPage.showHelpPage(activeChar, "charmanage.htm");
	}
}