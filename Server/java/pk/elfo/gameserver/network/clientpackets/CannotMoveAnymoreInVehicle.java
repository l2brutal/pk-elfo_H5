package pk.elfo.gameserver.network.clientpackets;

import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.network.serverpackets.StopMoveInVehicle;
import pk.elfo.gameserver.util.Point3D;

public final class CannotMoveAnymoreInVehicle extends L2GameClientPacket
{
	private static final String _C__76_CANNOTMOVEANYMOREINVEHICLE = "[C] 76 CannotMoveAnymoreInVehicle";
	
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private int _boatId;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		if (player.isInBoat())
		{
			if (player.getBoat().getObjectId() == _boatId)
			{
				player.setInVehiclePosition(new Point3D(_x, _y, _z));
				player.getPosition().setHeading(_heading);
				StopMoveInVehicle msg = new StopMoveInVehicle(player, _boatId);
				player.broadcastPacket(msg);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return _C__76_CANNOTMOVEANYMOREINVEHICLE;
	}
}