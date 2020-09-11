package l2jexpress.gameserver.listener.game;

import l2jexpress.gameserver.listener.GameListener;
import l2jexpress.gameserver.Shutdown;

public interface OnShutdownListener extends GameListener
{
	void onShutdown(Shutdown.ShutdownMode p0);
}
