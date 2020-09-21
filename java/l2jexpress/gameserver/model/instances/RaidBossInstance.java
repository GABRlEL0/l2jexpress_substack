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
import l2jexpress.gameserver.network.serverpackets.components.CustomMessage;
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

	//FIXME [G1ta0] Ã‘â‚¬Ã�Â°Ã�Â·Ã�Â¾Ã�Â±Ã‘â‚¬Ã�Â°Ã‘â€šÃ‘Å’ Ã‘ï¿½Ã‘â€šÃ�Â¾Ã‘â€š Ã‘â€¦Ã�Â»Ã�Â°Ã�Â¼
	@SuppressWarnings("unchecked")
	private void calcRaidPointsReward(int totalPoints)
	{
		// Object groupkey (L2Party/L2CommandChannel/L2Player) | [List<L2Player> group, Long GroupDdamage]
		Map<Object, Object[]> participants = new HashMap<Object, Object[]>();
		double totalHp = getMaxHp();

		// Ã�Â Ã�Â°Ã�Â·Ã�Â±Ã�Â¸Ã�Â²Ã�Â°Ã�ÂµÃ�Â¼ Ã�Â¸Ã�Â³Ã‘â‚¬Ã�Â¾Ã�ÂºÃ�Â¾Ã�Â² Ã�Â¿Ã�Â¾ Ã�Â³Ã‘â‚¬Ã‘Æ’Ã�Â¿Ã�Â¿Ã�Â°Ã�Â¼. Ã�Å¸Ã�Â¾ Ã�Â²Ã�Â¾Ã�Â·Ã�Â¼Ã�Â¾Ã�Â¶Ã�Â½Ã�Â¾Ã‘ï¿½Ã‘â€šÃ�Â¸ Ã�Â¸Ã‘ï¿½Ã�Â¿Ã�Â¾Ã�Â»Ã‘Å’Ã�Â·Ã‘Æ’Ã�ÂµÃ�Â¼ Ã�Â½Ã�Â°Ã�Â¸Ã�Â±Ã�Â¾Ã�Â»Ã‘Å’Ã‘Ë†Ã‘Æ’Ã‘Å½ Ã�Â¸Ã�Â· Ã�Â´Ã�Â¾Ã‘ï¿½Ã‘â€šÃ‘Æ’Ã�Â¿Ã�Â½Ã‘â€¹Ã‘â€¦ Ã�Â³Ã‘â‚¬Ã‘Æ’Ã�Â¿Ã�Â¿: Command Channel Ã¢â€ â€™ Party Ã¢â€ â€™ StandAlone (Ã‘ï¿½Ã�Â°Ã�Â¼ Ã�Â¿Ã�Â»Ã‘Å½Ã‘ï¿½ Ã�Â¿Ã�ÂµÃ‘â€š :)
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

			// Ã�ÂµÃ‘ï¿½Ã�Â»Ã�Â¸ Ã‘ï¿½Ã‘â€šÃ�Â¾ Ã�Â¿Ã�Â°Ã‘â€šÃ�Â¸ Ã�Â¸Ã�Â»Ã�Â¸ Ã�ÂºÃ�Â¾Ã�Â¼Ã�Â°Ã�Â½Ã�Â´Ã�Â½Ã‘â€¹Ã�Â¹ Ã�ÂºÃ�Â°Ã�Â½Ã�Â°Ã�Â» Ã‘â€šÃ�Â¾ Ã�Â±Ã�ÂµÃ‘â‚¬Ã�ÂµÃ�Â¼ Ã�Â¾Ã‘â€šÃ‘â€šÃ‘Æ’Ã�Â´Ã�Â° Ã�Â²Ã�ÂµÃ‘ï¿½Ã‘Å’ Ã‘ï¿½Ã�Â¿Ã�Â¸Ã‘ï¿½Ã�Â¾Ã�Âº Ã‘Æ’Ã‘â€¡Ã�Â°Ã‘ï¿½Ã‘â€šÃ�Â²Ã‘Æ’Ã‘Å½Ã‘â€°Ã�Â¸Ã‘â€¦, Ã�Â´Ã�Â°Ã�Â¶Ã�Âµ Ã‘â€šÃ�ÂµÃ‘â€¦ Ã�ÂºÃ‘â€šÃ�Â¾ Ã�Â½Ã�Âµ Ã�Â² Ã�Â°Ã�Â³Ã�Â³Ã‘â‚¬Ã�Â¾Ã�Â»Ã�Â¸Ã‘ï¿½Ã‘â€šÃ�Âµ
			// Ã�Â´Ã‘Æ’Ã�Â±Ã�Â»Ã�Â¸Ã�ÂºÃ�Â°Ã‘â€šÃ‘â€¹ Ã�Â½Ã�Âµ Ã‘ï¿½Ã‘â€šÃ‘â‚¬Ã�Â°Ã‘Ë†Ã�Â½Ã‘â€¹ - Ã‘ï¿½Ã‘â€šÃ�Â¾ Ã‘â€¦Ã�Â°Ã‘Ë†Ã‘ï¿½Ã�ÂµÃ‘â€š
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
			// Ã‘ï¿½Ã‘â€šÃ�Â¾ Ã‘â€šÃ�Â° Ã‘â€¡Ã�Â°Ã‘ï¿½Ã‘â€šÃ‘Å’, Ã�ÂºÃ�Â¾Ã‘â€šÃ�Â¾Ã‘â‚¬Ã‘Æ’Ã‘Å½ Ã�Â¸Ã�Â³Ã‘â‚¬Ã�Â¾Ã�Âº Ã�Â·Ã�Â°Ã‘ï¿½Ã�Â»Ã‘Æ’Ã�Â¶Ã�Â¸Ã�Â» Ã�Â´Ã�Â°Ã�Â¼Ã�Â°Ã�Â³Ã�Â¾Ã�Â¼ Ã�Â³Ã‘â‚¬Ã‘Æ’Ã�Â¿Ã�Â¿Ã‘â€¹, Ã�Â½Ã�Â¾ Ã�Â½Ã�Â° Ã�Â½Ã�ÂµÃ�Âµ Ã�Â¼Ã�Â¾Ã�Â¶Ã�ÂµÃ‘â€š Ã�Â±Ã‘â€¹Ã‘â€šÃ‘Å’ Ã�Â½Ã�Â°Ã�Â»Ã�Â¾Ã�Â¶Ã�ÂµÃ�Â½ Ã‘Ë†Ã‘â€šÃ‘â‚¬Ã�Â°Ã‘â€ž Ã�Â¾Ã‘â€š Ã‘Æ’Ã‘â‚¬Ã�Â¾Ã�Â²Ã�Â½Ã‘ï¿½ Ã�Â¸Ã�Â³Ã‘â‚¬Ã�Â¾Ã�ÂºÃ�Â°
			int perPlayer = (int) Math.round(totalPoints * ((Long) groupInfo[1]).longValue() / (totalHp * players.size()));
			for (Player player : players)
			{
				int playerReward = perPlayer;
				// Ã�Â¿Ã‘â‚¬Ã�Â¸Ã�Â¼Ã�ÂµÃ�Â½Ã‘ï¿½Ã�ÂµÃ�Â¼ Ã‘Ë†Ã‘â€šÃ‘â‚¬Ã�Â°Ã‘â€ž Ã�ÂµÃ‘ï¿½Ã�Â»Ã�Â¸ Ã�Â½Ã‘Æ’Ã�Â¶Ã�ÂµÃ�Â½
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
		//4fun adhesiÃ³n de grito  ESTOS ESTÃ�N VIVOS
		if (this.getTemplate().getNpcId() == id_Qa)
		{
			Announcements.getInstance().announceToAll(" Queen ant itÂ´s Alive!! ");
		}
		if (this.getTemplate().getNpcId() == id_Orfen)
		{
			Announcements.getInstance().announceToAll(" Orfen itÂ´s Alive!! ");
		}
		if (this.getTemplate().getNpcId() == id_Core)
		{
			Announcements.getInstance().announceToAll(" Core itÂ´s Alive!! ");
		}
		if (Config.ANNOUNCE_SPAWN_RB_SPECIAL_IDs)
			if (this.getTemplate().getNpcId() == 40001)
			{
				Announcements.getInstance().announceToAll("Queen Ant Clone is Alive!! ");
			}
		
			if (this.getTemplate().getNpcId() == 40002)
			{
				Announcements.getInstance().announceToAll("Antharas Clone is Alive!! ");
			}
			if (this.getTemplate().getNpcId() == 40003)
			{
				Announcements.getInstance().announceToAll("Baium Clone is Alive!! ");
			}
			if (this.getTemplate().getNpcId() == 40004)
			{
				Announcements.getInstance().announceToAll("Valakas Clone is Alive!! ");
			}
			if (this.getTemplate().getNpcId() == 40005)
			{
				Announcements.getInstance().announceToAll("Scarlet van Halisha Clone is Alive!! ");
			}
			if (this.getTemplate().getNpcId() == 40006)
			{
				Announcements.getInstance().announceToAll("Beleth Clone Alive!! ");
			}
			if (this.getTemplate().getNpcId() == 40007)
			{
				Announcements.getInstance().announceToAll("The Allmighty God Neptuno is Alive!! ");
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