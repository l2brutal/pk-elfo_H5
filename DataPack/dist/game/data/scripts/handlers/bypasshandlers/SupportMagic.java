package handlers.bypasshandlers;

import pk.elfo.gameserver.handler.IBypassHandler;
import pk.elfo.gameserver.model.actor.L2Character;
import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.base.ClassId;
import pk.elfo.gameserver.model.holders.SkillHolder;

/**
 * Projeto PkElfo
 */

public class SupportMagic implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"supportmagicservitor",
		"supportmagic"
	};
	
	// Buffs
	private static final SkillHolder HASTE_1 = new SkillHolder(4327, 1);
	private static final SkillHolder HASTE_2 = new SkillHolder(5632, 1);
	private static final SkillHolder CUBIC = new SkillHolder(4338, 1);
	private static final SkillHolder[] FIGHTER_BUFFS =
	{
		new SkillHolder(4322, 1), // Wind Walk
		new SkillHolder(4323, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(4324, 1), // Bless the Body
		new SkillHolder(4325, 1), // Vampiric Rage
		new SkillHolder(4326, 1), // Regeneration
	};
	private static final SkillHolder[] MAGE_BUFFS =
	{
		new SkillHolder(4322, 1), // Wind Walk
		new SkillHolder(4323, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(4328, 1), // Bless the Soul
		new SkillHolder(4329, 1), // Acumen
		new SkillHolder(4330, 1), // Concentration
		new SkillHolder(4331, 1), // Empower
	};
	private static final SkillHolder[] SUMMON_BUFFS =
	{
		new SkillHolder(4322, 1), // Wind Walk
		new SkillHolder(4323, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(4324, 1), // Bless the Body
		new SkillHolder(4325, 1), // Vampiric Rage
		new SkillHolder(4326, 1), // Regeneration
		new SkillHolder(4328, 1), // Bless the Soul
		new SkillHolder(4329, 1), // Acumen
		new SkillHolder(4330, 1), // Concentration
		new SkillHolder(4331, 1), // Empower
	};
	
	// Levels
	private static final int LOWEST_LEVEL = 6;
	private static final int HIGHEST_LEVEL = 75;
	private static final int CUBIC_LOWEST = 16;
	private static final int CUBIC_HIGHEST = 34;
	private static final int HASTE_LEVEL_2 = 40;
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!target.isNpc() || activeChar.isCursedWeaponEquipped())
		{
			return false;
		}
		
		if (command.equalsIgnoreCase(COMMANDS[0]))
		{
			makeSupportMagic(activeChar, (L2Npc) target, true);
		}
		else if (command.equalsIgnoreCase(COMMANDS[1]))
		{
			makeSupportMagic(activeChar, (L2Npc) target, false);
		}
		return true;
	}
	
	private static void makeSupportMagic(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final int level = player.getLevel();
		if (isSummon && (!player.hasSummon() || !player.getSummon().isServitor()))
		{
			npc.showChatWindow(player, "data/html/default/SupportMagicNoSummon.htm");
			return;
		}
		else if (level > HIGHEST_LEVEL)
		{
			npc.showChatWindow(player, "data/html/default/SupportMagicHighLevel.htm");
			return;
		}
		else if (level < LOWEST_LEVEL)
		{
			npc.showChatWindow(player, "data/html/default/SupportMagicLowLevel.htm");
			return;
		}
		else if (player.getClassId().level() == 3)
		{
			player.sendMessage("Somente os jogadores que nao tenham concluido a sua 3rd transferencia de classe podem receber estes buffs."); // Custom message
			return;
		}
		
		if (isSummon)
		{
			npc.setTarget(player.getSummon());
			for (SkillHolder skill : SUMMON_BUFFS)
			{
				npc.doCast(skill.getSkill());
			}
			
			if (level >= HASTE_LEVEL_2)
			{
				npc.doCast(HASTE_2.getSkill());
			}
			else
			{
				npc.doCast(HASTE_1.getSkill());
			}
		}
		else
		{
			npc.setTarget(player);
			if (player.isMageClass() && (player.getClassId() != ClassId.overlord) && (player.getClassId() != ClassId.warcryer))
			{
				for (SkillHolder skill : MAGE_BUFFS)
				{
					npc.doCast(skill.getSkill());
				}
			}
			else
			{
				for (SkillHolder skill : FIGHTER_BUFFS)
				{
					npc.doCast(skill.getSkill());
				}
				
				if (level >= HASTE_LEVEL_2)
				{
					npc.doCast(HASTE_2.getSkill());
				}
				else
				{
					npc.doCast(HASTE_1.getSkill());
				}
			}
			
			if ((level >= CUBIC_LOWEST) && (level <= CUBIC_HIGHEST))
			{
				player.doSimultaneousCast(CUBIC.getSkill());
			}
		}
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}