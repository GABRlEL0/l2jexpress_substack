package l2jexpress.gameserver.permission.actor.player;

import l2jexpress.gameserver.model.Playable;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.permission.PlayablePermission;

public interface UseItemPermission extends PlayablePermission
{
	boolean canUseItem(Playable p0, ItemInstance p1, boolean p2);
	
	void sendPermissionDeniedError(Playable p0, ItemInstance p1, boolean p2);
}
