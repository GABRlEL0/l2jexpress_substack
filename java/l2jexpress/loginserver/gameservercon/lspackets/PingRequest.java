package l2jexpress.loginserver.gameservercon.lspackets;

import l2jexpress.loginserver.gameservercon.SendablePacket;

public class PingRequest extends SendablePacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xff);
	}
}