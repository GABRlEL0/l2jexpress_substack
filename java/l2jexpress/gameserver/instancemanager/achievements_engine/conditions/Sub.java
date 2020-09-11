package l2jexpress.gameserver.instancemanager.achievements_engine.conditions;

import l2jexpress.gameserver.instancemanager.achievements_engine.base.Condition;
import l2jexpress.gameserver.model.Player;

public class Sub extends Condition
{
	public Sub(Object value)
	{
		super(value);
		setName("Subclass Count");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		
		int val = Integer.parseInt(getValue().toString());
		
		if (player.getSubClasses().size() >= val)
		{
			return true;
		}
		
		return false;
	}
}