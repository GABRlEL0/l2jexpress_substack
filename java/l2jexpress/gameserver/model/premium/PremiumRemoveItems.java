package l2jexpress.gameserver.model.premium;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.serverpackets.components.SystemMsg;
import l2jexpress.gameserver.network.serverpackets.SystemMessage;
import l2jexpress.gameserver.utils.ItemFunctions;
import java.util.ArrayList;
import java.util.List;

public class PremiumRemoveItems
{
	private static PremiumRemoveItems _instance = new PremiumRemoveItems();
	
	public static PremiumRemoveItems getInstance()
	{
		return _instance;
	}
	
	private List<PremiumGift> _list = new ArrayList<PremiumGift>();
	
	protected void remove(Player player)
	{
		boolean removed = false;
		for (PremiumGift gift : _list)
		{
			ItemFunctions.removeItem(player, gift.getId(), gift.getCount(), true, "removed");
		}
		
		if (removed)
		{
			player.sendPacket(new SystemMessage(SystemMsg.THE_PREMIUM_ACCOUNT_HAS_BEEN_TERMINATED));
		}
	}
	
	public void add(PremiumGift gift)
	{
		_list.add(gift);
	}
}
