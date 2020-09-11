package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;

public interface OnFishDieListener extends PlayerListener
{
	void onFishDied(Player player, int fishId, boolean isMonster);
}
