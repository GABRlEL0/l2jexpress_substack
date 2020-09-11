package l2jexpress.loginserver.gameservercon.lspackets;

import l2jexpress.loginserver.gameservercon.SendablePacket;

public class AskForIds extends SendablePacket
{
	private int _count;

	public AskForIds(int count)
	{
		_count = count;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x07);
		writeD(_count);
	}
}
