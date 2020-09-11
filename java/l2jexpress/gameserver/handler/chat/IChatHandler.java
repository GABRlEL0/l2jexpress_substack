package l2jexpress.gameserver.handler.chat;

import l2jexpress.gameserver.network.serverpackets.components.ChatType;

public interface IChatHandler
{
	void say();

	ChatType getType();
}
