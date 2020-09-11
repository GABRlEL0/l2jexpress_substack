package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.tables.GmListTable;

public class RequestGmList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar != null)
			GmListTable.sendListToPlayer(activeChar);
	}
}