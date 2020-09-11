package ai.hellbound;

import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.ai.CtrlIntention;
import l2jexpress.gameserver.ai.Fighter;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.scripts.Functions;

public class TownGuard extends Fighter
{
	public TownGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
			Functions.npcSay(actor, "Invader!");
		super.onIntentionAttack(target);
	}
}