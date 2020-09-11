package ai;

import l2jexpress.gameserver.ai.Guard;
import l2jexpress.gameserver.model.instances.NpcInstance;

public class GuardRndWalkAndAnim extends Guard
{
	public GuardRndWalkAndAnim(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if (super.thinkActive())
			return true;

		if (randomAnimation())
			return true;

		if (randomWalk())
			return true;

		return false;
	}
}