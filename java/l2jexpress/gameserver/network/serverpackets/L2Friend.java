package l2jexpress.gameserver.network.serverpackets;

import l2jexpress.gameserver.model.Player;

public class L2Friend extends L2GameServerPacket
{
	private boolean _add, _online;
	private String _name;
	private int _object_id;

	public L2Friend(Player player, boolean add)
	{
		_add = add;
		_name = player.getName();
		_object_id = player.getObjectId();
		_online = true;
	}

	public L2Friend(String name, boolean add, boolean online, int object_id)
	{
		_name = name;
		_add = add;
		_object_id = object_id;
		_online = online;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x76);
		writeD(_add ? 1 : 3); // 1 - Ð´Ð¾Ð±Ð°Ð²Ð¸Ñ‚ÑŒ Ð´Ñ€ÑƒÐ³Ð° Ð² Ñ�Ð¿Ð¸ÐºÐ¾Ðº, 3 ÑƒÐ´Ð°Ð»Ð¸Ñ‚ÑŒ Ð´Ñ€ÑƒÐ³Ð° Ñ�Ð¾ Ñ�Ð¿Ð¸Ñ�ÐºÐ°
		writeD(0); //Ð¸ Ñ�Ð½Ð¾Ð²Ð° Ñ‚ÑƒÑ‚ Ð¸Ð´ÐµÑ‚ ID Ð¿ÐµÑ€Ñ�Ð¾Ð½Ð°Ð¶Ð° Ð² Ñ�Ð¿Ð¸Ñ�ÐºÐµ Ð¾Ñ„Ñ„Ð°, Ð½Ðµ object id
		writeS(_name);
		writeD(_online ? 1 : 0); // Ð¾Ð½Ð»Ð°Ð¹Ð½ Ð¸Ð»Ð¸ Ð¾Ñ„Ñ„Ð»Ð°Ð¹Ð½
		writeD(_object_id); //object_id if online
	}
}