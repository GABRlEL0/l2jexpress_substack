package l2jexpress.gameserver.model.instances;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import l2jexpress.commons.threading.RunnableImpl;
import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.Announcements;
import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.cache.Msg;
import l2jexpress.gameserver.data.xml.holder.NpcHolder;
import l2jexpress.gameserver.idfactory.IdFactory;
import l2jexpress.gameserver.instancemanager.QuestManager;
import l2jexpress.gameserver.instancemanager.RaidBossSpawnManager;
import l2jexpress.gameserver.model.AggroList.HateInfo;
import l2jexpress.gameserver.model.CommandChannel;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.GameObjectTasks;
import l2jexpress.gameserver.model.Party;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.base.Experience;
import l2jexpress.gameserver.model.entity.Hero;
import l2jexpress.gameserver.model.entity.HeroDiary;
import l2jexpress.gameserver.model.quest.Quest;
import l2jexpress.gameserver.model.quest.QuestState;
import l2jexpress.gameserver.network.serverpackets.ExShowScreenMessage;
import l2jexpress.gameserver.network.serverpackets.SystemMessage;
import l2jexpress.gameserver.network.serverpackets.components.NpcString;
import l2jexpress.gameserver.tables.SkillTable;
import l2jexpress.gameserver.templates.npc.NpcTemplate;

public class RaidBossInstance extends MonsterInstance
{
	private ScheduledFuture<?> minionMaintainTask;
	private String _killer;
	private int id_Qa = 29001;
	private int id_Orfen = 29014;
	private int id_Core = 29006;

	private static final int MINION_UNSPAWN_INTERVAL = 5000; //time to unspawn minions when boss is dead, msec

	public RaidBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean isRaid()
	{
		return true;
	}

	protected int getMinionUnspawnInterval()
	{
		return MINION_UNSPAWN_INTERVAL;
	}

	protected int getKilledInterval(MinionInstance minion)
	{
		return 120000; //2 minutes to respawn
	}

	@Override
	public void notifyMinionDied(MinionInstance minion)
	{
		minionMaintainTask = ThreadPoolManager.getInstance().schedule(new MaintainKilledMinion(minion), getKilledInterval(minion));
		super.notifyMinionDied(minion);
	}

	private class MaintainKilledMinion extends RunnableImpl
	{
		private final MinionInstance minion;

		public MaintainKilledMinion(MinionInstance minion)
		{
			this.minion = minion;
		}

		@Override
		public void runImpl()
		{
			if (!isDead())
			{
				minion.refreshID();
				spawnMinion(minion);
			}
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		if (minionMaintainTask != null)
		{
			minionMaintainTask.cancel(false);
			minionMaintainTask = null;
		}
		

		final int points = getTemplate().rewardRp;
		if (points > 0)
			calcRaidPointsReward(points);

		if (this instanceof ReflectionBossInstance)
		{
			super.onDeath(killer);
			return;
		}

		_killer = (killer.getClan() != null ? killer.getClan().getName() : "");

		if (killer.isPlayable())
		{
			Player player = killer.getPlayer();
			if (player.isInParty())
			{
				for (Player member : player.getParty().getMembers())
				{
					member.updateRaidKills();
					if (member.isNoble())
						Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
				}
				player.getParty().sendPacket(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
			}
			else
			{
				if (player.isNoble())
					Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
				
				player.getCounters().raidsKilled++;
				player.sendPacket(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
				player.updateRaidKills();
			}

			Quest q = QuestManager.getQuest(508);
			if (q != null)
			{
				String qn = q.getName();
				if (player.getClan() != null && player.getClan().getLeader().isOnline() && player.getClan().getLeader().getPlayer().getQuestState(qn) != null)
				{
					QuestState st = player.getClan().getLeader().getPlayer().getQuestState(qn);
					st.getQuest().onKill(this, st);
				}
			}
		}

		if (getMinionList().hasAliveMinions())
		{
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					if (isDead())
						getMinionList().unspawnMinions();
				}
			}, getMinionUnspawnInterval());
		}

