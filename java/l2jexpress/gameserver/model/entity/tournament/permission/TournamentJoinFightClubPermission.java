package l2jexpress.gameserver.model.entity.tournament.permission;

import l2jexpress.gameserver.data.StringHolder;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.permission.actor.player.JoinFightClubPermission;

public class TournamentJoinFightClubPermission implements JoinFightClubPermission
{
	@Override
	public boolean joinSignFightClub(Player actor)
	{
		return false;
	}

	@Override
	public String getPermissionDeniedError(Player actor)
	{
		return StringHolder.getNotNull(actor, "Tournament.NotAllowed.JoinFightClub", new Object[0]);
	}
}
