package npc.model.residences.fortress.peace;

import java.util.List;

import l2jexpress.gameserver.dao.SiegeClanDAO;
import l2jexpress.gameserver.data.xml.holder.EventHolder;
import l2jexpress.gameserver.data.xml.holder.ResidenceHolder;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.entity.events.EventType;
import l2jexpress.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2jexpress.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2jexpress.gameserver.model.entity.events.impl.SiegeEvent;
import l2jexpress.gameserver.model.entity.events.objects.SiegeClanObject;
import l2jexpress.gameserver.model.entity.residence.Castle;
import l2jexpress.gameserver.model.entity.residence.Fortress;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.model.pledge.Clan;
import l2jexpress.gameserver.model.pledge.Privilege;
import l2jexpress.gameserver.network.serverpackets.NpcHtmlMessage;
import l2jexpress.gameserver.network.serverpackets.SystemMessage2;
import l2jexpress.gameserver.network.serverpackets.components.SystemMsg;
import l2jexpress.gameserver.templates.item.ItemTemplate;
import l2jexpress.gameserver.templates.npc.NpcTemplate;

public class SuspiciousMerchantInstance extends NpcInstance
{
	public SuspiciousMerchantInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
			return;
		final Fortress fortress = getFortress();
		final FortressSiegeEvent siegeEvent = (FortressSiegeEvent) fortress.getSiegeEvent();
		if (command.equalsIgnoreCase("register"))
			siegeEvent.tryToRegisterClan(player, true, true, this, true);
		else if (command.equalsIgnoreCase("cancel"))
			siegeEvent.tryToCancelRegisterClan(player, true, true, this, true);
		else if (command.equalsIgnoreCase("state"))
		{
			int attackersSize = siegeEvent.getObjects(SiegeEvent.ATTACKERS).size();
			if (attackersSize == 0)
				showChatWindow(player, "residence2/fortress/fortress_ordery019.htm");
			else
				showChatWindow(player, "residence2/fortress/fortress_ordery020.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		Fortress fortress = getFortress();
		if (fortress.getOwner() != null)
		{
			html.setFile("residence2/fortress/fortress_ordery001a.htm");
			html.replace("%clan_name%", fortress.getOwner().getName());
		}
		else
			html.setFile("residence2/fortress/fortress_ordery001.htm");

		player.sendPacket(html);
	}
}