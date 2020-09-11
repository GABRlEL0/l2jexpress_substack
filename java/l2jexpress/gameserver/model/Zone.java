package l2jexpress.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2jexpress.commons.collections.MultiValueSet;
import l2jexpress.commons.listener.Listener;
import l2jexpress.commons.listener.ListenerList;
import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.instancemanager.ReflectionManager;
import l2jexpress.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2jexpress.gameserver.model.base.Race;
import l2jexpress.gameserver.model.entity.Reflection;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.network.serverpackets.EventTrigger;
import l2jexpress.gameserver.network.serverpackets.L2GameServerPacket;
import l2jexpress.gameserver.network.serverpackets.Say2;
import l2jexpress.gameserver.network.serverpackets.SystemMessage2;
import l2jexpress.gameserver.network.serverpackets.components.ChatType;
import l2jexpress.gameserver.network.serverpackets.components.SystemMsg;
import l2jexpress.gameserver.stats.Stats;
import l2jexpress.gameserver.stats.funcs.FuncAdd;
import l2jexpress.gameserver.templates.ZoneTemplate;
import l2jexpress.gameserver.utils.Location;
import l2jexpress.gameserver.utils.PositionUtils;

public class Zone
{
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(Zone.class);

	public static final Zone[] EMPTY_L2ZONE_ARRAY = new Zone[0];

	public static enum ZoneType
	{
		AirshipController,

		SIEGE,
		RESIDENCE,
		HEADQUARTER,
		FISHING,
		UnderGroundColiseum,
		water,
		battle_zone,
		damage,
		instant_skill,
		mother_tree,
		peace_zone,
		poison,
		ssq_zone,
		swamp,
		no_escape,
		no_landing,
		no_restart,
		no_summon,
		dummy,
		offshore,
		epic,
		buff_store_only,
		fix_beleth,
		global_pvp_zone
		/*,
		auto_flag*/
	}

	public enum ZoneTarget
	{
		pc,
		npc,
		only_pc
	}

	public static final String BLOCKED_ACTION_PRIVATE_STORE = "open_private_store";
	public static final String BLOCKED_ACTION_PRIVATE_WORKSHOP = "open_private_workshop";
	public static final String BLOCKED_ACTION_DROP_MERCHANT_GUARD = "drop_merchant_guard";
	public static final String BLOCKED_ACTION_SAVE_BOOKMARK = "save_bookmark";
	public static final String BLOCKED_ACTION_USE_BOOKMARK = "use_bookmark";
	public static final String BLOCKED_ACTION_MINIMAP = "open_minimap";

	// Prims - Better implementation for zone effects and damage threads
	private Future<?> _effectThread = null;
	private Future<?> _damageThread = null;

	private final class SkillTimer implements Runnable
	{
		private final Skill _skill;
		private final int _zoneTime;
		private final int _randomTime;
		private long _activateTime = 0;

		protected SkillTimer()
		{
			_skill = getZoneSkill();
			_zoneTime = getTemplate().getUnitTick() * 1000;
			_randomTime = getTemplate().getRandomTick() * 1000;
		}

		@Override
		public void run()
		{
			if (!isActive())
				return;

			if (_skill == null)
				return;

			for (Creature target : getObjects())
			{
				if (target == null || target.isDead())
					continue;

				if (!checkTarget(target))
					continue;

				if (Rnd.chance(getTemplate().getSkillProb()) && !target.isDead())
				{
					_skill.getEffects(target, target, false, false);
				}
			}

			// TODO: This is not the same as in l2j, as we dont have on, off times, only unit ticks, so we use the same for both
			if (_activateTime == 0)
			{
				_activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
			}
			// If the zone is activated and over time, deactivate the zone and the next activation seteamos
			else if (isActive())
			{
				if (_activateTime < System.currentTimeMillis())
				{
					setActive(false);
					_activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
				}
			}
			// If the zone is disabled and the time and way, activate the area and seteamos the next deactivation
			else
			{
				if (_activateTime < System.currentTimeMillis())
				{
					setActive(true);
					_activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
				}
			}
		}
	}

	private final class DamageTimer implements Runnable
	{
		private final int _hp;
		private final int _mp;
		private final int _message;
		private final int _zoneTime;
		private final int _randomTime;
		private long _activateTime = 0;

		protected DamageTimer()
		{
			_hp = getDamageOnHP();
			_mp = getDamageOnMP();
			_message = getDamageMessageId();
			_zoneTime = getTemplate().getUnitTick() * 1000;
			_randomTime = getTemplate().getRandomTick() * 1000;
		}

		@Override
		public void run()
		{
			if (!isActive())
				return;

			if (_hp == 0 || _mp == 0)
				return;

			for (Creature target : getObjects())
			{
				if (target == null || target.isDead())
					continue;

				if (!checkTarget(target))
					continue;

				if (_hp > 0)
				{
					target.reduceCurrentHp(_hp, target, null, false, false, true, false, false, false, true);
					if (_message > 0)
						target.sendPacket(new SystemMessage2(SystemMsg.valueOf(_message)).addInteger(_hp));
				}

				if (_mp > 0)
				{
					target.reduceCurrentMp(_mp, null);
					if (_message > 0)
						target.sendPacket(new SystemMessage2(SystemMsg.valueOf(_message)).addInteger(_mp));
				}
			}

			// TODO: This is not the same as in l2j, as we dont have on, off times, only unit ticks, so we use the same for both
			if (_activateTime == 0)
			{
				_activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
			}
			else if (isActive())
			{
				if (_activateTime < System.currentTimeMillis())
				{
					setActive(false);
					_activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
				}
			}
			else
			{
				if (_activateTime < System.currentTimeMillis())
				{
					setActive(true);
					_activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
				}
			}
		}
	}

