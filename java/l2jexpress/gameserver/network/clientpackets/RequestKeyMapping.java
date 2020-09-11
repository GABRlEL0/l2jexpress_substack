package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.serverpackets.ExUISetting;

public class RequestKeyMapping extends L2GameClientPacket
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
		
		activeChar.sendPacket(new ExUISetting(activeChar));
	}
}