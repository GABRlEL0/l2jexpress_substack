package l2jexpress.gameserver.listener.game;

import l2jexpress.gameserver.listener.GameListener;

public interface OnDayNightChangeListener extends GameListener
{
	public void onDay();

	public void onNight();
}
