package l2jexpress.gameserver.stats;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.model.items.ItemInstance;

public final class Env
{
	public Creature character;
	public Creature target;
	public ItemInstance item;
	public Skill skill;
	public double value;

	public Env()
	{}

	public Env(Creature cha, Creature tar, Skill sk)
	{
		character = cha;
		target = tar;
		skill = sk;
	}
}
