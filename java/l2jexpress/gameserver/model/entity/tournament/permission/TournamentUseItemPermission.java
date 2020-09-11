package l2jexpress.gameserver.model.entity.tournament.permission;

import l2jexpress.gameserver.ConfigHolder;
import l2jexpress.gameserver.model.Playable;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.entity.tournament.BattleInstance;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.permission.actor.player.UseItemPermission;

import org.apache.commons.lang3.ArrayUtils;

public class TournamentUseItemPermission implements UseItemPermission
{
	private final BattleInstance battle;
	
	public TournamentUseItemPermission(BattleInstance battle)
	{
		super();
		this.battle = battle;
	}
	
	@Override
	public boolean canUseItem(Playable actor, ItemInstance item, boolean ctrlPressed)
	{
		return canUseItem(battle, actor.getPlayer(), item);
	}
	
	@Override
	public void sendPermissionDeniedError(Playable actor, ItemInstance item, boolean ctrlPressed)
	{
		actor.getPlayer().sendCustomMessage("Tournament.NotAllowed.UseItem", new Object[0]);
	}
	
	public static boolean canUseItem(BattleInstance battle, Player player, ItemInstance item)
	{
		return ArrayUtils.contains(ConfigHolder.getIntArray("TournamentOtherItemsAllowedToUse"), item.getItemId()) || ConfigHolder.getMap("TournamentItemsToEveryPlayer", Integer.class, Long.class).containsKey(item.getItemId()) && (item.isStackable() || battle.containsReceivedItem(player, item)) || battle.containsReceivedItem(player, item);
	}
}
