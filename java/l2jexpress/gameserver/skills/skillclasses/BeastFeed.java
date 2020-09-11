package l2jexpress.gameserver.skills.skillclasses;

import java.util.List;

import l2jexpress.commons.threading.RunnableImpl;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.model.instances.FeedableBeastInstance;
import l2jexpress.gameserver.templates.StatsSet;

public class BeastFeed extends Skill
{
	public BeastFeed(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			ThreadPoolManager.getInstance().execute(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					if (target instanceof FeedableBeastInstance)
						((FeedableBeastInstance) target).onSkillUse((Player) activeChar, _id);
				}
			});
		}
	}
}
