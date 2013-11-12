/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package events.CatchATiger;

import king.server.gameserver.ThreadPoolManager;
import king.server.gameserver.model.L2Party;
import king.server.gameserver.model.actor.L2Npc;
import king.server.gameserver.model.actor.instance.L2PcInstance;
import king.server.gameserver.model.event.LongTimeEvent;
import king.server.gameserver.model.holders.SkillHolder;
import king.server.gameserver.model.itemcontainer.PcInventory;
import king.server.gameserver.model.skills.L2Skill;
import king.server.gameserver.model.quest.QuestState;
import king.server.gameserver.network.NpcStringId;
import king.server.gameserver.network.SystemMessageId;
import king.server.gameserver.network.clientpackets.Say2;
import king.server.gameserver.network.serverpackets.ExShowScreenMessage; 
import king.server.gameserver.network.serverpackets.NpcSay;
import king.server.gameserver.network.serverpackets.PlaySound;
import king.server.gameserver.network.serverpackets.SystemMessage;
import king.server.gameserver.util.Util;
import king.server.util.Rnd;

/**
 * PkElfo
 *
 */
 
public class CatchATiger extends LongTimeEvent
{
	private static final int PIG = 13196;
	private static final int MANAGER = 13292;
	private static final int BABY_TIGER = 13286;
	private static final int BABY_TIGER_CAPTAIN = 13287;
	private static final int GLOOMY_TIGER = 13288;
	private static final int GLOOMY_TIGER_CAPTAIN = 13289;
	private static final int WHITE_TIGER = 13290;
	private static final int WHITE_TIGER_CAPTAIN = 13291;
	private static final int SUMMONER = 13293;
	private static final int BOSS_SUMMONER = 13294;
	private static final int[] SKILLS_DMG_TO_ME = { 9088, 9089 };
	private static final SkillHolder[] SKILLS_DMG_BY_ME = { new SkillHolder(6133, 1), new SkillHolder(6135, 1) }; 
	private static final int PAYMENT = 2010;
	private static final long INTERVAL = 12L * 60 * 60 * 1000; //12h
	private static final int PACKAGE = 17066;
	private static final int POTION = 17067;
	private static final int APIGA = 14720;
	private static final int GOLDEN_APIGA = 14721;
	private static final int[] REWARDS = { 17080, 17079, 17078, 17077, 17076, 17075, 17074, 17073, 17072, 17071, 17070, 17069 };
	private static final int[] CHANCES = { 484871, 227307, 550, 515, 470, 382, 275, 211, 168, 80, 35, 0 };

	public static final NpcStringId[] COUNTDOWN_MESSAGES = 
	{
		NpcStringId.TIME_UP,
		NpcStringId.N1_SECONDS_ARE_REMAINING,
		NpcStringId.N2_SECONDS_ARE_REMAINING,
		NpcStringId.N3_SECONDS_ARE_REMAINING,
		NpcStringId.N4_SECONDS_ARE_REMAINING,
		NpcStringId.N5_SECONDS_ARE_REMAINING,
		NpcStringId.N6_SECONDS_ARE_REMAINING,
		NpcStringId.N7_SECONDS_ARE_REMAINING,
		NpcStringId.N8_SECONDS_ARE_REMAINING,
		NpcStringId.N9_SECONDS_ARE_REMAINING,
		NpcStringId.N10_SECONDS_ARE_REMAINING,
		NpcStringId.N11_SECONDS_ARE_REMAINING,
		NpcStringId.N12_SECONDS_ARE_REMAINING,
		NpcStringId.N13_SECONDS_ARE_REMAINING,
		NpcStringId.N14_SECONDS_ARE_REMAINING,
		NpcStringId.N15_SECONDS_ARE_REMAINING,
		NpcStringId.N16_SECONDS_ARE_REMAINING,
		NpcStringId.N17_SECONDS_ARE_REMAINING,
		NpcStringId.N18_SECONDS_ARE_REMAINING,
		NpcStringId.N19_SECONDS_ARE_REMAINING,
		NpcStringId.N20_SECONDS_ARE_REMAINING,
		NpcStringId.N21_SECONDS_ARE_REMAINING,
		NpcStringId.N22_SECONDS_ARE_REMAINING,
		NpcStringId.N23_SECONDS_ARE_REMAINING,
		NpcStringId.N24_SECONDS_ARE_REMAINING,
		NpcStringId.N25_SECONDS_ARE_REMAINING,
		NpcStringId.N26_SECONDS_ARE_REMAINING,
		NpcStringId.N27_SECONDS_ARE_REMAINING,
		NpcStringId.N28_SECONDS_ARE_REMAINING,
		NpcStringId.N29_SECONDS_ARE_REMAINING,
		NpcStringId.N30_SECONDS_ARE_REMAINING,
		NpcStringId.N31_SECONDS_ARE_REMAINING,
		NpcStringId.N32_SECONDS_ARE_REMAINING,
		NpcStringId.N33_SECONDS_ARE_REMAINING,
		NpcStringId.N34_SECONDS_ARE_REMAINING,
		NpcStringId.N35_SECONDS_ARE_REMAINING,
		NpcStringId.N36_SECONDS_ARE_REMAINING,
		NpcStringId.N37_SECONDS_ARE_REMAINING,
		NpcStringId.N38_SECONDS_ARE_REMAINING,
		NpcStringId.N39_SECONDS_ARE_REMAINING,
		NpcStringId.N40_SECONDS_ARE_REMAINING,
	};
	
