package services.totalonline;

import l2jexpress.gameserver.database.DatabaseFactory;
import l2jexpress.gameserver.model.GameObjectsStorage;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.scripts.Functions;
import l2jexpress.gameserver.scripts.ScriptFile;
import l2jexpress.gameserver.tables.FakePlayersTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Online -> real + fake
 */
public class totalonline extends Functions implements ScriptFile
{
	private static final Logger LOG = LoggerFactory.getLogger(totalonline.class);

	public void onLoad()
	{
		//if(Config.ALLOW_ONLINE_PARSE)
		//{
			//ThreadPoolManager.getInstance().scheduleAtFixedRate(new updateOnline(), Config.FIRST_UPDATE*60000, Config.DELAY_UPDATE*60000);
			
		//}
	}
	private class updateOnline implements Runnable
	{
		public void run()
		{
			int members = getOnlineMembers();
			int offMembers = getOfflineMembers();
			try (Connection con = DatabaseFactory.getInstance().getConnection();
			    PreparedStatement statement = con.prepareStatement("update online set online =?, offline = ? where 'index' =0");)
			{
				statement.setInt(1, members);
				statement.setInt(2, offMembers);
				statement.execute();	
			}
			catch (SQLException e)
			{
				LOG.error("updateOnline: ", e);
			}	
		}
	}
	//for future possibility of parsing names of players method is taking also name to array for init
	private int getOnlineMembers()
	{
		int i = 0;
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			i++;
		}
		i = i + FakePlayersTable.getFakePlayersCount();
		
		return i;	
	}
	private int getOfflineMembers()
	{
		int i = 0;
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.isInOfflineMode())
				i++;
		}
		
		return i;	
	}		
		
	
	public void onReload()
	{}

	public void onShutdown()
	{}
}