package l2jexpress.loginserver.gameservercon.gspackets;

import l2jexpress.loginserver.gameservercon.GameServer;
import l2jexpress.loginserver.gameservercon.ReceivablePacket;

public class PlayerLogout extends ReceivablePacket
{
	private String account;

	@Override
	protected void readImpl()
	{
		account = readS();
	}

	@Override
	protected void runImpl()
	{
		final GameServer gs = getGameServer();
		if (gs.isAuthed())
			gs.removeAccount(account);
	}
}
