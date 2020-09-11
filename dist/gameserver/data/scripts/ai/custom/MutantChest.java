package ai.custom;

import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.ai.Fighter;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.scripts.Functions;

public class MutantChest extends Fighter
{
	public MutantChest(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (Rnd.chance(30))
			Functions.npcSay(actor, "Enemies! Enemies everywhere! Everything here, the enemies here!");

		actor.deleteMe();
	}
}