package l2jexpress.gameserver.handler.admincommands.impl;

import l2jexpress.gameserver.handler.admincommands.IAdminCommandHandler;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.quest.Quest;
import l2jexpress.gameserver.model.quest.QuestState;
import l2jexpress.gameserver.scripts.Scripts;

public class AdminScripts implements IAdminCommandHandler
{
	private static enum Commands
	{
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.isGM())
			return false;
		
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}