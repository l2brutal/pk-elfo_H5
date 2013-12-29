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
package handlers.targethandlers;

import java.util.Collection;
import java.util.List;

import pk.elfo.gameserver.handler.ITargetTypeHandler;
import pk.elfo.gameserver.model.L2Object;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.model.skills.targets.L2TargetType;
import pk.elfo.gameserver.model.zone.ZoneId;
import pk.elfo.gameserver.util.Util;
import javolution.util.FastList;

/**
 * @author UnAfraid
 */
public class TargetAreaSummon implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(L2Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new FastList<>();
		target = activeChar.getSummon();
		if ((target == null) || !target.isServitor() || target.isDead())
		{
			return _emptyTargetList;
		}
		
		if (onlyFirst)
		{
			return new L2Character[]
			{
				target
			};
		}
		
		final boolean srcInArena = (activeChar.isInsideZone(ZoneId.PVP) && !activeChar.isInsideZone(ZoneId.SIEGE));
		final Collection<L2Character> objs = target.getKnownList().getKnownCharacters();
		final int radius = skill.getSkillRadius();
		
		for (L2Character obj : objs)
		{
			if ((obj == null) || (obj == target) || (obj == activeChar))
			{
				continue;
			}
			
			if (!Util.checkIfInRange(radius, target, obj, true))
			{
				continue;
			}
			
			if (!(obj.isL2Attackable() || obj.isPlayable()))
			{
				continue;
			}
			
			if (!L2Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena))
			{
				continue;
			}
			
			if ((skill.getMaxTargets() > -1) && (targetList.size() >= skill.getMaxTargets()))
			{
				break;
			}
			
			targetList.add(obj);
		}
		
		if (targetList.isEmpty())
		{
			return _emptyTargetList;
		}
		
		return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.TARGET_AREA_SUMMON;
	}
}
