package handlers;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import pk.elfo.Config;
import pk.elfo.gameserver.handler.ActionHandler;
import pk.elfo.gameserver.handler.ActionShiftHandler;
import pk.elfo.gameserver.handler.AdminCommandHandler;
import pk.elfo.gameserver.handler.BypassHandler;
import pk.elfo.gameserver.handler.ChatHandler;
import pk.elfo.gameserver.handler.ItemHandler;
import pk.elfo.gameserver.handler.SkillHandler;
import pk.elfo.gameserver.handler.TargetHandler;
import pk.elfo.gameserver.handler.TelnetHandler;
import pk.elfo.gameserver.handler.UserCommandHandler;
import pk.elfo.gameserver.handler.VoicedCommandHandler;
import handlers.actionhandlers.L2ArtefactInstanceAction;
import handlers.actionhandlers.L2DecoyAction;
import handlers.actionhandlers.L2DoorInstanceAction;
import handlers.actionhandlers.L2DoorInstanceActionShift;
import handlers.actionhandlers.L2ItemInstanceAction;
import handlers.actionhandlers.L2ItemInstanceActionShift;
import handlers.actionhandlers.L2NpcAction;
import handlers.actionhandlers.L2NpcActionShift;
import handlers.actionhandlers.L2PcInstanceAction;
import handlers.actionhandlers.L2PcInstanceActionShift;
import handlers.actionhandlers.L2PetInstanceAction;
import handlers.actionhandlers.L2StaticObjectInstanceAction;
import handlers.actionhandlers.L2StaticObjectInstanceActionShift;
import handlers.actionhandlers.L2SummonAction;
import handlers.actionhandlers.L2SummonActionShift;
import handlers.actionhandlers.L2TrapAction;
import handlers.admincommandhandlers.AdminAdmin;
import handlers.admincommandhandlers.AdminAio;
import handlers.admincommandhandlers.AdminAnnouncements;
import handlers.admincommandhandlers.AdminBan;
import handlers.admincommandhandlers.AdminBBS;
import handlers.admincommandhandlers.AdminBuffs;
import handlers.admincommandhandlers.AdminCache;
import handlers.admincommandhandlers.AdminCamera;
import handlers.admincommandhandlers.AdminChangeAccessLevel;
import handlers.admincommandhandlers.AdminCheckBot;
import handlers.admincommandhandlers.AdminCHSiege;
import handlers.admincommandhandlers.AdminClan;
import handlers.admincommandhandlers.AdminClanFull;
import handlers.admincommandhandlers.AdminClanSkills;
import handlers.admincommandhandlers.AdminCrazy;
import handlers.admincommandhandlers.AdminCreateItem;
import handlers.admincommandhandlers.AdminCursedWeapons;
import handlers.admincommandhandlers.AdminDanceSystem;
import handlers.admincommandhandlers.AdminDebug;
import handlers.admincommandhandlers.AdminDelete;
import handlers.admincommandhandlers.AdminDeport;
import handlers.admincommandhandlers.AdminDisconnect;
import handlers.admincommandhandlers.AdminDoorControl;
import handlers.admincommandhandlers.AdminEditChar;
import handlers.admincommandhandlers.AdminEditNpc;
import handlers.admincommandhandlers.AdminEffects;
import handlers.admincommandhandlers.AdminElement;
import handlers.admincommandhandlers.AdminEnchant;
import handlers.admincommandhandlers.AdminEventEngine;
import handlers.admincommandhandlers.AdminEvents;
import handlers.admincommandhandlers.AdminExpSp;
import handlers.admincommandhandlers.AdminFence;
import handlers.admincommandhandlers.AdminFightCalculator;
import handlers.admincommandhandlers.AdminFortSiege;
import handlers.admincommandhandlers.AdminGeodata;
import handlers.admincommandhandlers.AdminGeoEditor;
import handlers.admincommandhandlers.AdminGm;
import handlers.admincommandhandlers.AdminGmChat;
import handlers.admincommandhandlers.AdminGraciaSeeds;
import handlers.admincommandhandlers.AdminHeal;
import handlers.admincommandhandlers.AdminHellbound;
import handlers.admincommandhandlers.AdminHelpPage;
import handlers.admincommandhandlers.AdminInstance;
import handlers.admincommandhandlers.AdminInstanceZone;
import handlers.admincommandhandlers.AdminInvul;
import handlers.admincommandhandlers.AdminKick;
import handlers.admincommandhandlers.AdminKill;
import handlers.admincommandhandlers.AdminLevel;
import handlers.admincommandhandlers.AdminLogin;
import handlers.admincommandhandlers.AdminMammon;
import handlers.admincommandhandlers.AdminManor;
import handlers.admincommandhandlers.AdminMassHero;
import handlers.admincommandhandlers.AdminMenu;
import handlers.admincommandhandlers.AdminMessages;
import handlers.admincommandhandlers.AdminMobGroup;
import handlers.admincommandhandlers.AdminMonsterRace;
import handlers.admincommandhandlers.AdminMovieMaker;
import handlers.admincommandhandlers.AdminOlympiad;
import handlers.admincommandhandlers.AdminPathNode;
import handlers.admincommandhandlers.AdminPcCondOverride;
import handlers.admincommandhandlers.AdminPetition;
import handlers.admincommandhandlers.AdminPForge;
import handlers.admincommandhandlers.AdminPledge;
import handlers.admincommandhandlers.AdminPolymorph;
import handlers.admincommandhandlers.AdminPremium;
import handlers.admincommandhandlers.AdminQuest;
import handlers.admincommandhandlers.AdminRecallAll;
import handlers.admincommandhandlers.AdminRepairChar;
import handlers.admincommandhandlers.AdminRes;
import handlers.admincommandhandlers.AdminReuse;
import handlers.admincommandhandlers.AdminRide;
import handlers.admincommandhandlers.AdminSetTeam;
import handlers.admincommandhandlers.AdminShop;
import handlers.admincommandhandlers.AdminShowQuests;
import handlers.admincommandhandlers.AdminShutdown;
import handlers.admincommandhandlers.AdminSiege;
import handlers.admincommandhandlers.AdminSkill;
import handlers.admincommandhandlers.AdminSpawn;
import handlers.admincommandhandlers.AdminSummon;
import handlers.admincommandhandlers.AdminTarget;
import handlers.admincommandhandlers.AdminTargetSay;
import handlers.admincommandhandlers.AdminTeleport;
import handlers.admincommandhandlers.AdminTerritoryWar;
import handlers.admincommandhandlers.AdminTest;
import handlers.admincommandhandlers.AdminTownWar;
import handlers.admincommandhandlers.AdminTvTEvent;
import handlers.admincommandhandlers.AdminTvTRoundEvent;
import handlers.admincommandhandlers.AdminUnblockIp;
import handlers.admincommandhandlers.AdminVip;
import handlers.admincommandhandlers.AdminVitality;
import handlers.admincommandhandlers.AdminZone;
import handlers.admincommandhandlers.AdminMensagem;
import handlers.admincommandhandlers.AdminHappyHour;
import handlers.bypasshandlers.ArenaBuff;
import handlers.bypasshandlers.Augment;
import handlers.bypasshandlers.BloodAlliance;
import handlers.bypasshandlers.Buy;
import handlers.bypasshandlers.BuyShadowItem;
import handlers.bypasshandlers.ChatLink;
import handlers.bypasshandlers.ClanWarehouse;
import handlers.bypasshandlers.CustomLinks;
import handlers.bypasshandlers.EventEngine;
import handlers.bypasshandlers.Festival;
import handlers.bypasshandlers.FortSiege;
import handlers.bypasshandlers.Freight;
import handlers.bypasshandlers.Henna;
import handlers.bypasshandlers.ItemAuctionLink;
import handlers.bypasshandlers.Leaderboard;
import handlers.bypasshandlers.Link;
import handlers.bypasshandlers.Loto;
import handlers.bypasshandlers.ManorManager;
import handlers.bypasshandlers.Multisell;
import handlers.bypasshandlers.Observation;
import handlers.bypasshandlers.OlympiadManagerLink;
import handlers.bypasshandlers.OlympiadObservation;
import handlers.bypasshandlers.PlayerHelp;
import handlers.bypasshandlers.PrivateWarehouse;
import handlers.bypasshandlers.QuestLink;
import handlers.bypasshandlers.QuestList;
import handlers.bypasshandlers.ReceivePremium;
import handlers.bypasshandlers.ReleaseAttribute;
import handlers.bypasshandlers.RemoveDeathPenalty;
import handlers.bypasshandlers.RentPet;
import handlers.bypasshandlers.RideWyvern;
import handlers.bypasshandlers.Rift;
import handlers.bypasshandlers.SkillList;
import handlers.bypasshandlers.SupportBlessing;
import handlers.bypasshandlers.SupportMagic;
import handlers.bypasshandlers.TerritoryStatus;
import handlers.bypasshandlers.TerritoryWar;
import handlers.bypasshandlers.VoiceCommand;
import handlers.bypasshandlers.Wear;
import handlers.chathandlers.ChatAll;
import handlers.chathandlers.ChatAlliance;
import handlers.chathandlers.ChatBattlefield;
import handlers.chathandlers.ChatClan;
import handlers.chathandlers.ChatHeroVoice;
import handlers.chathandlers.ChatParty;
import handlers.chathandlers.ChatPartyMatchRoom;
import handlers.chathandlers.ChatPartyRoomAll;
import handlers.chathandlers.ChatPartyRoomCommander;
import handlers.chathandlers.ChatPetition;
import handlers.chathandlers.ChatShout;
import handlers.chathandlers.ChatTell;
import handlers.chathandlers.ChatTrade;
import handlers.itemhandlers.BeastSoulShot;
import handlers.itemhandlers.BeastSpice;
import handlers.itemhandlers.BeastSpiritShot;
import handlers.itemhandlers.BlessedSpiritShot;
import handlers.itemhandlers.Book;
import handlers.itemhandlers.Bypass;
import handlers.itemhandlers.Calculator;
import handlers.itemhandlers.ClanRepsItem;
import handlers.itemhandlers.Disguise;
import handlers.itemhandlers.Elixir;
import handlers.itemhandlers.EnchantAttribute;
import handlers.itemhandlers.EnchantScrolls;
import handlers.itemhandlers.EnergyStarStone;
import handlers.itemhandlers.EventItem;
import handlers.itemhandlers.ExtractableItems;
import handlers.itemhandlers.FishShots;
import handlers.itemhandlers.Harvester;
import handlers.itemhandlers.ItemSkills;
import handlers.itemhandlers.ItemSkillsTemplate;
import handlers.itemhandlers.ManaPotion;
import handlers.itemhandlers.Maps;
import handlers.itemhandlers.MercTicket;
import handlers.itemhandlers.NevitHourglass;
import handlers.itemhandlers.NicknameColor;
import handlers.itemhandlers.NobleCustomItem;
import handlers.itemhandlers.PaganKeys;
import handlers.itemhandlers.PetFood;
import handlers.itemhandlers.QuestItems;
import handlers.itemhandlers.Recipes;
import handlers.itemhandlers.RollingDice;
import handlers.itemhandlers.ScrollOfResurrection;
import handlers.itemhandlers.Seed;
import handlers.itemhandlers.SevenSignsRecord;
import handlers.itemhandlers.SoulShots;
import handlers.itemhandlers.SpecialXMas;
import handlers.itemhandlers.SpiritShot;
import handlers.itemhandlers.SummonItems;
import handlers.itemhandlers.SummonNpc;
import handlers.itemhandlers.TeleportBookmark;
import handlers.skillhandlers.BallistaBomb;
import handlers.skillhandlers.BeastSkills;
import handlers.skillhandlers.Blow;
import handlers.skillhandlers.Cancel;
import handlers.skillhandlers.ChainHeal;
import handlers.skillhandlers.Charge;
import handlers.skillhandlers.CombatPointHeal;
import handlers.skillhandlers.Continuous;
import handlers.skillhandlers.CpDam;
import handlers.skillhandlers.CpDamPercent;
import handlers.skillhandlers.Craft;
import handlers.skillhandlers.DeluxeKey;
import handlers.skillhandlers.Detection;
import handlers.skillhandlers.Disablers;
import handlers.skillhandlers.Dummy;
import handlers.skillhandlers.Fishing;
import handlers.skillhandlers.FishingSkill;
import handlers.skillhandlers.GetPlayer;
import handlers.skillhandlers.GiveReco;
import handlers.skillhandlers.GiveSp;
import handlers.skillhandlers.GiveVitality;
import handlers.skillhandlers.Heal;
import handlers.skillhandlers.HealPercent;
import handlers.skillhandlers.InstantJump;
import handlers.skillhandlers.ManaHeal;
import handlers.skillhandlers.Manadam;
import handlers.skillhandlers.Mdam;
import handlers.skillhandlers.NornilsPower;
import handlers.skillhandlers.Pdam;
import handlers.skillhandlers.RefuelAirShip;
import handlers.skillhandlers.Resurrect;
import handlers.skillhandlers.ShiftTarget;
import handlers.skillhandlers.Soul;
import handlers.skillhandlers.Sow;
import handlers.skillhandlers.Spoil;
import handlers.skillhandlers.StealBuffs;
import handlers.skillhandlers.StrSiegeAssault;
import handlers.skillhandlers.SummonFriend;
import handlers.skillhandlers.Sweep;
import handlers.skillhandlers.TakeCastle;
import handlers.skillhandlers.TakeFort;
import handlers.skillhandlers.TransformDispel;
import handlers.skillhandlers.Trap;
import handlers.skillhandlers.Unlock;
import handlers.targethandlers.TargetAlly;
import handlers.targethandlers.TargetArea;
import handlers.targethandlers.TargetAreaCorpseMob;
import handlers.targethandlers.TargetAreaSummon;
import handlers.targethandlers.TargetAura;
import handlers.targethandlers.TargetAuraCorpseMob;
import handlers.targethandlers.TargetBehindArea;
import handlers.targethandlers.TargetBehindAura;
import handlers.targethandlers.TargetClan;
import handlers.targethandlers.TargetClanMember;
import handlers.targethandlers.TargetCorpseAlly;
import handlers.targethandlers.TargetCorpseClan;
import handlers.targethandlers.TargetCorpseMob;
import handlers.targethandlers.TargetCorpsePet;
import handlers.targethandlers.TargetCorpsePlayer;
import handlers.targethandlers.TargetEnemySummon;
import handlers.targethandlers.TargetFlagPole;
import handlers.targethandlers.TargetFrontArea;
import handlers.targethandlers.TargetFrontAura;
import handlers.targethandlers.TargetGround;
import handlers.targethandlers.TargetHoly;
import handlers.targethandlers.TargetOne;
import handlers.targethandlers.TargetOwnerPet;
import handlers.targethandlers.TargetParty;
import handlers.targethandlers.TargetPartyClan;
import handlers.targethandlers.TargetPartyMember;
import handlers.targethandlers.TargetPartyNotMe;
import handlers.targethandlers.TargetPartyOther;
import handlers.targethandlers.TargetPet;
import handlers.targethandlers.TargetSelf;
import handlers.targethandlers.TargetSummon;
import handlers.targethandlers.TargetUnlockable;
import handlers.telnethandlers.ChatsHandler;
import handlers.telnethandlers.DebugHandler;
import handlers.telnethandlers.HelpHandler;
import handlers.telnethandlers.PlayerHandler;
import handlers.telnethandlers.ReloadHandler;
import handlers.telnethandlers.ServerHandler;
import handlers.telnethandlers.StatusHandler;
import handlers.telnethandlers.ThreadHandler;
import handlers.usercommandhandlers.ChannelDelete;
import handlers.usercommandhandlers.ChannelInfo;
import handlers.usercommandhandlers.ChannelLeave;
import handlers.usercommandhandlers.ClanPenalty;
import handlers.usercommandhandlers.ClanWarsList;
import handlers.usercommandhandlers.Dismount;
import handlers.usercommandhandlers.InstanceZone;
import handlers.usercommandhandlers.Loc;
import handlers.usercommandhandlers.Mount;
import handlers.usercommandhandlers.MyBirthday;
import handlers.usercommandhandlers.OlympiadStat;
import handlers.usercommandhandlers.PartyInfo;
import handlers.usercommandhandlers.Time;
import handlers.usercommandhandlers.Unstuck;
import handlers.voicedcommandhandlers.BadBuffShield;
import handlers.voicedcommandhandlers.Banking;
//import handlers.voicedcommandhandlers.CastleVCmd; - Precisa de revisao e testes
import handlers.voicedcommandhandlers.ChangePassword;
import handlers.voicedcommandhandlers.ChatAdmin;
import handlers.voicedcommandhandlers.Debug;
import handlers.voicedcommandhandlers.Hellbound;
import handlers.voicedcommandhandlers.Lang;
import handlers.voicedcommandhandlers.OpenAtod;
import handlers.voicedcommandhandlers.Online;
import handlers.voicedcommandhandlers.Premium;
import handlers.voicedcommandhandlers.RaidBoosStatus;
import handlers.voicedcommandhandlers.RefuseBuff;
import handlers.voicedcommandhandlers.Repair;
import handlers.voicedcommandhandlers.Siege;
import handlers.voicedcommandhandlers.Teleport;
import handlers.voicedcommandhandlers.StatsVCmd;
import handlers.voicedcommandhandlers.TWGm;
import handlers.voicedcommandhandlers.TvTRoundVoicedInfo;
import handlers.voicedcommandhandlers.TvTVoiced;
import handlers.voicedcommandhandlers.VoiceInfo;
import handlers.voicedcommandhandlers.Wedding;
import handlers.voicedcommandhandlers.UserActions;
import handlers.voicedcommandhandlers.User;
import handlers.voicedcommandhandlers.Lider;
import handlers.voicedcommandhandlers.Logout;
import handlers.voicedcommandhandlers.Cancelar;
import handlers.voicedcommandhandlers.ClanMensagem;

