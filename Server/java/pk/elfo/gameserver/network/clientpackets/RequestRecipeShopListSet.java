package pk.elfo.gameserver.network.clientpackets;

import static pk.elfo.gameserver.model.itemcontainer.PcInventory.MAX_ADENA;

import java.util.Arrays;
import java.util.List;

import pk.elfo.Config;
import pk.elfo.gameserver.datatables.RecipeData;
import pk.elfo.gameserver.model.L2ManufactureItem;
import pk.elfo.gameserver.model.L2ManufactureList;
import pk.elfo.gameserver.model.L2RecipeList;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.zone.ZoneId;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.ActionFailed;
import pk.elfo.gameserver.network.serverpackets.RecipeShopMsg;
import pk.elfo.gameserver.taskmanager.AttackStanceTaskManager;
import pk.elfo.gameserver.util.Util;

/**
 * RequestRecipeShopListSet client packet class.
 */
public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private static final String _C__BB_RequestRecipeShopListSet = "[C] BB RequestRecipeShopListSet";
	
	private static final int BATCH_LENGTH = 12;
	
	private Recipe[] _items = null;
	
	@Override
	protected void readImpl()
	{
		int count = readD();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != _buf.remaining()))
		{
			return;
		}
		
		_items = new Recipe[count];
		for (int i = 0; i < count; i++)
		{
			int id = readD();
			long cost = readQ();
			if (cost < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new Recipe(id, cost);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (_items == null)
		{
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			player.sendPacket(SystemMessageId.NO_PRIVATE_WORKSHOP_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
        if (!player.canOpenPrivateStore())
        {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
		
		L2ManufactureList createList = new L2ManufactureList();
		
		List<L2RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
		List<L2RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());
		final RecipeData rd = RecipeData.getInstance();
		for (Recipe i : _items)
		{
			L2RecipeList list = rd.getRecipeList(i.getRecipeId());
			if (!dwarfRecipes.contains(list) && !commonRecipes.contains(list))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Player " + player.getName() + " of account " + player.getAccountName() + " tried to set recipe which he dont have.", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (!i.addToList(createList))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price more than " + MAX_ADENA + " adena in Private Manufacture.", Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		createList.setStoreName(player.getCreateList() != null ? player.getCreateList().getStoreName() : "");
		player.setCreateList(createList);
		
		player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_MANUFACTURE);
		player.sitDown();
		player.broadcastUserInfo();
		player.sendPacket(new RecipeShopMsg(player));
		player.broadcastPacket(new RecipeShopMsg(player));
	}
	
	private static class Recipe
	{
		private final int _recipeId;
		private final long _cost;
		
		public Recipe(int id, long c)
		{
			_recipeId = id;
			_cost = c;
		}
		
		public boolean addToList(L2ManufactureList list)
		{
			if (_cost > MAX_ADENA)
			{
				return false;
			}
			
			list.add(new L2ManufactureItem(_recipeId, _cost));
			return true;
		}
		
		public int getRecipeId()
		{
			return _recipeId;
		}
	}
	
	@Override
	public String getType()
	{
		return _C__BB_RequestRecipeShopListSet;
	}
}
