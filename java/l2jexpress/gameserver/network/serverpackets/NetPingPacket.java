package l2jexpress.gameserver.network.serverpackets;

import l2jexpress.gameserver.model.Player;

/**
 * @author l2jexpress_devs
 */
public class NetPingPacket extends L2GameServerPacket
{
	private final int _clientId;

	public NetPingPacket(Player cha)
	{
		_clientId = cha.getObjectId();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xD9);
		writeD(_clientId);
	}
}