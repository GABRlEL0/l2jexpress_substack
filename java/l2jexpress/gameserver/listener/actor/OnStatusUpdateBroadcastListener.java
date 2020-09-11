package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.Creature;

public interface OnStatusUpdateBroadcastListener extends CharListener
{
	void onStatusUpdate(Creature p0);
}
