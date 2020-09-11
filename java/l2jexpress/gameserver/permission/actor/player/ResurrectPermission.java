package l2jexpress.gameserver.permission.actor.player;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.permission.PlayerPermission;

public interface ResurrectPermission extends PlayerPermission
{
	boolean canResurrect(Player p0, Creature p1, boolean p2, boolean p3);
	
	void sendPermissionDeniedError(Player p0, Creature p1, boolean p2, boolean p3);
}
