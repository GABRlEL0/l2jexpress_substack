package ai;

import l2jexpress.gameserver.ai.Fighter;
import l2jexpress.gameserver.instancemanager.QuestManager;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.World;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.model.quest.Quest;
import l2jexpress.gameserver.model.quest.QuestState;
import quests._024_InhabitantsOfTheForestOfTheDead;

/**
 * @author VISTALL
 */
public class Quest024Fighter extends Fighter
{
	public Quest024Fighter(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		Quest q = QuestManager.getQuest(_024_InhabitantsOfTheForestOfTheDead.class);
		if (q != null)
			for (Player player : World.getAroundPlayers(getActor(), 300, 200))
			{
				QuestState questState = player.getQuestState(_024_InhabitantsOfTheForestOfTheDead.class);
				if (questState != null && questState.getCond() == 3)
					q.notifyEvent("seePlayer", questState, getActor());
			}

		return super.thinkActive();
	}
}