package quests;

import l2jexpress.gameserver.data.xml.holder.EventHolder;
import l2jexpress.gameserver.model.entity.events.EventType;
import l2jexpress.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2jexpress.gameserver.model.quest.Quest;
import l2jexpress.gameserver.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 2:15/09.06.2011
 */
public class _729_ProtectTheTerritoryCatapult extends Quest implements ScriptFile
{
	public _729_ProtectTheTerritoryCatapult()
	{
		super(PARTY_NONE);
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		runnerEvent.addBreakQuest(this);
	}

	@Override
	public void onLoad()
	{

	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}
