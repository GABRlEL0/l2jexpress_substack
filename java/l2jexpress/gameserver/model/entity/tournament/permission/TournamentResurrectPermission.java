package l2jexpress.gameserver.model.entity.tournament.permission;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.permission.actor.player.ResurrectPermission;

public class TournamentResurrectPermission implements ResurrectPermission
{
	@Override
	public boolean canResurrect(Player actor, Creature target, boolean force, boolean isSalvation)
	{
		return false;
	}
	
	@Override
	public void sendPermissionDeniedError(Player actor, Creature target, boolean force, boolean isSalvation)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.Resurrect", new Object[0]);
	}
}
