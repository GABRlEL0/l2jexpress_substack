package ai.den_of_evil;

import l2jexpress.gameserver.ai.DefaultAI;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.World;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.network.serverpackets.components.NpcString;
import l2jexpress.gameserver.scripts.Functions;
import l2jexpress.gameserver.taskmanager.AiTaskManager;

/**
 * @author l2jexpress
 * @date 20.01.2012
 * Npc Id: 32026
 */
public class HestuiGuard extends DefaultAI
{
	public HestuiGuard(NpcInstance actor)
	{
		super(actor);

	}

	@Override
	public synchronized void startAITask()
	{
		if (_aiTask == null)
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
	}

	@Override
	protected synchronized void switchAITask(long NEW_DELAY)
	{}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		for (Player player : World.getAroundPlayers(actor))
		{
			if (player.getLevel() <= 37)
				Functions.npcSay(actor, NpcString.THIS_PLACE_IS_DANGEROUS_S1, player.getName());
		}

		return false;
	}
}
