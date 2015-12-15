package handlers.chathandlers;

import pk.elfo.Config;
import pk.elfo.gameserver.handler.IChatHandler;
import pk.elfo.gameserver.instancemanager.MapRegionManager;
import pk.elfo.gameserver.model.BlockList;
import pk.elfo.gameserver.model.L2World;
import pk.elfo.gameserver.model.PcCondOverride;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.clientpackets.Say2;
import pk.elfo.gameserver.network.serverpackets.CreatureSay;
import pk.elfo.gameserver.util.Util;

/**
 * Projeto PkElfo
 */

public class ChatTrade implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		8
	};
	
	/**
	 * Handle chat type 'trade'
	 */
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
       if (Config.CHAT_TRADE_NEED_PVPS)
       {
              if (activeChar.getPvpKills() < Config.PVPS_TO_USE_CHAT_TRADE)
              {
                       CreatureSay ct = new CreatureSay(0, Say2.TELL,"Trade","Voce presisa ter " + Config.PVPS_TO_USE_CHAT_TRADE + " PvPs para usar o Trade Chat.");
                       activeChar.sendPacket(ct);
                       return;
              }
        }
		if (activeChar.isChatBanned() && Util.contains(Config.BAN_CHAT_CHANNELS, type))
		{
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
			return;
		}
		
		if (activeChar.isAio() && (Config.ENABLE_AIO_CHAT))
		{
			activeChar.sendMessage("Voce presisar ser AIO para falar aqui.");
			return;
		}
		
		CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		
		L2PcInstance[] pls = L2World.getInstance().getAllPlayersArray();
		
		if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("on") || (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("gm") && activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)))
		{
			int region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
			for (L2PcInstance player : pls)
			{
				if ((region == MapRegionManager.getInstance().getMapRegionLocId(player)) && !BlockList.isBlocked(player, activeChar) && (player.getInstanceId() == activeChar.getInstanceId()))
				{
					player.sendPacket(cs);
				}
			}
		}
		else if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("global"))
		{
			if (!activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !activeChar.getFloodProtectors().getGlobalChat().tryPerformAction("global chat"))
			{
				activeChar.sendMessage("Nao pode flodar no chat global.");
				return;
			}
			
			for (L2PcInstance player : pls)
			{
				if (!BlockList.isBlocked(player, activeChar))
				{
					player.sendPacket(cs);
				}
			}
		}
	}
	
	/**
	 * Returns the chat types registered to this handler.
	 */
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}