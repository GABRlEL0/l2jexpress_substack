package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Zone;

public interface OnCharEnterLeaveZoneListener
{
	void onEnter(Creature p0, Zone p1);

	void onLeave(Creature p0, Zone p1);
}
