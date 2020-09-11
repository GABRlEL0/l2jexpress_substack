package l2jexpress.gameserver.listener.zone.impl;

import l2jexpress.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Zone;
import l2jexpress.gameserver.model.entity.boat.ClanAirShip;
import l2jexpress.gameserver.model.instances.ClanAirShipControllerInstance;

public class AirshipControllerZoneListener implements OnZoneEnterLeaveListener
{
	private ClanAirShipControllerInstance _controllerInstance;

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if (_controllerInstance == null && actor instanceof ClanAirShipControllerInstance)
			_controllerInstance = (ClanAirShipControllerInstance) actor;
		else if (actor.isClanAirShip())
			_controllerInstance.setDockedShip((ClanAirShip) actor);
	}

	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{
		if (actor.isClanAirShip())
			_controllerInstance.setDockedShip(null);
	}

    @Override
    public void onEquipChanged(Zone zone, Creature actor)
    {
    }
}
