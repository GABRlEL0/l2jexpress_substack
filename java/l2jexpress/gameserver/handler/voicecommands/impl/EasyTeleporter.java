package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Zone.ZoneType;
import l2jexpress.gameserver.scripts.Functions;

/**
 * @author L2JExpress 
 */
public class EasyTeleporter implements IVoicedCommandHandler
{
	private static final String[] _commandList = {
			
			"giran",
			"goddard",
			"dion",
			"gludio",
			"schuttgart",
			"hunters",
			"aden",
			"rune",
			"oren",
			"gludin",
			"heine",
			"coliseum"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		command = command.intern();
		if (command.equalsIgnoreCase("giran"))
			return giran(command, activeChar, args);
		if (command.equalsIgnoreCase("goddard"))
			return goddard(command, activeChar, args);
		if (command.equalsIgnoreCase("dion"))
			return dion(command, activeChar, args);
		if (command.equalsIgnoreCase("gludio"))
			return gludio(command, activeChar, args);
		if (command.equalsIgnoreCase("schuttgart"))
			return schuttgart(command, activeChar, args);
		if (command.equalsIgnoreCase("hunters"))
			return hunters(command, activeChar, args);
		if (command.equalsIgnoreCase("aden"))
			return aden(command, activeChar, args);
		if (command.equalsIgnoreCase("rune"))
			return rune(command, activeChar, args);
		if (command.equalsIgnoreCase("oren"))
			return oren(command, activeChar, args);
		if (command.equalsIgnoreCase("gludin"))
			return gludin(command, activeChar, args);
		if (command.equalsIgnoreCase("heine"))
			return heine(command, activeChar, args);
		if (command.equalsIgnoreCase("coliseum"))
			return coliseum(command, activeChar, args);

		return false;
	}

	private boolean giran(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;//ADENA ID
					if (command.equalsIgnoreCase("giran"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(82696, 148648, -3494);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".giran");
						activeChar.sendMessage("Wellcome to Giran Town");
					}
			}
		return true;
		}
	
	private boolean goddard(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("goddard"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic) )
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(147736, -56360, -2806);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".goddard");
						activeChar.sendMessage("Wellcome to Goddard Town");
					}
			}
				return true;
		}


	private boolean dion(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("dion"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic) )
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(18748, 145437, -3132);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".dion");
						activeChar.sendMessage("Wellcome to Dion Town");
					}
			}
		return true;
	}
	
	private boolean gludio(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("gludio"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic) )
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(-14225, 123540, -3121);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".gludio");
						activeChar.sendMessage("Wellcome to Gludio Town");
					}
			}
		return true;
	}
	
	
	private boolean schuttgart(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("schuttgart"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(87368, -142424, -1344);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".schuttgart");
						activeChar.sendMessage("Wellcome to Schuttgart Town");
					}
			}
		return true;
	}
		
		
	private boolean hunters(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("hunters"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(116589, 76268, -2734);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".hunters");
						activeChar.sendMessage("Wellcome to Hunters Town");
					}
			}
		return true;
	}
	
	private boolean aden(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("aden"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(147450, 27064, -2208);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".aden");
						activeChar.sendMessage("Wellcome to Aden Town");
					}
			}
		return true;
	}
	
	private boolean rune(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("rune"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(44070, -50243, -796);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".rune");
						activeChar.sendMessage("Wellcome to Rune Town");
					}
			}
		return true;
	}
		
	private boolean oren(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("oren"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(82760, 53576, -1521);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".oren");
						activeChar.sendMessage("Wellcome to Oren Town");
					}
			}
		return true;
	}
	
	private boolean gludin(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
					if (command.equalsIgnoreCase("gludin"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(-83016, 150856, -3154);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".gludin");
						activeChar.sendMessage("Wellcome to Gludin Town");
					}
			}
		return true;
	}
	
	
	private boolean heine(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;

					if (command.equalsIgnoreCase("heine"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(111115, 219017, -3547);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".heine");
						activeChar.sendMessage("Wellcome to Heine Town");
					}
			}
		return true;
	}
	
	private boolean coliseum(String command, Player activeChar, String args)
	{
		if (Config.EASY_TELEPORTER)
		{
			final int TeleportCoin = Config.EASY_TELEPORTER_ITEM;
			
					if (command.equalsIgnoreCase("coliseum"))
					{
						if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic))
						{
							activeChar.sendMessage("Teleportation is not possible");
							return false;
						}
						if (TeleportCoin != 0 && activeChar.getInventory().getItemByItemId(Config.EASY_TELEPORTER_ITEM).getCount() < Config.EASY_TELEPORTER_PRICE)
						{
							activeChar.sendMessage("You do not have enough money");
							activeChar.sendActionFailed();
							return false;
						}
						activeChar.teleToLocation(147896, 46712, -3433);
						Functions.removeItem(activeChar, Config.EASY_TELEPORTER_ITEM, TeleportCoin, ".coliseum");
						activeChar.sendMessage("Wellcome to Aden Coliseum");
					}
			}
		return true;
	}


	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

}