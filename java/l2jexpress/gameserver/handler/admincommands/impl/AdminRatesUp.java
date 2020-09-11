package l2jexpress.gameserver.handler.admincommands.impl;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.handler.admincommands.IAdminCommandHandler;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.GameObject;
import l2jexpress.gameserver.model.Party;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.World;
import l2jexpress.gameserver.model.actor.instances.player.Bonus;
import l2jexpress.gameserver.network.serverpackets.components.SystemMsg;
import l2jexpress.gameserver.stats.Stats;
import l2jexpress.gameserver.model.Player;


import org.apache.commons.lang3.math.NumberUtils;
@SuppressWarnings("unused")
public class AdminRatesUp implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_event_on,
		admin_event_off
	}
	private final Bonus _bonus = new Bonus();
	
	private double exp;
	private double sp;
	private double qDrop;
	private double adena;
	private double items;
	private double spoil;
	

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		Bonus bonus = activeChar.getBonus();



		switch (command)
		{
			case admin_event_on:
				System.out.println("Arranco el evento!");
				
				if (Config.EVENT_RATEUP_ENABLED == true )
				bonus.setRateXp(exp * activeChar.getRateExp());
				bonus.setRateSp(sp * activeChar.getRateSp());
				bonus.setDropAdena(qDrop * activeChar.getRateAdena());
				bonus.setDropItems(adena * activeChar.getRateItems());
				bonus.setDropSpoil(spoil * activeChar.getRateSpoil());
				if(activeChar.isGM())
					activeChar.sendMessage("Evento Iniciado");
				
				break;
			case admin_event_off:
				System.out.println("se termino el evento!");
				bonus.setRateXp(1.);
				bonus.setRateSp(1.);
				bonus.setDropAdena(1.);
				bonus.setDropItems(1.);
				bonus.setDropSpoil(1.);		
				if(activeChar.isGM())
					activeChar.sendMessage("Evento Finalizado");
				break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
	
}