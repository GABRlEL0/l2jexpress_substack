package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;

public class RequestTeleportBookMark extends L2GameClientPacket
{
	private int slot;

	@Override
	protected void readImpl()
	{
		slot = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar != null)
			activeChar.bookmarks.tryTeleport(slot);
	}
}