package l2jexpress.gameserver.network.clientpackets;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.data.xml.holder.PetitionGroupHolder;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.petition.PetitionMainGroup;
import l2jexpress.gameserver.network.serverpackets.ExResponseShowStepTwo;

/**
 * @author VISTALL
 */
public class RequestExShowStepTwo extends L2GameClientPacket
{
	private int _petitionGroupId;

	@Override
	protected void readImpl()
	{
		_petitionGroupId = readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
			return;

		PetitionMainGroup group = PetitionGroupHolder.getInstance().getPetitionGroup(_petitionGroupId);
		if (group == null)
			return;

		player.setPetitionGroup(group);
		player.sendPacket(new ExResponseShowStepTwo(player, group));
	}
}