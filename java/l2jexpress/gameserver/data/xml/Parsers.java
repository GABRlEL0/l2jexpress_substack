package l2jexpress.gameserver.data.xml;


import l2jexpress.gameserver.data.StringHolder;
import l2jexpress.gameserver.data.htm.HtmCache;
import l2jexpress.gameserver.data.xml.holder.BuyListHolder;
import l2jexpress.gameserver.data.xml.holder.MultiSellHolder;
import l2jexpress.gameserver.data.xml.holder.ProductHolder;
import l2jexpress.gameserver.data.xml.holder.RecipeHolder;
import l2jexpress.gameserver.data.xml.parser.AirshipDockParser;
import l2jexpress.gameserver.data.xml.parser.ArmorSetsParser;
import l2jexpress.gameserver.data.xml.parser.AugmentationDataParser;
import l2jexpress.gameserver.data.xml.parser.CharTemplateParser;
import l2jexpress.gameserver.data.xml.parser.CubicParser;
import l2jexpress.gameserver.data.xml.parser.DomainParser;
import l2jexpress.gameserver.data.xml.parser.DonationParse;
import l2jexpress.gameserver.data.xml.parser.DoorParser;
import l2jexpress.gameserver.data.xml.parser.DressArmorParser;
import l2jexpress.gameserver.data.xml.parser.DressCloakParser;
import l2jexpress.gameserver.data.xml.parser.DressShieldParser;
import l2jexpress.gameserver.data.xml.parser.DressWeaponParser;
import l2jexpress.gameserver.data.xml.parser.EnchantItemParser;
import l2jexpress.gameserver.data.xml.parser.EventParser;
import l2jexpress.gameserver.data.xml.parser.ExchangeItemParser;
import l2jexpress.gameserver.data.xml.parser.FacebookCommentsParser;
import l2jexpress.gameserver.data.xml.parser.FakePlayerNpcsParser;
import l2jexpress.gameserver.data.xml.parser.FightClubMapParser;
import l2jexpress.gameserver.data.xml.parser.FoundationParser;
import l2jexpress.gameserver.data.xml.parser.HennaParser;
import l2jexpress.gameserver.data.xml.parser.InstantZoneParser;
import l2jexpress.gameserver.data.xml.parser.ItemParser;
import l2jexpress.gameserver.data.xml.parser.NpcParser;
import l2jexpress.gameserver.data.xml.parser.OptionDataParser;
import l2jexpress.gameserver.data.xml.parser.PetitionGroupParser;
import l2jexpress.gameserver.data.xml.parser.PremiumParser;
import l2jexpress.gameserver.data.xml.parser.ResidenceParser;
import l2jexpress.gameserver.data.xml.parser.RestartPointParser;
import l2jexpress.gameserver.data.xml.parser.SkillAcquireParser;
import l2jexpress.gameserver.data.xml.parser.SoulCrystalParser;
import l2jexpress.gameserver.data.xml.parser.SpawnParser;
import l2jexpress.gameserver.data.xml.parser.StaticObjectParser;
import l2jexpress.gameserver.data.xml.parser.TournamentMapParser;
import l2jexpress.gameserver.data.xml.parser.ZoneParser;
import l2jexpress.gameserver.instancemanager.ReflectionManager;
import l2jexpress.gameserver.tables.SkillTable;
import l2jexpress.gameserver.tables.SpawnTable;

public abstract class Parsers
{
	public static void parseAll()
	{
//		if ((!Config.EXTERNAL_HOSTNAME.equalsIgnoreCase("127.0.0.1")) && (!Config.EXTERNAL_HOSTNAME.equalsIgnoreCase("178.33.90.147")))
//		{
//			return;
//		}
		HtmCache.getInstance().reload();
		StringHolder.getInstance().load();
		//
		SkillTable.getInstance().load(); // - SkillParser.getInstance();
		OptionDataParser.getInstance().load();
		ItemParser.getInstance().load();
		//
		FakePlayerNpcsParser.getInstance().load();
		NpcParser.getInstance().load();

		DomainParser.getInstance().load();
		RestartPointParser.getInstance().load();
		ExchangeItemParser.getInstance().load();
		StaticObjectParser.getInstance().load();
		DoorParser.getInstance().load();
		ZoneParser.getInstance().load();
		SpawnTable.getInstance();
		SpawnParser.getInstance().load();
		InstantZoneParser.getInstance().load();

		ReflectionManager.getInstance();

		//
		AirshipDockParser.getInstance().load();
		SkillAcquireParser.getInstance().load();
		//
		CharTemplateParser.getInstance().load();
		//
		ResidenceParser.getInstance().load();
		EventParser.getInstance().load();
		FightClubMapParser.getInstance().load();
		// support(cubic & agathion)
		CubicParser.getInstance().load();
		//
		BuyListHolder.getInstance();
		RecipeHolder.getInstance();
		MultiSellHolder.getInstance();
		ProductHolder.getInstance();
		// AgathionParser.getInstance();
		// item support
		HennaParser.getInstance().load();
		EnchantItemParser.getInstance().load();
		SoulCrystalParser.getInstance().load();
		ArmorSetsParser.getInstance().load();

		// etc
		PetitionGroupParser.getInstance().load();
		DressArmorParser.getInstance().load();
		DressCloakParser.getInstance().load();
		DressShieldParser.getInstance().load();
		DressWeaponParser.getInstance().load();
		AugmentationDataParser.getInstance().load();

		// Premium
		PremiumParser.getInstance().load();

		// Community Board Adds
		FoundationParser.getInstance().load();
		DonationParse.getInstance().load();

		// New
		TournamentMapParser.getInstance().load();
		FacebookCommentsParser.getInstance().load();
	}
}
