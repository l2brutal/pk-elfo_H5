package pk.elfo.gameserver.network.clientpackets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;

import pk.elfo.gameserver.network.L2GameClient;

/**
 * Format: c dddd
 */
public class GameGuardReply extends L2GameClientPacket
{
	private static final String _C__CB_GAMEGUARDREPLY = "[C] CB GameGuardReply";
	
	private static final byte[] VALID =
	{
		(byte) 0x88,
		0x40,
		0x1c,
		(byte) 0xa7,
		(byte) 0x83,
		0x42,
		(byte) 0xe9,
		0x15,
		(byte) 0xde,
		(byte) 0xc3,
		0x68,
		(byte) 0xf6,
		0x2d,
		0x23,
		(byte) 0xf1,
		0x3f,
		(byte) 0xee,
		0x68,
		0x5b,
		(byte) 0xc5,
	};
	
	private final byte[] _reply = new byte[8];
	
	@Override
	protected void readImpl()
	{
		readB(_reply, 0, 4);
		readD();
		readB(_reply, 4, 4);
	}
	
	@Override
	protected void runImpl()
	{
		L2GameClient client = getClient();
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] result = md.digest(_reply);
			if (Arrays.equals(result, VALID))
			{
				client.setGameGuardOk(true);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			_log.log(Level.WARNING, "", e);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__CB_GAMEGUARDREPLY;
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}