package l2jexpress.gameserver.listener.actor.npc;

import l2jexpress.gameserver.listener.NpcListener;
import l2jexpress.gameserver.model.instances.NpcInstance;

public interface OnDecayListener extends NpcListener
{
	public void onDecay(NpcInstance actor);
}
