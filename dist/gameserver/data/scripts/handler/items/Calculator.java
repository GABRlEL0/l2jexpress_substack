package handler.items;

import l2jexpress.gameserver.handler.items.ItemHandler;
import l2jexpress.gameserver.model.Playable;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.network.serverpackets.ShowCalc;
import l2jexpress.gameserver.scripts.ScriptFile;

public class Calculator extends ScriptItemHandler implements ScriptFile
{
	private static final int CALCULATOR = 4393;


	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (!playable.isPlayer())
			return false;

		playable.sendPacket(new ShowCalc(item.getItemId()));
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return new int[]{CALCULATOR};
	}
}
