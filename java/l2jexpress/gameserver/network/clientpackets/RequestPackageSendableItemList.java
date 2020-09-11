package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.serverpackets.PackageSendableList;

/**
 * @author VISTALL
 * @date 20:35/16.05.2011
 */
public class RequestPackageSendableItemList extends L2GameClientPacket
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
		Player player = getClient().getActiveChar();
		if (player == null)
			return;

		player.sendPacket(new PackageSendableList(_objectId, player));
	}
}
