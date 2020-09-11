package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.model.items.PcInventory;
import l2jexpress.gameserver.network.serverpackets.ExPutEnchantSupportItemResult;
import l2jexpress.gameserver.utils.ItemFunctions;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket
{
	private int _itemId;
	private int _catalystId;

	@Override
	protected void readImpl()
	{
		_catalystId = readD();
		_itemId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		PcInventory inventory = activeChar.getInventory();
		ItemInstance itemToEnchant = inventory.getItemByObjectId(_itemId);
		ItemInstance catalyst = inventory.getItemByObjectId(_catalystId);

		if (ItemFunctions.checkCatalyst(itemToEnchant, catalyst))
			activeChar.sendPacket(new ExPutEnchantSupportItemResult(1));
		else
			activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
	}
}