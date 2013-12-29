/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.itemhandlers;

import java.util.logging.Level;

import pk.elfo.gameserver.handler.IItemHandler;
import pk.elfo.gameserver.instancemanager.CastleManorManager;
import pk.elfo.gameserver.model.actor.L2Playable;
import pk.elfo.gameserver.model.actor.instance.L2MonsterInstance;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.holders.SkillHolder;
import pk.elfo.gameserver.model.items.instance.L2ItemInstance;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.ActionFailed;

/**
 * @author l3x
 */
public class Harvester implements IItemHandler
{
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		if (CastleManorManager.getInstance().isDisabled())
		{
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		final SkillHolder[] skills = item.getItem().getSkills();
		L2MonsterInstance target = null;
		if ((activeChar.getTarget() != null) && activeChar.getTarget().isMonster())
		{
			target = (L2MonsterInstance) activeChar.getTarget();
		}
		
		if (skills == null)
		{
			_log.log(Level.WARNING, getClass().getSimpleName() + ": is missing skills!");
			return false;
		}
		
		if ((target == null) || !target.isDead())
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		for (SkillHolder sk : skills)
		{
			activeChar.useMagic(sk.getSkill(), false, false);
		}
		return true;
	}
}
