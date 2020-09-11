package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;

public interface OnQuestionMarkListener extends PlayerListener
{
	public void onQuestionMarkClicked(Player player, int questionMarkId);
}
