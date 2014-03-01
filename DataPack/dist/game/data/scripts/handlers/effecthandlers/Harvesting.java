package handlers.effecthandlers;

import pk.elfo.Config;
import pk.elfo.gameserver.model.L2Object;
import pk.elfo.gameserver.model.actor.L2Attackable.RewardItem;
import pk.elfo.gameserver.model.actor.instance.L2MonsterInstance;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.effects.EffectTemplate;
import pk.elfo.gameserver.model.effects.L2Effect;
import pk.elfo.gameserver.model.effects.L2EffectType;
import pk.elfo.gameserver.model.stats.Env;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.InventoryUpdate;
import pk.elfo.gameserver.network.serverpackets.ItemList;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;
import pk.elfo.util.Rnd;

/**
 * PkElfo
 */
 
public class Harvesting extends L2Effect
{
	public Harvesting(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean onStart()
	{
		if ((getEffector() == null) || (getEffected() == null) || !getEffector().isPlayer() || !getEffected().isNpc() || !getEffected().isDead())
		{
			return false;
		}
		
		final L2PcInstance player = getEffector().getActingPlayer();
		final L2Object[] targets = getSkill().getTargetList(player, false, getEffected());
		if ((targets == null) || (targets.length == 0))
		{
			return false;
		}
		
		L2MonsterInstance monster;
		final InventoryUpdate iu = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		for (L2Object target : targets)
		{
			if ((target == null) || !target.isMonster())
			{
				continue;
			}
			
			monster = (L2MonsterInstance) target;
			
			if (player.getObjectId() != monster.getSeederId())
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
				player.sendPacket(sm);
				continue;
			}
			
			boolean send = false;
			int total = 0;
			int cropId = 0;
			
			if (monster.isSeeded())
			{
				if (calcSuccess(player, monster))
				{
					final RewardItem[] items = monster.takeHarvest();
					if ((items != null) && (items.length > 0))
					{
						for (RewardItem reward : items)
						{
							cropId = reward.getItemId(); // always got 1 type of crop as reward
							if (player.isInParty())
							{
								player.getParty().distributeItem(player, reward, true, monster);
							}
							else
							{
								if (iu != null)
								{
									iu.addItem(player.getInventory().addItem("Harvesting", reward.getItemId(), reward.getCount(), player, monster));
								}
								send = true;
								total += reward.getCount();
							}
						}
						
						if (send)
						{
							SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1_S2);
							smsg.addNumber(total);
							smsg.addItemName(cropId);
							player.sendPacket(smsg);
							if (player.isInParty())
							{
								smsg = SystemMessage.getSystemMessage(SystemMessageId.C1_HARVESTED_S3_S2S);
								smsg.addString(player.getName());
								smsg.addNumber(total);
								smsg.addItemName(cropId);
								player.getParty().broadcastToPartyMembers(player, smsg);
							}
							
							if (iu != null)
							{
								player.sendPacket(iu);
							}
							else
							{
								player.sendPacket(new ItemList(player, false));
							}
							return true;
						}
					}
				}
				else
				{
					player.sendPacket(SystemMessageId.THE_HARVEST_HAS_FAILED);
				}
			}
			else
			{
				player.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
			}
		}
		return false;
	}
	
	/**
	 * @param activeChar
	 * @param target
	 * @return
	 */
	private boolean calcSuccess(L2PcInstance activeChar, L2MonsterInstance target)
	{
		int basicSuccess = 100;
		final int levelPlayer = activeChar.getLevel();
		final int levelTarget = target.getLevel();
		
		int diff = (levelPlayer - levelTarget);
		if (diff < 0)
		{
			diff = -diff;
		}
		
		// apply penalty, target <=> player levels
		// 5% penalty for each level
		if (diff > 5)
		{
			basicSuccess -= (diff - 5) * 5;
		}
		
		// success rate can't be less than 1%
		if (basicSuccess < 1)
		{
			basicSuccess = 1;
		}
		return Rnd.nextInt(99) < basicSuccess;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.HARVESTING;
	}
}