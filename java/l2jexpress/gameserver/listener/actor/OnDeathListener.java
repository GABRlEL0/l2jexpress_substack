package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.Creature;

public interface OnDeathListener extends CharListener
{
	public void onDeath(Creature actor, Creature killer);
}
