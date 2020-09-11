package l2jexpress.gameserver.listener.event;

import l2jexpress.gameserver.listener.EventListener;
import l2jexpress.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener extends EventListener
{
	void onStart(GlobalEvent event);

	void onStop(GlobalEvent event);
}
