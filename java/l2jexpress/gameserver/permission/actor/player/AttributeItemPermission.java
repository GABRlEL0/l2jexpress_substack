package l2jexpress.gameserver.permission.actor.player;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.permission.PlayablePermission;

public interface AttributeItemPermission extends PlayablePermission
{
	boolean canAttributeItem(Player p0, ItemInstance p1, ItemInstance p2);
	
	void sendPermissionDeniedError(Player p0, ItemInstance p1, ItemInstance p2);
}
