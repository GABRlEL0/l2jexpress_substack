package l2jexpress.gameserver.network.clientpackets;

public class RequestExEventMatchObserverEnd extends L2GameClientPacket
{
	private int unk, unk2;

	/**
	 * format: dd
	 */
	@Override
	protected void readImpl()
	{
		unk = readD();
		unk2 = readD();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}

	// l2jexpress - This packet can be used while the character is blocked
	@Override
    public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}