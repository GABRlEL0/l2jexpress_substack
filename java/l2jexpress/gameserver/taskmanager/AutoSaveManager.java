package l2jexpress.gameserver.taskmanager;

import java.util.concurrent.Future;

import l2jexpress.commons.threading.RunnableImpl;
import l2jexpress.commons.threading.SteppingRunnableQueueManager;
import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.ai.PhantomPlayerAI;
import l2jexpress.gameserver.model.Player;

public class AutoSaveManager extends SteppingRunnableQueueManager
{
	private static final AutoSaveManager _instance = new AutoSaveManager();

	public static final AutoSaveManager getInstance()
	{
		return _instance;
	}

	private AutoSaveManager()
	{
		super(10000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
		//Очистка каждые 60 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				AutoSaveManager.this.purge();
			}

		}, 60000L, 60000L);
	}

	public Future<?> addAutoSaveTask(Player player)
	{
		long delay = Rnd.get(180, 360) * 1000L;

		return scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				if (player == null || !player.isOnline())
					return;

				player.store(true);

				// Kick stuck phantoms.
				if (player.isPhantom() && player.getAI().isPhantomPlayerAI())
				{
					long stuckTime = (System.currentTimeMillis() - ((PhantomPlayerAI) player.getAI()).getLastAiResponse());
					if (stuckTime > 10000)
					{
						player.kick();
						_log.info("Kicking stuck phantom player: " + player + " stuck time is " + stuckTime + "ms.");
					}
				}
			}

		}, delay, delay);
	}
}