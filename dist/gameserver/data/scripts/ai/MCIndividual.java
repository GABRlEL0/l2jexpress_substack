package ai;

import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.ai.DefaultAI;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.network.serverpackets.SocialAction;

/**
 * @author l2jexpress
  * - AI for individual monsters (32439, 32440, 32441).
  * - Indicates social programs.
  * - AI is tested and works.
 */
public class MCIndividual extends DefaultAI
{
	public MCIndividual(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		if (actor == null)
			return;

		ThreadPoolManager.getInstance().schedule(new ScheduleSocial(), 1000);
		super.onEvtSpawn();
	}

	private class ScheduleSocial implements Runnable
	{
		@Override
		public void run()
		{
			NpcInstance actor = getActor();
			actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
		}
	}
}