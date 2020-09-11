package npc.model;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.instances.MerchantInstance;
import l2jexpress.gameserver.network.serverpackets.PackageToList;
import l2jexpress.gameserver.templates.npc.NpcTemplate;
import l2jexpress.gameserver.utils.WarehouseFunctions;

/**
 * @author VISTALL
 * @date 20:32/16.05.2011
 */
public class FreightSenderInstance extends MerchantInstance
{
	public FreightSenderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
			return;

		if (command.equalsIgnoreCase("deposit_items"))
			player.sendPacket(new PackageToList(player));
		else if (command.equalsIgnoreCase("withdraw_items"))
			WarehouseFunctions.showFreightWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
}
