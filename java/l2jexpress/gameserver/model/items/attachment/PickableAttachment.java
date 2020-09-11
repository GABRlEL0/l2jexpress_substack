package l2jexpress.gameserver.model.items.attachment;

import l2jexpress.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment
{
	boolean canPickUp(Player player);

	void pickUp(Player player);
}
