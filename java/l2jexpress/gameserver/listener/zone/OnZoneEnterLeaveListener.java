package l2jexpress.gameserver.listener.zone;

import l2jexpress.commons.listener.Listener;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone>
{
	public void onZoneEnter(Zone zone, Creature actor);

	public void onZoneLeave(Zone zone, Creature actor);

	public void onEquipChanged(Zone zone, Creature actor);
}
