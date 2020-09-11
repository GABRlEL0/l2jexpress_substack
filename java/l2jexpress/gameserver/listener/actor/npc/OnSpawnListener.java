package l2jexpress.gameserver.listener.actor.npc;

import l2jexpress.gameserver.listener.NpcListener;
import l2jexpress.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	public void onSpawn(NpcInstance actor);
}