	public class ZoneListenerList extends ListenerList<Zone>
	{
		public void onEnter(Creature actor)
		{
			if (!getListeners().isEmpty())
				for (Listener<Zone> listener : getListeners())
					((OnZoneEnterLeaveListener) listener).onZoneEnter(Zone.this, actor);
		}

		public void onLeave(Creature actor)
		{
			if (!getListeners().isEmpty())
				for (Listener<Zone> listener : getListeners())
					((OnZoneEnterLeaveListener) listener).onZoneLeave(Zone.this, actor);
		}

		public void onEquipChanged(Creature actor)
		{
			if (!getListeners().isEmpty())
				for (Listener<Zone> listener : getListeners())
					((OnZoneEnterLeaveListener) listener).onEquipChanged(Zone.this, actor);
		}
	}

	private ZoneType _type;
	private boolean _active;
	private final MultiValueSet<String> _params;

	private final ZoneTemplate _template;

	private Reflection _reflection;

	private final ZoneListenerList listeners = new ZoneListenerList();

	private final List<Creature> _objects = new CopyOnWriteArrayList<Creature>();

	/**
	 * Ã�Å¾Ã‘â‚¬Ã�Â´Ã�ÂµÃ‘â‚¬ Ã�Â² Ã�Â·Ã�Â¾Ã�Â½Ã�Â°Ã‘â€¦, Ã‘ï¿½ Ã�Â½Ã�Â¸Ã�Â¼ Ã�Â¼Ã‘â€¹ Ã�Â¸ Ã�Â´Ã�Â¾Ã�Â±Ã�Â°Ã�Â²Ã�Â»Ã‘ï¿½Ã�ÂµÃ�Â¼/Ã‘Æ’Ã�Â±Ã�Â¸Ã‘â‚¬Ã�Â°Ã�ÂµÃ�Â¼ Ã‘ï¿½Ã‘â€šÃ�Â°Ã‘â€šÃ‘â€¹. TODO: Ã‘ï¿½Ã‘â‚¬Ã�Â°Ã�Â²Ã�Â½Ã�Â¸Ã‘â€šÃ‘Å’ Ã�Â¾Ã‘â‚¬Ã�Â´Ã�ÂµÃ‘â‚¬ Ã‘ï¿½ Ã�Â¾Ã‘â€žÃ‘â€žÃ�Â¾Ã�Â¼, Ã�Â¿Ã�Â¾Ã�ÂºÃ�Â° Ã�Â¾Ã‘â€š Ã‘â€žÃ�Â¾Ã�Â½Ã�Â°Ã‘â‚¬Ã‘ï¿½
	 */
	public final static int ZONE_STATS_ORDER = 0x40;

	public Zone(ZoneTemplate template)
	{
		this(template.getType(), template);
	}

	public Zone(ZoneType type, ZoneTemplate template)
	{
		_type = type;
		_template = template;
		_params = template.getParams();
	}

	public ZoneTemplate getTemplate()
	{
		return _template;
	}

	public final String getName()
	{
		return getTemplate().getName();
	}

	public ZoneType getType()
	{
		return _type;
	}

	public void setType(ZoneType type)
	{
		_type = type;
	}

	public Territory getTerritory()
	{
		return getTemplate().getTerritory();
	}

	public final int getEnteringMessageId()
	{
		return getTemplate().getEnteringMessageId();
	}

	public final int getLeavingMessageId()
	{
		return getTemplate().getLeavingMessageId();
	}

	public Skill getZoneSkill()
	{
		return getTemplate().getZoneSkill();
	}

	public ZoneTarget getZoneTarget()
	{
		return getTemplate().getZoneTarget();
	}

	public Race getAffectRace()
	{
		return getTemplate().getAffectRace();
	}

	/**
	 * Ã�ï¿½Ã�Â¾Ã�Â¼Ã�ÂµÃ‘â‚¬ Ã‘ï¿½Ã�Â¸Ã‘ï¿½Ã‘â€šÃ�ÂµÃ�Â¼Ã�Â½Ã�Â¾Ã�Â³Ã�Â¾ Ã�Â²Ã�Â¾Ã�Â¾Ã�Â±Ã‘â€°Ã�ÂµÃ�Â½Ã�Â¸Ã‘ï¿½ Ã�ÂºÃ�Â¾Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¾Ã�Âµ Ã�Â±Ã‘Æ’Ã�Â´Ã�ÂµÃ‘â€š Ã�Â¾Ã‘â€šÃ�Â¾Ã‘ï¿½Ã�Â»Ã�Â°Ã�Â½Ã�Â¾ Ã�Â¸Ã�Â³Ã‘â‚¬Ã�Â¾Ã�ÂºÃ‘Æ’ Ã�Â¿Ã‘â‚¬Ã�Â¸ Ã�Â½Ã�Â°Ã�Â½Ã�ÂµÃ‘ï¿½Ã�ÂµÃ�Â½Ã�Â¸Ã�Â¸ Ã‘Æ’Ã‘â‚¬Ã�Â¾Ã�Â½Ã�Â° Ã�Â·Ã�Â¾Ã�Â½Ã�Â¾Ã�Â¹
	 * @return SystemMessage ID
	 */
	public int getDamageMessageId()
	{
		return getTemplate().getDamageMessageId();
	}

