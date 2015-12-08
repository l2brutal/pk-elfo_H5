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
package pk.elfo.gameserver.network.clientpackets;

import pk.elfo.Config;
import pk.elfo.gameserver.ai.CtrlIntention;
import pk.elfo.gameserver.datatables.SkillTable;
import pk.elfo.gameserver.model.L2CharPosition;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.actor.position.PcPosition;
import pk.elfo.gameserver.model.holders.SkillHolder;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.model.skills.L2SkillType;
import pk.elfo.gameserver.model.skills.targets.L2TargetType;
import pk.elfo.gameserver.network.serverpackets.ActionFailed;

public final class RequestMagicSkillUse extends L2GameClientPacket
{
	private static final String _C__39_REQUESTMAGICSKILLUSE = "[C] 39 RequestMagicSkillUse";
	
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_magicId = readD(); // Identifier of the used skill
		_ctrlPressed = readD() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = readC() != 0; // True if Shift pressed
	}
	
	@Override
	protected void runImpl()
	{
		// Get the current L2PcInstance of the player
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Get the level of the used skill
		int level = activeChar.getSkillLevel(_magicId);
		if (level <= 0)
		{
			// Player doesn't know this skill, maybe it's the display Id.
			final SkillHolder customSkill = activeChar.getCustomSkills().get(_magicId);
			if (customSkill != null)
			{
				_magicId = customSkill.getSkillId();
				level = customSkill.getSkillLvl();
			}
			else
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		// Get the L2Skill template corresponding to the skillID received from the client
		L2Skill skill = SkillTable.getInstance().getInfo(_magicId, level);
		
		// Check the validity of the skill
		if (skill != null)
		{
			if ((activeChar.isTransformed() || activeChar.isInStance()) && !activeChar.containsAllowedTransformSkill(skill.getId()))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (Config.DEBUG)
			{
				_log.fine("	skill:" + skill.getName() + " level:" + skill.getLevel() + " passive:" + skill.isPassive());
				_log.fine("	range:" + skill.getCastRange() + " targettype:" + skill.getTargetType() + " power:" + skill.getPower());
				_log.fine("	reusedelay:" + skill.getReuseDelay() + " hittime:" + skill.getHitTime());
			}
			
			// If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
			if ((skill.getSkillType() == L2SkillType.RECALL) && !Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (activeChar.getKarma() > 0))
			{
				return;
			}
			
			// players mounted on pets cannot use any toggle skills
			if (skill.isToggle() && activeChar.isMounted())
			{
				return;
			}
			
			activeChar.useMagic(skill, _ctrlPressed, _shiftPressed);
			
			// Stop if use self-buff (except if on AirShip or Boat).
			if (((skill.getSkillType() == L2SkillType.BUFF) && (skill.getTargetType() == L2TargetType.TARGET_SELF)) && (!activeChar.isInAirShip() || !activeChar.isInBoat()))
			{
				final PcPosition charPos = activeChar.getPosition();
				final L2CharPosition stopPos = new L2CharPosition(charPos.getX(), charPos.getY(), charPos.getZ(), charPos.getHeading());
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, stopPos);
			}
		}
		else
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			_log.warning("No skill found with id " + _magicId + " and level " + level + " !!");
		}
	}
	
	@Override
	public String getType()
	{
		return _C__39_REQUESTMAGICSKILLUSE;
	}
}