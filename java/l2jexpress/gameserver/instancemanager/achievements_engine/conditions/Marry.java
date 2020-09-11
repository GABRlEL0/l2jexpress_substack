package l2jexpress.gameserver.instancemanager.achievements_engine.conditions;

import l2jexpress.gameserver.instancemanager.achievements_engine.base.Condition;
import l2jexpress.gameserver.model.Player;

public class Marry extends Condition
{
	public Marry(Object value)
	{
		super(value);
		setName("Married");
	}
	
	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		
		if (player.isMaried())
		{
			return true;
		}
		
		return false;
	}
}