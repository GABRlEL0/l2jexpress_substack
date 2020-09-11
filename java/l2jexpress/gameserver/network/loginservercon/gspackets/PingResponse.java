package l2jexpress.gameserver.network.loginservercon.gspackets;

import l2jexpress.gameserver.network.loginservercon.SendablePacket;

public class PingResponse extends SendablePacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xff);
		writeQ(System.currentTimeMillis());
	}
}