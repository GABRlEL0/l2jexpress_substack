package ai.monas.FurnaceSpawnRoom;

import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.ai.DefaultAI;
import l2jexpress.gameserver.data.xml.holder.EventHolder;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.entity.events.EventType;
import l2jexpress.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import l2jexpress.gameserver.model.instances.NpcInstance;

/**
 * @author PaInKiLlEr
 * 		- AI Monster 22798, 22799, 22800.
 * 		- AI for spawning braziers room.
 * 		- There is a 5% chance that the spawn to death four braziers unlikely.
 * 		- AI is tested and works.
 */
public class DivinityMonster extends DefaultAI
{
	public DivinityMonster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		int event_id = actor.getAISpawnParam();
		MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);

		if (Rnd.chance(5) && !furnace.isInProgress())
			furnace.spawnAction(MonasteryFurnaceEvent.FURNACE_ROOM, true);

		super.onEvtDead(killer);
	}
}