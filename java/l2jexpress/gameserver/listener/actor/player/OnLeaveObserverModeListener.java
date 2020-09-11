package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;

public interface OnLeaveObserverModeListener extends PlayerListener
{
	void onLeaveObserverMode(Player p0);
}
