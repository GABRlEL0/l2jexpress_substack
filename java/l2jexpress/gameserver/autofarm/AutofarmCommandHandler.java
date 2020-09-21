package l2jexpress.gameserver.autofarm;

import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;


public class AutofarmCommandHandler implements IVoicedCommandHandler {
    

	private final String[] _commandList = {"farm", "farmon", "farmoff"};

    @Override
    public String[] getVoicedCommandList()
    {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args)
    {
    	 switch (command) {
    	             case "farm":
    	                 AutofarmManager.INSTANCE.toggleFarm(activeChar);
    	                 break;
    	             case "farmon":
    	                 AutofarmManager.INSTANCE.startFarm(activeChar);
    	                 break;
    	             case "farmoff":
    	                 AutofarmManager.INSTANCE.stopFarm(activeChar);
    	                 break;
    }
		return true;
    }
}    