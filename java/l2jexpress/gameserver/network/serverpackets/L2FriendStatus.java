package l2jexpress.gameserver.network.serverpackets;

import l2jexpress.gameserver.model.Player;

public class L2FriendStatus extends L2GameServerPacket
{
	private String _charName;
	private boolean _login;

	public L2FriendStatus(Player player, boolean login)
	{
		_login = login;
		_charName = player.getName();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x77);
		writeD(_login ? 1 : 0); //Logged in 1 logged off 0
		writeS(_charName);
		writeD(0); //id Ð¿ÐµÑ€Ñ�Ð¾Ð½Ð°Ð¶Ð° Ñ� Ð±Ð°Ð·Ñ‹ Ð¾Ñ„Ñ„Ð°, Ð½Ðµ object_id
	}
}