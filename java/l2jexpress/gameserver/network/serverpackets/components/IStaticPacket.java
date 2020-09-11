package l2jexpress.gameserver.network.serverpackets.components;

import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.serverpackets.L2GameServerPacket;

public interface IStaticPacket
{
	L2GameServerPacket packet(Player player);
}
