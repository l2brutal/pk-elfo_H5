package handlers.skillhandlers;

import pk.elfo.gameserver.handler.ISkillHandler;
import pk.elfo.gameserver.handler.SkillHandler;
import pk.elfo.gameserver.model.L2Object;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.actor.instance.L2SiegeFlagInstance;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.model.skills.L2SkillType;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.StatusUpdate;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;

/**
 * Projeto PkElfo
 */
 
public class HealPercent implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.HEAL_PERCENT,
		L2SkillType.MANAHEAL_PERCENT,
		L2SkillType.CPHEAL_PERCENT,
		L2SkillType.HPMPHEAL_PERCENT,
		L2SkillType.HPMPCPHEAL_PERCENT,
		L2SkillType.HPCPHEAL_PERCENT
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		// check for other effects
		ISkillHandler handler = SkillHandler.getInstance().getHandler(L2SkillType.BUFF);
		
		if (handler != null)
		{
			handler.useSkill(activeChar, skill, targets);
		}
		
		boolean cp = false;
		boolean hp = false;
		boolean mp = false;
		switch (skill.getSkillType())
		{
			case CPHEAL_PERCENT:
				cp = true;
				break;
			case HEAL_PERCENT:
				hp = true;
				break;
			case MANAHEAL_PERCENT:
				mp = true;
				break;
			case HPMPHEAL_PERCENT:
				mp = true;
				hp = true;
				break;
			case HPMPCPHEAL_PERCENT:
				cp = true;
				hp = true;
				mp = true;
				break;
			case HPCPHEAL_PERCENT:
				hp = true;
				cp = true;
				break;
			default:
				break;
		}
		
		StatusUpdate su = null;
		SystemMessage sm;
		double amount = 0;
		boolean full = skill.getPower() == 100.0;
		for (L2Character target : (L2Character[]) targets)
		{
			// if skill power is "0 or less" don't show heal system message.
			if (skill.getPower() <= 0)
			{
				continue;
			}
			
			// 1505 - sublime self sacrifice
			if ((target.isDead() || target.isInvul()) && (skill.getId() != 1505))
			{
				continue;
			}
			
			// Cursed weapon owner can't heal or be healed
			if (target != activeChar)
			{
				if (activeChar.isPlayer() && activeChar.getActingPlayer().isCursedWeaponEquipped())
				{
					continue;
				}
				if (target.isPlayer() && target.getActingPlayer().isCursedWeaponEquipped())
				{
					continue;
				}
			}
			
			// Doors and flags can't be healed in any way
			if (hp && (target.isDoor() || (target instanceof L2SiegeFlagInstance)))
			{
				continue;
			}
			
			// Only players have CP
			if (cp && target.isPlayer())
			{
				if (full)
				{
					amount = target.getMaxCp();
				}
				else
				{
					amount = (target.getMaxCp() * skill.getPower()) / 100.0;
				}
				
				amount = Math.min(amount, target.getMaxRecoverableCp() - target.getCurrentCp());
				
				// Prevent negative amounts
				if (amount < 0)
				{
					amount = 0;
				}
				
				// To prevent -value heals, set the value only if current cp is less than max recoverable.
				if (target.getCurrentCp() < target.getMaxRecoverableCp())
				{
					target.setCurrentCp(amount + target.getCurrentCp());
				}
				
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
				sm.addNumber((int) amount);
				target.sendPacket(sm);
				su = new StatusUpdate(target);
				su.addAttribute(StatusUpdate.CUR_CP, (int) target.getCurrentCp());
			}
			
			if (hp)
			{
				if (full)
				{
					amount = target.getMaxHp();
				}
				else
				{
					amount = (target.getMaxHp() * skill.getPower()) / 100.0;
				}
				
				amount = Math.min(amount, target.getMaxRecoverableHp() - target.getCurrentHp());
				
				// Prevent negative amounts
				if (amount < 0)
				{
					amount = 0;
				}
				
				// To prevent -value heals, set the value only if current hp is less than max recoverable.
				if (target.getCurrentHp() < target.getMaxRecoverableHp())
				{
					target.setCurrentHp(amount + target.getCurrentHp());
				}
				
				if (target.isPlayer())
				{
					if (activeChar != target)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_C1);
						sm.addCharName(activeChar);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED);
					}
					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su = new StatusUpdate(target);
					su.addAttribute(StatusUpdate.CUR_HP, (int) target.getCurrentHp());
				}
			}
			
			if (mp)
			{
				if (full)
				{
					amount = target.getMaxMp();
				}
				else
				{
					amount = (target.getMaxMp() * skill.getPower()) / 100.0;
				}
				
				amount = Math.min(amount, target.getMaxRecoverableMp() - target.getCurrentMp());
				
				// Prevent negative amounts
				if (amount < 0)
				{
					amount = 0;
				}
				
				// To prevent -value heals, set the value only if current mp is less than max recoverable.
				if (target.getCurrentMp() < target.getMaxRecoverableMp())
				{
					target.setCurrentMp(amount + target.getCurrentMp());
				}
				
				if (target.isPlayer())
				{
					if (activeChar != target)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_RESTORED_BY_C1);
						sm.addCharName(activeChar);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED);
					}
					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su = new StatusUpdate(target);
					su.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
				}
			}
			
			if (target.isPlayer())
			{
				target.sendPacket(su);
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}