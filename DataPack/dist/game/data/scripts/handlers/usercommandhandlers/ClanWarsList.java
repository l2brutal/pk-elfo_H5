package handlers.usercommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import pk.elfo.L2DatabaseFactory;
import pk.elfo.gameserver.handler.IUserCommandHandler;
import pk.elfo.gameserver.model.L2Clan;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;

/**
 * PkElfo
 */

public class ClanWarsList implements IUserCommandHandler
{
	private static final Logger _log = Logger.getLogger(ClanWarsList.class.getName());
	private static final int[] COMMAND_IDS =
	{
		88,
		89,
		90
	};
	// SQL queries
	private static final String ATTACK_LIST = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan1=? AND clan_id=clan2 AND clan2 NOT IN (SELECT clan1 FROM clan_wars WHERE clan2=?)";
	private static final String UNDER_ATTACK_LIST = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan2=? AND clan_id=clan1 AND clan1 NOT IN (SELECT clan2 FROM clan_wars WHERE clan1=?)";
	private static final String WAR_LIST = "SELECT clan_name,clan_id,ally_id,ally_name FROM clan_data,clan_wars WHERE clan1=? AND clan_id=clan2 AND clan2 IN (SELECT clan1 FROM clan_wars WHERE clan2=?)";
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if ((id != COMMAND_IDS[0]) && (id != COMMAND_IDS[1]) && (id != COMMAND_IDS[2]))
		{
			return false;
		}
		
		final L2Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendPacket(SystemMessageId.NOT_JOINED_IN_ANY_CLAN);
			return false;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			String query;
			// Attack List
			if (id == 88)
			{
				activeChar.sendPacket(SystemMessageId.CLANS_YOU_DECLARED_WAR_ON);
				query = ATTACK_LIST;
			}
			// Under Attack List
			else if (id == 89)
			{
				activeChar.sendPacket(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU);
				query = UNDER_ATTACK_LIST;
			}
			// War List
			else
			{
				activeChar.sendPacket(SystemMessageId.WAR_LIST);
				query = WAR_LIST;
			}
			
			try (PreparedStatement ps = con.prepareStatement(query))
			{
				ps.setInt(1, clan.getClanId());
				ps.setInt(2, clan.getClanId());
				
				SystemMessage sm;
				try (ResultSet rs = ps.executeQuery())
				{
					String clanName;
					int ally_id;
					while (rs.next())
					{
						clanName = rs.getString("clan_name");
						ally_id = rs.getInt("ally_id");
						if (ally_id > 0)
						{
							// Target With Ally
							sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_ALLIANCE);
							sm.addString(clanName);
							sm.addString(rs.getString("ally_name"));
						}
						else
						{
							// Target Without Ally
							sm = SystemMessage.getSystemMessage(SystemMessageId.S1_NO_ALLI_EXISTS);
							sm.addString(clanName);
						}
						activeChar.sendPacket(sm);
					}
				}
			}
			activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "", e);
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}