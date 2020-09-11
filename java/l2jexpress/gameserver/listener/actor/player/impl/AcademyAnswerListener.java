package l2jexpress.gameserver.listener.actor.player.impl;

import l2jexpress.commons.lang.reference.HardReference;
import l2jexpress.gameserver.listener.actor.player.OnAnswerListener;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Request;
import l2jexpress.gameserver.network.serverpackets.components.ChatType;
import express.academy.AcademyList;

public class AcademyAnswerListener implements OnAnswerListener
{
	private final HardReference<Player> _activeChar;
	private final HardReference<Player> _academyChar;

	public AcademyAnswerListener(Player activeChar, Player academyChar)
	{
		_activeChar = activeChar.getRef();
		_academyChar = academyChar.getRef();
	}

	@Override
	public void sayYes()
	{
		final Player activeChar = _activeChar.get();
		final Player academyChar = _academyChar.get();
		if (activeChar == null || academyChar == null)
			return;
		AcademyList.inviteInAcademy(activeChar, academyChar);
	}

	@Override
	public void sayNo()
	{
		final Player activeChar = _activeChar.get();
		final Player academyChar = _academyChar.get();
		if (activeChar == null || academyChar == null)
			return;
		final Request req = activeChar.getRequest();
		if (req != null && req.isInProgress())
			req.cancel();
		activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Player " + academyChar.getName() + " refused to join!");
	}
}