		int boxId = 0;
		switch (getNpcId())
		{
			case 25035: // Shilens Messenger Cabrio
				boxId = 31027;
				break;
			case 25054: // Demon Kernon
				boxId = 31028;
				break;
			case 25126: // Golkonda, the Longhorn General
				boxId = 31029;
				break;
			case 25220: // Death Lord Hallate
				boxId = 31030;
				break;
		}

		if (boxId != 0)
		{
			NpcTemplate boxTemplate = NpcHolder.getInstance().getTemplate(boxId);
			if (boxTemplate != null)
			{
				final NpcInstance box = new NpcInstance(IdFactory.getInstance().getNextId(), boxTemplate);
				box.spawnMe(getLoc());
				box.setSpawnedLoc(getLoc());

				ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(box), 60000);
			}
		}

		super.onDeath(killer);
	}

	//FIXME [G1ta0] Ñ€Ð°Ð·Ð¾Ð±Ñ€Ð°Ñ‚ÑŒ Ñ�Ñ‚Ð¾Ñ‚ Ñ…Ð»Ð°Ð¼
	@SuppressWarnings("unchecked")
	private void calcRaidPointsReward(int totalPoints)
	{
		// Object groupkey (L2Party/L2CommandChannel/L2Player) | [List<L2Player> group, Long GroupDdamage]
		Map<Object, Object[]> participants = new HashMap<Object, Object[]>();
		double totalHp = getMaxHp();

		// Ð Ð°Ð·Ð±Ð¸Ð²Ð°ÐµÐ¼ Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð² Ð¿Ð¾ Ð³Ñ€ÑƒÐ¿Ð¿Ð°Ð¼. ÐŸÐ¾ Ð²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ð¾Ñ�Ñ‚Ð¸ Ð¸Ñ�Ð¿Ð¾Ð»ÑŒÐ·ÑƒÐµÐ¼ Ð½Ð°Ð¸Ð±Ð¾Ð»ÑŒÑˆÑƒÑŽ Ð¸Ð· Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ñ‹Ñ… Ð³Ñ€ÑƒÐ¿Ð¿: Command Channel â†’ Party â†’ StandAlone (Ñ�Ð°Ð¼ Ð¿Ð»ÑŽÑ� Ð¿ÐµÑ‚ :)
		for (HateInfo ai : getAggroList().getPlayableMap().values())
		{
			Player player = ai.attacker.getPlayer();
			Object key = player.getParty() != null ? player.getParty().getCommandChannel() != null ? player.getParty().getCommandChannel() : player.getParty() : player.getPlayer();
			Object[] info = participants.get(key);
			if (info == null)
			{
				info = new Object[] { new HashSet<Player>(), new Long(0) };
				participants.put(key, info);
			}

			// ÐµÑ�Ð»Ð¸ Ñ�Ñ‚Ð¾ Ð¿Ð°Ñ‚Ð¸ Ð¸Ð»Ð¸ ÐºÐ¾Ð¼Ð°Ð½Ð´Ð½Ñ‹Ð¹ ÐºÐ°Ð½Ð°Ð» Ñ‚Ð¾ Ð±ÐµÑ€ÐµÐ¼ Ð¾Ñ‚Ñ‚ÑƒÐ´Ð° Ð²ÐµÑ�ÑŒ Ñ�Ð¿Ð¸Ñ�Ð¾Ðº ÑƒÑ‡Ð°Ñ�Ñ‚Ð²ÑƒÑŽÑ‰Ð¸Ñ…, Ð´Ð°Ð¶Ðµ Ñ‚ÐµÑ… ÐºÑ‚Ð¾ Ð½Ðµ Ð² Ð°Ð³Ð³Ñ€Ð¾Ð»Ð¸Ñ�Ñ‚Ðµ
			// Ð´ÑƒÐ±Ð»Ð¸ÐºÐ°Ñ‚Ñ‹ Ð½Ðµ Ñ�Ñ‚Ñ€Ð°ÑˆÐ½Ñ‹ - Ñ�Ñ‚Ð¾ Ñ…Ð°ÑˆÑ�ÐµÑ‚
			if (key instanceof CommandChannel)
			{
				for (Player p : ((CommandChannel) key))
					if (p.isInRangeZ(this, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						((Set<Player>) info[0]).add(p);
			}
			else if (key instanceof Party)
			{
				for (Player p : ((Party) key).getMembers())
					if (p.isInRangeZ(this, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						((Set<Player>) info[0]).add(p);
			}
			else
				((Set<Player>) info[0]).add(player);

			info[1] = ((Long) info[1]).longValue() + ai.damage;
		}

		for (Object[] groupInfo : participants.values())
		{
			Set<Player> players = (HashSet<Player>) groupInfo[0];
			// Ñ�Ñ‚Ð¾ Ñ‚Ð° Ñ‡Ð°Ñ�Ñ‚ÑŒ, ÐºÐ¾Ñ‚Ð¾Ñ€ÑƒÑŽ Ð¸Ð³Ñ€Ð¾Ðº Ð·Ð°Ñ�Ð»ÑƒÐ¶Ð¸Ð» Ð´Ð°Ð¼Ð°Ð³Ð¾Ð¼ Ð³Ñ€ÑƒÐ¿Ð¿Ñ‹, Ð½Ð¾ Ð½Ð° Ð½ÐµÐµ Ð¼Ð¾Ð¶ÐµÑ‚ Ð±Ñ‹Ñ‚ÑŒ Ð½Ð°Ð»Ð¾Ð¶ÐµÐ½ ÑˆÑ‚Ñ€Ð°Ñ„ Ð¾Ñ‚ ÑƒÑ€Ð¾Ð²Ð½Ñ� Ð¸Ð³Ñ€Ð¾ÐºÐ°
			int perPlayer = (int) Math.round(totalPoints * ((Long) groupInfo[1]).longValue() / (totalHp * players.size()));
			for (Player player : players)
			{
				int playerReward = perPlayer;
				// Ð¿Ñ€Ð¸Ð¼ÐµÐ½Ñ�ÐµÐ¼ ÑˆÑ‚Ñ€Ð°Ñ„ ÐµÑ�Ð»Ð¸ Ð½ÑƒÐ¶ÐµÐ½
				playerReward = (int) Math.round(playerReward * Experience.penaltyModifier(calculateLevelDiffForDrop(player.getLevel()), 9));
				if (playerReward == 0)
					continue;
				player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_RAID_POINTS).addNumber(playerReward));
				RaidBossSpawnManager.getInstance().addPoints(player.getObjectId(), getNpcId(), playerReward);
			}
		}

		RaidBossSpawnManager.getInstance().updatePointsDb();
		RaidBossSpawnManager.getInstance().calculateRanking();
	}

	@Override
	protected void onDecay()
	{
		super.onDecay();
		RaidBossSpawnManager.getInstance().setRaidBossDied(getNpcId(), _killer);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		addSkill(SkillTable.getInstance().getInfo(4045, 1)); // Resist Full Magic Attack
		RaidBossSpawnManager.getInstance().onBossSpawned(this);
		//4fun adhesión de grito  ESTOS ESTÁN VIVOS
		if (this.getTemplate().getNpcId() == id_Qa)
		{
			Announcements.getInstance().announceToAll(" Queen ant it´s Alive!! ");
		}
		if (this.getTemplate().getNpcId() == id_Orfen)
		{
			Announcements.getInstance().announceToAll(" Orfen it´s Alive!! ");
		}
		if (this.getTemplate().getNpcId() == id_Core)
		{
			Announcements.getInstance().announceToAll(" Core it´s Alive!! ");
		}
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public boolean hasRandomWalk()
	{
		return false;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isHealBlocked()
	{
		return true;
	}
}