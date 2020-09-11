package l2jexpress.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInfo;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.network.clientpackets.RequestExPostItemList;

/**
 * Ответ на запрос создания нового письма.
 * Отсылается при получении {@link RequestExPostItemList}
 * Содержит список вещей, которые можно приложить к письму.
 */
public class ExReplyPostItemList extends L2GameServerPacket
{
	private List<ItemInfo> _itemsList = new ArrayList<ItemInfo>();

	public ExReplyPostItemList(Player activeChar)
	{
		ItemInstance[] items = activeChar.getInventory().getItems();
		for (ItemInstance item : items)
			if (item.canBeTraded(activeChar))
				_itemsList.add(new ItemInfo(item));
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xB2);
		writeD(_itemsList.size());
		for (ItemInfo item : _itemsList)
			writeItemInfo(item);
	}
}