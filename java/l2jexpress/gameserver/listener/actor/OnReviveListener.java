package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.Creature;

/**
 * @author VISTALL
 */
public interface OnReviveListener extends CharListener
{
	public void onRevive(Creature actor);
}
