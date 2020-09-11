package l2jexpress.gameserver.listener.actor.door;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date 21:03/04.07.2011
 */
public interface OnOpenCloseListener extends CharListener
{
	void onOpen(DoorInstance doorInstance);

	void onClose(DoorInstance doorInstance);
}