	/**
	 * Ã�Â¡Ã�ÂºÃ�Â¾Ã�Â»Ã‘Å’Ã�ÂºÃ�Â¾ Ã‘Æ’Ã‘â‚¬Ã�Â¾Ã�Â½Ã�Â° Ã�Â·Ã�Â¾Ã�Â½Ã�Â° Ã�Â½Ã�Â°Ã�Â½Ã�ÂµÃ‘ï¿½Ã�ÂµÃ‘â€š Ã�Â¿Ã�Â¾ Ã‘â€¦Ã�Â¿
	 * @return Ã�ÂºÃ�Â¾Ã�Â»Ã�Â¸Ã‘â€¡Ã�ÂµÃ‘ï¿½Ã‘â€šÃ�Â²Ã�Â¾ Ã‘Æ’Ã‘â‚¬Ã�Â¾Ã�Â½Ã�Â°
	 */
	public int getDamageOnHP()
	{
		return getTemplate().getDamageOnHP();
	}

	/**
	 * Ã�Â¡Ã�ÂºÃ�Â¾Ã�Â»Ã‘Å’Ã�ÂºÃ�Â¾ Ã‘Æ’Ã‘â‚¬Ã�Â¾Ã�Â½Ã�Â° Ã�Â·Ã�Â¾Ã�Â½Ã�Â° Ã�Â½Ã�Â°Ã�Â½Ã�ÂµÃ‘ï¿½Ã�ÂµÃ‘â€š Ã�Â¿Ã�Â¾ Ã�Â¼Ã�Â¿
	 * @return Ã�ÂºÃ�Â¾Ã�Â»Ã�Â¸Ã‘â€¡Ã�ÂµÃ‘ï¿½Ã‘â€šÃ�Â²Ã�Â¾ Ã‘Æ’Ã‘â‚¬Ã�Â¾Ã�Â½Ã�Â°
	 */
	public int getDamageOnMP()
	{
		return getTemplate().getDamageOnMP();
	}

	/**
	 * @return Ã�â€˜Ã�Â¾Ã�Â½Ã‘Æ’Ã‘ï¿½ Ã�Âº Ã‘ï¿½Ã�ÂºÃ�Â¾Ã‘â‚¬Ã�Â¾Ã‘ï¿½Ã‘â€šÃ�Â¸ Ã�Â´Ã�Â²Ã�Â¸Ã�Â¶Ã�ÂµÃ�Â½Ã�Â¸Ã‘ï¿½ Ã�Â² Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ
	 */
	public double getMoveBonus()
	{
		return getTemplate().getMoveBonus();
	}

	/**
	 * Ã�â€™Ã�Â¾Ã�Â·Ã�Â²Ã‘â‚¬Ã�Â°Ã‘â€°Ã�Â°Ã�ÂµÃ‘â€š Ã�Â±Ã�Â¾Ã�Â½Ã‘Æ’Ã‘ï¿½ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�ÂµÃ�Â½Ã�ÂµÃ‘â‚¬Ã�Â°Ã‘â€ Ã�Â¸Ã�Â¸ Ã‘â€¦Ã�Â¿ Ã�Â² Ã‘ï¿½Ã‘â€šÃ�Â¾Ã�Â¹ Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ
	 * @return Ã�â€˜Ã�Â¾Ã�Â½Ã‘Æ’Ã‘ï¿½ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�ÂµÃ�Â½Ã�Â°Ã‘â‚¬Ã�Â°Ã‘â€ Ã�Â¸Ã�Â¸ Ã‘â€¦Ã�Â¿ Ã�Â² Ã‘ï¿½Ã‘â€šÃ�Â¾Ã�Â¹ Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ
	 */
	public double getRegenBonusHP()
	{
		return getTemplate().getRegenBonusHP();
	}

	/**
	 * Ã�â€™Ã�Â¾Ã�Â·Ã�Â²Ã‘â‚¬Ã�Â°Ã‘â€°Ã�Â°Ã�ÂµÃ‘â€š Ã�Â±Ã�Â¾Ã�Â½Ã‘Æ’Ã‘ï¿½ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�ÂµÃ�Â½Ã�ÂµÃ‘â‚¬Ã�Â°Ã‘â€ Ã�Â¸Ã�Â¸ Ã�Â¼Ã�Â¿ Ã�Â² Ã‘ï¿½Ã‘â€šÃ�Â¾Ã�Â¹ Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ
	 * @return Ã�â€˜Ã�Â¾Ã�Â½Ã‘Æ’Ã‘ï¿½ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�ÂµÃ�Â½Ã�Â°Ã‘â‚¬Ã�Â°Ã‘â€ Ã�Â¸Ã�Â¸ Ã�Â¼Ã�Â¿ Ã�Â² Ã‘ï¿½Ã‘â€šÃ�Â¾Ã�Â¹ Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ
	 */
	public double getRegenBonusMP()
	{
		return getTemplate().getRegenBonusMP();
	}

