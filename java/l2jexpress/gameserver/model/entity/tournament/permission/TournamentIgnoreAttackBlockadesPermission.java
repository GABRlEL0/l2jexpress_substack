package l2jexpress.gameserver.model.entity.tournament.permission;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.permission.actor.IgnoreAttackBlockadesPermission;

public class TournamentIgnoreAttackBlockadesPermission implements IgnoreAttackBlockadesPermission
{
	@Override
	public boolean canIgnoreAttackBlockades(Creature actor, Creature target, Skill skill, boolean force)
	{
		return true;
	}
}
