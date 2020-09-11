package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.network.serverpackets.ExCursedWeaponList;

public class RequestCursedWeaponList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Creature activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		activeChar.sendPacket(new ExCursedWeaponList());
	}
}