/**
 * PkElfo
 */

public class MasterHandler
{
	private static final Logger _log = Logger.getLogger(MasterHandler.class.getName());
	
	private static final Class<?>[] _loadInstances =
	{
		ActionHandler.class,
		ActionShiftHandler.class,
		AdminCommandHandler.class,
		BypassHandler.class,
		ChatHandler.class,
		ItemHandler.class,
		SkillHandler.class,
		UserCommandHandler.class,
		VoicedCommandHandler.class,
		TargetHandler.class,
		TelnetHandler.class,
	};
	
	private static final Class<?>[][] _handlers =
	{
		{
			// Action Shift Handlers
			L2ArtefactInstanceAction.class,
			L2DecoyAction.class,
			L2DoorInstanceAction.class,
			L2ItemInstanceAction.class,
			L2NpcAction.class,
			L2PcInstanceAction.class,
			L2PetInstanceAction.class,
			L2StaticObjectInstanceAction.class,
			L2SummonAction.class,
			L2TrapAction.class,
		},
		{
			// Action Shift Handlers
			L2DoorInstanceActionShift.class,
			L2ItemInstanceActionShift.class,
			L2NpcActionShift.class,
			L2PcInstanceActionShift.class,
			L2StaticObjectInstanceActionShift.class,
			L2SummonActionShift.class,
		},
		{
			// Admin Command Handlers
			AdminAdmin.class,
			AdminAio.class,
			AdminAnnouncements.class,
			AdminBan.class,
			AdminBBS.class,
			AdminBuffs.class,
			AdminCache.class,
			AdminCamera.class,
			AdminChangeAccessLevel.class,
			AdminCHSiege.class,
			AdminClan.class,
			AdminClanFull.class,
			AdminCreateItem.class,
			AdminCursedWeapons.class,
			AdminDanceSystem.class,
			AdminDebug.class,
			AdminDelete.class,
			AdminDisconnect.class,
			AdminDoorControl.class,
			AdminEditChar.class,
			AdminEditNpc.class,
			AdminEffects.class,
			AdminElement.class,
			AdminEnchant.class,
			AdminEventEngine.class,
			AdminEvents.class,
			AdminExpSp.class,
			AdminFightCalculator.class,
			AdminFortSiege.class,
			AdminGeodata.class,
			AdminGeoEditor.class,
			AdminGm.class,
			AdminGmChat.class,
			AdminGraciaSeeds.class,
			AdminHeal.class,
			AdminHellbound.class,
			AdminHelpPage.class,
			AdminInstance.class,
			AdminInstanceZone.class,
			AdminInvul.class,
			AdminKick.class,
			AdminKill.class,
			AdminLevel.class,
			AdminDeport.class,
			AdminLogin.class,
			AdminMammon.class,
			AdminManor.class,
			AdminMenu.class,
			AdminMessages.class,
			AdminMobGroup.class,
			AdminMonsterRace.class,
			AdminPathNode.class,
			AdminPcCondOverride.class,
			AdminPetition.class,
			AdminPForge.class,
			AdminPledge.class,
			AdminPolymorph.class,
			AdminPremium.class,
			AdminQuest.class,
			AdminRecallAll.class,
			AdminRepairChar.class,
			AdminRes.class,
			AdminRide.class,
			AdminShop.class,
			AdminShowQuests.class,
			AdminShutdown.class,
			AdminSiege.class,
			AdminSkill.class,
			AdminSpawn.class,
			AdminSummon.class,
			AdminTarget.class,
			AdminTargetSay.class,
			AdminTeleport.class,
			AdminTerritoryWar.class,
			AdminTest.class,
			AdminTvTEvent.class,
			AdminUnblockIp.class,
			AdminVitality.class,
			AdminZone.class,
			AdminMassHero.class,
			AdminClanSkills.class,
			AdminReuse.class,
			AdminCheckBot.class,
			AdminSetTeam.class,
			AdminTownWar.class,
			AdminTvTRoundEvent.class,
			AdminOlympiad.class,
			AdminFence.class,
			AdminMovieMaker.class,
			AdminCrazy.class,
			AdminVip.class,
			AdminMensagem.class,
			AdminHappyHour.class,
		},
		{
			// Bypass Handlers
			Augment.class,
			ArenaBuff.class,
			BloodAlliance.class,
			Buy.class,
			BuyShadowItem.class,
			ChatLink.class,
			ClanWarehouse.class,
			EventEngine.class,
			Festival.class,
			FortSiege.class,
			Freight.class,
			Henna.class,
			ItemAuctionLink.class,
			Link.class,
			Loto.class,
			ManorManager.class,
			Multisell.class,
			Observation.class,
			OlympiadObservation.class,
			OlympiadManagerLink.class,
			QuestLink.class,
			PlayerHelp.class,
			PrivateWarehouse.class,
			QuestList.class,
			ReceivePremium.class,
			ReleaseAttribute.class,
			RemoveDeathPenalty.class,
			RentPet.class,
			RideWyvern.class,
			Rift.class,
			SkillList.class,
			SupportBlessing.class,
			SupportMagic.class,
			TerritoryStatus.class,
			TerritoryWar.class,
			VoiceCommand.class,
			Wear.class,
			CustomLinks.class,
			Leaderboard.class,
		},
		{
			// Chat Handlers
			ChatAll.class,
			ChatAlliance.class,
			ChatBattlefield.class,
			ChatClan.class,
			ChatHeroVoice.class,
			ChatParty.class,
			ChatPartyMatchRoom.class,
			ChatPartyRoomAll.class,
			ChatPartyRoomCommander.class,
			ChatPetition.class,
			ChatShout.class,
			ChatTell.class,
			ChatTrade.class,
		},
		{
			// Item Handlers
			ScrollOfResurrection.class,
			SoulShots.class,
			SpiritShot.class,
			BlessedSpiritShot.class,
			BeastSoulShot.class,
			BeastSpiritShot.class,
			Bypass.class,
			Calculator.class,
			PaganKeys.class,
			Maps.class,
			NicknameColor.class,
			Recipes.class,
			RollingDice.class,
			EnchantAttribute.class,
			EnchantScrolls.class,
			ExtractableItems.class,
			Book.class,
			SevenSignsRecord.class,
			ItemSkills.class,
			ItemSkillsTemplate.class,
			Seed.class,
			Harvester.class,
			MercTicket.class,
			FishShots.class,
			PetFood.class,
			QuestItems.class,
			SpecialXMas.class,
			SummonItems.class,
			BeastSpice.class,
			TeleportBookmark.class,
			Elixir.class,
			Disguise.class,
			ManaPotion.class,
			EnergyStarStone.class,
			EventItem.class,
			SummonNpc.class,
			NevitHourglass.class,
			ClanRepsItem.class,
			NobleCustomItem.class,
		},
		{
			// Skill Handlers
			Blow.class,
			Pdam.class,
			Mdam.class,
			CpDam.class,
			CpDamPercent.class,
			Manadam.class,
			Heal.class,
			HealPercent.class,
			CombatPointHeal.class,
			ManaHeal.class,
			Charge.class,
			Continuous.class,
			Detection.class,
			Resurrect.class,
			ShiftTarget.class,
			Spoil.class,
			Sweep.class,
			StrSiegeAssault.class,
			SummonFriend.class,
			Disablers.class,
			Cancel.class,
			ChainHeal.class,
			StealBuffs.class,
			BallistaBomb.class,
			TakeCastle.class,
			TakeFort.class,
			Unlock.class,
			Craft.class,
			Fishing.class,
			FishingSkill.class,
			BeastSkills.class,
			DeluxeKey.class,
			Sow.class,
			Soul.class,
			GetPlayer.class,
			TransformDispel.class,
			Trap.class,
			GiveSp.class,
			GiveReco.class,
			GiveVitality.class,
			InstantJump.class,
			Dummy.class,
			RefuelAirShip.class,
			NornilsPower.class,
		},
		{
			// User Command Handlers
			ClanPenalty.class,
			ClanWarsList.class,
			Dismount.class,
			Unstuck.class,
			InstanceZone.class,
			Loc.class,
			Mount.class,
			PartyInfo.class,
			Time.class,
			OlympiadStat.class,
			ChannelLeave.class,
			ChannelDelete.class,
			ChannelInfo.class,
			MyBirthday.class,
		},
		{
			// Voiced Command Handlers
			StatsVCmd.class,
			// TODO: Add configuration options for this voiced commands:
			// CastleVCmd.class,
			// SetVCmd.class,
			OpenAtod.class,
			TWGm.class,
			Repair.class,
			BadBuffShield.class,
			Siege.class,
			Teleport.class,
			VoiceInfo.class,
			User.class,
			UserActions.class,
			Lider.class,
			Logout.class,
			ClanMensagem.class,
			(Config.CANCELAR_ONLINE ? Cancelar.class : null),
			(Config.CMD_ONLINE ? Online.class : null),
			(Config.BOSS_STATS ? RaidBoosStatus.class : null),
			(Config.ALLOW_REFUSE_BUFF ? RefuseBuff.class : null),
			(Config.PREMIUM_SERVICE_ENABLED ? Premium.class : null),
			(Config.L2JMOD_ALLOW_WEDDING ? Wedding.class : null),
			(Config.BANKING_SYSTEM_ENABLED ? Banking.class : null),
			(Config.TVT_ALLOW_VOICED_COMMAND || Config.TVT_ALLOW_REGISTER_VOICED_COMMAND ? TvTVoiced.class : null),
			(Config.TVT_ROUND_ALLOW_VOICED_COMMAND ? TvTRoundVoicedInfo.class : null),
			(Config.L2JMOD_CHAT_ADMIN ? ChatAdmin.class : null),
			(Config.L2JMOD_MULTILANG_ENABLE && Config.L2JMOD_MULTILANG_VOICED_ALLOW ? Lang.class : null),
			(Config.L2JMOD_DEBUG_VOICE_COMMAND ? Debug.class : null),
			(Config.L2JMOD_ALLOW_CHANGE_PASSWORD ? ChangePassword.class : null),
			(Config.L2JMOD_HELLBOUND_STATUS ? Hellbound.class : null),
		},
		{
			// Target Handlers
			TargetAlly.class,
			TargetArea.class,
			TargetAreaCorpseMob.class,
			TargetAreaSummon.class,
			TargetAura.class,
			TargetAuraCorpseMob.class,
			TargetBehindArea.class,
			TargetBehindAura.class,
			TargetClan.class,
			TargetClanMember.class,
			TargetCorpseAlly.class,
			TargetCorpseClan.class,
			TargetCorpseMob.class,
			TargetCorpsePet.class,
			TargetCorpsePlayer.class,
			TargetEnemySummon.class,
			TargetFlagPole.class,
			TargetFrontArea.class,
			TargetFrontAura.class,
			TargetGround.class,
			TargetHoly.class,
			TargetOne.class,
			TargetOwnerPet.class,
			TargetParty.class,
			TargetPartyClan.class,
			TargetPartyMember.class,
			TargetPartyNotMe.class,
			TargetPartyOther.class,
			TargetPet.class,
			TargetSelf.class,
			TargetSummon.class,
			TargetUnlockable.class,
		},
		{
			// Telnet Handlers
			ChatsHandler.class,
			DebugHandler.class,
			HelpHandler.class,
			PlayerHandler.class,
			ReloadHandler.class,
			ServerHandler.class,
			StatusHandler.class,
			ThreadHandler.class,
		},
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		_log.log(Level.INFO, "Iniciando carregamento dos Handlers...");
		
		Object loadInstance = null;
		Method method = null;
		Class<?>[] interfaces = null;
		Object handler = null;
		
		for (int i = 0; i < _loadInstances.length; i++)
		{
			try
			{
				method = _loadInstances[i].getMethod("getInstance");
				loadInstance = method.invoke(_loadInstances[i]);
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Falha no metodo getInstance invocando para manipulador: " + _loadInstances[i].getSimpleName(), e);
				continue;
			}
			
			method = null;
			
			for (Class<?> c : _handlers[i])
			{
				try
				{
					if (c == null)
					{
						continue; // Disabled handler
					}
					// Don't wtf some classes extending another like ItemHandler, Elixir, etc.. and we need to find where the hell is interface xD
					interfaces = c.getInterfaces().length > 0 ? // Standardly handler has implementation
					c.getInterfaces() : c.getSuperclass().getInterfaces().length > 0 ? // No? then it extends another handler like (ItemSkills->ItemSkillsTemplate)
					c.getSuperclass().getInterfaces() : c.getSuperclass().getSuperclass().getInterfaces(); // O noh that's Elixir->ItemSkills->ItemSkillsTemplate
					if (method == null)
					{
						method = loadInstance.getClass().getMethod("registerHandler", interfaces);
					}
					handler = c.newInstance();
					if (method.getParameterTypes()[0].isInstance(handler))
					{
						method.invoke(loadInstance, handler);
					}
				}
				catch (Exception e)
				{
					_log.log(Level.WARNING, "Manipulador de carga falhou" + ((c == null) ? "!" : ":" + c.getSimpleName()), e);
					continue;
				}
			}
			// And lets try get size
			try
			{
				method = loadInstance.getClass().getMethod("size");
				Object returnVal = method.invoke(loadInstance);
				_log.log(Level.INFO, loadInstance.getClass().getSimpleName() + ": " + returnVal + " Handlers");
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Falha no metodo tamanho invoca��o de manipulador: " + loadInstance.getClass().getSimpleName(), e);
				continue;
			}
		}
		
		_log.log(Level.INFO, "Todos os Handlers foram carregados...");
	}
}