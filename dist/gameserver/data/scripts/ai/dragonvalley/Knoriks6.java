package ai.dragonvalley;

import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.utils.Location;

public class Knoriks6 extends Patrollers
{
	public Knoriks6(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
			{
				new Location(140536, 117784, -3912),
				new Location(140584, 117960, -3912)
			};
	}
}