	public long getRestartTime()
	{
		return getTemplate().getRestartTime();
	}

	public List<Location> getRestartPoints()
	{
		return getTemplate().getRestartPoints();
	}

	public List<Location> getPKRestartPoints()
	{
		return getTemplate().getPKRestartPoints();
	}

	public Location getSpawn()
	{
		if (getRestartPoints() == null)
			return null;
		Location loc = getRestartPoints().get(Rnd.get(getRestartPoints().size()));
		return loc.clone();
	}

	public Location getPKSpawn()
	{
		if (getPKRestartPoints() == null)
			return getSpawn();
		Location loc = getPKRestartPoints().get(Rnd.get(getPKRestartPoints().size()));
		return loc.clone();
	}

	/**
	 * Ã�Å¸Ã‘â‚¬Ã�Â¾Ã�Â²Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�ÂµÃ‘â€š Ã�Â½Ã�Â°Ã‘â€¦Ã�Â¾Ã�Â´Ã‘ï¿½Ã‘â€šÃ‘ï¿½Ã‘ï¿½ Ã�Â»Ã�Â¸ Ã�Â´Ã�Â°Ã�Â½Ã‘â€¹Ã�Âµ Ã�ÂºÃ�Â¾Ã�Â¾Ã‘â‚¬Ã�Â´Ã�Â¸Ã�Â½Ã�Â°Ã‘â€šÃ‘â€¹ Ã�Â² Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ. _loc - Ã‘ï¿½Ã‘â€šÃ�Â°Ã�Â½Ã�Â´Ã�Â°Ã‘â‚¬Ã‘â€šÃ�Â½Ã�Â°Ã‘ï¿½ Ã‘â€šÃ�ÂµÃ‘â‚¬Ã‘â‚¬Ã�Â¸Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¸Ã‘ï¿½ Ã�Â´Ã�Â»Ã‘ï¿½ Ã�Â·Ã�Â¾Ã�Â½Ã‘â€¹
	 * @param x Ã�ÂºÃ�Â¾Ã�Â¾Ã‘â‚¬Ã�Â´Ã�Â¸Ã�Â½Ã�Â°Ã‘â€šÃ�Â°
	 * @param y Ã�ÂºÃ�Â¾Ã�Â¾Ã‘â‚¬Ã�Â´Ã�Â¸Ã�Â½Ã�Â°Ã‘â€šÃ�Â°
	 * @return Ã�Â½Ã�Â°Ã‘â€¦Ã�Â¾Ã�Â´Ã‘ï¿½Ã‘â€šÃ‘ï¿½Ã‘ï¿½ Ã�Â»Ã�Â¸ Ã�ÂºÃ�Â¾Ã�Â¾Ã‘â‚¬Ã�Â´Ã�Â¸Ã�Â½Ã�Â°Ã‘â€šÃ‘â€¹ Ã�Â² Ã�Â»Ã�Â¾Ã�ÂºÃ�Â°Ã‘â€ Ã�Â¸Ã�Â¸
	 */
	public boolean checkIfInZone(int x, int y)
	{
		return getTerritory().isInside(x, y);
	}

	public boolean checkIfInZone(int x, int y, int z)
	{
		return checkIfInZone(x, y, z, getReflection());
	}

	public boolean checkIfInZone(int x, int y, int z, Reflection reflection)
	{
		return isActive() && _reflection == reflection && getTerritory().isInside(x, y, z);
	}

	public boolean checkIfInZone(Creature cha)
	{
		return _objects.contains(cha);
	}

	public final double findDistanceToZone(GameObject obj, boolean includeZAxis)
	{
		return findDistanceToZone(obj.getX(), obj.getY(), obj.getZ(), includeZAxis);
	}

	public final double findDistanceToZone(int x, int y, int z, boolean includeZAxis)
	{
		return PositionUtils.calculateDistance(x, y, z, (getTerritory().getXmax() + getTerritory().getXmin()) / 2, (getTerritory().getYmax() + getTerritory().getYmin()) / 2, (getTerritory().getZmax() + getTerritory().getZmin()) / 2, includeZAxis);
	}

