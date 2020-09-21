package GameGuard;

import java.io.IOException;

import GameGuard.GGConfig;
import GameGuard.crypt.BlowfishEngine;
import GameGuard.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameGuard
{
	private static final Logger _log = LoggerFactory.getLogger(GameGuard.class);
	private static byte[] _key = new byte[16];

	public static void Init()
	{
		GGConfig.load();
		if(isProtectionOn())
		{_log.info("************[ Protection System: Start Loading ]*************");
		//	HwidBan.getInstance();
		//	HwidManager.getInstance();
		//	ProtectManager.getInstance();
		//	AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminHWID());
		_log.info("************[ Protection System: Finish Loading ]*************");
		_log.info("************[ Protection System: GameGuard ON ]*************");}
	}

	public static boolean isProtectionOn()
	{
		if(GGConfig.ALLOW_GUARD_SYSTEM)
			return true;
		return false;
	}

	public static String getHwid(String hwid)
	{
		if(hwid.isEmpty() || hwid == "" || hwid == null || hwid == " ")
			return "NoClientPatch!!!";
		return Util.asHwidString(hwid);
	}

	public static byte[] getKey(byte[] key)
	{
		byte[] bfkey = {78, 78, 28, 34, 21, 41, 75, 45, 54, 45, 1, 11, 56, 76, -5, 11};
		try
		{
			BlowfishEngine bf = new BlowfishEngine();
			bf.init(true, bfkey);
			bf.processBlock(key, 0, _key, 0);
			bf.processBlock(key, 8, _key, 8);
		}
		catch(IOException e)
		{
			_log.info("Bad key!!!");
		}
		return _key;
	}
}