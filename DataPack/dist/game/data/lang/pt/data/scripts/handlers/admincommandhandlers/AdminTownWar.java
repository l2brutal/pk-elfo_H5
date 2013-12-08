/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import king.server.Config;
import king.server.gameserver.Announcements;
import king.server.gameserver.handler.IAdminCommandHandler;
import king.server.gameserver.instancemanager.TownManager;
import king.server.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author SolidSnake, mrTJO
 */
public class AdminTownWar implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = {
		"admin_townwar_start",
		"admin_townwar_end"
	};
	
	private static final int ALL_TOWNS_INT = 17;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_townwar_start"))
		{
			startTownWar();
		}
		if (command.startsWith("admin_townwar_end"))
		{
			endTownWar();
		}
		return true;
	}
	
	private void startTownWar()
	{
		if (Config.TW_ALL_TOWNS)
		{
			for (int i = 1; i <= ALL_TOWNS_INT; i++)
			{
				TownManager.getTown(i).setIsTWZone(true);
				TownManager.getTown(i).updateForCharactersInside();
			}
			TownManager.getTown(20).setIsTWZone(true);
			TownManager.getTown(20).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: Todas as cidades sao zona de guerra.");
		}
		else
		{
			TownManager.getTown(Config.TW_TOWN_ID).setIsTWZone(true);
			TownManager.getTown(Config.TW_TOWN_ID).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: " + Config.TW_TOWN_NAME + " esta como zona de guerra.");
		}
	}
	
	private void endTownWar()
	{
		if (Config.TW_ALL_TOWNS)
		{
			for (int i = 1; i <= ALL_TOWNS_INT; i++)
			{
				TownManager.getTown(i).setIsTWZone(false);
				TownManager.getTown(i).updateForCharactersInside();
			}
			TownManager.getTown(20).setIsTWZone(false);
			TownManager.getTown(20).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: Todas as cidades voltaram ao normal.");
		}
		else
		{
			TownManager.getTown(Config.TW_TOWN_ID).setIsTWZone(false);
			TownManager.getTown(Config.TW_TOWN_ID).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: " + Config.TW_TOWN_NAME + " retornou ao normal.");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}