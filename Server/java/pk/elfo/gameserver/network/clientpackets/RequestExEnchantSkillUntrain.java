package pk.elfo.gameserver.network.clientpackets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import pk.elfo.Config;
import pk.elfo.gameserver.datatables.EnchantGroupsData;
import pk.elfo.gameserver.datatables.SkillTable;
import pk.elfo.gameserver.model.L2EnchantSkillLearn;
import pk.elfo.gameserver.model.L2EnchantSkillGroup.EnchantSkillHolder;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.itemcontainer.PcInventory;
import pk.elfo.gameserver.model.items.instance.L2ItemInstance;
import pk.elfo.gameserver.model.skills.L2Skill;
import pk.elfo.gameserver.network.SystemMessageId;
import pk.elfo.gameserver.network.serverpackets.ExBrExtraUserInfo;
import pk.elfo.gameserver.network.serverpackets.ExEnchantSkillInfo;
import pk.elfo.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;
import pk.elfo.gameserver.network.serverpackets.ExEnchantSkillResult;
import pk.elfo.gameserver.network.serverpackets.SystemMessage;
import pk.elfo.gameserver.network.serverpackets.UserInfo;

/**
 * Format (ch) dd c: (id) 0xD0 h: (subid) 0x33 d: skill id d: skill lvl
 */
public final class RequestExEnchantSkillUntrain extends L2GameClientPacket
{
	private static final String _C__D0_33_REQUESTEXENCHANTSKILLUNTRAIN = "[C] D0:33 RequestExEnchantSkillUntrain";
	private static final Logger _logEnchant = Logger.getLogger("enchant");
	
	private int _skillId;
	private int _skillLvl;
	
	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if ((_skillId <= 0) || (_skillLvl <= 0))
		{
			return;
		}
		
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getClassId().level() < 3) // requires to have 3rd class quest completed
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_SKILL_ENCHANT_IN_THIS_CLASS);
			return;
		}
		
		if (player.getLevel() < 76)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_SKILL_ENCHANT_ON_THIS_LEVEL);
			return;
		}
		
		if (!player.isAllowedToEnchantSkills())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_SKILL_ENCHANT_ATTACKING_TRANSFORMED_BOAT);
			return;
		}
		
		L2EnchantSkillLearn s = EnchantGroupsData.getInstance().getSkillEnchantmentBySkillId(_skillId);
		if (s == null)
		{
			return;
		}
		
		if ((_skillLvl % 100) == 0)
		{
			_skillLvl = s.getBaseLevel();
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLvl);
		if (skill == null)
		{
			return;
		}
		
		int reqItemId = EnchantGroupsData.UNTRAIN_ENCHANT_BOOK;
		
		final int beforeUntrainSkillLevel = player.getSkillLevel(_skillId);
		if (((beforeUntrainSkillLevel - 1) != _skillLvl) && (((beforeUntrainSkillLevel % 100) != 1) || (_skillLvl != s.getBaseLevel())))
		{
			return;
		}
		
		EnchantSkillHolder esd = s.getEnchantSkillHolder(beforeUntrainSkillLevel);
		
		int requiredSp = esd.getSpCost();
		int requireditems = esd.getAdenaCost();
		
		L2ItemInstance spb = player.getInventory().getItemByItemId(reqItemId);
		if (Config.ES_SP_BOOK_NEEDED)
		{
			if (spb == null) // Haven't spellbook
			{
				player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
				return;
			}
		}
		
		if (player.getInventory().getAdena() < requireditems)
		{
			player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		boolean check = true;
		if (Config.ES_SP_BOOK_NEEDED)
		{
			check &= player.destroyItem("Consume", spb.getObjectId(), 1, player, true);
		}
		
		check &= player.destroyItemByItemId("Consume", PcInventory.ADENA_ID, requireditems, player, true);
		
		if (!check)
		{
			player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		player.getStat().addSp((int) (requiredSp * 0.8));
		
		if (Config.LOG_SKILL_ENCHANTS)
		{
			LogRecord record = new LogRecord(Level.INFO, "Untrain");
			record.setParameters(new Object[]
			{
				player,
				skill,
				spb
			});
			record.setLoggerName("skill");
			_logEnchant.log(record);
		}
		
		player.addSkill(skill, true);
		player.sendPacket(ExEnchantSkillResult.valueOf(true));
		
		if (Config.DEBUG)
		{
			_log.fine("Learned skill ID: " + _skillId + " Level: " + _skillLvl + " for " + requiredSp + " SP, " + requireditems + " Adena.");
		}
		
		player.sendPacket(new UserInfo(player));
		player.sendPacket(new ExBrExtraUserInfo(player));
		
		if (_skillLvl > 100)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.UNTRAIN_SUCCESSFUL_SKILL_S1_ENCHANT_LEVEL_DECREASED_BY_ONE);
			sm.addSkillName(_skillId);
			player.sendPacket(sm);
		}
		else
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.UNTRAIN_SUCCESSFUL_SKILL_S1_ENCHANT_LEVEL_RESETED);
			sm.addSkillName(_skillId);
			player.sendPacket(sm);
		}
		player.sendSkillList();
		final int afterUntrainSkillLevel = player.getSkillLevel(_skillId);
		player.sendPacket(new ExEnchantSkillInfo(_skillId, afterUntrainSkillLevel));
		player.sendPacket(new ExEnchantSkillInfoDetail(2, _skillId, afterUntrainSkillLevel - 1, player));
		player.updateShortCuts(_skillId, afterUntrainSkillLevel);
	}
	
	@Override
	public String getType()
	{
		return _C__D0_33_REQUESTEXENCHANTSKILLUNTRAIN;
	}
}