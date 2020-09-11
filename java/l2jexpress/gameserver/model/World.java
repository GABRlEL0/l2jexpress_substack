package l2jexpress.gameserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.model.Zone.ZoneType;
import l2jexpress.gameserver.model.entity.Reflection;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.network.serverpackets.L2GameServerPacket;
import l2jexpress.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class World
{
	private static final Logger _log = LoggerFactory.getLogger(World.class);

	/** Map dimensions */
	public static final int MAP_MIN_X = Config.GEO_X_FIRST - 20 << 15;
	public static final int MAP_MAX_X = (Config.GEO_X_LAST - 20 + 1 << 15) - 1;
	public static final int MAP_MIN_Y = Config.GEO_Y_FIRST - 18 << 15;
	public static final int MAP_MAX_Y = (Config.GEO_Y_LAST - 18 + 1 << 15) - 1;
	public static final int MAP_MIN_Z = Config.MAP_MIN_Z;
	public static final int MAP_MAX_Z = Config.MAP_MAX_Z;

	public static final int WORLD_SIZE_X = Config.GEO_X_LAST - Config.GEO_X_FIRST + 1;
	public static final int WORLD_SIZE_Y = Config.GEO_Y_LAST - Config.GEO_Y_FIRST + 1;

	public static final int SHIFT_BY = Config.SHIFT_BY;
	public static final int SHIFT_BY_Z = Config.SHIFT_BY_Z;

	/** calculated offset used so top left region is 0,0 */
	public static final int OFFSET_X = Math.abs(MAP_MIN_X >> SHIFT_BY);
	public static final int OFFSET_Y = Math.abs(MAP_MIN_Y >> SHIFT_BY);
	public static final int OFFSET_Z = Math.abs(MAP_MIN_Z >> SHIFT_BY_Z);

	/** Ð Ð°Ð·Ð¼ÐµÑ€Ð½Ð¾Ñ�Ñ‚ÑŒ Ð¼Ð°Ñ�Ñ�Ð¸Ð²Ð° Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð² */
	private static final int REGIONS_X = (MAP_MAX_X >> SHIFT_BY) + OFFSET_X;
	private static final int REGIONS_Y = (MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y;
	private static final int REGIONS_Z = (MAP_MAX_Z >> SHIFT_BY_Z) + OFFSET_Z;

	private static volatile WorldRegion[][][] _worldRegions = new WorldRegion[REGIONS_X + 1][REGIONS_Y + 1][REGIONS_Z + 1];
	private static native void prepareWorld();
	
	public static Map<String, Integer> ipPlayers = new LinkedHashMap<String, Integer>();
	public static Map<Player, String> nameIp = new LinkedHashMap<Player, String>();

	public static void init()
	{
		_log.info("L2World: Creating regions: [" + (REGIONS_X + 1) + "][" + (REGIONS_Y + 1) + "][" + (REGIONS_Z + 1) + "].");
	}

	private static WorldRegion[][][] getRegions()
	{
		return _worldRegions;
	}

	private static int validX(int x)
	{
		if (x < 0)
			x = 0;
		else if (x > REGIONS_X)
			x = REGIONS_X;
		return x;
	}

	private static int validY(int y)
	{
		if (y < 0)
			y = 0;
		else if (y > REGIONS_Y)
			y = REGIONS_Y;
		return y;
	}

	private static int validZ(int z)
	{
		if (z < 0)
			z = 0;
		else if (z > REGIONS_Z)
			z = REGIONS_Z;
		return z;
	}

	public static int validCoordX(int x)
	{
		if (x < MAP_MIN_X)
			x = MAP_MIN_X + 1;
		else if (x > MAP_MAX_X)
			x = MAP_MAX_X - 1;
		return x;
	}

	public static int validCoordY(int y)
	{
		if (y < MAP_MIN_Y)
			y = MAP_MIN_Y + 1;
		else if (y > MAP_MAX_Y)
			y = MAP_MAX_Y - 1;
		return y;
	}

	public static int validCoordZ(int z)
	{
		if (z < MAP_MIN_Z)
			z = MAP_MIN_Z + 1;
		else if (z > MAP_MAX_Z)
			z = MAP_MAX_Z - 1;
		return z;
	}

	private static int regionX(int x)
	{
		return (x >> SHIFT_BY) + OFFSET_X;
	}

	private static int regionY(int y)
	{
		return (y >> SHIFT_BY) + OFFSET_Y;
	}

	private static int regionZ(int z)
	{
		return (z >> SHIFT_BY_Z) + OFFSET_Z;
	}

	static boolean isNeighbour(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		return x1 <= x2 + 1 && x1 >= x2 - 1 && y1 <= y2 + 1 && y1 >= y2 - 1 && z1 <= z2 + 1 && z1 >= z2 - 1;
	}

	/**
	 * @param loc Ð»Ð¾ÐºÐ°Ñ†Ð¸Ñ� Ð´Ð»Ñ� Ð¿Ð¾Ð¸Ñ�ÐºÐ° Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°
	 * @return Ð ÐµÐ³Ð¸Ð¾Ð½, Ñ�Ð¾Ð¾Ñ‚Ð²ÐµÑ‚Ñ�Ñ‚Ð²ÑƒÑŽÑ‰Ð¸Ð¹ Ð»Ð¾ÐºÐ°Ñ†Ð¸Ð¸
	 */
	public static WorldRegion getRegion(Location loc)
	{
		return getRegion(validX(regionX(loc.x)), validY(regionY(loc.y)), validZ(regionZ(loc.z)));
	}

	/**
	 * @param obj Ð¾Ð±ÑŒÐµÐºÑ‚ Ð´Ð»Ñ� Ð¿Ð¾Ð¸Ñ�ÐºÐ° Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°
	 * @return Ð ÐµÐ³Ð¸Ð¾Ð½, Ñ�Ð¾Ð¾Ñ‚Ð²ÐµÑ‚Ñ�Ñ‚Ð²ÑƒÑŽÑ‰Ð¸Ð¹ ÐºÐ¾Ð¾Ñ€Ð´Ð¸Ð½Ð°Ñ‚Ð°Ð¼ Ð¾Ð±ÑŒÐµÐºÑ‚Ð°
	 */
	public static WorldRegion getRegion(GameObject obj)
	{
		return getRegion(validX(regionX(obj.getX())), validY(regionY(obj.getY())), validZ(regionZ(obj.getZ())));
	}

	/**
	 * @param x ÐºÐ¾Ð¾Ñ€Ð´Ð¸Ð½Ð°Ñ‚Ð° Ð½Ð° ÐºÐ°Ñ€Ñ‚Ðµ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð²
	 * @param y ÐºÐ¾Ð¾Ñ€Ð´Ð¸Ð½Ð°Ñ‚Ð° Ð½Ð° ÐºÐ°Ñ€Ñ‚Ðµ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð²
	 * @param z ÐºÐ¾Ð¾Ñ€Ð´Ð¸Ð½Ð°Ñ‚Ð° Ð½Ð° ÐºÐ°Ñ€Ñ‚Ðµ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð²
	 * @return Ð ÐµÐ³Ð¸Ð¾Ð½, Ñ�Ð¾Ð¾Ñ‚Ð²ÐµÑ‚Ñ�Ñ‚Ð²ÑƒÑŽÑ‰Ð¸Ð¹ ÐºÐ¾Ð¾Ñ€Ð´Ð¸Ð½Ð°Ñ‚Ð°Ð¼
	 */
	private static WorldRegion getRegion(int x, int y, int z)
	{
		WorldRegion[][][] regions = getRegions();
		WorldRegion region = null;
		region = regions[x][y][z];
		if (region == null)
			synchronized (regions)
			{
				region = regions[x][y][z];
				if (region == null)
					region = regions[x][y][z] = new WorldRegion(x, y, z);
			}
		return region;
	}

	/**
	 * Ð�Ð°Ñ…Ð¾Ð´Ð¸Ñ‚ Ð¸Ð³Ñ€Ð¾ÐºÐ° Ð¿Ð¾ Ð¸Ð¼ÐµÐ½Ð¸
	 * Ð ÐµÐ³Ð¸Ñ�Ñ‚Ñ€ Ñ�Ð¸Ð¼Ð²Ð¾Ð»Ð¾Ð² Ð»ÑŽÐ±Ð¾Ð¹.
	 * @param name Ð¸Ð¼Ñ�
	 * @return Ð½Ð°Ð¹Ð´ÐµÐ½Ñ‹Ð¹ Ð¸Ð³Ñ€Ð¾Ðº Ð¸Ð»Ð¸ null ÐµÑ�Ð»Ð¸ Ð¸Ð³Ñ€Ð¾ÐºÐ° Ð½ÐµÑ‚
	 */
	public static Player getPlayer(String name)
	{
		return GameObjectsStorage.getPlayer(name);
	}

	/**
	 * Ð�Ð°Ñ…Ð¾Ð´Ð¸Ñ‚ Ð¸Ð³Ñ€Ð¾ÐºÐ° Ð¿Ð¾ objectId
	 *
	 * @param objId
	 * @return Ð½Ð°Ð¹Ð´ÐµÐ½Ñ‹Ð¹ Ð¸Ð³Ñ€Ð¾Ðº Ð¸Ð»Ð¸ null ÐµÑ�Ð»Ð¸ Ð¸Ð³Ñ€Ð¾ÐºÐ° Ð½ÐµÑ‚
	 */
	public static Player getPlayer(int objId)
	{
		return GameObjectsStorage.getPlayer(objId);
	}

	/**
	 * ÐŸÑ€Ð¾Ð²ÐµÑ€Ñ�ÐµÑ‚, Ñ�Ð¼ÐµÐ½Ð¸Ð»Ñ�Ñ� Ð»Ð¸ Ñ€ÐµÐ³Ð¸Ð¾Ð½ Ð² ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð¼ Ð½Ð°Ñ…Ð¾Ð´Ð¸Ñ‚Ñ�Ñ� Ð¾Ð±ÑŒÐµÐºÑ‚
	 * Ð•Ñ�Ð»Ð¸ Ñ�Ð¼ÐµÐ½Ð¸Ð»Ñ�Ñ� - ÑƒÐ´Ð°Ð»Ñ�ÐµÑ‚ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð¸Ð· Ñ�Ñ‚Ð°Ñ€Ð¾Ð³Ð¾ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð° Ð¸ Ð´Ð¾Ð±Ð°Ð²Ð»Ñ�ÐµÑ‚ Ð² Ð½Ð¾Ð²Ñ‹Ð¹.
	 * @param object Ð¾Ð±ÑŒÐµÐºÑ‚ Ð´Ð»Ñ� Ð¿Ñ€Ð¾Ð²ÐµÑ€ÐºÐ¸
	 * @param dropper - ÐµÑ�Ð»Ð¸ Ñ�Ñ‚Ð¾ L2ItemInstance, Ñ‚Ð¾ Ð±ÑƒÐ´ÐµÑ‚ Ð°Ð½Ð¸Ð¼Ð°Ñ†Ð¸Ñ� Ð´Ñ€Ð¾Ð¿Ð° Ñ� Ð¿ÐµÑ€Ñ�Ð°
	 */
	public static void addVisibleObject(GameObject object, Creature dropper)
	{
		if (object == null || !object.isVisible() || object.isInObserverMode())
			return;

		WorldRegion region = getRegion(object);
		WorldRegion currentRegion = object.getCurrentRegion();

		if (currentRegion == region)
			return;

		if (currentRegion == null) // Ð�Ð¾Ð²Ñ‹Ð¹ Ð¾Ð±ÑŒÐµÐºÑ‚ (Ð¿Ñ€Ð¸Ð¼ÐµÑ€ - Ð¸Ð³Ñ€Ð¾Ðº Ð²Ð¾ÑˆÐµÐ» Ð² Ð¼Ð¸Ñ€, Ð·Ð°Ñ�Ð¿Ð°ÑƒÐ½Ð¸Ð»Ñ�Ñ� Ð¼Ð¾Ð±, Ð´Ñ€Ð¾Ð¿Ð½ÑƒÐ»Ð¸ Ð²ÐµÑ‰ÑŒ)
		{
			// Ð”Ð¾Ð±Ð°Ð²Ð»Ñ�ÐµÐ¼ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð² Ñ�Ð¿Ð¸Ñ�Ð¾Ðº Ð²Ð¸Ð´Ð¸Ð¼Ñ‹Ñ…
			object.setCurrentRegion(region);
			region.addObject(object);

			// ÐŸÐ¾ÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÐ¼ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð² Ñ‚ÐµÐºÑƒÑ‰ÐµÐ¼ Ð¸ Ñ�Ð¾Ñ�ÐµÐ´Ð½Ð¸Ñ… Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°Ñ…
			// Ð•Ñ�Ð»Ð¸ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð¸Ð³Ñ€Ð¾Ðº, Ð¿Ð¾ÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÐ¼ ÐµÐ¼Ñƒ Ð²Ñ�Ðµ Ð¾Ð±ÑŒÐµÐºÑ‚Ñ‹ Ð² Ñ‚ÐµÐºÑƒÑ‰ÐµÐ¼ Ð¸ Ñ�Ð¾Ñ�ÐµÐ´Ð½Ð¸Ñ… Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°Ñ…
			for (int x = validX(region.getX() - 1); x <= validX(region.getX() + 1); x++)
				for (int y = validY(region.getY() - 1); y <= validY(region.getY() + 1); y++)
					for (int z = validZ(region.getZ() - 1); z <= validZ(region.getZ() + 1); z++)
						getRegion(x, y, z).addToPlayers(object, dropper);
		}
		else// ÐžÐ±ÑŒÐµÐºÑ‚ ÑƒÐ¶Ðµ Ñ�ÑƒÑ‰ÐµÑ�Ñ‚Ð²ÑƒÐµÑ‚, Ð¿ÐµÑ€ÐµÑˆÐµÐ» Ð¸Ð· Ð¾Ð´Ð½Ð¾Ð³Ð¾ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð° Ð² Ð´Ñ€ÑƒÐ³Ð¾Ð¹
		{
			currentRegion.removeObject(object); // Ð£Ð´Ð°Ð»Ñ�ÐµÐ¼ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð¸Ð· Ñ�Ñ‚Ð°Ñ€Ð¾Ð³Ð¾ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°
			object.setCurrentRegion(region);
			region.addObject(object); // Ð”Ð¾Ð±Ð°Ð²Ð»Ñ�ÐµÐ¼ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð² Ñ�Ð¿Ð¸Ñ�Ð¾Ðº Ð²Ð¸Ð´Ð¸Ð¼Ñ‹Ñ…

			// Ð£Ð±Ð¸Ñ€Ð°ÐµÐ¼ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð¸Ð· Ñ�Ñ‚Ð°Ñ€Ñ‹Ñ… Ñ�Ð¾Ñ�ÐµÐ´ÐµÐ¹.
			for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
				for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
					for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
						if (!isNeighbour(region.getX(), region.getY(), region.getZ(), x, y, z))
							getRegion(x, y, z).removeFromPlayers(object);

			// ÐŸÐ¾ÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÐ¼ Ð¾Ð±ÑŒÐµÐºÑ‚, Ð½Ð¾ Ð² Ð¾Ñ‚Ð»Ð¸Ñ‡Ð¸Ðµ Ð¾Ñ‚ Ð¿ÐµÑ€Ð²Ð¾Ð³Ð¾ Ñ�Ð»ÑƒÑ‡Ð°Ñ� - Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ð´Ð»Ñ� Ð½Ð¾Ð²Ñ‹Ñ… Ñ�Ð¾Ñ�ÐµÐ´ÐµÐ¹.
			for (int x = validX(region.getX() - 1); x <= validX(region.getX() + 1); x++)
				for (int y = validY(region.getY() - 1); y <= validY(region.getY() + 1); y++)
					for (int z = validZ(region.getZ() - 1); z <= validZ(region.getZ() + 1); z++)
						if (!isNeighbour(currentRegion.getX(), currentRegion.getY(), currentRegion.getZ(), x, y, z))
							getRegion(x, y, z).addToPlayers(object, dropper);
		}
	}

	/**
	 * Ð£Ð´Ð°Ð»Ñ�ÐµÑ‚ Ð¾Ð±ÑŒÐµÐºÑ‚ Ð¸Ð· Ñ‚ÐµÐºÑƒÑ‰ÐµÐ³Ð¾ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°
	 * @param object Ð¾Ð±ÑŒÐµÐºÑ‚ Ð´Ð»Ñ� ÑƒÐ´Ð°Ð»ÐµÐ½Ð¸Ñ�
	 */
	public static void removeVisibleObject(GameObject object)
	{
		if (object == null || object.isVisible() || object.isInObserverMode())
			return;

		WorldRegion currentRegion;
		if ((currentRegion = object.getCurrentRegion()) == null)
			return;

		object.setCurrentRegion(null);
		currentRegion.removeObject(object);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					getRegion(x, y, z).removeFromPlayers(object);
	}

	public static GameObject getAroundObjectById(GameObject object, int objId)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return null;

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (obj.getObjectId() == objId)
						{
							return obj;
						}
					}

		return null;
	}

	public static List<GameObject> getAroundObjects(GameObject object)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		List<GameObject> result = new ArrayList<GameObject>(128);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						result.add(obj);
					}

		return result;
	}

	public static List<GameObject> getAroundObjects(GameObject object, int radius, int height)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();
		int ox = object.getX();
		int oy = object.getY();
		int oz = object.getZ();
		int sqrad = radius * radius;

		List<GameObject> result = new ArrayList<GameObject>(128);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;
						if (Math.abs(obj.getZ() - oz) > height)
							continue;
						int dx = Math.abs(obj.getX() - ox);
						if (dx > radius)
							continue;
						int dy = Math.abs(obj.getY() - oy);
						if (dy > radius)
							continue;
						if (dx * dx + dy * dy > sqrad)
							continue;

						result.add(obj);
					}

		return result;
	}

	public static List<Creature> getAroundCharacters(GameObject object)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		List<Creature> result = new ArrayList<Creature>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isCreature() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						result.add((Creature)obj);
					}

		return result;
	}

	public static List<Creature> getAroundCharacters(GameObject object, int radius, int height)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();
		int ox = object.getX();
		int oy = object.getY();
		int oz = object.getZ();
		int sqrad = radius * radius;

		List<Creature> result = new ArrayList<Creature>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isCreature() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;
						if (Math.abs(obj.getZ() - oz) > height)
							continue;
						int dx = Math.abs(obj.getX() - ox);
						if (dx > radius)
							continue;
						int dy = Math.abs(obj.getY() - oy);
						if (dy > radius)
							continue;
						if (dx * dx + dy * dy > sqrad)
							continue;

						result.add((Creature)obj);
					}
		return result;
	}

	public static List<Creature> getAroundCharacters(Location loc, int radius)
	{
		WorldRegion currentRegion = World.getRegion(loc);
		if(currentRegion == null)
			return Collections.emptyList();

		int sqrad = radius * radius;
		List<Creature> result = new ArrayList<Creature>(64);

		for(int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for(int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for(int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for(GameObject obj : getRegion(x, y, z))
					{
						if(!obj.isCreature())
							continue;
						int dx = Math.abs(obj.getX() - loc.getX());
						if(dx > radius)
							continue;
						int dy = Math.abs(obj.getY() - loc.getY());
						if(dy > radius)
							continue;
						if(dx * dx + dy * dy > sqrad)
							continue;

						result.add((Creature)obj);
					}
		return result;
	}

	public static List<NpcInstance> getAroundNpc(GameObject object)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		List<NpcInstance> result = new ArrayList<NpcInstance>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isNpc() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						result.add((NpcInstance)obj);
					}
		return result;
	}

	public static List<NpcInstance> getAroundNpc(GameObject object, int radius, int height)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();
		int ox = object.getX();
		int oy = object.getY();
		int oz = object.getZ();
		int sqrad = radius * radius;

		List<NpcInstance> result = new ArrayList<NpcInstance>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isNpc() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;
						if (Math.abs(obj.getZ() - oz) > height)
							continue;
						int dx = Math.abs(obj.getX() - ox);
						if (dx > radius)
							continue;
						int dy = Math.abs(obj.getY() - oy);
						if (dy > radius)
							continue;
						if (dx * dx + dy * dy > sqrad)
							continue;

						result.add((NpcInstance)obj);
					}
		return result;
	}

	public static List<Playable> getAroundPlayables(GameObject object)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		List<Playable> result = new ArrayList<Playable>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isPlayable() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						result.add((Playable)obj);
					}
		return result;
	}

	public static List<Playable> getAroundPlayables(GameObject object, int radius, int height)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();
		int ox = object.getX();
		int oy = object.getY();
		int oz = object.getZ();
		int sqrad = radius * radius;

		List<Playable> result = new ArrayList<Playable>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isPlayable() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;
						if (Math.abs(obj.getZ() - oz) > height)
							continue;
						int dx = Math.abs(obj.getX() - ox);
						if (dx > radius)
							continue;
						int dy = Math.abs(obj.getY() - oy);
						if (dy > radius)
							continue;
						if (dx * dx + dy * dy > sqrad)
							continue;

						result.add((Playable)obj);
					}
		return result;
	}

	public static List<Player> getAroundPlayers(GameObject object)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		List<Player> result = new ArrayList<Player>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isPlayer() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						result.add((Player)obj);
					}
		return result;
	}

	public static List<Player> getAroundPlayers(GameObject object, int radius, int height)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return Collections.emptyList();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();
		int ox = object.getX();
		int oy = object.getY();
		int oz = object.getZ();
		int sqrad = radius * radius;

		List<Player> result = new ArrayList<Player>(64);

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isPlayer() || obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;
						if (Math.abs(obj.getZ() - oz) > height)
							continue;
						int dx = Math.abs(obj.getX() - ox);
						if (dx > radius)
							continue;
						int dy = Math.abs(obj.getY() - oy);
						if (dy > radius)
							continue;
						if (dx * dx + dy * dy > sqrad)
							continue;

						result.add((Player)obj);
					}
		return result;
	}

	/**
	 * ÐŸÑ€Ð¾Ð²ÐµÑ€Ð¸Ñ‚ÑŒ, Ð¿ÑƒÑ�Ñ‚Ñ‹Ðµ Ð»Ð¸ Ñ�Ð¾Ñ�ÐµÐ´Ð½Ð¸Ðµ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ñ‹ Ð¾Ñ‚ Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð², Ð²ÐºÐ»ÑŽÑ‡Ð°Ñ� Ñ‚ÐµÐºÑƒÑ‰Ð¸Ð¹
	 * @return
	 */
	public static boolean isNeighborsEmpty(WorldRegion region)
	{
		for (int x = validX(region.getX() - 1); x <= validX(region.getX() + 1); x++)
			for (int y = validY(region.getY() - 1); y <= validY(region.getY() + 1); y++)
				for (int z = validZ(region.getZ() - 1); z <= validZ(region.getZ() + 1); z++)
					if (!getRegion(x, y, z).isEmpty())
						return false;
		return true;
	}

	public static void activate(WorldRegion currentRegion)
	{
		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					getRegion(x, y, z).setActive(true);
	}

	public static void deactivate(WorldRegion currentRegion)
	{
		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					if (isNeighborsEmpty(getRegion(x, y, z)))
						getRegion(x, y, z).setActive(false);
	}

	/**
	 * ÐŸÐ¾ÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÑ‚ Ð¸Ð³Ñ€Ð¾ÐºÑƒ Ð²Ñ�Ðµ Ð²Ð¸Ð´Ð¸Ð¼Ñ‹Ðµ Ð¾Ð±ÑŒÐµÐºÑ‚Ñ‹ Ð² Ñ‚ÐµÐºÑƒÑ‰ÐµÐ¼ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ðµ Ð¸ Ñ�Ð¾Ñ�ÐµÐ´Ð½Ð¸Ñ…
	 */
	public static void showObjectsToPlayer(Player player)
	{
		WorldRegion currentRegion = player.isInObserverMode() ? player.getObserverRegion() : player.getCurrentRegion();
		if (currentRegion == null)
			return;

		int oid = player.getObjectId();
		int rid = player.getReflectionId();

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						player.sendPacket(player.addVisibleObject(obj, null));
					}
	}

	/**
	 * Ð£Ð±Ð¸Ñ€Ð°ÐµÑ‚ Ñƒ Ð¸Ð³Ñ€Ð¾ÐºÐ° Ð²Ñ�Ðµ Ð²Ð¸Ð´Ð¸Ð¼Ñ‹Ðµ Ð¾Ð±ÑŒÐµÐºÑ‚Ñ‹ Ð² Ñ‚ÐµÐºÑƒÑ‰ÐµÐ¼ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ðµ Ð¸ Ñ�Ð¾Ñ�ÐµÐ´Ð½Ð¸Ñ…
	 */
	public static void removeObjectsFromPlayer(Player player)
	{
		WorldRegion currentRegion = player.isInObserverMode() ? player.getObserverRegion() : player.getCurrentRegion();
		if (currentRegion == null)
			return;

		int oid = player.getObjectId();
		int rid = player.getReflectionId();

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (obj.getObjectId() == oid || obj.getReflectionId() != rid)
							continue;

						player.sendPacket(player.removeVisibleObject(obj, null));
					}
	}

	/**
	 * Ð£Ð±Ð¸Ñ€Ð°ÐµÑ‚ Ð¾Ð±ÑŒÐµÐºÑ‚ Ñƒ Ð²Ñ�ÐµÑ… Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð² Ð² Ñ€ÐµÐ³Ð¸Ð¾Ð½Ðµ
	 */
	public static void removeObjectFromPlayers(GameObject object)
	{
		WorldRegion currentRegion = object.getCurrentRegion();
		if (currentRegion == null)
			return;

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		Player p;
		List<L2GameServerPacket> d = null;

		for (int x = validX(currentRegion.getX() - 1); x <= validX(currentRegion.getX() + 1); x++)
			for (int y = validY(currentRegion.getY() - 1); y <= validY(currentRegion.getY() + 1); y++)
				for (int z = validZ(currentRegion.getZ() - 1); z <= validZ(currentRegion.getZ() + 1); z++)
					for (GameObject obj : getRegion(x, y, z))
					{
						if (!obj.isPlayer() || obj.getObjectId() == oid || obj.getReflectionId() != rid || obj.getPlayer().isGM())
							continue;

						p = (Player) obj;
						p.sendPacket(p.removeVisibleObject(object, d == null ? d = object.deletePacketList() : d));
					}
	}

	static void addZone(Zone zone)
	{
		Reflection reflection = zone.getReflection();

		Territory territory = zone.getTerritory();
		if (territory == null)
		{
			_log.info("World: zone - " + zone.getName() + " not has territory.");
			return;
		}
		for (int x = validX(regionX(territory.getXmin())); x <= validX(regionX(territory.getXmax())); x++)
			for (int y = validY(regionY(territory.getYmin())); y <= validY(regionY(territory.getYmax())); y++)
				for (int z = validZ(regionZ(territory.getZmin())); z <= validZ(regionZ(territory.getZmax())); z++)
				{
					WorldRegion region = getRegion(x, y, z);
					region.addZone(zone);
					for (GameObject obj : region)
					{
						if (!obj.isCreature() || obj.getReflection() != reflection)
							continue;

						((Creature)obj).updateZones();
					}
				}
	}

	static void removeZone(Zone zone)
	{
		Reflection reflection = zone.getReflection();

		Territory territory = zone.getTerritory();
		if (territory == null)
		{
			_log.info("World: zone - " + zone.getName() + " not has territory.");
			return;
		}
		for (int x = validX(regionX(territory.getXmin())); x <= validX(regionX(territory.getXmax())); x++)
			for (int y = validY(regionY(territory.getYmin())); y <= validY(regionY(territory.getYmax())); y++)
				for (int z = validZ(regionZ(territory.getZmin())); z <= validZ(regionZ(territory.getZmax())); z++)
				{
					WorldRegion region = getRegion(x, y, z);
					region.removeZone(zone);
					for (GameObject obj : region)
					{
						if (!obj.isCreature() || obj.getReflection() != reflection)
							continue;

						((Creature)obj).updateZones();
					}
				}
	}

	/**
	 * Ð¡Ð¾Ð·Ð´Ð°ÐµÑ‚ Ð¸ Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ Ñ�Ð¿Ð¸Ñ�Ð¾Ðº Ñ‚ÐµÑ€Ñ€Ð¸Ñ‚Ð¾Ñ€Ð¸Ð¹ Ð´Ð»Ñ� Ñ‚Ð¾Ñ‡ÐµÐº x, y, z
	 */
	public static void getZones(List<Zone> inside, Location loc, Reflection reflection)
	{
		WorldRegion region = getRegion(loc);
		Zone[] zones = region.getZones();
		if (zones.length == 0)
			return;
		for (Zone zone : zones)
			if (zone.checkIfInZone(loc.x, loc.y, loc.z, reflection))
				inside.add(zone);
	}

	public static boolean isWater(Location loc, Reflection reflection)
	{
		return getWater(loc, reflection) != null;
	}

	public static Zone getWater(Location loc, Reflection reflection)
	{
		WorldRegion region = getRegion(loc);
		Zone[] zones = region.getZones();
		if (zones.length == 0)
			return null;
		for (Zone zone : zones)
			if (zone != null && zone.getType() == ZoneType.water && zone.checkIfInZone(loc.x, loc.y, loc.z, reflection))
				return zone;
		return null;
	}

	/**
	 * Ð’Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ Ñ�Ñ‚Ð°Ñ‚Ð¸Ñ�Ñ‚ÐºÑƒ Ð¿Ð¾ Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð°Ð¼
	 * @return int[], Ð³Ð´Ðµ<br>
	 * [0] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð°ÐºÑ‚Ð¸Ð²Ð½Ñ‹Ñ… Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð²<br>
	 * [1] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð½ÐµÐ°ÐºÑ‚Ð¸Ð²Ð½Ñ‹Ñ… Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð²<br>
	 * [2] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð½ÐµÐ¸Ð½Ð¸Ñ†Ð¸Ð°Ð»Ð¸Ð·Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð½Ñ‹Ñ… Ñ€ÐµÐ³Ð¸Ð¾Ð½Ð¾Ð²<br>
	 *
	 * [10] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð¾Ð±ÑŠÐµÐºÑ‚Ð¾Ð²<br>
	 * [11] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð¿ÐµÑ€Ñ�Ð¾Ð½Ð°Ð¶ÐµÐ¹<br>
	 * [12] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð²<br>
	 * [13] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð¸Ð³Ñ€Ð¾ÐºÐ¾Ð² Ð² Ð¾Ñ„Ñ„Ð»Ð°Ð¹Ð½-Ñ€ÐµÐ¶Ð¸Ð¼Ðµ<br>
	 * [14] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ NPC<br>
	 * [15] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð°ÐºÑ‚Ð¸Ð²Ð½Ñ‹Ñ… NPC<br>
	 * [16] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð¼Ð¾Ð½Ñ�Ñ‚Ñ€Ð¾Ð²<br>
	 * [17] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð¼Ð¸Ð½Ð¸Ð¾Ð½Ð¾Ð²<br>
	 * [18] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ñ�Ð°Ð¼Ð¼Ð¾Ð½Ð¾Ð²/Ð¿ÐµÑ‚Ð¾Ð²<br>
	 * [19] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð´Ð²ÐµÑ€ÐµÐ¹<br>
	 * [20] ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð²ÐµÑ‰ÐµÐ¹<br>
	 */
	public static int[] getStats()
	{
		WorldRegion region;
		int[] ret = new int[32];

		for (int x = 0; x <= REGIONS_X; x++)
			for (int y = 0; y <= REGIONS_Y; y++)
				for (int z = 0; z <= REGIONS_Z; z++)
				{
					ret[0]++;

					region = _worldRegions[x][y][z];

					if (region != null)
					{
						if (region.isActive())
							ret[1]++;
						else
							ret[2]++;

						for (GameObject obj : region)
						{
							ret[10]++;

							if (obj.isCreature())
							{
								ret[11]++;

								if (obj.isPlayer())
								{
									ret[12]++;
									Player p = (Player)obj;

									if (p.isInOfflineMode())
										ret[13]++;
								}
								else if (obj.isNpc())
								{
									ret[14]++;

									if (obj.isMonster())
									{
										ret[16]++;
										if (obj.isMinion())
											ret[17]++;
									}

									NpcInstance npc = (NpcInstance) obj;
									if (npc.hasAI())
									{
										if (npc.getAI().isActive())
											ret[15]++;
									}
								}
								else if (obj.isPlayable())
								{
									ret[18]++;
								}
								else if (obj.isDoor())
								{
									ret[19]++;
								}
							}
							else if (obj.isItem())
							{
								ret[20]++;
							}
						}
					}
					else
						ret[3]++;

				}
		return ret;
	}
}