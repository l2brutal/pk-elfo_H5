package instances.DemonPrinceFloor;

import java.util.Calendar;

import pk.elfo.gameserver.instancemanager.InstanceManager;
import pk.elfo.gameserver.model.L2Party;
import pk.elfo.gameserver.model.L2World;
import pk.elfo.gameserver.model.Location;
import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.entity.Instance;
import pk.elfo.gameserver.model.instancezone.InstanceWorld;
import pk.elfo.gameserver.model.quest.Quest;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;
import pk.elfo.gameserver.util.Util;

public class DemonPrinceFloor extends Quest
{
	private static final int INSTANCEID = 142; // this is the client number
	private static final int RESET_HOUR = 6;
	private static final int RESET_MIN = 30;
	
	// NPCs
	private static final int GK_4 = 32748;
	private static final int CUBE = 32375;
	private static final int DEMON_PRINCE = 25540;
	
	private static final int SEAL_BREAKER_5 = 15515;
	
	private static final Location ENTRY_POINT = new Location(-22208, 277056, -8239);
	private static final Location EXIT_POINT = new Location(-19024, 277122, -8256);
	
	public DemonPrinceFloor(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(GK_4);
		addStartNpc(CUBE);
		addTalkId(GK_4);
		addTalkId(CUBE);
		addKillId(DEMON_PRINCE);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (npc.getNpcId() == GK_4)
		{
			htmltext = checkConditions(player);
			
			if (htmltext == null)
			{
				enterInstance(player, "[010] DemonPrince.xml");
			}
		}
		else if (npc.getNpcId() == CUBE)
		{
			InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if ((world != null) && (world.getInstanceId() == INSTANCEID))
			{
				world.removeAllowed(player.getObjectId());
				teleportPlayer(player, EXIT_POINT, 0);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		int instanceId = npc.getInstanceId();
		if (instanceId > 0)
		{
			Instance inst = InstanceManager.getInstance().getInstance(instanceId);
			InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			inst.setSpawnLoc(EXIT_POINT);
			
			// Terminate instance in 10 min
			if ((inst.getInstanceEndTime() - System.currentTimeMillis()) > 600000)
			{
				inst.setDuration(600000);
			}
			
			inst.setEmptyDestroyTime(0);
			
			if ((world != null) && (world.getInstanceId() == INSTANCEID))
			{
				setReenterTime(world);
			}
			
			addSpawn(CUBE, -22144, 278744, -8239, 0, false, 0, false, instanceId);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private String checkConditions(L2PcInstance player)
	{
		if (player.getParty() == null)
		{
			return "gk-noparty.htm";
		}
		else if (player.getParty().getLeaderObjectId() != player.getObjectId())
		{
			return "gk-noleader.htm";
		}
		
		return null;
	}
	
	private boolean checkTeleport(L2PcInstance player)
	{
		L2Party party = player.getParty();
		
		if (party == null)
		{
			return false;
		}
		
		if (player.getObjectId() != party.getLeaderObjectId())
		{
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		
		for (L2PcInstance partyMember : party.getMembers())
		{
			if (partyMember.getLevel() < 78)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (!Util.checkIfInRange(500, player, partyMember, true))
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_LOCATION_THAT_CANNOT_BE_ENTERED);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (InstanceManager.getInstance().getPlayerWorld(player) != null)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), INSTANCEID);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_REENTER_YET);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (partyMember.getInventory().getInventoryItemCount(SEAL_BREAKER_5, -1, false) < 1)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_QUEST_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
		}
		
		return true;
	}
	
	private int enterInstance(L2PcInstance player, String template)
	{
		int instanceId = 0;
		// check for existing instances for this player
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		// existing instance
		if (world != null)
		{
			if ((world.getInstanceId() != INSTANCEID))
			{
				player.sendPacket(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER);
				return 0;
			}
			teleportPlayer(player, ENTRY_POINT, world.getInstanceId());
			return world.getInstanceId();
		}
		
		if (!checkTeleport(player))
		{
			return 0;
		}
		
		instanceId = InstanceManager.getInstance().createDynamicInstance(template);
		world = new InstanceWorld();
		world.setInstanceId(instanceId);
		world.setTemplateId(INSTANCEID);
		world.setStatus(0);
		InstanceManager.getInstance().addWorld(world);
		_log.info("Tower of Infinitum - Demon Prince floor started " + template + " Instance: " + instanceId + " created by player: " + player.getName());
		
		for (L2PcInstance partyMember : player.getParty().getMembers())
		{
			teleportPlayer(partyMember, ENTRY_POINT, instanceId);
			partyMember.destroyItemByItemId("Quest", SEAL_BREAKER_5, 1, null, true);
			world.addAllowed(partyMember.getObjectId());
		}
		return instanceId;
	}
	
	public void setReenterTime(InstanceWorld world)
	{
		if (world.getInstanceId() == INSTANCEID)
		{
			// Reenter time should be cleared every Wed and Sat at 6:30 AM, so we set next suitable
			Calendar reenter;
			Calendar now = Calendar.getInstance();
			Calendar reenterPointWed = (Calendar) now.clone();
			reenterPointWed.set(Calendar.AM_PM, Calendar.AM);
			reenterPointWed.set(Calendar.MINUTE, RESET_MIN);
			reenterPointWed.set(Calendar.HOUR_OF_DAY, RESET_HOUR);
			reenterPointWed.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			Calendar reenterPointSat = (Calendar) reenterPointWed.clone();
			reenterPointSat.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			
			if (now.after(reenterPointSat))
			{
				reenterPointWed.add(Calendar.WEEK_OF_MONTH, 1);
				reenter = (Calendar) reenterPointWed.clone();
			}
			else
			{
				reenter = (Calendar) reenterPointSat.clone();
			}
			
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_RESTRICTED);
			sm.addInstanceName(world.getTemplateId());
			// set instance reenter time for all allowed players
			for (int objectId : world.getAllowed())
			{
				L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if ((player != null) && player.isOnline())
				{
					InstanceManager.getInstance().setInstanceTime(objectId, world.getTemplateId(), reenter.getTimeInMillis());
					player.sendPacket(sm);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new DemonPrinceFloor(-1, DemonPrinceFloor.class.getSimpleName(), "instances");
	}
}