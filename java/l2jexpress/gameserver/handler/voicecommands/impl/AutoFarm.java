package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;


/**
 * @author L2JExpress 
 */
public class AutoFarm implements IVoicedCommandHandler
{

	boolean alter = true;
	private static final String[] _commandList = {
			
			"75sagsdg878"		
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		command = command.intern();
		if (command.equalsIgnoreCase("75sagsdg878"))
			return AutofarmOnOff(command, activeChar, args);
		return false;
	}

	private boolean AutofarmOnOff(String command, Player activeChar, String args)
	{

		
		if (Config.AUTOFARM_ENABLED)
		{
			if (command.equalsIgnoreCase("75sagsdg878"))
			{
				
				if (activeChar.getVar("Autofarm") == null)
				{
					activeChar.setVar("Autofarm", "1", -1);
					activeChar.sendMessage("You turned Autofarm ON");
					activeChar.sendMessage("Be ready...");
					activeChar.startAutoFarm();
				}
				
				else
				{
					activeChar.unsetVar("Autofarm");
					activeChar.sendMessage("You turned Autofarm Off");
					activeChar.stopAutoFarm();
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