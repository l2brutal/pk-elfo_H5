package handlers.usercommandhandlers;

import java.util.Calendar;

import pk.elfo.gameserver.handler.IUserCommandHandler;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;
 
/**
 * Projeto PkElfo
 */

public class MyBirthday implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		126
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		Calendar date = activeChar.getCreateDate();
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_BIRTHDAY_IS_S3_S4_S2);
		sm.addPcName(activeChar);
		sm.addString(Integer.toString(date.get(Calendar.YEAR)));
		sm.addString(Integer.toString(date.get(Calendar.MONTH) + 1));
		sm.addString(Integer.toString(date.get(Calendar.DATE)));
		
		activeChar.sendPacket(sm);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}