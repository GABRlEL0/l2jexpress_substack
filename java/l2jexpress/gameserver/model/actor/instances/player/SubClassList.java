package l2jexpress.gameserver.model.actor.instances.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2jexpress.commons.dbutils.DbUtils;
import l2jexpress.gameserver.database.DatabaseFactory;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.SubClass;

/**
 * @author Bonux
 * @date 03/11/2011 12:19 AM
**/
public class SubClassList implements Iterable<SubClass>
{
	private static final Logger _log = LoggerFactory.getLogger(SubClassList.class);

	public static final int MAX_SUB_COUNT = 4;

	private final TreeMap<Integer, SubClass> _listByIndex = new TreeMap<Integer, SubClass>();
	private final TreeMap<Integer, SubClass> _listByClassId = new TreeMap<Integer, SubClass>();
	
	public static final String SELECT_SQL_QUERY = "SELECT class_id, exp, sp, curHp, curCp, curMp, active, isBase, death_penalty, certification FROM character_subclasses WHERE char_obj_id=?";
	
	private final Player _owner;

	private SubClass _baseSubClass = null;
	private SubClass _activeSubClass = null;

	public SubClassList(Player owner)
	{
		_owner = owner;
	}

	public boolean restore()
	{
		_listByIndex.clear();
		_listByClassId.clear();

		List<SubClass> subclasses = restore(_owner);
		if(subclasses.isEmpty())
		{
			_log.warn("SubClassList:restore: Could not restore any sub-classes! Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");
			return false;
		}

		int index = 2;
		for(SubClass sub : subclasses)
		{
			if(sub == null) // Невозможно, но хай будет.
				continue;

			if(size() >= MAX_SUB_COUNT)
			{
				_log.warn("SubClassList:restore: Limit is subclass! Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");
				break;
			}

			if(sub.isActive())
				_activeSubClass = sub;

			if(sub.isBase())
			{
				_baseSubClass = sub;
				sub.setIndex(1);
			}
			else
			{
				sub.setIndex(index);
				index++;
			}

			if(_listByIndex.containsKey(sub.getIndex()))
				_log.warn("SubClassList:restore: Duplicate index in player subclasses! Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");
			_listByIndex.put(sub.getIndex(), sub);

			if(_listByClassId.containsKey(sub.getClassId()))
				_log.warn("SubClassList:restore: Duplicate class_id in player subclasses! Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");
			_listByClassId.put(sub.getClassId(), sub);
		}

		if(_baseSubClass == null)
		{
			_log.warn("SubClassList:restore: Could not restore base sub-class! Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");
			return false;
		}

		if(_activeSubClass == null)
		{
			_activeSubClass = _baseSubClass;
			_activeSubClass.setActive(true);
			_log.warn("SubClassList:restore: Could not restore active sub-class! Base class applied to active sub-class. Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");
		}

		if(_listByIndex.size() != _listByClassId.size()) // Невозможно, но хай будет.
			_log.warn("SubClassList:restore: The size of the lists do not match! Player: " + _owner.getName() + "(" + _owner.getObjectId() + ")");

		return true;
	}

	@Override
	public Iterator<SubClass> iterator()
	{
		return _listByIndex.values().iterator();
	}

	public Collection<SubClass> values()
	{
		return _listByIndex.values();
	}

	public SubClass getByClassId(int classId)
	{
		return _listByClassId.get(classId);
	}

	public SubClass getByIndex(int index)
	{
		return _listByIndex.get(index);
	}

	public void removeByClassId(int classId)
	{
		if(!_listByClassId.containsKey(classId))
			return;

		int index = _listByClassId.get(classId).getIndex();
		_listByIndex.remove(index);
		_listByClassId.remove(classId);
	}

	public SubClass getActiveSubClass()
	{
		return _activeSubClass;
	}

	public SubClass getBaseSubClass()
	{
		return _baseSubClass;
	}

	public boolean isBaseClassActive()
	{
		return _activeSubClass == _baseSubClass;
	}

	public boolean haveSubClasses()
	{
		return size() > 1;
	}

	public boolean changeSubClassId(int oldClassId, int newClassId)
	{
		if(!_listByClassId.containsKey(oldClassId))
			return false;

		if(_listByClassId.containsKey(newClassId))
			return false;

		SubClass sub = _listByClassId.get(oldClassId);
		sub.setClassId(newClassId);

		_listByClassId.remove(oldClassId);
		_listByClassId.put(sub.getClassId(), sub);
		return true;
	}

	public boolean add(SubClass sub)
	{
		if(sub == null)
			return false;

		if(size() >= MAX_SUB_COUNT)
			return false;

		if(_listByClassId.containsKey(sub.getClassId()))
			return false;

		int index = 1;
		while(_listByIndex.containsKey(index))
			index++;

		sub.setIndex(index);

		_listByIndex.put(sub.getIndex(), sub);
		_listByClassId.put(sub.getClassId(), sub);
		return true;
	}

	public SubClass changeActiveSubClass(int classId)
	{
		SubClass sub = _listByClassId.get(classId);
		if(sub == null)
			return null;

		if(_activeSubClass != null)
			_activeSubClass.setActive(false);

		sub.setActive(true);

		_activeSubClass = sub;
		return sub;
	}

	public boolean containsClassId(int classId)
	{
		return _listByClassId.containsKey(classId);
	}

	public int size()
	{
		return _listByIndex.size();
	}

	@Override
	public String toString()
	{
		return "SubClassList[owner=" + _owner.getName() + "]";
	}
	
	public List<SubClass> restore(Player player)
	{
		List<SubClass> result = new ArrayList<SubClass>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				SubClass subClass = new SubClass(player);
				//ÐŸÐ¾Ñ€Ñ�Ð´Ð¾Ðº Ð½Ðµ Ð¼ÐµÐ½Ñ�Ñ‚ÑŒ, Ð±ÑƒÐ´ÑƒÑ‚ Ð¿Ð»Ð¾Ñ…Ð¸Ðµ Ð¿Ð¾Ñ�Ð»ÐµÐ´Ñ�Ñ‚Ð²Ð¸Ñ�!
				subClass.setBase(rset.getInt("isBase") == 1);
				subClass.setClassId(rset.getInt("class_id"));
				subClass.setExp(rset.getLong("exp"));
				subClass.setSp(rset.getLong("sp"));
				subClass.setHp(rset.getDouble("curHp"));
				subClass.setMp(rset.getDouble("curMp"));
				subClass.setCp(rset.getDouble("curCp"));
				subClass.setActive(rset.getInt("active") == 1);
				subClass.setDeathPenalty(rset.getInt("death_penalty"));
				subClass.setCertification(rset.getInt("certification"));
				result.add(subClass);
			}
		}
		catch(Exception e)
		{
			_log.error("CharacterSubclassDAO:restore(player)", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return result;
	}
}
