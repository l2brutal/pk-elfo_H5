/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package king.server.gameserver.network.communityserver.writepackets;

import java.util.List;

import javolution.util.FastList;
import king.server.gameserver.datatables.ClanTable;
import king.server.gameserver.model.L2Clan;
import king.server.gameserver.model.actor.instance.L2PcInstance;

import org.netcon.BaseWritePacket;

/**
 * @authors Forsaiken, Gigiikun
 */
public final class WorldInfo extends BaseWritePacket
{
	public static final byte TYPE_INIT_PACKET = 0;
	public static final byte TYPE_UPDATE_PLAYER_DATA = 1;
	public static final byte TYPE_UPDATE_PLAYER_STATUS = 2;
	public static final byte TYPE_UPDATE_CLAN_DATA = 3;
	public static final byte TYPE_SEND_CLAN_NOTICE = 4;
	
	public WorldInfo(L2PcInstance player, L2Clan clan, final byte type)
	{
		super.writeC(0x01);
		
		switch (type)
		{
			case TYPE_INIT_PACKET:
			{
				// this should never happen
				super.writeC(0x00);
				break;
			}
			
			case TYPE_UPDATE_PLAYER_DATA:
			{
				super.writeC(0x01);
				super.writeC(0x00);
				super.writeD(player.getObjectId());
				super.writeS(player.getName());
				super.writeS(player.getAccountName());
				super.writeD(player.getLevel());
				super.writeD(player.getClanId());
				super.writeD(player.getAccessLevel().getLevel());
				super.writeC(player.isOnlineInt());
				List<Integer> list = player.getFriendList();
				super.writeD(list.size());
				for (int j : list)
				{
					super.writeD(j);
				}
				break;
			}
			
			case TYPE_UPDATE_PLAYER_STATUS:
			{
				super.writeC(0x01);
				super.writeC(0x01);
				super.writeD(player.getObjectId());
				super.writeC(player.isOnlineInt());
				break;
			}
			
			case TYPE_UPDATE_CLAN_DATA:
			{
				super.writeC(0x02);
				super.writeD(clan.getClanId());
				super.writeS(clan.getName());
				super.writeD(clan.getLevel());
				super.writeD(clan.getLeader().getObjectId());
				super.writeS(clan.getLeader().getName());
				super.writeD(clan.getMembersCount());
				super.writeC((clan.isNoticeEnabled() ? 1 : 0));
				super.writeS(clan.getAllyName());
				FastList<Integer> allyClanIdList = FastList.newInstance();
				if (clan.getAllyId() != 0)
				{
					for (L2Clan c : ClanTable.getInstance().getClanAllies(clan.getAllyId()))
					{
						allyClanIdList.add(c.getClanId());
					}
				}
				super.writeD(allyClanIdList.size());
				for (int k : allyClanIdList)
				{
					super.writeD(k);
				}
				FastList.recycle(allyClanIdList);
				break;
			}
			case TYPE_SEND_CLAN_NOTICE:
				super.writeC(0x03);
				super.writeD(clan.getClanId());
				super.writeS(clan.getNotice());
				break;
		}
	}
}
