package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2jexpress.gameserver.scripts.Functions;

/**
 * @author l2jexpress_devs
 */
public class Ping extends Functions implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		//"ping"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		CCPSmallCommands.getPing(activeChar);
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}