package conquerablehalls.DevastatedCastle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pk.elfo.gameserver.ai.CtrlIntention;
import pk.elfo.gameserver.datatables.ClanTable;
import pk.elfo.gameserver.datatables.NpcTable;
import pk.elfo.gameserver.datatables.SkillTable;
import pk.elfo.gameserver.model.L2Clan;
import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import pk.elfo.gameserver.network.NpcStringId;
import pk.elfo.gameserver.network.clientpackets.Say2;
 
/**
 * Projeto PkElfo
 */

public final class DevastatedCastle extends ClanHallSiegeEngine
{
	private static final String qn = "DevastatedCastle";
	
	private static final int GUSTAV = 35410;
	private static final int MIKHAIL = 35409;
	private static final int DIETRICH = 35408;
	private static final double GUSTAV_TRIGGER_HP = NpcTable.getInstance().getTemplate(GUSTAV).getBaseHpMax() / 12;
	
	private static Map<Integer, Integer> _damageToGustav = new HashMap<>();
	
	public DevastatedCastle(int questId, String name, String descr, int hallId)
	{
		super(questId, name, descr, hallId);
		addKillId(GUSTAV);
		
		addSpawnId(MIKHAIL);
		addSpawnId(DIETRICH);
		
		addAttackId(GUSTAV);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == MIKHAIL)
		{
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.GLORY_TO_ADEN_THE_KINGDOM_OF_THE_LION_GLORY_TO_SIR_GUSTAV_OUR_IMMORTAL_LORD);
		}
		else if (npc.getNpcId() == DIETRICH)
		{
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.SOLDIERS_OF_GUSTAV_GO_FORTH_AND_DESTROY_THE_INVADERS);
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (!_hall.isInSiege())
		{
			return null;
		}
		
		synchronized (this)
		{
			final L2Clan clan = attacker.getClan();
			
			if ((clan != null) && checkIsAttacker(clan))
			{
				final int id = clan.getClanId();
				if (_damageToGustav.containsKey(id))
				{
					int newDamage = _damageToGustav.get(id);
					newDamage += damage;
					_damageToGustav.put(id, newDamage);
				}
				else
				{
					_damageToGustav.put(id, damage);
				}
			}
			
			if ((npc.getCurrentHp() < GUSTAV_TRIGGER_HP) && (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST))
			{
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, SkillTable.getInstance().getInfo(4235, 1), npc);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (!_hall.isInSiege())
		{
			return null;
		}
		
		_missionAccomplished = true;
		
		if (npc.getNpcId() == GUSTAV)
		{
			synchronized (this)
			{
				cancelSiegeTask();
				endSiege();
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public L2Clan getWinner()
	{
		int counter = 0;
		int damagest = 0;
		for (Entry<Integer, Integer> e : _damageToGustav.entrySet())
		{
			final int damage = e.getValue();
			if (damage > counter)
			{
				counter = damage;
				damagest = e.getKey();
			}
		}
		return ClanTable.getInstance().getClan(damagest);
	}
	
	public static void main(String[] args)
	{
		new DevastatedCastle(-1, qn, "conquerablehalls", DEVASTATED_CASTLE);
	}
}