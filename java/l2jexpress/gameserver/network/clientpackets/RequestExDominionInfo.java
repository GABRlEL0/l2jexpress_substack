package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.data.xml.holder.EventHolder;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.entity.events.EventType;
import l2jexpress.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2jexpress.gameserver.network.serverpackets.ExReplyDominionInfo;
import l2jexpress.gameserver.network.serverpackets.ExShowOwnthingPos;

public class RequestExDominionInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		activeChar.sendPacket(new ExReplyDominionInfo());

		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		if (runnerEvent.isInProgress())
			activeChar.sendPacket(new ExShowOwnthingPos());
	}
}