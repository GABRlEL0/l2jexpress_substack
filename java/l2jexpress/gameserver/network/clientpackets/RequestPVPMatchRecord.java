package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.base.TeamType;
import l2jexpress.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2jexpress.gameserver.network.serverpackets.ExPVPMatchRecord;

public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;

		final UndergroundColiseumBattleEvent battleEvent = player.getEvent(UndergroundColiseumBattleEvent.class);
		if (battleEvent == null)
			return;

		player.sendPacket(new ExPVPMatchRecord(ExPVPMatchRecord.UPDATE, TeamType.NONE, battleEvent));
	}
}