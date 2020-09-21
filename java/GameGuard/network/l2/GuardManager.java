package GameGuard.network.l2;

import l2jexpress.commons.threading.RunnableImpl;

import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.GameTimeController;
import l2jexpress.gameserver.model.GameObjectsStorage;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.network.GameClient;

import GameGuard.GGConfig;
import GameGuard.GameGuard;
import GameGuard.network.l2.c2s.GameGuardReply;
import GameGuard.network.l2.s2c.GameGuardQuery;
import GameGuard.network.l2.s2c.SpecialString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GuardManager
{
	private static final Logger _log = LoggerFactory.getLogger(GuardManager.class);
	
	public static void SendSpecialSting(GameClient client){
		if(GameGuard.isProtectionOn()){
			if(GGConfig.SHOW_PROTECTION_INFO_IN_CLIENT)
				client.sendPacket(new SpecialString(0, true, -1, GGConfig.PositionXProtectionInfoInClient, GGConfig.PositionYProtectionInfoInClient, GGConfig.ColorProtectionInfoInClient, "PROTECTION ON"));
			if(GGConfig.SHOW_NAME_SERVER_IN_CLIENT)
				client.sendPacket(new SpecialString(1, true, -1, GGConfig.PositionXNameServerInfoInClient, GGConfig.PositionYNameServerInfoInClient, GGConfig.ColorNameServerInfoInClient, (client.getActiveChar().isLangRus() ? "Ð¡ÐµÑ€Ð²ÐµÑ€: " : "Server: ") + GGConfig.NameServerInfoInClient));
			if(GGConfig.SHOW_REAL_TIME_IN_CLIENT)
				client.sendPacket(new SpecialString(15, true, -1, GGConfig.PositionXRealTimeInClient, GGConfig.PositionYRealTimeInClient, GGConfig.ColorRealTimeInClient, client.getActiveChar().isLangRus() ? "Ð ÐµÐ°Ð»ÑŒÐ½Ð¾Ðµ Ð²Ñ€ÐµÐ¼Ñ�: " : "Real time: "));
			sendToClient(client.getActiveChar());
			//if(GGConfig.ALLOW_SEND_GG_REPLY)
			//	sendGGReply(client);
			}
			}

	public static void sendToClient(Player client)
	{
		if(GGConfig.SHOW_ONLINE_IN_CLIENT)
			client.sendPacket(new SpecialString(2, true, -1, GGConfig.PositionXOnlineInClient, GGConfig.PositionYOnlineInClient, GGConfig.ColorOnlineInClient, (client.isLangRus() ? "ÐžÐ½Ð»Ð°Ð¹Ð½: " : "Online: ") + GameObjectsStorage.getAllPlayersCount()));
		if(GGConfig.SHOW_SERVER_TIME_IN_CLIENT)
		{
			String strH, strM;
			int h = GameTimeController.getInstance().getGameHour();
			int m = GameTimeController.getInstance().getGameMin();
			String nd;
			if(GameTimeController.getInstance().isNowNight())
				nd = client.isLangRus() ? "Ð�Ð¾Ñ‡ÑŒ." : "Night.";
			else
				nd = client.isLangRus() ? "Ð”ÐµÐ½ÑŒ." : "Day.";
			if(h < 10)
				strH = "0" + h;
			else
				strH = "" + h;
			if(m < 10)
				strM = "0" + m;
			else
				strM = "" + m;
			client.sendPacket(new SpecialString(3, true, -1, GGConfig.PositionXServerTimeInClient, GGConfig.PositionYServerTimeInClient, GGConfig.ColorServerTimeInClient, (client.isLangRus() ? "Ð˜Ð³Ñ€Ð¾Ð²Ð¾Ðµ Ð²Ñ€ÐµÐ¼Ñ�: " : "Game time: ") + strH + ":" + strM + " (" + nd + ")"));
		}
		if(GGConfig.SHOW_PING_IN_CLIENT)
			client.sendPacket(new SpecialString(14, true, -1, GGConfig.PositionXPingInClient, GGConfig.PositionYPingInClient, GGConfig.ColorPingInClient, client.isLangRus() ? "ÐŸÐ¸Ð½Ð³: " : "Ping: "));
		scheduleSendPacketToClient(GGConfig.TIME_REFRESH_SPECIAL_STRING, client);
	}

	public static void OffMessage(Player client){
		if(client != null){
			client.sendPacket(new SpecialString(0, false, -1, GGConfig.PositionXProtectionInfoInClient, GGConfig.PositionYProtectionInfoInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(1, false, -1, GGConfig.PositionXNameServerInfoInClient, GGConfig.PositionYNameServerInfoInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(2, false, -1, GGConfig.PositionXOnlineInClient, GGConfig.PositionYOnlineInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(3, false, -1, GGConfig.PositionXServerTimeInClient, GGConfig.PositionYServerTimeInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(14, false, -1, GGConfig.PositionXPingInClient, GGConfig.PositionYPingInClient, 0xFF00FF00, ""));
			client.sendPacket(new SpecialString(15, false, -1, GGConfig.PositionXRealTimeInClient, GGConfig.PositionYRealTimeInClient, 0xFF00FF00, ""));
		}return;}

	public static void scheduleSendPacketToClient(long time, final Player client){
		if(time <= 0){
			OffMessage(client);
			return;}

		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl() throws Exception
			{sendToClient(client);}}, time);}

	public static void sendGGReply(GameClient client)
	{
		//if(client != null && client.getActiveChar() != null)
		//{
		//	client.sendPacket(new GameGuardQuery());
		//	if(GGConfig.ALLOW_SEND_GG_REPLY)
		//		scheduleSendGG(GGConfig.TIME_SEND_GG_REPLY * 1000, client);
		//}
		//if (client != null)
		//{
		//	client.setGameGuardOk(true);
		//	client.sendPacket(new GameGuardQuery());
		//	if(GGConfig.ALLOW_SEND_GG_REPLY)
		//		scheduleSendGG(GGConfig.TIME_SEND_GG_REPLY * 1000, client);
		//}
	}

	public static void scheduleSendGG(long time, final GameClient client)
	{
		if(time <= 0)
			return;

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				if (client != null && client.getActiveChar() != null && !client.isGameGuardOk())
				{
					_log.info("Client "+client+" failed to reply GameGuard query and is being kicked!");
					//client.closeNow(true);
				}
				client.sendPacket(new GameGuardQuery());
			}
		}, time, time);
	}
}