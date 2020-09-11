package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;

public interface OnPlayerLevelIncreasedListener extends PlayerListener
{
	void onLevelIncreased(Player p0);
}
