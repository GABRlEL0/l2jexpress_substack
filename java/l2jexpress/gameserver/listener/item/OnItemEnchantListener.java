package l2jexpress.gameserver.listener.item;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInstance;

public interface OnItemEnchantListener extends PlayerListener
{
	public void onEnchantFinish(Player player, ItemInstance item, boolean succeed);
}
