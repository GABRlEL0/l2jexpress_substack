package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.serverpackets.ExShowSeedMapInfo;

public class RequestExSeedPhase extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		sendPacket(new ExShowSeedMapInfo());
	}
}