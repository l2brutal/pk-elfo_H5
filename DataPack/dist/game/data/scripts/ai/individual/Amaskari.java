package ai.individual;

import pk.elfo.gameserver.ai.CtrlIntention;
import pk.elfo.gameserver.instancemanager.HellboundManager;
import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.instance.L2MonsterInstance;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.effects.L2Effect;
import pk.elfo.gameserver.model.holders.SkillHolder;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.network.NpcStringId;
import pk.elfo.gameserver.network.clientpackets.Say2;
import pk.elfo.gameserver.network.serverpackets.NpcSay;
import ai.npc.AbstractNpcAI;

/**
 * Manages Amaskari's and minions' chat and some skill usage.
 * Projeto PkElfo
 */
public class Amaskari extends AbstractNpcAI
{
	private static final int AMASKARI = 22449;
	private static final int AMASKARI_PRISONER = 22450;
	
	private static final int BUFF_ID = 4632;
	private static SkillHolder[] BUFF =
	{
		new SkillHolder(BUFF_ID, 1),
		new SkillHolder(BUFF_ID, 2),
		new SkillHolder(BUFF_ID, 3)
	};
	// private static SkillHolder INVINCIBILITY = new SkillHolder(5417, 1);
	
	private static final NpcStringId[] AMASKARI_NPCSTRING_ID =
	{
		NpcStringId.ILL_MAKE_EVERYONE_FEEL_THE_SAME_SUFFERING_AS_ME,
		NpcStringId.HA_HA_YES_DIE_SLOWLY_WRITHING_IN_PAIN_AND_AGONY,
		NpcStringId.MORE_NEED_MORE_SEVERE_PAIN,
		NpcStringId.SOMETHING_IS_BURNING_INSIDE_MY_BODY
	};
	
	private static final NpcStringId[] MINIONS_NPCSTRING_ID =
	{
		NpcStringId.AHH_MY_LIFE_IS_BEING_DRAINED_OUT,
		NpcStringId.THANK_YOU_FOR_SAVING_ME,
		NpcStringId.IT_WILL_KILL_EVERYONE,
		NpcStringId.EEEK_I_FEEL_SICKYOW
	};
	
	private Amaskari(String name, String descr)
	{
		super(name, descr);
		
		addKillId(AMASKARI, AMASKARI_PRISONER);
		addAttackId(AMASKARI);
		addSpawnId(AMASKARI_PRISONER);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("stop_toggle"))
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getNpcId(), AMASKARI_NPCSTRING_ID[2]));
			((L2MonsterInstance) npc).clearAggroList();
			((L2MonsterInstance) npc).getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			npc.setIsInvul(false);
			// npc.doCast(INVINCIBILITY.getSkill())
		}
		else if (event.equalsIgnoreCase("onspawn_msg") && (npc != null) && !npc.isDead())
		{
			if (getRandom(100) > 20)
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getNpcId(), MINIONS_NPCSTRING_ID[2]));
			}
			else if (getRandom(100) > 40)
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getNpcId(), MINIONS_NPCSTRING_ID[3]));
			}
			startQuestTimer("onspawn_msg", (getRandom(8) + 1) * 30000, npc, null);
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		if ((npc.getNpcId() == AMASKARI) && (getRandom(1000) < 25))
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getNpcId(), AMASKARI_NPCSTRING_ID[0]));
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if ((minion != null) && !minion.isDead() && (getRandom(10) == 0))
				{
					minion.broadcastPacket(new NpcSay(minion.getObjectId(), Say2.NPC_ALL, minion.getNpcId(), MINIONS_NPCSTRING_ID[0]));
					minion.setCurrentHp(minion.getCurrentHp() - (minion.getCurrentHp() / 5));
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.getNpcId() == AMASKARI_PRISONER)
		{
			final L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead())
			{
				master.broadcastPacket(new NpcSay(master.getObjectId(), Say2.NPC_ALL, master.getNpcId(), AMASKARI_NPCSTRING_ID[1]));
				final L2Effect e = master.getFirstEffect(BUFF_ID);
				if ((e != null) && (e.getAbnormalLvl() == 3) && master.isInvul())
				{
					master.setCurrentHp(master.getCurrentHp() + (master.getCurrentHp() / 5));
				}
				else
				{
					master.clearAggroList();
					master.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					if (e == null)
					{
						master.doCast(BUFF[0].getSkill());
					}
					else if (e.getAbnormalLvl() < 3)
					{
						master.doCast(BUFF[e.getAbnormalLvl()].getSkill());
					}
					else
					{
						master.broadcastPacket(new NpcSay(master.getObjectId(), Say2.NPC_ALL, master.getNpcId(), AMASKARI_NPCSTRING_ID[3]));
						// master.doCast(INVINCIBILITY.getSkill())
						master.setIsInvul(true);
						startQuestTimer("stop_toggle", 10000, master, null);
					}
				}
			}
		}
		else if (npc.getNpcId() == AMASKARI)
		{
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if ((minion != null) && !minion.isDead())
				{
					if (getRandom(1000) > 300)
					{
						minion.broadcastPacket(new NpcSay(minion.getObjectId(), Say2.NPC_ALL, minion.getNpcId(), MINIONS_NPCSTRING_ID[1]));
					}
					
					HellboundManager.getInstance().updateTrust(30, true);
					minion.deleteMe();
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		if (!npc.isTeleporting())
		{
			startQuestTimer("onspawn_msg", (getRandom(3) + 1) * 30000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Amaskari(Amaskari.class.getSimpleName(), "ai");
	}
}