package l2jexpress.gameserver.handler.petition;

import l2jexpress.gameserver.model.Player;

public interface IPetitionHandler
{
	void handle(Player player, int id, String txt);
}
