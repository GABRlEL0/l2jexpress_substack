package ai.hellbound;

import l2jexpress.gameserver.ai.Fighter;
import l2jexpress.gameserver.instancemanager.naia.NaiaCoreManager;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.instances.NpcInstance;

public class Epidos extends Fighter
{

	public Epidos(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NaiaCoreManager.removeSporesAndSpawnCube();
		super.onEvtDead(killer);
	}
}