package l2jexpress.gameserver.stats.conditions;

import l2jexpress.gameserver.stats.Env;

public abstract class ConditionInventory extends Condition
{
	protected final int _slot;

	public ConditionInventory(int slot)
	{
		_slot = slot;
	}

	@Override
	protected abstract boolean testImpl(Env env);
}