	private static final NpcStringId[] PIG_SKILL_ATTACK_TEXT = 
	{
		NpcStringId.WHATS_THIS_FOOD,
		NpcStringId.MY_ENERGY_IS_OVERFLOWING_I_DONT_NEED_ANY_FATIGUE_RECOVERY_POTION,
		NpcStringId.WHATS_THE_MATTER_THATS_AN_AMATEUR_MOVE
	};
	private static final NpcStringId[] PIG_ON_SPAWN_TEXT =
	{
		NpcStringId.ROAR_NO_OINK_OINK_SEE_IM_A_PIG_OINK_OINK,
		NpcStringId.WHO_AM_I_WHERE_AM_I_OINK_OINK
	};
	private static final NpcStringId[] NO_SKILL_ATTACK_TEXT =
	{
		NpcStringId.HEY_ARE_YOU_PLANNING_ON_EATING_ME_USE_A_CUPIDS_FATIGUE_RECOVERY_POTION_ALREADY,
		NpcStringId.ILL_PASS_ON_AN_AMATEURS_MERIDIAN_MASSAGE_USE_A_CUPIDS_FATIGUE_RECOVERY_POTION_ALREADY
	};
	private static final String[] SKILL_ATTACK_TEXT = 
	{
		"*Roar* *Grunt Grunt* I don't feel like doing anything right now.",
		"*Roar* Yeah, right there! That tickles!",
		"[I feel kind of sleepy...",
		"Wow I feel really tired today... I wonder why?",
		"*Roar* My body feels as light as a feather."
	};
	private static final String[] DEATH_TEXT =
	{
		"*Roar* I feel like I could use a nap...!",
		"*Meow* I'm sleepy. Think I'll go take a nap.",
		"I can't feel my legs anymore... ZzzZzz"
	};
	private static final String[] FORTUNE_DEATH_TEXT =
	{
		"*Roar* I think I'll go to sleep.",
		"So sleepy. You wouldn't happen to be the sandman, %name%, would you?",
		"Incredible. From now on, I'll compare all massages to this one with %name%!"
	};
	public CatchATiger(String name, String descr)
	{
		super(name, descr);
		addFirstTalkId(MANAGER);
		addStartNpc(MANAGER);
		addTalkId(MANAGER);

		for (int i = BABY_TIGER; i <= WHITE_TIGER_CAPTAIN; i++)
		{
			addSpawnId(i);
			addAttackId(i);
			addKillId(i);
		}
		
		addSpawnId(SUMMONER);
		addSpawnId(BOSS_SUMMONER);
		addSpawnId(PIG);
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		return isDropPeriod() ? "13292-01.htm" : "13292-11.htm";  
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("spawn_summon"))
		{
			if (npc.getSummoner() != null)
			{
				int summonId;
				if (Rnd.get(100) <= 75)
				{
					summonId = npc.getNpcId() == SUMMONER ? GLOOMY_TIGER : GLOOMY_TIGER_CAPTAIN;
				}
				else
				{
					summonId = PIG;
				}

				L2Npc summon = addSpawn(summonId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, summonId == PIG ? 10000 : 360000);
				summon.setSummoner(npc.getSummoner());
			}

			return null;
		}
		QuestState st = player.getQuestState(getName());
		String htmltext = event.endsWith(".htm") ? event : "";

