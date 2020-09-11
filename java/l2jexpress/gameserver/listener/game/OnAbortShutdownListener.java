package l2jexpress.gameserver.listener.game;

import l2jexpress.gameserver.listener.GameListener;
import l2jexpress.gameserver.Shutdown;

public interface OnAbortShutdownListener extends GameListener
{
	void onAbortShutdown(Shutdown.ShutdownMode p0, int p1);
}
