package npc.model;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.network.serverpackets.NpcHtmlMessage;
import l2jexpress.gameserver.templates.npc.NpcTemplate;

public class RiganInstance extends NpcInstance
{
	private static final String FILE_PATH = "custom/";

	public RiganInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String fileName = FILE_PATH;
		fileName += getNpcId();
		if(val > 0)
			fileName += "-" + val;
		fileName += ".htm";
		player.sendPacket(new NpcHtmlMessage(player, this, fileName, val));
	}
}
