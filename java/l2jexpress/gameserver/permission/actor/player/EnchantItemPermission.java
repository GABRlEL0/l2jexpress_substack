package l2jexpress.gameserver.permission.actor.player;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.permission.PlayerPermission;

public interface EnchantItemPermission extends PlayerPermission
{
	boolean canEnchantItem(Player p0, ItemInstance p1, ItemInstance p2, ItemInstance p3);
	
	void sendPermissionDeniedError(Player p0, ItemInstance p1, ItemInstance p2, ItemInstance p3);
}