	/**
	 * Ã�Å¾Ã�Â±Ã‘â‚¬Ã�Â°Ã�Â±Ã�Â¾Ã‘â€šÃ�ÂºÃ�Â° Ã�Â²Ã‘â€¦Ã�Â¾Ã�Â´Ã�Â° Ã�Â² Ã‘â€šÃ�ÂµÃ‘â‚¬Ã‘â‚¬Ã�Â¸Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¸Ã‘Å½ Ã�Å¸Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�Â¾Ã�Â½Ã�Â°Ã�Â¶ Ã�Â²Ã‘ï¿½Ã�ÂµÃ�Â³Ã�Â´Ã�Â° Ã�Â´Ã�Â¾Ã�Â±Ã�Â°Ã�Â²Ã�Â»Ã‘ï¿½Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½ Ã�Â² Ã‘ï¿½Ã�Â¿Ã�Â¸Ã‘ï¿½Ã�Â¾Ã�Âº Ã�Â²Ã�Â½Ã�Âµ Ã�Â·Ã�Â°Ã�Â²Ã�Â¸Ã‘ï¿½Ã�Â¸Ã�Â¼Ã�Â¾Ã‘ï¿½Ã‘â€šÃ�Â¸ Ã�Â¾Ã‘â€š Ã�Â°Ã�ÂºÃ‘â€šÃ�Â¸Ã�Â²Ã�Â½Ã�Â¾Ã‘ï¿½Ã‘â€šÃ�Â¸ Ã‘â€šÃ�ÂµÃ‘â‚¬Ã‘â‚¬Ã�Â¸Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¸Ã�Â¸. Ã�â€¢Ã‘ï¿½Ã�Â»Ã�Â¸ Ã�Â·Ã�Â¾Ã�Â½Ã�Â° Ã�Â°Ã�ÂºÃ�Â¸Ã�Â²Ã�Â½Ã�Â°Ã‘ï¿½, Ã‘â€šÃ�Â¾ Ã�Â¾Ã�Â±Ã‘â‚¬Ã�Â°Ã�Â±Ã�Â¾Ã‘â€šÃ�Â°Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½ Ã�Â²Ã‘â€¦Ã�Â¾Ã�Â´ Ã�Â² Ã�Â·Ã�Â¾Ã�Â½Ã‘Æ’
	 * @param cha Ã�ÂºÃ‘â€šÃ�Â¾ Ã�Â²Ã‘â€¦Ã�Â¾Ã�Â´Ã�Â¸Ã‘â€š
	 */
	public void doEnter(Creature cha)
	{
		boolean added = false;

		if (!_objects.contains(cha))
			added = _objects.add(cha);

		if (added)
			onZoneEnter(cha);

		if ((cha != null) && (cha.isPlayer()) && (cha.getPlayer().isGM()))
		{
			cha.sendMessage("Entered the zone " + getName());
		}
	}

	/**
	 * Log processing zone
	 * @param actor who enters
	 */
	protected void onZoneEnter(Creature actor)
	{
		checkEffects(actor, true);
		addZoneStats(actor);

		if (actor.isPlayer())
		{
			if (getType() == ZoneType.buff_store_only)
				actor.sendPacket(new Say2(0, ChatType.BATTLEFIELD, Config.SERVER_NAME, "You are now in a offline buff store area!"));
			if (getType() == ZoneType.epic)
				actor.sendPacket(new Say2(0, ChatType.BATTLEFIELD, Config.SERVER_NAME, "You are now in a PvP Area!"));
			if (getEnteringMessageId() != 0)
				actor.sendPacket(new SystemMessage2(SystemMsg.valueOf(getEnteringMessageId())));
			if (getTemplate().getEventId() != 0)
				actor.sendPacket(new EventTrigger(getTemplate().getEventId(), true));
			if (getTemplate().getBlockedActions() != null)
				actor.getPlayer().blockActions(getTemplate().getBlockedActions());
			if (getType() == ZoneType.fix_beleth)
			{
				actor.getPlayer().sendMessage("Anti-beleth exploit");
				actor.getPlayer().teleToClosestTown();
				return;
			}

			// l2jexpress - Solo puede haber 2 pjs por hwid en una zona epica. Si otro trata de entrar, es kickeado. En queen ant solo permitimos 1. No se activa en instancias
			final int maxHwidCount = (getName().equalsIgnoreCase("queen_ant_epic") ? 1 : 2);
			if (getType() == ZoneType.epic && (getReflection() == null || getReflection() == ReflectionManager.DEFAULT))
			{
				int hwidCount = 0;
				for (Creature obj : getObjects())
				{
					if (obj == null || !obj.isPlayer())
						continue;

					if (obj.getPlayer().getHWID().equals(actor.getPlayer().getHWID()))
						hwidCount++;
				}

				if (hwidCount >= maxHwidCount)
				{
					actor.getPlayer().sendMessage("You have been kicked out of the zone because you already have " + maxHwidCount + " character(s) inside");
					actor.getPlayer().teleToClosestTown();
					return;
				}
			}
		}

		listeners.onEnter(actor);
		actor.getListeners().onZoneEnter(this);
	}

	/**
	 * Processing exit zone Object always removed from the list, regardless of the zone If the active zone, it is parsed out of the zone
	 * @param cha
	 */
	public void doLeave(Creature cha)
	{
		boolean removed = false;

		removed = _objects.remove(cha);

		if (removed)
			onZoneLeave(cha);

		if ((cha != null) && (cha.isPlayer()) && (cha.getPlayer().isGM()))
		{
			cha.sendMessage("Left the area " + getName());
		}
	}

	/**
	 * Processing exit zone
	 * @param actor who goes out
	 */
	protected void onZoneLeave(Creature actor)
	{
		checkEffects(actor, false);
		removeZoneStats(actor);

		if (actor.isPlayer())
		{
			if (getType() == ZoneType.buff_store_only)
				actor.sendPacket(new Say2(0, ChatType.BATTLEFIELD, Config.SERVER_NAME, "You have left the offline buff store area!"));
			if (getType() == ZoneType.epic)
				actor.sendPacket(new Say2(0, ChatType.BATTLEFIELD, Config.SERVER_NAME, "You have left the PvP area!"));
			if (getLeavingMessageId() != 0 && actor.isPlayer())
				actor.sendPacket(new SystemMessage2(SystemMsg.valueOf(getLeavingMessageId())));
			if (getTemplate().getEventId() != 0 && actor.isPlayer())
				actor.sendPacket(new EventTrigger(getTemplate().getEventId(), false));
			if (getTemplate().getBlockedActions() != null)
				((Player) actor).unblockActions(getTemplate().getBlockedActions());
		}

		listeners.onLeave(actor);
		actor.getListeners().onZoneLeave(this);
	}

