package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;

public interface OnTeleportedListener extends PlayerListener
{
	void onTeleported(Player p0);
}
