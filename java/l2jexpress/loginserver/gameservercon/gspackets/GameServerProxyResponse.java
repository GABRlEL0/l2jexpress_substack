package l2jexpress.loginserver.gameservercon.gspackets;

import l2jexpress.loginserver.gameservercon.ReceivablePacket;
import l2jexpress.loginserver.utils.ProxyWaitingList;

public class GameServerProxyResponse extends ReceivablePacket
{
	private String accountName;
	private String proxyIp;

	@Override
	protected void readImpl()
	{
		accountName = readS();
		proxyIp = readS();
	}

	@Override
	protected void runImpl()
	{
		ProxyWaitingList.getInstance().receiveProxy(getGameServer().getId(), accountName, proxyIp);
	}
}
