package l2jexpress.gameserver.listener.inventory;

import l2jexpress.commons.listener.Listener;
import l2jexpress.gameserver.model.Playable;
import l2jexpress.gameserver.model.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable>
{
	public void onEquip(int slot, ItemInstance item, Playable actor);

	public void onUnequip(int slot, ItemInstance item, Playable actor);
}
