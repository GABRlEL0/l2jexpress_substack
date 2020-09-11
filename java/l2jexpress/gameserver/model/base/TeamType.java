package l2jexpress.gameserver.model.base;

import java.util.Arrays;

import l2jexpress.gameserver.model.base.TeamType;

public enum TeamType
{
	NONE,
	BLUE,
	RED;
	
	public static TeamType[] VALUES = Arrays.copyOfRange(values(), 1, 3);

	public int ordinalWithoutNone()
	{
		return ordinal() - 1;
	}
	
	public TeamType revert()
	{
		return this == BLUE ? RED : this == RED ? BLUE : NONE;
	}
}
