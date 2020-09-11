package ai.seedofinfinity;

import l2jexpress.gameserver.ai.Fighter;
import l2jexpress.gameserver.model.instances.NpcInstance;

public class FeralHound extends Fighter
{
	public FeralHound(NpcInstance actor)
	{
		super(actor);
		actor.setIsInvul(true);
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}