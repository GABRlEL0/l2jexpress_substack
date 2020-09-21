package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Opens the community board 
 *
 * @author l2jexpress_devs
 */
public class Cbinterface implements IVoicedCommandHandler
{
    private final String[] _commandList = {"75bSXcb878", "887HsenSjdst655"};

    @Override
    public String[] getVoicedCommandList()
    {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args)
    {
        final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
        html.setFile("scripts/services/community/pages/btnservicios.htm");
        player.sendPacket(html);
        return true;
    }
}