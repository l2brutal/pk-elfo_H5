package pk.elfo.gameserver.network.clientpackets;

import pk.elfo.gameserver.ThreadPoolManager;
import pk.elfo.gameserver.instancemanager.CHSiegeManager;
import pk.elfo.gameserver.instancemanager.CastleManager;
import pk.elfo.gameserver.instancemanager.ClanHallManager;
import pk.elfo.gameserver.instancemanager.FortManager;
import pk.elfo.gameserver.instancemanager.MapRegionManager;
import pk.elfo.gameserver.instancemanager.TerritoryWarManager;
import pk.elfo.gameserver.model.L2SiegeClan;
import pk.elfo.gameserver.model.Location;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.actor.instance.L2SiegeFlagInstance;
import pk.elfo.gameserver.model.entity.Castle;
import pk.elfo.gameserver.model.entity.ClanHall;
import pk.elfo.gameserver.model.entity.Fort;
import pk.elfo.gameserver.model.entity.TvTEvent;
import pk.elfo.gameserver.model.entity.TvTRoundEvent;
import pk.elfo.gameserver.model.entity.clanhall.SiegableHall;

/**
 * This class ...
 * @version $Revision: 1.7.2.3.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRestartPoint extends L2GameClientPacket
{
	private static final String _C__7D_REQUESTRESTARTPOINT = "[C] 7D RequestRestartPoint";
	
	protected int _requestedPointType;
	protected boolean _continuation;
	
	@Override
	protected void readImpl()
	{
		_requestedPointType = readD();
	}
	
	class DeathTask implements Runnable
	{
		final L2PcInstance activeChar;
		
		DeathTask(L2PcInstance _activeChar)
		{
			activeChar = _activeChar;
		}
		
		@Override
		public void run()
		{
			portPlayer(activeChar);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(activeChar.getObjectId()))
		{
			return;
		}
		
		if (TvTRoundEvent.isStarted() && TvTRoundEvent.isPlayerParticipant(activeChar.getObjectId()))
		{
			return;
		}
		
		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(true);
			return;
		}
		else if (!activeChar.isDead())
		{
			_log.warning("Living player [" + activeChar.getName() + "] called RestartPointPacket! Ban this player!");
			return;
		}
		
		Castle castle = CastleManager.getInstance().getCastle(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		if ((castle != null) && castle.getSiege().getIsInProgress())
		{
			if ((activeChar.getClan() != null) && castle.getSiege().checkIsAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getAttackerRespawnDelay());
				if (castle.getSiege().getAttackerRespawnDelay() > 0)
				{
					activeChar.sendMessage("You will be re-spawned in " + (castle.getSiege().getAttackerRespawnDelay() / 1000) + " seconds");
				}
				return;
			}
		}
		
		portPlayer(activeChar);
	}
	
	protected final void portPlayer(final L2PcInstance activeChar)
	{
		Location loc = null;
		Castle castle = null;
		Fort fort = null;
		SiegableHall hall = null;
		boolean isInDefense = false;
		int instanceId = 0;
		
		// force jail
		if (activeChar.isInJail())
		{
			_requestedPointType = 27;
		}
		else if (activeChar.isFestivalParticipant())
		{
			_requestedPointType = 5;
		}
		switch (_requestedPointType)
		{
			case 1: // to clanhall
			{
				if ((activeChar.getClan() == null) || (activeChar.getClan().getHideoutId() == 0))
				{
					_log.warning("Player [" + activeChar.getName() + "] called RestartPointPacket - To Clanhall and he doesn't have Clanhall!");
					return;
				}
				if (activeChar.isInTownWarEvent())
				{
					loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.Town);
				}
				else
				{
					loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.ClanHall);
				}
				
				if ((ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()) != null) && (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case 2: // to castle
			{
				castle = CastleManager.getInstance().getCastle(activeChar);
				
				if ((castle != null) && castle.getSiege().getIsInProgress())
				{
					// Siege in progress
					if (castle.getSiege().checkIsDefender(activeChar.getClan()))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.Castle);
					}
					else if (castle.getSiege().checkIsAttacker(activeChar.getClan()))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.Town);
					}
					else
					{
						_log.warning("Player [" + activeChar.getName() + "] called RestartPointPacket - To Castle and he doesn't have Castle!");
						return;
					}
				}
				else
				{
					if ((activeChar.getClan() == null) || (activeChar.getClan().getCastleId() == 0))
					{
						return;
					}
					loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.Castle);
				}
				if ((CastleManager.getInstance().getCastleByOwner(activeChar.getClan()) != null) && (CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getFunction(Castle.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getFunction(Castle.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case 3: // to fortress
			{
				if (((activeChar.getClan() == null) || (activeChar.getClan().getFortId() == 0)) && !isInDefense)
				{
					_log.warning("Player [" + activeChar.getName() + "] called RestartPointPacket - To Fortress and he doesn't have Fortress!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.Fortress);
				if ((FortManager.getInstance().getFortByOwner(activeChar.getClan()) != null) && (FortManager.getInstance().getFortByOwner(activeChar.getClan()).getFunction(Fort.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(FortManager.getInstance().getFortByOwner(activeChar.getClan()).getFunction(Fort.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case 4: // to siege HQ
			{
				L2SiegeClan siegeClan = null;
				castle = CastleManager.getInstance().getCastle(activeChar);
				fort = FortManager.getInstance().getFort(activeChar);
				hall = CHSiegeManager.getInstance().getNearbyClanHall(activeChar);
				L2SiegeFlagInstance flag = TerritoryWarManager.getInstance().getFlagForClan(activeChar.getClan());
				
				if ((castle != null) && castle.getSiege().getIsInProgress())
				{
					siegeClan = castle.getSiege().getAttackerClan(activeChar.getClan());
				}
				else if ((fort != null) && fort.getSiege().getIsInProgress())
				{
					siegeClan = fort.getSiege().getAttackerClan(activeChar.getClan());
				}
				else if ((hall != null) && hall.isInSiege())
				{
					siegeClan = hall.getSiege().getAttackerClan(activeChar.getClan());
				}
				if (((siegeClan == null) || siegeClan.getFlag().isEmpty()) && (flag == null))
				{
					// Check if clan hall has inner spawns loc
					if ((hall != null) && ((loc = hall.getSiege().getInnerSpawnLoc(activeChar)) != null))
					{
						break;
					}
					
					_log.warning("Player [" + activeChar.getName() + "] called RestartPointPacket - To Siege HQ and he doesn't have Siege HQ!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.SiegeFlag);
				break;
			}
			case 5: // Fixed or Player is a festival participant
			{
				if (!activeChar.isGM() && !activeChar.isFestivalParticipant())
				{
					_log.warning("Player [" + activeChar.getName() + "] called RestartPointPacket - Fixed and he isn't festival participant!");
					return;
				}
				if (activeChar.isGM())
				{
					activeChar.doRevive(100.00);
				}
				else
				// Festival Participant
				{
					instanceId = activeChar.getInstanceId();
					loc = new Location(activeChar);
				}
				break;
			}
			case 6: // TODO: agathion ress
			{
				break;
			}
			case 27: // to jail
			{
				if (!activeChar.isInJail())
				{
					return;
				}
				loc = new Location(-114356, -249645, -2984);
				break;
			}
			default:
			{
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, MapRegionManager.TeleportWhereType.Town);
				break;
			}
		}
		
		// Teleport and revive
		if (loc != null)
		{
			activeChar.setInstanceId(instanceId);
			activeChar.setIsIn7sDungeon(false);
			activeChar.setIsPendingRevive(true);
			activeChar.teleToLocation(loc, true);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__7D_REQUESTRESTARTPOINT;
	}
}
