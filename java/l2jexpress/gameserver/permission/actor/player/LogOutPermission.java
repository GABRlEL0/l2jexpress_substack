package l2jexpress.gameserver.permission.actor.player;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.permission.PlayerPermission;

public interface LogOutPermission extends PlayerPermission
{
	boolean canLogOut(Player p0, boolean p1);
	
	void sendPermissionDeniedError(Player p0, boolean p1);
}
