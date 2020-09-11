package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2jexpress.gameserver.scripts.Functions;

public class CombineTalismans extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = new String[]
	{
		"combine",
		"talisman"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		CCPSmallCommands.combineTalismans(activeChar);
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}