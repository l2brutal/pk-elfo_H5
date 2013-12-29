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
package handlers.skillhandlers;

import java.util.List;

import pk.elfo.gameserver.handler.ISkillHandler;
import pk.elfo.gameserver.model.L2Object;
import pk.elfo.gameserver.model.ShotType;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.actor.instance.L2PetInstance;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.model.skills.L2SkillType;
import pk.elfo.gameserver.model.skills.targets.L2TargetType;
import pk.elfo.gameserver.model.stats.Formulas;
import pk.elfo.gameserver.taskmanager.DecayTaskManager;
import javolution.util.FastList;

public class Resurrect implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.RESURRECT
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		L2PcInstance player = null;
		if (activeChar.isPlayer())
		{
			player = activeChar.getActingPlayer();
		}
		
		L2PcInstance targetPlayer;
		List<L2Character> targetToRes = new FastList<>();
		
		for (L2Character target : (L2Character[]) targets)
		{
			if (target.isPlayer())
			{
				targetPlayer = target.getActingPlayer();
				
				// Check for same party or for same clan, if target is for clan.
				if (skill.getTargetType() == L2TargetType.TARGET_CORPSE_CLAN)
				{
					if ((player != null) && (player.getClanId() != targetPlayer.getClanId()))
					{
						continue;
					}
				}
			}
			if (target.isVisible())
			{
				targetToRes.add(target);
			}
		}
		
		if (targetToRes.isEmpty())
		{
			activeChar.abortCast();
			return;
		}
		
		for (L2Character cha : targetToRes)
		{
			if (activeChar.isPlayer())
			{
				if (cha.isPlayer())
				{
					cha.getActingPlayer().reviveRequest(activeChar.getActingPlayer(), skill, false);
				}
				else if (cha.isPet())
				{
					((L2PetInstance) cha).getOwner().reviveRequest(activeChar.getActingPlayer(), skill, true);
				}
			}
			else
			{
				DecayTaskManager.getInstance().cancelDecayTask(cha);
				cha.doRevive(Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), activeChar));
			}
		}
		
		activeChar.setChargedShot(activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
