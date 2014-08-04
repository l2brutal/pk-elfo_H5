package instances.ChamberOfDelusion.East;

import pk.elfo.Config;
import pk.elfo.gameserver.ai.CtrlIntention;
import pk.elfo.gameserver.cache.HtmCache;
import pk.elfo.gameserver.instancemanager.InstanceManager;
import pk.elfo.gameserver.model.L2Party;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.model.actor.L2Attackable.RewardItem;
import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.L2Summon;
import pk.elfo.gameserver.model.actor.instance.L2MonsterInstance;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.entity.Instance;
import pk.elfo.gameserver.model.quest.Quest;
import pk.elfo.gameserver.model.quest.QuestState;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.Earthquake;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;
import pk.elfo.gameserver.model.holders.SkillHolder;
import pk.elfo.gameserver.model.instancezone.InstanceWorld;
import pk.elfo.gameserver.util.Util;
import pk.elfo.util.Rnd;

import java.util.List;
import javolution.util.FastList;

public class East extends Quest
{
	private class CDWorld extends InstanceWorld
	{
		protected L2Npc[] managers = new L2Npc[5];
		
		protected int currentRoom;
		private L2Party partyInside;
		protected List<Integer> rewardedBoxes;
		
		public CDWorld(L2Party party)
		{
			super();
			partyInside = party;
			rewardedBoxes = new FastList<Integer>();
		}
		
		protected L2Party getPartyInside()
		{
			return partyInside;
		}
	}
	
	private static final String qn = "East";
	private static final int INSTANCEID = 127; // this is the client number
	//NPCs
	private static final int ENTRANCE_GATEKEEPER = 32658;
	private static final int ROOM_GATEKEEPER_FIRST = 32664;
	private static final int ROOM_GATEKEEPER_LAST = 32668;
	private static final int AENKINEL = 25690;
	private static final int BOX = 18838;
	//Items
	private static final int ENRIA = 4042;
	private static final int ASOFE = 4043;
	private static final int THONS = 4044;
	private static final int LEONARD = 9628;
	private static final int DELUSION_MARK = 15311;
	//Skills
	private static final SkillHolder SUCCESS_SKILL = new SkillHolder(5758, 1);
	private static final SkillHolder FAIL_SKILL = new SkillHolder(5376, 4);
	
	//Managers spawn coordinates (npcId, x, y, z, heading)
	private int[][] MANAGER_SPAWN_POINTS = 
	{
		{ 32664, -122368, -219072, -6720, 0 },
		{ 32665, -122352, -218144, -6720, 0 },
		{ 32666, -122368, -220320, -6720, 0 },
		{ 32667, -121440, -218544, -6720, 0 },
		{ 32668, -121424, -220224, -6720, 0 }
	
	};

	private int[][] BOX_SPAWN_POINTS = 
	{
		{ -121359, -219866, -6720, 0 },
		{ -121663, -219885, -6720, 0 },
		{ -121627, -220530, -6720, 0 },
		{ -121368, -220411, -6720, 0 }
	};

	private static final int[][] ROOM_ENTER_POINTS = 
	{ 
		{ -122368, -218972, -6720 }, 
		{ -122352, -218044, -6720 }, 
		{ -122368, -220220, -6720 }, 
		{ -121440, -218444, -6720 },
		{ -121424, -220124, -6720 } //Raid room
	};

	private static final int[] AENKINEL_SPAWN = { -121359, -219866, -6728, 0 };

	private static final int[] RETURN_POINT = { -114592, -152509, -6723 };
	
	private static final long ROOM_CHANGE_INTERVAL = 480000; //8 min
	private static final int ROOM_CHANGE_RANDOM_TIME = 120; //2 min

	private class TeleCoord
	{
		int instanceId;
		int x;
		int y;
		int z;
	}
	