	/**
	 * l2jexpress - Custom listener to check equipment changes inside a zone
	 *
	 * @param actor
	 */
	public void onEquipChanged(Creature actor)
	{
		listeners.onEquipChanged(actor);
	}

	/**
	 * Ã�â€�Ã�Â¾Ã�Â±Ã�Â°Ã�Â²Ã�Â»Ã‘ï¿½Ã�ÂµÃ‘â€š Ã‘ï¿½Ã‘â€šÃ�Â°Ã‘â€šÃ‘â€¹ Ã�Â·Ã�Â¾Ã�Â½Ã�Âµ
	 * @param cha Ã�Â¿Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�Â¾Ã�Â½Ã�Â°Ã�Â¶ Ã�ÂºÃ�Â¾Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¾Ã�Â¼Ã‘Æ’ Ã�Â´Ã�Â¾Ã�Â±Ã�Â°Ã�Â²Ã�Â»Ã‘ï¿½Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½
	 */
	private void addZoneStats(Creature cha)
	{
		// Ã�Å¸Ã‘â‚¬Ã�Â¾Ã�Â²Ã�ÂµÃ‘â‚¬Ã�ÂºÃ�Â° Ã‘â€ Ã�ÂµÃ�Â»Ã�Â¸
		if (!checkTarget(cha))
			return;

		// Ã�Â¡Ã�ÂºÃ�Â¾Ã‘â‚¬Ã�Â¾Ã‘ï¿½Ã‘â€šÃ‘Å’ Ã�Â´Ã�Â²Ã�Â¸Ã�Â¶Ã�ÂµÃ�Â½Ã�Â¸Ã‘ï¿½ Ã�Â½Ã�Â°Ã�ÂºÃ�Â»Ã�Â°Ã�Â´Ã‘â€¹Ã�Â²Ã�Â°Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½ Ã‘â€šÃ�Â¾Ã�Â»Ã‘Å’Ã�ÂºÃ�Â¾ Ã�Â½Ã�Â° L2Playable
		// affectRace Ã�Â² Ã�Â±Ã�Â°Ã�Â·Ã�Âµ Ã�Â½Ã�Âµ Ã‘Æ’Ã�ÂºÃ�Â°Ã�Â·Ã�Â°Ã�Â½, Ã�ÂµÃ‘ï¿½Ã�Â»Ã�Â¸ Ã�Â½Ã�Â°Ã�Â´Ã�Â¾ Ã�Â±Ã‘Æ’Ã�Â´Ã�ÂµÃ‘â€š Ã�Â²Ã�Â»Ã�Â¸Ã‘ï¿½Ã�Â½Ã�Â¸Ã�Âµ, Ã‘â€šÃ�Â¾ Ã�Â¿Ã�Â¾Ã�Â¿Ã‘â‚¬Ã�Â°Ã�Â²Ã�Â¸Ã�Â¼
		if (getMoveBonus() != 0)
			if (cha.isPlayable())
			{
				cha.addStatFunc(new FuncAdd(Stats.RUN_SPEED, ZONE_STATS_ORDER, this, getMoveBonus()));
				cha.sendChanges();
			}

		// Ã�â€¢Ã‘ï¿½Ã�Â»Ã�Â¸ Ã‘Æ’ Ã�Â½Ã�Â°Ã‘ï¿½ Ã�ÂµÃ‘ï¿½Ã‘â€šÃ‘Å’ Ã‘â€¡Ã‘â€šÃ�Â¾ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�ÂµÃ�Â½Ã�Â¸Ã‘â€šÃ‘Å’
		if (getRegenBonusHP() != 0)
			cha.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, ZONE_STATS_ORDER, this, getRegenBonusHP()));

		// Ã�â€¢Ã‘ï¿½Ã�Â»Ã�Â¸ Ã‘Æ’ Ã�Â½Ã�Â°Ã‘ï¿½ Ã�ÂµÃ‘ï¿½Ã‘â€šÃ‘Å’ Ã‘â€¡Ã‘â€šÃ�Â¾ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�ÂµÃ�Â½Ã�Â¸Ã‘â€šÃ‘Å’
		if (getRegenBonusMP() != 0)
			cha.addStatFunc(new FuncAdd(Stats.REGENERATE_MP_RATE, ZONE_STATS_ORDER, this, getRegenBonusMP()));
	}

	/**
	 * Ã�Â£Ã�Â±Ã�Â¸Ã‘â‚¬Ã�Â°Ã�ÂµÃ‘â€š Ã�Â´Ã�Â¾Ã�Â±Ã�Â°Ã�Â²Ã�Â»Ã�ÂµÃ�Â½Ã‘â€¹Ã�Âµ Ã�Â·Ã�Â¾Ã�Â½Ã�Â¾Ã�Â¹ Ã‘ï¿½Ã‘â€šÃ�Â°Ã‘â€šÃ‘â€¹
	 * @param cha Ã�Â¿Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�Â¾Ã�Â½Ã�Â°Ã�Â¶ Ã‘Æ’ Ã�ÂºÃ�Â¾Ã‘â€šÃ�Â¾Ã‘â‚¬Ã�Â¾Ã�Â³Ã�Â¾ Ã‘Æ’Ã�Â±Ã�Â¸Ã‘â‚¬Ã�Â°Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½
	 */
	private void removeZoneStats(Creature cha)
	{
		if (getRegenBonusHP() == 0 && getRegenBonusMP() == 0 && getMoveBonus() == 0)
			return;

		cha.removeStatsOwner(this);

		cha.sendChanges();
	}

	/**
	 * Ã�Å¸Ã‘â‚¬Ã�Â¸Ã�Â¼Ã�ÂµÃ�Â½Ã‘ï¿½Ã�ÂµÃ‘â€š Ã‘ï¿½Ã‘â€žÃ‘â€žÃ�ÂµÃ�ÂºÃ‘â€šÃ‘â€¹ Ã�Â¿Ã‘â‚¬Ã�Â¸ Ã�Â²Ã‘â€¦Ã�Â¾Ã�Â´Ã�Âµ/Ã�Â²Ã‘â€¹Ã‘â€¦Ã�Â¾Ã�Â´Ã�Âµ Ã�Â¸Ã�Â·(Ã�Â²) Ã�Â·Ã�Â¾Ã�Â½Ã‘Æ’
	 * @param cha Ã�Â¾Ã�Â±Ã‘Å’Ã�ÂµÃ�ÂºÃ‘â€š
	 * @param enter Ã�Â²Ã�Â¾Ã‘Ë†Ã�ÂµÃ�Â» Ã�Â¸Ã�Â»Ã�Â¸ Ã�Â²Ã‘â€¹Ã‘Ë†Ã�ÂµÃ�Â»
	 */
	private void checkEffects(Creature cha, boolean enter)
	{
		if (checkTarget(cha))
		{
			// Prims - New implementation of zone effect and damage threads
			if (enter)
			{
				if (getZoneSkill() != null)
				{
					if (_effectThread == null)
					{
						synchronized (this)
						{
							if (_effectThread == null)
							{
								// TODO: Reuse 30 hardcoded
								_effectThread = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SkillTimer(), getTemplate().getInitialDelay(), 30000);
							}
						}
					}
				}
				else if (getDamageOnHP() > 0 || getDamageOnHP() > 0)
				{
					if (_damageThread == null)
					{
						synchronized (this)
						{
							if (_damageThread == null)
							{
								// TODO: Reuse 30 hardcoded
								_damageThread = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DamageTimer(), getTemplate().getInitialDelay(), 30000);
							}
						}
					}
				}
			}
			else
			{
				if (getZoneSkill() != null)
					cha.getEffectList().stopEffect(getZoneSkill());
			}
		}
	}

	/**
	 * Ã�Å¸Ã‘â‚¬Ã�Â¾Ã�Â²Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�ÂµÃ‘â€š Ã�Â¿Ã�Â¾Ã�Â´Ã‘â€¦Ã�Â¾Ã�Â´Ã�Â¸Ã‘â€š Ã�Â»Ã�Â¸ Ã�Â¿Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�Â¾Ã�Â½Ã�Â°Ã�Â¶ Ã�Â´Ã�Â»Ã‘ï¿½ Ã�Â²Ã‘â€¹Ã�Â·Ã�Â²Ã�Â°Ã�Â²Ã‘Ë†Ã�ÂµÃ�Â³Ã�Â¾ Ã�Â´Ã�ÂµÃ�Â¹Ã‘ï¿½Ã‘â€šÃ�Â²Ã�Â¸Ã‘ï¿½
	 * @param cha Ã�Â¿Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�Â¾Ã�Â½Ã�Â°Ã�Â¶
	 * @return Ã�Â¿Ã�Â¾Ã�Â´Ã�Â¾Ã‘Ë†Ã�ÂµÃ�Â» Ã�Â»Ã�Â¸
	 */
	protected boolean checkTarget(Creature cha)
	{
		switch (getZoneTarget())
		{
			case pc:
				if (!cha.isPlayable())
					return false;
				break;
			case only_pc:
				if (!cha.isPlayer())
					return false;
				break;
			case npc:
				if (!cha.isNpc())
					return false;
				break;
		}

		// Ã�â€¢Ã‘ï¿½Ã�Â»Ã�Â¸ Ã‘Æ’ Ã�Â½Ã�Â°Ã‘ï¿½ Ã‘â‚¬Ã�Â°Ã‘ï¿½Ã�Â° Ã�Â½Ã�Âµ "all"
		if (getAffectRace() != null)
		{
			Player player = cha.getPlayer();
			// Ã�ÂµÃ‘ï¿½Ã�Â»Ã�Â¸ Ã�Â½Ã�Âµ Ã�Â¸Ã�Â³Ã‘â‚¬Ã�Â¾Ã�Â²Ã�Â¾Ã�Â¹ Ã�Â¿Ã�ÂµÃ‘â‚¬Ã‘ï¿½Ã�Â¾Ã�Â½Ã�Â°Ã�Â¶
			if (player == null)
				return false;
			// Ã�ÂµÃ‘ï¿½Ã�Â»Ã�Â¸ Ã‘â‚¬Ã�Â°Ã‘ï¿½Ã�Â° Ã�Â½Ã�Âµ Ã�Â¿Ã�Â¾Ã�Â´Ã‘â€¦Ã�Â¾Ã�Â´Ã�Â¸Ã‘â€š
			if (player.getRace() != getAffectRace())
				return false;
		}

		return true;
	}

	public Creature[] getObjects()
	{
		return _objects.toArray(new Creature[_objects.size()]);
	}

	public List<Player> getInsidePlayers()
	{
		final List<Player> result = new ArrayList<Player>();

		Creature cha;
		for (int i = 0; i < _objects.size(); i++)
		{
			if ((cha = _objects.get(i)) != null && cha.isPlayer())
				result.add((Player) cha);
		}

		return result;
	}

	public List<Playable> getInsidePlayables()
	{
		final List<Playable> result = new ArrayList<Playable>();

		Creature cha;
		for (int i = 0; i < _objects.size(); i++)
		{
			if ((cha = _objects.get(i)) != null && cha.isPlayable())
				result.add((Playable) cha);
		}

		return result;
	}

	public List<NpcInstance> getInsideNpcs()
	{
		List<NpcInstance> result = new ArrayList<NpcInstance>();

		Creature cha;
		for (int i = 0; i < _objects.size(); i++)
		{
			if ((cha = _objects.get(i)) != null && cha.isNpc())
				result.add((NpcInstance) cha);
		}

		return result;
	}

	/**
	 * Ã�Â£Ã‘ï¿½Ã‘â€šÃ�Â°Ã�Â½Ã�Â¾Ã�Â²Ã�ÂºÃ�Â° Ã�Â°Ã�ÂºÃ‘â€šÃ�Â¸Ã�Â²Ã�Â½Ã�Â¾Ã‘ï¿½Ã‘â€šÃ�Â¸ Ã�Â·Ã�Â¾Ã�Â½Ã‘â€¹. Ã�Å¸Ã‘â‚¬Ã�Â¸ Ã‘Æ’Ã‘ï¿½Ã‘â€šÃ�Â°Ã�Â½Ã�Â¾Ã�Â²Ã�ÂºÃ�Â¸ Ã‘â€žÃ�Â»Ã�Â°Ã�Â³Ã�Â° Ã�Â°Ã�ÂºÃ‘â€šÃ�Â¸Ã�Â²Ã�Â½Ã�Â¾Ã‘ï¿½Ã‘â€šÃ�Â¸, Ã�Â·Ã�Â¾Ã�Â½Ã�Â° Ã�Â´Ã�Â¾Ã�Â±Ã�Â°Ã�Â²Ã�Â»Ã‘ï¿½Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½ Ã�Â² Ã‘ï¿½Ã�Â¾Ã�Â¾Ã‘â€šÃ�Â²Ã�ÂµÃ‘ï¿½Ã‘â€šÃ�Â²Ã‘Æ’Ã‘Å½Ã‘â€°Ã�Â¸Ã�Âµ Ã‘â‚¬Ã�ÂµÃ�Â³Ã�Â¸Ã�Â¾Ã�Â½Ã‘â€¹. Ã�â€™ Ã‘ï¿½Ã�Â»Ã‘Æ’Ã‘â€¡Ã�Â°Ã�Âµ Ã‘ï¿½Ã�Â±Ã‘â‚¬Ã�Â¾Ã‘ï¿½Ã�Â° - Ã‘Æ’Ã�Â´Ã�Â°Ã�Â»Ã‘ï¿½Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½.
	 * @param value Ã�Â°Ã�ÂºÃ‘â€šÃ�Â¸Ã�Â²Ã�Â½Ã�Â° Ã�Â»Ã�Â¸ Ã�Â·Ã�Â¾Ã�Â½Ã�Â°
	 */
	public void setActive(boolean value)
	{
		if (_active == value)
			return;

		_active = value;

		if (isActive())
			World.addZone(Zone.this);
		else
			World.removeZone(Zone.this);
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setReflection(Reflection reflection)
	{
		_reflection = reflection;
	}

	public Reflection getReflection()
	{
		return _reflection;
	}

	public void setParam(String name, String value)
	{
		_params.put(name, value);
	}

	public void setParam(String name, Object value)
	{
		_params.put(name, value);
	}

	public MultiValueSet<String> getParams()
	{
		return _params;
	}

	public <T extends Listener<Zone>> boolean addListener(T listener)
	{
		return listeners.add(listener);
	}

	public <T extends Listener<Zone>> boolean removeListener(T listener)
	{
		return listeners.remove(listener);
	}

	@Override
	public final String toString()
	{
		return "[Zone " + getType() + " name: " + getName() + "]";
	}

	public void broadcastPacket(L2GameServerPacket packet, boolean toAliveOnly)
	{
		List<Player> insideZoners = getInsidePlayers();

		if (insideZoners != null && !insideZoners.isEmpty())
			for (Player player : insideZoners)
				if (toAliveOnly)
				{
					if (!player.isDead())
						player.broadcastPacket(packet);
				}
				else
					player.broadcastPacket(packet);
	}
}