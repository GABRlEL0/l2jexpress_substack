package l2jexpress.gameserver.network.loginservercon.lspackets;

import l2jexpress.gameserver.cache.Msg;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.GameClient;
import l2jexpress.gameserver.network.loginservercon.AuthServerCommunication;
import l2jexpress.gameserver.network.loginservercon.ReceivablePacket;
import l2jexpress.gameserver.network.serverpackets.ServerClose;

public class KickPlayer extends ReceivablePacket
{
	String account;

	@Override
	public void readImpl()
	{
		account = readS();
	}

	@Override
	protected void runImpl()
	{
		GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
		if (client == null)		
			client = AuthServerCommunication.getInstance().removeAuthedClient(account);
		if (client == null)
			return;
		final Player activeChar = client.getActiveChar();
		if (activeChar != null)
		{
			activeChar.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
			activeChar.kick();
		}
		else
		{
			client.close(ServerClose.STATIC);
		}
	}
}