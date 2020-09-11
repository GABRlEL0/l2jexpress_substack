package l2jexpress.gameserver.templates.spawn;

import l2jexpress.gameserver.utils.Location;

public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}
