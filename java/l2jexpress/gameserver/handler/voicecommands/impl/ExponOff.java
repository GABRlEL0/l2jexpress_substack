package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;


/**
 * @author L2JExpress 
 */
public class ExponOff implements IVoicedCommandHandler
{
	
	boolean alter = true;
	private static final String[] _commandList = {
			
			"noexp"		
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		command = command.intern();
		if (command.equalsIgnoreCase("noexp"))
			return expOnOff(command, activeChar, args);
		return false;
	}

	private boolean expOnOff(String command, Player activeChar, String args)
	{
		if (Config.EXP_ON_OFF_ENABLED)
		{
			if (command.equalsIgnoreCase("noexp"))
			{
				
				if (activeChar.getVar("NoExp") == null)
				{
					activeChar.setVar("NoExp", "1", -1);
					activeChar.sendMessage("You turned off the Exp and Sp Gain");
				}
				
				else
				{
					activeChar.unsetVar("NoExp");
					activeChar.sendMessage("You turned on the Exp and Sp Gain");
				}
			}
		}
		return true;
		}
	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

}