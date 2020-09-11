package l2jexpress.gameserver.model.instances;

import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l2jexpress.commons.threading.RunnableImpl;
import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.Announcements;
import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.cache.Msg;
import l2jexpress.gameserver.instancemanager.CursedWeaponsManager;
import l2jexpress.gameserver.model.AggroList.HateInfo;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Effect;
import l2jexpress.gameserver.model.GameObjectsStorage;
import l2jexpress.gameserver.model.Manor;
import l2jexpress.gameserver.model.MinionList;
import l2jexpress.gameserver.model.Party;
import l2jexpress.gameserver.model.Playable;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.model.base.Experience;
import l2jexpress.gameserver.model.base.TeamType;
import l2jexpress.gameserver.model.entity.Reflection;
import l2jexpress.gameserver.model.pledge.Clan;
import l2jexpress.gameserver.model.quest.Quest;
import l2jexpress.gameserver.model.quest.QuestEventType;
import l2jexpress.gameserver.model.quest.QuestState;
import l2jexpress.gameserver.model.reward.RewardItem;
import l2jexpress.gameserver.model.reward.RewardList;
import l2jexpress.gameserver.model.reward.RewardType;
import l2jexpress.gameserver.network.serverpackets.ExShowScreenMessage;
import l2jexpress.gameserver.network.serverpackets.SocialAction;
import l2jexpress.gameserver.network.serverpackets.SystemMessage;
import l2jexpress.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2jexpress.gameserver.network.serverpackets.components.ChatType;
import l2jexpress.gameserver.stats.Stats;
import l2jexpress.gameserver.tables.SkillTable;
import l2jexpress.gameserver.templates.npc.Faction;
import l2jexpress.gameserver.templates.npc.NpcTemplate;
import l2jexpress.gameserver.utils.Location;
//import express.managers.AutoRaidEventManager;

public class MonsterInstance extends NpcInstance
{
	private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;

	protected static final class RewardInfo
	{
		protected Creature _attacker;
		protected int _dmg = 0;

		public RewardInfo(Creature attacker, int dmg)
		{
			_attacker = attacker;
			_dmg = dmg;
		}

		public void addDamage(int dmg)
		{
			if (dmg < 0)
				dmg = 0;

			_dmg += dmg;
		}

		@Override
		public int hashCode()
		{
			return _attacker.getObjectId();
		}
	}

	private ScheduledFuture<?> minionMaintainTask;
	private final MinionList minionList;

	/** crops */
	private boolean _isSeeded;
	private int _seederId;
	private boolean _altSeed;
	private RewardItem _harvestItem;

	private final Lock harvestLock = new ReentrantLock();

	private int overhitAttackerId;
	/** Stores the extra (over-hit) damage done to the L2NpcInstance when the attacker uses an over-hit enabled skill */
	private double _overhitDamage;

	/** The table containing all players objectID that successfully absorbed the soul of this L2NpcInstance */
	private TIntHashSet _absorbersIds;
	private final Lock absorbLock = new ReentrantLock();

	/** True if a Dwarf has used Spoil on this L2NpcInstance */
	private boolean _isSpoiled;
	private int spoilerId;
	/** Table containing all Items that a Dwarf can Sweep on this L2NpcInstance */
	private List<RewardItem> _sweepItems;
	private final Lock sweepLock = new ReentrantLock();

	private int _isChampion;

