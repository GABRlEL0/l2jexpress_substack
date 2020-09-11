package l2jexpress.gameserver.stats.conditions;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.stats.Env;

public final class ConditionTargetHasForbiddenSkill extends Condition
{
	private final int _skillId;

	public ConditionTargetHasForbiddenSkill(int skillId)
	{
		_skillId = skillId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if (!target.isPlayable())
			return false;
		return !(target.getSkillLevel(_skillId) > 0);
	}
}