		if (st == null)
		{
			return null;
		}
		if (event.equalsIgnoreCase("give_package"))
		{
			if (isDropPeriod())
			{
				if (st.getQuestItemsCount(PcInventory.ADENA_ID) >= PAYMENT)
				{
					long now = System.currentTimeMillis();
					String val = loadGlobalQuestVar(player.getAccountName());
					long nextTime = val.equals("") ? 0 : Long.parseLong(val);

					if (now > nextTime)
					{
						st.startQuest();
						st.takeItems(PcInventory.ADENA_ID, PAYMENT);
						st.giveItems(PACKAGE, 1);
						saveGlobalQuestVar(player.getAccountName(), Long.toString(System.currentTimeMillis() + INTERVAL));
					}
					else
					{
						long remainingTime = (nextTime - System.currentTimeMillis()) / 1000;
						int hours = (int) (remainingTime / 3600);
						int minutes = (int) ((remainingTime % 3600) / 60);
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
						sm.addItemName(PACKAGE);
						sm.addNumber(hours);
						sm.addNumber(minutes);
						player.sendPacket(sm);
					}
				}
				else
				{
					htmltext = "13292-03.htm";
				}
			}
			else
			{
				htmltext = "13292-12.htm";
			}
		}
		else if (event.equalsIgnoreCase("give_potions"))
		{
			if (st.getQuestItemsCount(PcInventory.ADENA_ID) >= PAYMENT)
			{
				st.takeItems(PcInventory.ADENA_ID, PAYMENT);
				st.giveItems(POTION, 200);
			}
			else
			{
				htmltext = "13292-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("give_reward"))
		{
			if (st.getQuestItemsCount(APIGA) >= 20)
			{
				htmltext = "13292-06.htm";
				st.takeItems(APIGA, 20);
				int random = Rnd.get(1000000);
				for (int i = 0; i < REWARDS.length; i++)
				{
					if (random >= CHANCES[i])
					{
						st.giveItems(REWARDS[i], 1);
						break;
					}
				}
			}
			else
			{
				htmltext = "13292-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("give_adv_reward"))
		{
			if (st.getQuestItemsCount(GOLDEN_APIGA) >= 20)
			{
				htmltext = "13292-06.htm";
				st.takeItems(APIGA, 20);
				st.giveItems(17081, 1);
			}
			else
			{
				htmltext = "13292-05.htm";
			}
		}
		else if (event.equalsIgnoreCase("success"))
		{
			ExShowScreenMessage sm = new ExShowScreenMessage(2, 0, 2, 0, 1, 0, 0, true, 1000, false, null, NpcStringId.MISSION_SUCCESS, null);
			player.sendPacket(sm);
		}

		return htmltext;
	}

	@Override
	public String onAttack (L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getSummoner().getActingPlayer() == null || !npc.getSummoner().getActingPlayer().isOnline())
		{
			npc.deleteMe();
		}
		else
		{ 
			int npcId = npc.getNpcId();
			if (Rnd.get(100) < 10)
			{
				npc.setTarget(attacker);
				npc.doCast(SKILLS_DMG_BY_ME[0].getSkill());
				npc.doCast(SKILLS_DMG_BY_ME[1].getSkill());
			}
			if ((skill != null) && Util.contains(SKILLS_DMG_TO_ME, skill.getId()))
			{
				if (!npc.isBusy() && (npcId >= BABY_TIGER) && (npcId <= WHITE_TIGER_CAPTAIN))
				{
					npc.setBusy(true); //there is only one chance :)
					//Works for Tigers regardless of party, for Tiger Captains - only if party is gathered 
					if ((((npcId == BABY_TIGER) || (npcId == GLOOMY_TIGER) || (npcId == WHITE_TIGER)) || 
							(((npcId == BABY_TIGER_CAPTAIN) || (npcId == GLOOMY_TIGER_CAPTAIN) || (npcId == WHITE_TIGER_CAPTAIN)) && 
								attacker.isInParty() && !attacker.getParty().getMembers().isEmpty())) && (Rnd.get(100) < 30))
					{
						npc.setBusyMessage("fortune");
						int counter = ((npcId == BABY_TIGER) || (npcId == GLOOMY_TIGER) || (npcId == WHITE_TIGER)) ? 10 : 40;
						String snd = ((npcId == BABY_TIGER) || (npcId == GLOOMY_TIGER) || (npcId == WHITE_TIGER)) ? "EV_04" : "EV_03";
						NpcStringId fstringId = ((npcId == BABY_TIGER) || (npcId == GLOOMY_TIGER) || (npcId == WHITE_TIGER)) ? NpcStringId.FORTUNE_TIMER_REWARD_INCREASES_2_TIMES_IF_COMPLETED_WITHIN_10_SECONDS : NpcStringId.FORTUNE_TIMER_REWARD_INCREASES_2_TIMES_IF_COMPLETED_WITHIN_40_SECONDS;
						PlaySound ps = new PlaySound(1, snd, 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ());
						ExShowScreenMessage sm = new ExShowScreenMessage(2, 0, 2, 0, 1, 0, 0, true, 1000, false, null, fstringId, null);

						if ((npc.getNpcId() == BABY_TIGER) || (npc.getNpcId() == GLOOMY_TIGER))
						{
							attacker.sendPacket(ps);
							attacker.sendPacket(sm);
						}
						else
						{
							attacker.getParty().broadcastPacket(ps);
							attacker.getParty().broadcastPacket(sm);
						}
						ThreadPoolManager.getInstance().scheduleGeneral(new CountdownTask(npc, counter), 1000);
					}
				}
				else if (npcId == PIG)
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), PIG_SKILL_ATTACK_TEXT[Rnd.get(3)]));
				}
				else
				{
					if (npc.getSummoner().getObjectId() == attacker.getObjectId())
					{
						if (Rnd.get(100) < 10)
						{
							//npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), 1801178 + Rnd.get(5)));
							//I have client crash on fstringId, so String constructor is used here
							npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), SKILL_ATTACK_TEXT[Rnd.get(5)]));
						}
					}
					else if (((npcId == BABY_TIGER) || (npcId == GLOOMY_TIGER) || (npcId == WHITE_TIGER)) && (Rnd.get(100) < 10))
					{
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), NpcStringId.HEY_I_ALREADY_HAVE_AN_OWNER));
					}
				}
			}
			else if (Rnd.get(100) < 10)
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), NO_SKILL_ATTACK_TEXT[Rnd.get(2)]));
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}

	@Override
	public String onKill (L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getSummoner().getActingPlayer() != null && npc.getSummoner().getActingPlayer().isOnline())
		{
			QuestState st = killer.getQuestState(getName());
			if (st == null)
			{
				st = newQuestState(killer);
			}
			long owner_count = Rnd.get(2) + 1;
			if ((npc.getNpcId() == BABY_TIGER) || (npc.getNpcId() == GLOOMY_TIGER) || (npc.getNpcId() == WHITE_TIGER))
			{
				if (npc.getBusyMessage().equalsIgnoreCase("fortune"))
				{
					if (npc.isInsideRadius(killer, 1500, true, false))
					{
						if (npc.getNpcId() == WHITE_TIGER)
						{
							long golden_count = Rnd.get(2);
							if (golden_count > 0)
							{
								st.giveItems(GOLDEN_APIGA, golden_count * 2);
							}
						}
						st.giveItems(APIGA, owner_count * 2);
						ExShowScreenMessage sm = new ExShowScreenMessage(2, 0, 2, 0, 1, 0, 0, true, 5000, false, null, NpcStringId.MISSION_SUCCESS, null);						
						killer.sendPacket(sm);
					}		
					//NpcSay ns = new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), 1801183 + Rnd.get(3));
					//ns.addStringParameter(killer.getName());
					NpcSay ns = new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), FORTUNE_DEATH_TEXT[Rnd.get(3)].replaceFirst("%.*?%", killer.getName()));
					npc.broadcastPacket(ns);
				}
				else
				{
					if (npc.isInsideRadius(killer, 1500, true, false))
					{
						if (npc.getNpcId() == WHITE_TIGER)
						{
							long golden_count = Rnd.get(2);
							if (golden_count > 0)
							{
								st.giveItems(GOLDEN_APIGA, golden_count * 2);
							}
						}
						st.giveItems(APIGA, owner_count);
					}
					//npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), 1801186 + Rnd.get(3)));
					//I have client crash on fstringId, so String constructor is used here
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), DEATH_TEXT[Rnd.get(3)]));
				}
				if ((npc.getNpcId() == GLOOMY_TIGER) && (Rnd.get(100) < 30))
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), NpcStringId.SORRY_BUT_ILL_LEAVE_MY_FRIEND_IN_YOUR_CARE_AS_WELL_THANKS)); // Sorry, but let me ask my friend too~ Thanks.
					L2Npc monster =	addSpawn(WHITE_TIGER, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 360000);
					monster.setSummoner(npc.getSummoner());
				}
			}	
			else if ((npc.getNpcId() == BABY_TIGER_CAPTAIN) || (npc.getNpcId() == GLOOMY_TIGER_CAPTAIN) || (npc.getNpcId() == WHITE_TIGER_CAPTAIN))
			{
				if ((!killer.isInParty()) || killer.getParty().getMembers().isEmpty())
				{
					if (npc.isInsideRadius(killer, 1500, true, false))
					{
						st.giveItems(APIGA, owner_count);
					}
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), NpcStringId.ISNT_IT_TOUGH_DOING_IT_ALL_ON_YOUR_OWN_NEXT_TIME_TRY_MAKING_A_PARTY_WITH_SOME_COMRADES));
				}
				else
				{
					L2Party party = killer.getParty();
					if (npc.getBusyMessage().equalsIgnoreCase("fortune"))
					{
						if (npc.isInsideRadius(killer, 1500, true, false))
						{
							if (npc.getNpcId() == WHITE_TIGER_CAPTAIN)
							{
								st.giveItems(GOLDEN_APIGA, (long) (owner_count * party.getMemberCount() * 0.2 * 2));
							}

							st.giveItems(APIGA, (long) (owner_count * party.getMemberCount() * 0.2 * 2));
						}				
						//NpcSay ns = new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), 1801183 + Rnd.get(3));
						//ns.addStringParameter(killer.getName());
						NpcSay ns = new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), FORTUNE_DEATH_TEXT[Rnd.get(3)].replaceFirst("%.*?%", killer.getName()));
						npc.broadcastPacket(ns);
						ExShowScreenMessage sm = new ExShowScreenMessage(2, 0, 2, 0, 1, 0, 0, true, 5000, false, null, NpcStringId.MISSION_SUCCESS, null);

						for (L2PcInstance partyMember : party.getMembers())
						{
							if (partyMember != null && !partyMember.isDead() && npc.isInsideRadius(partyMember, 1500, true, false))
							{
								QuestState st2 = partyMember.getQuestState(getName());
								if (st2 == null)
								{
									st2 = newQuestState(killer);
								}							
								if (npc.getNpcId() == WHITE_TIGER_CAPTAIN)
								{
									st2.giveItems(GOLDEN_APIGA, owner_count * 2);
								}
								st2.giveItems(APIGA, owner_count * 2);
								partyMember.sendPacket(sm);
							} 
						}						
					}
					else
					{
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), DEATH_TEXT[Rnd.get(3)]));
						if (npc.isInsideRadius(killer, 1500, true, false))
						{
							if (npc.getNpcId() == WHITE_TIGER_CAPTAIN)
							{
								st.giveItems(GOLDEN_APIGA, (long) (owner_count * party.getMemberCount() * 0.2));
							}
							
							st.giveItems(APIGA, (long) (owner_count * party.getMemberCount() * 0.2));
						}						
						for (L2PcInstance partyMember : party.getMembers())
						{
							if (partyMember != null && !partyMember.isDead() && npc.isInsideRadius(partyMember, 1500, true, false))
							{
								QuestState st2 = partyMember.getQuestState(getName());
								if (st2 == null)
								{
									st2 = newQuestState(killer);
								}
								st2.giveItems(APIGA, owner_count);
							} 
						}
					}
					if ((npc.getNpcId() == GLOOMY_TIGER_CAPTAIN) && (Rnd.get(100) < 30))
					{
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), NpcStringId.SORRY_BUT_ILL_LEAVE_MY_FRIEND_IN_YOUR_CARE_AS_WELL_THANKS)); // Sorry, but let me ask my friend too~ Thanks.
						L2Npc monster =	addSpawn(WHITE_TIGER_CAPTAIN, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 360000);
						monster.setSummoner(npc.getSummoner());
					}
				}
			}
		} 
		return super.onKill(npc, killer, isPet);
	}

	@Override
	public String onSpawn(L2Npc npc)
	{
		int npcId = npc.getNpcId();
		if ((npcId >= BABY_TIGER) && (npcId <= WHITE_TIGER_CAPTAIN))
		{
			npc.disableCoreAI(true);
			npc.setBusyMessage("");
			npc.setBusy(false);
		}
		else if ((npcId == SUMMONER) || (npcId == BOSS_SUMMONER))
		{
			startQuestTimer("spawn_summon", 1000, npc, null); // TODO: Temp hack, summoner sets AFTER spawn, so it needs to make delay.
		}
		else if (npcId == PIG)
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getNpcId(), PIG_ON_SPAWN_TEXT[Rnd.get(2)]));
		}
		return super.onSpawn(npc);
	}

	private class CountdownTask implements Runnable
	{
		private final L2Npc _npc;
		private int _counter;
		public CountdownTask(L2Npc npc, int counter)
		{
			_npc = npc;
			_counter = counter;
		}

		@Override
		public void run()
		{
			if (_npc != null)
			{
				ExShowScreenMessage sm = new ExShowScreenMessage(2, 0, 2, 0, 1, 0, 0, true, 1000, false, null, COUNTDOWN_MESSAGES[_counter], null);
				if ((_npc.getSummoner().getActingPlayer() == null) || !_npc.getSummoner().getActingPlayer().isOnline())
				{
					_npc.deleteMe();
				}
				else if ((_npc.getNpcId() == BABY_TIGER_CAPTAIN) && ((!_npc.getSummoner().isInParty()) || _npc.getSummoner().getParty().getMembers().isEmpty()))
				{
					_npc.setBusyMessage("");
				}
				else if (_npc.isDead())
				{
					startQuestTimer("success", 4000, _npc, _npc.getSummoner().getActingPlayer());
				}
				else if (_counter == 0)
				{
					_npc.setBusyMessage("");
					if (_npc.getNpcId() == BABY_TIGER_CAPTAIN)
					{
						_npc.getSummoner().getParty().broadcastPacket(sm);
					}
					else
					{ 
						_npc.getSummoner().sendPacket(sm);
					}
				}
				else
				{
					if (_npc.getNpcId() == BABY_TIGER_CAPTAIN)
					{
						_npc.getSummoner().getParty().broadcastPacket(sm);
					}
					else
					{
						_npc.getSummoner().sendPacket(sm);
					}
					ThreadPoolManager.getInstance().scheduleGeneral(new CountdownTask(_npc, _counter - 1), 1000);
				}
			}
		}
	}
	public static void main(String[] args)
	{
		new CatchATiger(CatchATiger.class.getSimpleName(), "events");
	}
}