	public MonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		minionList = new MinionList(this);
	}

	@Override
	public boolean isMovementDisabled()
	{
		// Ð�ÐµÐ²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ð¾Ñ�Ñ‚ÑŒ Ñ…Ð¾Ð´Ð¸Ñ‚ÑŒ Ð´Ð»Ñ� Ñ�Ñ‚Ð¸Ñ… Ð¼Ð¾Ð±Ð¾Ð²
		return getNpcId() == 18344 || getNpcId() == 18345 || super.isMovementDisabled();
	}

	@Override
	public boolean isLethalImmune()
	{
		return _isChampion > 0 || getNpcId() == 22215 || getNpcId() == 22216 || getNpcId() == 22217 || super.isLethalImmune();
	}

	@Override
	public boolean isFearImmune()
	{
		return _isChampion > 0 || super.isFearImmune();
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return _isChampion > 0 || super.isParalyzeImmune();
	}

	/**
	 * Return True if the attacker is not another L2MonsterInstance.<BR><BR>
	 */
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return !attacker.isMonster();
	}

	public int getChampion()
	{
		return _isChampion;
	}

	@Override
	public boolean isChampion()
	{
		if (getChampion() > 0)
			return true;
		return false;
	}

	public void setChampion()
	{
		if (getReflection().canChampions() && canChampion())
		{
			double random = Rnd.nextDouble();
			if (Config.ALT_CHAMPION_CHANCE2 / 100. >= random)
				setChampion(2);
			else if ((Config.ALT_CHAMPION_CHANCE1 + Config.ALT_CHAMPION_CHANCE2) / 100. >= random)
				setChampion(1);
			else
				setChampion(0);
		}
		else
			setChampion(0);
	}

	public void setChampion(int level)
	{
		if (level == 0)
		{
			removeSkillById(4407);
			_isChampion = 0;
		}
		else
		{
			addSkill(SkillTable.getInstance().getInfo(4407, level));
			_isChampion = level;
		}
	}

	public boolean canChampion()
	{
		return getTemplate().rewardExp > 0 && getTemplate().level <= Config.ALT_CHAMPION_TOP_LEVEL && getTemplate().level >=Config.ALT_CHAMPION_MIN_LEVEL;
	}

	@Override
	public TeamType getTeam()
	{
		return getChampion() == 2 ? TeamType.RED : getChampion() == 1 ? TeamType.BLUE : TeamType.NONE;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		setCurrentHpMp(getMaxHp(), getMaxMp(), true);

		if (getMinionList().hasMinions())
		{
			if (minionMaintainTask != null)
			{
				minionMaintainTask.cancel(false);
				minionMaintainTask = null;
			}
			minionMaintainTask = ThreadPoolManager.getInstance().schedule(new MinionMaintainTask(), 1000L);
		}
		

		switch (getNpcId())
		{	
			case 29001:
			case 29006:
			case 29014:
			case 25725: // Drake Lord
			case 25726: // Behemoth Leader
			case 25727: // Dragon Beast
				Announcements.getInstance().announceToAll(getName() + " has respawned", ChatType.COMMANDCHANNEL_COMMANDER);
				ExShowScreenMessage sm = new ExShowScreenMessage("RaidBoss: "+ getName() + " has respawned, come to kill it!", 10000, ScreenMessageAlign.TOP_CENTER, true);
				for (Player player : GameObjectsStorage.getAllPlayersForIterate())
					player.sendPacket(sm);
				
				break;
		}
	}

	@Override
	protected void onDespawn()
	{
		setOverhitDamage(0);
		setOverhitAttacker(null);
		clearSweep();
		clearHarvest();
		clearAbsorbers();

		super.onDespawn();
	}

	@Override
	public MinionList getMinionList()
	{
		return minionList;
	}

	public class MinionMaintainTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (isDead())
				return;

			getMinionList().spawnMinions();
		}
	}

	public Location getMinionPosition()
	{
		return Location.findPointToStay(this, 100, 150);
	}

	public void notifyMinionDied(MinionInstance minion)
	{

	}

	public void spawnMinion(MonsterInstance minion)
	{
		minion.setReflection(getReflection());
		if (getChampion() == 2)
			minion.setChampion(1);
		else
			minion.setChampion(0);
		minion.setHeading(getHeading());
		minion.setCurrentHpMp(minion.getMaxHp(), minion.getMaxMp(), true);
		minion.spawnMe(getMinionPosition());
	}

	@Override
	public boolean hasMinions()
	{
		return getMinionList().hasMinions();
	}

	@Override
	public void setReflection(Reflection reflection)
	{
		super.setReflection(reflection);

		if (hasMinions())
			for (MinionInstance m : getMinionList().getAliveMinions())
				m.setReflection(reflection);
	}

	@Override
	protected void onDelete()
	{
		if (minionMaintainTask != null)
		{
			minionMaintainTask.cancel(false);
			minionMaintainTask = null;
		}

		getMinionList().deleteMinions();

		super.onDelete();
	}

	@Override
	protected void onDeath(Creature killer)
	{
		if (minionMaintainTask != null)
		{
			minionMaintainTask.cancel(false);
			minionMaintainTask = null;
		}

		calculateRewards(killer);

		// l2jexpress - Auto Raid Event
		//AutoRaidEventManager.getInstance().onRaidDeath(this);

		// l2jexpress - Add a new raid killed stat to all that participated in it
//		if (isRaid() && !isMinion())
//		{
//			for (HateInfo ai : getAggroList().getPlayableMap().values())
//			{
//				final Player player = ai.attacker.getPlayer();
//				if (player != null && ai.hate > 0)
//					player.addPlayerStats(Ranking.STAT_TOP_RAIDS_KILLED);
//			}
//		}

		super.onDeath(killer);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if (skill != null && skill.isOverhit())
		{
			// Calculate the over-hit damage
			// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
			double overhitDmg = (getCurrentHp() - damage) * -1;
			if (overhitDmg <= 0)
			{
				setOverhitDamage(0);
				setOverhitAttacker(null);
			}
			else
			{
				setOverhitDamage(overhitDmg);
				setOverhitAttacker(attacker);
			}
		}

		// l2jexpress - We set that this player hit a monster. Used in the catpcha system to see if its fighting with mobs
		if (attacker != null && attacker.isPlayable())
			attacker.getPlayer().setLastMonsterDamageTime();

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	public void calculateRewards(Creature lastAttacker)
	{
		Creature topDamager = getAggroList().getTopDamager();
		if ((lastAttacker == null || !lastAttacker.isPlayable()) && getNpcId() != 22399)//Hardcoded Greater Evil
			lastAttacker = topDamager;

		if (lastAttacker == null || !lastAttacker.isPlayable())
			return;

		Player killer = lastAttacker.getPlayer();
		if (killer == null)
			return;

		Map<Playable, HateInfo> aggroMap = getAggroList().getPlayableMap();

		Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if (quests != null && quests.length > 0)
		{
			List<Player> players = null; // Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ� Ð¸Ð³Ñ€Ð¾ÐºÐ°Ð¼Ð¸, ÐºÐ¾Ñ‚Ð¾Ñ€Ñ‹Ðµ Ð¼Ð¾Ð³ÑƒÑ‚ Ð±Ñ‹Ñ‚ÑŒ Ð·Ð°Ð¸Ð½Ñ‚ÐµÑ€ÐµÑ�Ð¾Ð²Ð°Ð½Ñ‹ Ð² ÐºÐ²ÐµÑ�Ñ‚Ð°Ñ…
			if (isRaid() && Config.ALT_NO_LASTHIT) // Ð”Ð»Ñ� Ð°Ð»ÑŒÑ‚Ð° Ð½Ð° Ð»Ð°Ñ�Ñ‚Ñ…Ð¸Ñ‚ Ð±ÐµÑ€ÐµÐ¼ Ð²Ñ�ÐµÑ… Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð² Ð²Ð¾ÐºÑ€ÑƒÐ³
			{
				players = new ArrayList<Player>();
				for (Playable pl : aggroMap.keySet())
					if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						if (!players.contains(pl.getPlayer())) // Ð½Ðµ Ð´Ð¾Ð±Ð°Ð²Ð»Ñ�ÐµÐ¼ Ð´Ð²Ð°Ð¶Ð´Ñ‹ ÐµÑ�Ð»Ð¸ ÐµÑ�Ñ‚ÑŒ Ð¿ÐµÑ‚
							players.add(pl.getPlayer());
			}
			else if (killer.getParty() != null) // ÐµÑ�Ð»Ð¸ Ð¿Ð°Ñ‚Ð¸ Ñ‚Ð¾ Ñ�Ð¾Ð±Ð¸Ñ€Ð°ÐµÐ¼ Ð²Ñ�ÐµÑ… ÐºÑ‚Ð¾ Ð¿Ð¾Ð´Ñ…Ð¾Ð´Ð¸Ñ‚
			{
				players = new ArrayList<Player>(killer.getParty().size());
				for (Player pl : killer.getParty().getMembers())
					if (!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						players.add(pl);
			}

			for (Quest quest : quests)
			{
				Player toReward = killer;
				if (quest.getParty() != Quest.PARTY_NONE && players != null)
					if (isRaid() || quest.getParty() == Quest.PARTY_ALL) // ÐµÑ�Ð»Ð¸ Ñ†ÐµÐ»ÑŒ Ñ€ÐµÐ¹Ð´ Ð¸Ð»Ð¸ ÐºÐ²ÐµÑ�Ñ‚ Ð´Ð»Ñ� Ð²Ñ�ÐµÐ¹ Ð¿Ð°Ñ‚Ð¸ Ð½Ð°Ð³Ñ€Ð°Ð¶Ð´Ð°ÐµÐ¼ Ð²Ñ�ÐµÑ… ÑƒÑ‡Ð°Ñ�Ñ‚Ð½Ð¸ÐºÐ¾Ð²
					{
						for (Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if (qs != null && !qs.isCompleted())
								quest.notifyKill(this, qs);
						}
						toReward = null;
					}
					else
					{ // Ð¸Ð½Ð°Ñ‡Ðµ Ð²Ñ‹Ð±Ð¸Ñ€Ð°ÐµÐ¼ Ð¾Ð´Ð½Ð¾Ð³Ð¾
						List<Player> interested = new ArrayList<Player>(players.size());
						for (Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if (qs != null && !qs.isCompleted()) // Ð¸Ð· Ñ‚ÐµÑ…, Ñƒ ÐºÐ¾Ð³Ð¾ Ð²Ð·Ñ�Ñ‚ ÐºÐ²ÐµÑ�Ñ‚
								interested.add(pl);
						}

						if (interested.isEmpty())
							continue;

						toReward = interested.get(Rnd.get(interested.size()));
						if (toReward == null)
							toReward = killer;
					}

				if (toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest.getName());
					if (qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
			}
		}

		Map<Player, RewardInfo> rewards = new HashMap<Player, RewardInfo>();
		for (HateInfo info : aggroMap.values())
		{
			if (info.damage <= 1)
				continue;
			Playable attacker = (Playable) info.attacker;
			Player player = attacker.getPlayer();
			RewardInfo reward = rewards.get(player);
			if (reward == null)
				rewards.put(player, new RewardInfo(player, info.damage));
			else
				reward.addDamage(info.damage);
		}

		Player[] attackers = rewards.keySet().toArray(new Player[rewards.size()]);
		double[] xpsp = new double[2];

		for (Player attacker : attackers)
		{
			if (attacker.isDead())
				continue;

			RewardInfo reward = rewards.get(attacker);

			if (reward == null)
				continue;

			Party party = attacker.getParty();
			int maxHp = getMaxHp();

			xpsp[0] = 0.;
			xpsp[1] = 0.;

			if (party == null)
			{
				int damage = Math.min(reward._dmg, maxHp);
				if (damage > 0)
				{
					if (isInRangeZ(attacker, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						xpsp = calculateExpAndSp(attacker.getLevel(), damage);

					xpsp[0] = applyOverhit(killer, xpsp[0]);

					attacker.addExpAndCheckBonus(this, (long) xpsp[0], (long) xpsp[1], 1.);
				}
				rewards.remove(attacker);
			}
			else
			{
				int partyDmg = 0;
				int partylevel = 1;
				List<Player> rewardedMembers = new ArrayList<Player>();
				for (Player partyMember : party.getMembers())
				{
					RewardInfo ai = rewards.remove(partyMember);
					if (partyMember.isDead() || !isInRangeZ(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						continue;
					if (ai != null)
						partyDmg += ai._dmg;

					rewardedMembers.add(partyMember);
					if (partyMember.getLevel() > partylevel)
						partylevel = partyMember.getLevel();
				}
				partyDmg = Math.min(partyDmg, maxHp);
				if (partyDmg > 0)
				{
					xpsp = calculateExpAndSp(partylevel, partyDmg);
					double partyMul = (double) partyDmg / maxHp;
					xpsp[0] *= partyMul;
					xpsp[1] *= partyMul;
					xpsp[0] = applyOverhit(killer, xpsp[0]);
					party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
				}
			}
		}

		// Check the drop of a cursed weapon
		CursedWeaponsManager.getInstance().dropAttackable(this, killer);

		if (topDamager == null || !topDamager.isPlayable())
			return;

		for (Map.Entry<RewardType, RewardList> entry : getTemplate().getRewards().entrySet())
			rollRewards(entry, lastAttacker, topDamager);
	}

	@Override
	public void onRandomAnimation()
	{
		if (System.currentTimeMillis() - _lastSocialAction > 10000L)
		{
			broadcastPacket(new SocialAction(getObjectId(), 1));
			_lastSocialAction = System.currentTimeMillis();
		}
	}

	@Override
	public void startRandomAnimation()
	{
		//Ð£ Ð¼Ð¾Ð±Ð¾Ð² Ð°Ð½Ð¸Ð¼Ð°Ñ†Ð¸Ñ� Ð¾Ð±Ñ€Ð°Ð±Ð°Ñ‚Ñ‹Ð²Ð°ÐµÑ‚Ñ�Ñ� Ð² AI
	}

	@Override
	public int getKarma()
	{
		return 0;
	}

	public void addAbsorber(Player attacker)
	{
		// The attacker must not be null
		if (attacker == null)
			return;

		if (getCurrentHpPercents() > 50)
			return;

		absorbLock.lock();
		try
		{
			if (_absorbersIds == null)
				_absorbersIds = new TIntHashSet();

			_absorbersIds.add(attacker.getObjectId());
		}
		finally
		{
			absorbLock.unlock();
		}
	}

	public boolean isAbsorbed(Player player)
	{
		absorbLock.lock();
		try
		{
			if (_absorbersIds == null)
				return false;
			if (!_absorbersIds.contains(player.getObjectId()))
				return false;
		}
		finally
		{
			absorbLock.unlock();
		}
		return true;
	}

	public void clearAbsorbers()
	{
		absorbLock.lock();
		try
		{
			if (_absorbersIds != null)
				_absorbersIds.clear();
		}
		finally
		{
			absorbLock.unlock();
		}
	}

	public RewardItem takeHarvest()
	{
		harvestLock.lock();
		try
		{
			RewardItem harvest;
			harvest = _harvestItem;
			clearHarvest();
			return harvest;
		}
		finally
		{
			harvestLock.unlock();
		}
	}

	public void clearHarvest()
	{
		harvestLock.lock();
		try
		{
			_harvestItem = null;
			_altSeed = false;
			_seederId = 0;
			_isSeeded = false;
		}
		finally
		{
			harvestLock.unlock();
		}
	}

	public boolean setSeeded(Player player, int seedId, boolean altSeed)
	{
		harvestLock.lock();
		try
		{
			if (isSeeded())
				return false;
			_isSeeded = true;
			_altSeed = altSeed;
			_seederId = player.getObjectId();
			_harvestItem = new RewardItem(Manor.getInstance().getCropType(seedId));
			// ÐšÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð²Ñ�Ñ…Ð¾Ð´Ð¾Ð² Ð¾Ñ‚ xHP Ð´Ð¾ (xHP + xHP/2)
			if (getTemplate().rateHp > 1)
				_harvestItem.count = Rnd.get(Math.round(getTemplate().rateHp), Math.round(1.5 * getTemplate().rateHp));
		}
		finally
		{
			harvestLock.unlock();
		}

		return true;
	}

	public boolean isSeeded(Player player)
	{
		//Ð·Ð°Ñ�Ð¸Ð´ÐµÐ½ Ñ�Ñ‚Ð¸Ð¼ Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð¼, Ð¸ Ñ�Ð¼ÐµÑ€Ñ‚ÑŒ Ð½Ð°Ñ�Ñ‚ÑƒÐ¿Ð¸Ð»Ð° Ð½Ðµ Ð±Ð¾Ð»ÐµÐµ 20 Ñ�ÐµÐºÑƒÐ½Ð´ Ð½Ð°Ð·Ð°Ð´
		return isSeeded() && _seederId == player.getObjectId() && getDeadTime() < 20000L;
	}

	public boolean isSeeded()
	{
		return _isSeeded;
	}

	/**
	 * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
	 * @return
	 */
	public boolean isSpoiled()
	{
		return _isSpoiled;
	}

	public boolean isSpoiled(Player player)
	{
		if (!isSpoiled()) // ÐµÑ�Ð»Ð¸ Ð½Ðµ Ð·Ð°Ñ�Ð¿Ð¾Ð¹Ð»ÐµÐ½ Ñ‚Ð¾ false
			return false;

		//Ð·Ð°Ñ�Ð¿Ð¾Ð¹Ð»ÐµÐ½ Ñ�Ñ‚Ð¸Ð¼ Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð¼, Ð¸ Ñ�Ð¼ÐµÑ€Ñ‚ÑŒ Ð½Ð°Ñ�Ñ‚ÑƒÐ¿Ð¸Ð»Ð° Ð½Ðµ Ð±Ð¾Ð»ÐµÐµ 20 Ñ�ÐµÐºÑƒÐ½Ð´ Ð½Ð°Ð·Ð°Ð´
		if (getDeadTime() < 15000L)
		{
			if (player.getObjectId() == spoilerId)
				return true;
			else
				return false;
		}
		else
			return true;
	}

	/**
	 * Set the spoil state of this L2NpcInstance.<BR><BR>
	 * @param player
	 * @return
	 */
	public boolean setSpoiled(Player player)
	{
		sweepLock.lock();
		try
		{
			if (isSpoiled())
				return false;
			_isSpoiled = true;
			spoilerId = player.getObjectId();
		}
		finally
		{
			sweepLock.unlock();
		}
		return true;
	}

	/**
	 * Return True if a Dwarf use Sweep on the L2NpcInstance and if item can be spoiled.<BR><BR>
	 * @return
	 */
	public boolean isSweepActive()
	{
		sweepLock.lock();
		try
		{
			return _sweepItems != null && _sweepItems.size() > 0;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public List<RewardItem> takeSweep()
	{
		sweepLock.lock();
		try
		{
			List<RewardItem> sweep = _sweepItems;
			clearSweep();
			return sweep;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public void clearSweep()
	{
		sweepLock.lock();
		try
		{
			_isSpoiled = false;
			spoilerId = 0;
			_sweepItems = null;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public void rollRewards(Map.Entry<RewardType, RewardList> entry, Creature lastAttacker, Creature topDamager)
	{
		RewardType type = entry.getKey();
		RewardList list = entry.getValue();

		if (type == RewardType.SWEEP && !isSpoiled())
			return;

		final Creature activeChar = (type == RewardType.SWEEP ? lastAttacker : topDamager);
		final Player activePlayer = activeChar.getPlayer();

		if (activePlayer == null)
			return;

		final int diff = calculateLevelDiffForDrop(topDamager.getLevel());
		double mod = calcStat(Stats.REWARD_MULTIPLIER, 1., activeChar, null);
		mod *= Experience.penaltyModifier(diff, 9);

		List<RewardItem> rewardItems = list.roll(activePlayer, mod, this instanceof RaidBossInstance, isChampion());
		switch (type)
		{
			case SWEEP:
				_sweepItems = rewardItems;
				break;
			default:
				for (RewardItem drop : rewardItems)
				{
					// Ð•Ñ�Ð»Ð¸ Ð² Ð¼Ð¾Ð±Ð° Ð¿Ð¾Ñ�ÐµÑ�Ð½Ð¾ Ñ�ÐµÐ¼Ñ�, Ð¿Ñ€Ð¸Ñ‡ÐµÐ¼ Ð½Ðµ Ð°Ð»ÑŒÑ‚ÐµÑ€Ð½Ð°Ñ‚Ð¸Ð²Ð½Ð¾Ðµ - Ð½Ðµ Ð´Ð°Ð²Ð°Ñ‚ÑŒ Ð½Ð¸ÐºÐ°ÐºÐ¾Ð³Ð¾ Ð´Ñ€Ð¾Ð¿Ð°, ÐºÑ€Ð¾Ð¼Ðµ Ð°Ð´ÐµÐ½Ñ‹.
					if (isSeeded() && !_altSeed && !drop.isAdena)
						continue;
					dropItem(activePlayer, drop.itemId, drop.count, drop.enchantLvl);
				}
				break;
		}
	}

	private double[] calculateExpAndSp(int level, long damage)
	{
		int diff = level - getLevel();
		if (level > 77 && diff > 3 && diff <= 5) // kamael exp penalty
			diff += 3;

		double xp = getExpReward() * damage / getMaxHp();
		double sp = getSpReward() * damage / getMaxHp();

		if (diff > 5)
		{
			double mod = Math.pow(.83, diff - 5);
			xp *= mod;
			sp *= mod;
		}

		xp = Math.max(0., xp);
		sp = Math.max(0., sp);

		return new double[] { xp, sp };
	}

	private double applyOverhit(Player killer, double xp)
	{
		if (xp > 0 && killer.getObjectId() == overhitAttackerId)
		{
			int overHitExp = calculateOverhitExp(xp);
			killer.sendPacket(Msg.OVER_HIT, new SystemMessage(SystemMessage.ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT).addNumber(overHitExp));
			xp += overHitExp;
		}
		return xp;
	}

	@Override
	public void setOverhitAttacker(Creature attacker)
	{
		overhitAttackerId = attacker == null ? 0 : attacker.getObjectId();
	}

	public double getOverhitDamage()
	{
		return _overhitDamage;
	}

	@Override
	public void setOverhitDamage(double damage)
	{
		_overhitDamage = damage;
	}

	public int calculateOverhitExp(double normalExp)
	{
		double overhitPercentage = getOverhitDamage() * 100 / getMaxHp();
		if (overhitPercentage > 25)
			overhitPercentage = 25;
		double overhitExp = overhitPercentage / 100 * normalExp;
		setOverhitAttacker(null);
		setOverhitDamage(0);
		return (int) Math.round(overhitExp);
	}

	@Override
	public boolean isAggressive()
	{
		return (Config.ALT_CHAMPION_CAN_BE_AGGRO || getChampion() == 0) && super.isAggressive();
	}

	@Override
	public Faction getFaction()
	{
		return Config.ALT_CHAMPION_CAN_BE_SOCIAL || getChampion() == 0 ? super.getFaction() : Faction.NONE;
	}

	@Override
	public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		checkUD(attacker, i);
		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}

	private final double MIN_DISTANCE_FOR_USE_UD = 200.0;
	private final double MIN_DISTANCE_FOR_CANCEL_UD = 50.0;
	private final double UD_USE_CHANCE = 30.0;

	private void checkUD(Creature attacker, double damage)
	{
		if (getTemplate().baseAtkRange > MIN_DISTANCE_FOR_USE_UD || getLevel() < 20 || getLevel() > 78 || (attacker.getLevel() - getLevel()) > 9 || (getLevel() - attacker.getLevel()) > 9)
			return;

		if (isMinion() || getMinionList() != null || isRaid() || this instanceof ReflectionBossInstance || this instanceof ChestInstance || getChampion() > 0)
			return;

		int skillId = 5044;
		int skillLvl = 1;
		if (getLevel() >= 41 || getLevel() <= 60)
			skillLvl = 2;
		else if (getLevel() > 60)
			skillLvl = 3;

		double distance = getDistance(attacker);
		if (distance <= MIN_DISTANCE_FOR_CANCEL_UD)
		{
			if (getEffectList() != null && getEffectList().getEffectsBySkillId(skillId) != null)
				for (Effect e : getEffectList().getEffectsBySkillId(skillId))
					e.exit();
		}
		else if (distance >= MIN_DISTANCE_FOR_USE_UD)
		{
			double chance = UD_USE_CHANCE / (getMaxHp() / damage);
			if (Rnd.chance(chance))
			{
				Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
				if (skill != null)
					skill.getEffects(this, this, false, false);
			}
		}
	}

	@Override
	public boolean isMonster()
	{
		return true;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}

	@Override
	public boolean isInvul()
	{
		return _isInvul;
	}
}