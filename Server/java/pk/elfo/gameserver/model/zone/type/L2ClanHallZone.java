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
package pk.elfo.gameserver.model.zone.type;

import pk.elfo.gameserver.instancemanager.ClanHallManager;
import pk.elfo.gameserver.instancemanager.MapRegionManager.TeleportWhereType;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.entity.ClanHall;
import pk.elfo.gameserver.model.entity.clanhall.AuctionableHall;
import pk.elfo.gameserver.model.zone.L2ZoneRespawn;
import pk.elfo.gameserver.model.zone.ZoneId;
import pk.elfo.gameserver.network.serverpackets.AgitDecoInfo;

/**
 * A clan hall zone
 * @author durgus
 */
public class L2ClanHallZone extends L2ZoneRespawn
{
	private int _clanHallId;
	
	public L2ClanHallZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("clanHallId"))
		{
			_clanHallId = Integer.parseInt(value);
			// Register self to the correct clan hall
			ClanHall hall = ClanHallManager.getInstance().getClanHallById(_clanHallId);
			if (hall == null)
			{
				_log.warning("L2ClanHallZone: Clan hall with id " + _clanHallId + " does not exist!");
			}
			else
			{
				hall.setZone(this);
			}
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character.isPlayer())
		{
			// Set as in clan hall
			character.setInsideZone(ZoneId.CLAN_HALL, true);
			
			AuctionableHall clanHall = ClanHallManager.getInstance().getAuctionableHallById(_clanHallId);
			if (clanHall == null)
			{
				return;
			}
			
			// Send decoration packet
			AgitDecoInfo deco = new AgitDecoInfo(clanHall);
			character.sendPacket(deco);
			
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.CLAN_HALL, false);
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
	/**
	 * Removes all foreigners from the clan hall
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		TeleportWhereType type = TeleportWhereType.ClanHall_banish;
		for (L2PcInstance temp : getPlayersInside())
		{
			if ((temp.getClanId() == owningClanId) && (owningClanId != 0))
			{
				continue;
			}
			
			temp.teleToLocation(type);
		}
	}
	
	/**
	 * @return the clanHallId
	 */
	public int getClanHallId()
	{
		return _clanHallId;
	}
}
