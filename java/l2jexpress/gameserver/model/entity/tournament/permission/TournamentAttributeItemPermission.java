package l2jexpress.gameserver.model.entity.tournament.permission;

import l2jexpress.gameserver.ConfigHolder;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.permission.actor.player.AttributeItemPermission;

public class TournamentAttributeItemPermission implements AttributeItemPermission
{
	@Override
	public boolean canAttributeItem(Player actor, ItemInstance item, ItemInstance stone)
	{
		return ConfigHolder.getBool("TournamentAllowMakingAttribute");
	}
	
	@Override
	public void sendPermissionDeniedError(Player actor, ItemInstance item, ItemInstance stone)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.AttributeItem", new Object[0]);
	}
}
