package l2jexpress.gameserver.model.entity.tournament.permission;

import l2jexpress.gameserver.ConfigHolder;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.permission.actor.player.EnchantItemPermission;

public class TournamentEnchantItemPermission implements EnchantItemPermission
{
	@Override
	public boolean canEnchantItem(Player actor, ItemInstance item, ItemInstance scroll, ItemInstance catalyst)
	{
		return ConfigHolder.getBool("TournamentAllowEnchanting");
	}
	
	@Override
	public void sendPermissionDeniedError(Player actor, ItemInstance item, ItemInstance scroll, ItemInstance catalyst)
	{
		actor.sendCustomMessage("Tournament.NotAllowed.AttributeItem", new Object[0]);
	}
}
