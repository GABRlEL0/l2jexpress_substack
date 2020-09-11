package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.model.Party;
import l2jexpress.gameserver.model.Player;

public class AnswerPartyLootModification extends L2GameClientPacket
{
	public int _answer;

	@Override
	protected void readImpl()
	{
		_answer = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		Party party = activeChar.getParty();
		if (party != null)
			party.answerLootChangeRequest(activeChar, _answer == 1);
	}
}