	private boolean checkConditions(L2PcInstance player)
	{
		L2Party party = player.getParty();
		if (party == null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_IN_PARTY_CANT_ENTER));
			return false;
		}
		if (party.getLeader() != player)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER)); 
			return false;
		}
		for (L2PcInstance partyMember : party.getMembers())
		{
			if (partyMember.getLevel() < 80)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			if (!Util.checkIfInRange(1000, player, partyMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_LOCATION_THAT_CANNOT_BE_ENTERED);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
		}

		return true;
	}
	
	private void teleportplayer(L2PcInstance player, TeleCoord teleto)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(teleto.instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
		return;
	}
	
	private void banishStrangers(CDWorld world)
	{
		L2Party party = world.getPartyInside();

		L2Npc manager = world.managers[world.currentRoom -1];
		TeleCoord tele = new TeleCoord();

		tele.x = RETURN_POINT[0];
		tele.y = RETURN_POINT[1];
		tele.z = RETURN_POINT[2];

		for (L2PcInstance player : manager.getKnownList().getKnownPlayersInRadius(1000))
		{
			if (party == null || player.getParty() == null || player.getParty() != world.getPartyInside())
			{
				world.removeAllowed(player.getObjectId());			
				exitInstance(player);
			}
		}	

    Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		
		//Schedule next banish task only if remaining time is enough
		if (inst.getInstanceEndTime() - System.currentTimeMillis() > 60000)
			startQuestTimer("banish_strangers", 60000, world.managers[world.currentRoom - 1], null);

		return;
	}

	private void changeRoom(CDWorld world)
	{
		L2Party party = world.getPartyInside();

		if (party == null)
			return;

		int newRoom = world.currentRoom;
		
		if (world.currentRoom != ROOM_ENTER_POINTS.length && Rnd.get(100) < 10) //10% chance for teleport to raid room if not here already
			newRoom = ROOM_ENTER_POINTS.length;
		else
		{
			while (newRoom == world.currentRoom) //otherwise teleport to another room, except current
				newRoom = Rnd.get(ROOM_ENTER_POINTS.length - 1) + 1; 
		}

		cancelQuestTimer("banish_strangers", world.managers[world.currentRoom - 1], null);
		banishStrangers(world);		

		for (L2PcInstance partyMember : party.getMembers())
		{
			if (world.getInstanceId() == partyMember.getInstanceId())
			{
				partyMember.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				partyMember.teleToLocation(ROOM_ENTER_POINTS[newRoom-1][0] - 50 + Rnd.get(100), ROOM_ENTER_POINTS[newRoom-1][1] - 50 + Rnd.get(100), ROOM_ENTER_POINTS[newRoom-1][2]);
			}
		}

    Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		long nextInterval = ROOM_CHANGE_INTERVAL + Rnd.get(ROOM_CHANGE_RANDOM_TIME) * 1000;
		
		//Schedule next room change only if remaining time is enough and here is no raid room
		if (inst.getInstanceEndTime() - System.currentTimeMillis() > nextInterval && newRoom != ROOM_ENTER_POINTS.length)
			startQuestTimer("prepare_change_room", nextInterval, world.managers[newRoom - 1], null);

		if (inst.getInstanceEndTime() - System.currentTimeMillis() > 60000)
			startQuestTimer("banish_strangers", 60000, world.managers[newRoom - 1], null);
		
		world.currentRoom = newRoom;
				
		return;
	}
	
	private void enter(CDWorld world)
	{
		L2Party party = world.getPartyInside();

		if (party == null)
			return;
		
		int newRoom = Rnd.get(ROOM_ENTER_POINTS.length - 1) + 1;

		for (L2PcInstance partyMember : party.getMembers())
		{
				QuestState st = partyMember.getQuestState(qn);
				if (st == null)
					st = newQuestState(partyMember);

				if (st.getQuestItemsCount(DELUSION_MARK) > 0)
					st.takeItems(DELUSION_MARK, st.getQuestItemsCount(DELUSION_MARK));
				
				if (partyMember.getObjectId() == party.getLeaderObjectId())
					st.giveItems(DELUSION_MARK, 1);
				
				//Save location for teleport back into main hall
				st.set("return_point", Integer.toString(partyMember.getX()) + ";" + Integer.toString(partyMember.getY()) + ";" + Integer.toString(partyMember.getZ())); 

				partyMember.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				partyMember.setInstanceId(world.getInstanceId());
				world.addAllowed(partyMember.getObjectId());
				partyMember.teleToLocation(ROOM_ENTER_POINTS[newRoom-1][0] - 50 + Rnd.get(100), ROOM_ENTER_POINTS[newRoom-1][1] - 50 + Rnd.get(100), ROOM_ENTER_POINTS[newRoom-1][2]);
		}

    startQuestTimer("prepare_change_room", ROOM_CHANGE_INTERVAL + Rnd.get(ROOM_CHANGE_RANDOM_TIME - 10) * 1000, world.managers[newRoom - 1], null); //schedule room change
    startQuestTimer("banish_strangers", 60000, world.managers[newRoom - 1], null); //schedule checkup for player without party or another party

    world.currentRoom = newRoom;
    
    return;
	}

	private void earthQuake(CDWorld world)
	{
		L2Party party = world.getPartyInside();

		if (party == null)
			return;
 
		for (L2PcInstance partyMember : party.getMembers())
		{
			if (world.getInstanceId() == partyMember.getInstanceId())
				partyMember.sendPacket(new Earthquake(partyMember.getX(), partyMember.getY(), partyMember.getZ(), 20, 10));
		}
	
		startQuestTimer("change_room", 5000, world.managers[world.currentRoom - 1], null);

		return;
	}
	
	protected void spawnState(CDWorld world)
	{
		addSpawn(AENKINEL, AENKINEL_SPAWN[0], AENKINEL_SPAWN[1], AENKINEL_SPAWN[2], AENKINEL_SPAWN[3], false, 0, false, world.getInstanceId());

		for (int i=0; i < MANAGER_SPAWN_POINTS.length; i++)
			world.managers[i] = addSpawn(MANAGER_SPAWN_POINTS[i][0], MANAGER_SPAWN_POINTS[i][1], MANAGER_SPAWN_POINTS[i][2], MANAGER_SPAWN_POINTS[i][3], MANAGER_SPAWN_POINTS[i][4], false, 0, false, world.getInstanceId());

		return;
	}
	
	protected int enterInstance(L2PcInstance player, String template)
	{
		int instanceId = 0;
		//check for existing instances for this player
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		//existing instance
		if (world != null)
		{
			if (!(world instanceof CDWorld))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
				return 0;
			}
			CDWorld currentWorld = (CDWorld) world;  
			TeleCoord tele = new TeleCoord();
			tele.x = ROOM_ENTER_POINTS[currentWorld.currentRoom - 1][0];
			tele.y = ROOM_ENTER_POINTS[currentWorld.currentRoom - 1][1];
			tele.z = ROOM_ENTER_POINTS[currentWorld.currentRoom - 1][2];
			tele.instanceId = world.getInstanceId();
			teleportplayer(player, tele);
			return instanceId;
		}
		//New instance
		if (!checkConditions(player))
			return 0;
		L2Party party = player.getParty();
		instanceId = InstanceManager.getInstance().createDynamicInstance(template);
		world = new CDWorld(party);
		world.setInstanceId(instanceId);
		world.setTemplateId(INSTANCEID);
		world.setStatus(0);
		InstanceManager.getInstance().addWorld(world);
		_log.info("Chamber Of Delusion started " + template + " Instance: " + instanceId + " created by player: " + player.getName());
		spawnState((CDWorld) world);
		enter((CDWorld) world);
		return instanceId;
	}
	
	private void exitInstance(L2PcInstance player)
	{
		int x = RETURN_POINT[0];
		int y = RETURN_POINT[1];
		int z = RETURN_POINT[2];

		QuestState st = player.getQuestState(qn);
		
		if (st != null)
		{
			String return_point = st.get("return_point");
			if (return_point != null)
			{
				String[] coords = return_point.split(";");
				if (coords.length == 3)
				{
					try
					{
						x = Integer.parseInt(coords[0]);
						y = Integer.parseInt(coords[1]);
						z = Integer.parseInt(coords[2]);
					}
					catch(Exception e)
					{
						x = RETURN_POINT[0];
						y = RETURN_POINT[1];
						z = RETURN_POINT[2];
					}
				} 
			}
		}		

		player.setInstanceId(0);
		player.teleToLocation(x, y, z);
		L2Summon pet = player.getSummon();
		if (pet != null)
		{
			pet.setInstanceId(0);
			pet.teleToLocation(x, y, z);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());

		if (tmpworld != null && tmpworld instanceof CDWorld && npc.getNpcId() >= ROOM_GATEKEEPER_FIRST && npc.getNpcId() <= ROOM_GATEKEEPER_LAST)
		{
			CDWorld world = (CDWorld) tmpworld;

			if (event.equals("prepare_change_room"))
				earthQuake(world);
			
			else if (event.equals("change_room"))
				changeRoom(world);

			else if (event.equals("banish_strangers"))
				banishStrangers(world);
			
			//Timers part ends here, further player cannot be null
			if (player == null)
				return null;

			QuestState st = player.getQuestState(qn);

			if (st == null)
				st = newQuestState(player);

			//Change room from dialog
			else if (event.equals("next_room"))
			{
				if (player.getParty() == null)
					htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(),"/data/scripts/instances/ChambersOfDelusion/no_party.htm");
				
				else if (player.getParty().getLeaderObjectId() != player.getObjectId())
					htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(),"/data/scripts/instances/ChambersOfDelusion/no_leader.htm");
				
				else if (st.getQuestItemsCount(DELUSION_MARK) > 0)
				{
					st.takeItems(DELUSION_MARK, 1);
					cancelQuestTimer("prepare_change_room", npc, null);
					cancelQuestTimer("change_room", npc, null);
					changeRoom(world);
				}
				
				else
				{
					htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(),"/data/scripts/instances/ChambersOfDelusion/no_item.htm");
				}
			}
			
			else if (event.equals("go_out"))
			{
				if (player.getParty() == null)
					htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(),"/data/scripts/instances/ChambersOfDelusion/no_party.htm");
				
				else if (player.getParty().getLeaderObjectId() != player.getObjectId())
					htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(),"/data/scripts/instances/ChambersOfDelusion/no_leader.htm");
				
				else
				{
					Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
					
					cancelQuestTimer("prepare_change_room", npc, null);
					cancelQuestTimer("change_room", npc, null);
					cancelQuestTimer("banish_strangers", npc, null);
					
					for (L2PcInstance partyMember : player.getParty().getMembers())
					{
						exitInstance(partyMember);
						world.removeAllowed(partyMember.getObjectId());
					}
					
					inst.setEmptyDestroyTime(0);
			  }
			}
			
			else if (event.equals("look_party"))
			{
				if (player.getParty() != null && player.getParty() == world.getPartyInside())
					player.teleToLocation(ROOM_ENTER_POINTS[world.currentRoom - 1][0], ROOM_ENTER_POINTS[world.currentRoom - 1][1], ROOM_ENTER_POINTS[world.currentRoom - 1][2]);
			}
		}

		return htmltext;
	}

	@Override
	public String onAttack(final L2Npc npc, final L2PcInstance attacker, final int damage, final boolean isPet, final L2Skill skill)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(attacker);
		if (tmpworld !=null && tmpworld instanceof CDWorld)
		{
			CDWorld world = (CDWorld) tmpworld;
			
			if (npc.getNpcId() == BOX && !world.rewardedBoxes.contains(npc.getObjectId()) && npc.getCurrentHp() < npc.getMaxHp() / 10) 
			{
				L2MonsterInstance box = (L2MonsterInstance) npc;
				RewardItem item;
				if (Rnd.get(100) < 25) //25% chance to reward
				{
					world.rewardedBoxes.add(box.getObjectId());
					if (Rnd.get(100) < 33)
					{
						item = new RewardItem(ENRIA, (int) (3 * Config.RATE_DROP_ITEMS));
						box.dropItem(attacker, item);
					}
					if (Rnd.get(100) < 50)
					{
						item = new RewardItem(THONS, (int) (4 * Config.RATE_DROP_ITEMS));
						box.dropItem(attacker, item);
					}
					if (Rnd.get(100) < 50)
					{
						item = new RewardItem(ASOFE, (int) (4 * Config.RATE_DROP_ITEMS));
						box.dropItem(attacker, item);
					}
					if (Rnd.get(100) < 16)
					{
						item = new RewardItem(LEONARD, (int) (2 * Config.RATE_DROP_ITEMS));
						box.dropItem(attacker, item);
					}
					
					box.doCast(SUCCESS_SKILL.getSkill());
				}
				else
					box.doCast(FAIL_SKILL.getSkill());
			}
		}

		return super.onAttack(npc, attacker, damage, isPet, skill);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld !=null && tmpworld instanceof CDWorld)
		{ 
			if (npc.getNpcId() == AENKINEL)
			{
				CDWorld world = (CDWorld) tmpworld;
				Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		
				if (inst.getInstanceEndTime() - System.currentTimeMillis() > 60000)
				{
					cancelQuestTimer("prepare_change_room", world.managers[MANAGER_SPAWN_POINTS.length - 1], null);
					cancelQuestTimer("change_room", world.managers[MANAGER_SPAWN_POINTS.length - 1], null);
					startQuestTimer("change_room", 60000, world.managers[MANAGER_SPAWN_POINTS.length - 1], null);
				}

				for (int i = 0; i < BOX_SPAWN_POINTS.length; i++)
				{
					L2MonsterInstance box = (L2MonsterInstance) addSpawn(BOX, BOX_SPAWN_POINTS[i][0], BOX_SPAWN_POINTS[i][1], BOX_SPAWN_POINTS[i][2], BOX_SPAWN_POINTS[i][3], false, 0, false, world.getInstanceId());
					box.setIsNoRndWalk(true);
				}
			}
		}

		return super.onKill(npc, player, isPet);
	}

	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if (npc.getNpcId() == BOX && skill.getId() == 5376 || skill.getId() == 5758 && !npc.isDead())
			npc.doDie(player); 

		return super.onSpellFinished(npc, player, skill);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		QuestState st = player.getQuestState(qn);

		if (st == null)
			st = newQuestState(player);

		if (npcId == ENTRANCE_GATEKEEPER)
		{
			enterInstance(player, "[001] Delusion Chamber, Eastern Seal.xml");
		}

		return "";
	}
	
	public East(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(ENTRANCE_GATEKEEPER);
		addTalkId(ENTRANCE_GATEKEEPER);
		for (int i = ROOM_GATEKEEPER_FIRST; i <= ROOM_GATEKEEPER_LAST; i++)
		{
			addStartNpc(i);
			addTalkId(i);
		}
		addKillId(AENKINEL);
		addAttackId(BOX);
		addSpellFinishedId(BOX);
	}
	
	public static void main(String[] args)
	{
		new East(-1, qn, "instances/ChamberOfDelusion");
	}
}