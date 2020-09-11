package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;

public interface OnAnswerListener extends PlayerListener
{
	void sayYes();

	void sayNo();
}
