package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.network.serverpackets.MagicSkillUse;
import l2jexpress.gameserver.tables.SkillTable;
import l2jexpress.gameserver.templates.npc.NpcTemplate;
/**
 * @author l2jexpress
 */
public final class NevitHeraldInstance extends NpcInstance
{
	private static final long serialVersionUID = -1L;
	public NevitHeraldInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
			return;

		if (command.equalsIgnoreCase("request_blessing"))
		{
			if (player.getEffectList().getEffectsBySkillId(23312) != null)
			{
				showChatWindow(player, 1);
				return;
			}
			List<Creature> target = new ArrayList<Creature>();
			target.add(player);
			broadcastPacket(new MagicSkillUse(this, player, 23312, 1, 0, 0));
			callSkill(SkillTable.getInstance().getInfo(23312, 1), target, true);
		}
		else
			super.onBypassFeedback(player, command);
	}
}