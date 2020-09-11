package l2jexpress.gameserver.handler.bypass;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.instances.NpcInstance;

public interface IBypassHandler
{
	String[] getBypasses();

	void onBypassFeedback(NpcInstance npc, Player player, String command);
}
