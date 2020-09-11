package l2jexpress.gameserver.network.loginservercon.gspackets;

import l2jexpress.gameserver.network.loginservercon.SendablePacket;

public class PlayerLogout extends SendablePacket
{
	private final String account;

	public PlayerLogout(String account)
	{
		this.account = account;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x04);
		writeS(account);
	}
}
