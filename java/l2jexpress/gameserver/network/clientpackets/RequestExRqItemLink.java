package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.cache.ItemInfoCache;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.ItemInfo;
import l2jexpress.gameserver.network.serverpackets.ActionFail;
import l2jexpress.gameserver.network.serverpackets.ExRpItemLink;

public class RequestExRqItemLink extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		ItemInfo item;
		if ((item = ItemInfoCache.getInstance().get(_objectId)) == null)
		{
			// Nik: Support for question mark listeners. Used for party find and other shits. objectId is used as the questionMarkId. Use with caution.
			getClient().getActiveChar().getListeners().onQuestionMarkClicked(_objectId);
			
			if (_objectId >= 5000000 && _objectId < 6000000)
			{
				Player player = getClient().getActiveChar();
				String varName = "DisabledAnnounce" + _objectId;
				if (!player.containsQuickVar(varName))
				{
					player.addQuickVar(varName, "true");
					player.sendMessage("Announcement Disabled!");
				}
			}
			sendPacket(ActionFail.STATIC);
		}
		else
			sendPacket(new ExRpItemLink(item));
	}
}