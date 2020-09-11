package l2jexpress.gameserver.permission.actor;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.permission.CharPermission;

public interface IgnoreAttackBlockadesPermission extends CharPermission
{
	boolean canIgnoreAttackBlockades(Creature p0, Creature p1, Skill p2, boolean p3);
}
