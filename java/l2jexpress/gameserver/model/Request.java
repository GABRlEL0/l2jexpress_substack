package l2jexpress.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import l2jexpress.commons.collections.MultiValueSet;
import l2jexpress.commons.lang.reference.HardReference;
import l2jexpress.commons.threading.RunnableImpl;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.network.serverpackets.components.SystemMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request extends MultiValueSet<String>
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(Request.class);

	public static enum L2RequestType
	{
		CUSTOM,
		PARTY,
		PARTY_ROOM,
		CLAN,
		ALLY,
		TRADE,
		TRADE_REQUEST,
		FRIEND,
		CHANNEL,
		DUEL,
		POST,
		COUPLE_ACTION,
		AUCTION_ITEM_ADD
	}

	private final static AtomicInteger _nextId = new AtomicInteger();

	private final int _id;
	private L2RequestType _type;
	private HardReference<Player> _requestor;
	private HardReference<Player> _reciever;
	private boolean _isRequestorConfirmed;
	private boolean _isRecieverConfirmed;
	private boolean _isCancelled;
	private boolean _isDone;

	private long _timeout;
	private Future<?> _timeoutTask;

	/**
	 * Ð¡Ð¾Ð·Ð´Ð°ÐµÑ‚ Ð·Ð°Ð¿Ñ€Ð¾Ñ�
	 */
	public Request(L2RequestType type, Player requestor, Player reciever)
	{
		_id = _nextId.incrementAndGet();
		_requestor = requestor.getRef();
		_reciever = reciever.getRef();
		_type = type;
		requestor.setRequest(this);
		reciever.setRequest(this);
	}

	public Request(L2RequestType type, Player requestor)
	{
		_id = _nextId.incrementAndGet();
		_requestor = requestor.getRef();
		_type = type;
		requestor.setRequest(this);
	}

	public Request setTimeout(long timeout)
	{
		_timeout = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
		_timeoutTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				timeout();
			}
		}, timeout);
		return this;
	}

	public int getId()
	{
		return _id;
	}

	/**
	 * ÐžÑ‚Ð¼ÐµÐ½Ñ�ÐµÑ‚ Ð·Ð°Ð¿Ñ€Ð¾Ñ� Ð¸ Ð¾Ñ‡Ð¸Ñ‰Ð°ÐµÑ‚ Ñ�Ð¾Ð¾Ñ‚Ð²ÐµÑ‚Ñ�Ñ‚Ð²ÑƒÑŽÑ‰ÐµÐµ Ð¿Ð¾Ð»Ðµ Ñƒ ÑƒÑ‡Ð°Ñ�Ñ‚Ð½Ð¸ÐºÐ¾Ð².
	 */
	public void cancel()
	{
		_isCancelled = true;
		if (_timeoutTask != null)
			_timeoutTask.cancel(false);
		_timeoutTask = null;
		Player player = getRequestor();
		if (player != null && player.getRequest() == this)
			player.setRequest(null);
		player = getReciever();
		if (player != null && player.getRequest() == this)
			player.setRequest(null);
	}

	/**
	 * Ð—Ð°ÐºÐ°Ð½Ñ‡Ð¸Ð²Ð°ÐµÑ‚ Ð·Ð°Ð¿Ñ€Ð¾Ñ� Ð¸ Ð¾Ñ‡Ð¸Ñ‰Ð°ÐµÑ‚ Ñ�Ð¾Ð¾Ñ‚Ð²ÐµÑ‚Ñ�Ñ‚Ð²ÑƒÑŽÑ‰ÐµÐµ Ð¿Ð¾Ð»Ðµ Ñƒ ÑƒÑ‡Ð°Ñ�Ñ‚Ð½Ð¸ÐºÐ¾Ð².
	 */
	public void done()
	{
		_isDone = true;
		if (_timeoutTask != null)
			_timeoutTask.cancel(false);
		_timeoutTask = null;
		Player player = getRequestor();
		if (player != null && player.getRequest() == this)
			player.setRequest(null);
		player = getReciever();
		if (player != null && player.getRequest() == this)
			player.setRequest(null);
	}

	/**
	 * Ð”ÐµÐ¹Ñ�Ñ‚Ð²Ð¸Ðµ Ð¿Ñ€Ð¸ Ñ‚Ð°Ð¹Ð¼Ð°ÑƒÑ‚Ðµ.
	 */
	public void timeout()
	{
		Player player = getReciever();
		if (player != null)
			if (player.getRequest() == this)
				player.sendPacket(SystemMsg.TIME_EXPIRED);
		cancel();
	}

	public Player getOtherPlayer(Player player)
	{
		if (player == getRequestor())
			return getReciever();
		if (player == getReciever())
			return getRequestor();
		return null;
	}

	public Player getRequestor()
	{
		return _requestor.get();
	}

	public Player getReciever()
	{
		return _reciever.get();
	}

	/**
	 * ÐŸÑ€Ð¾Ð²ÐµÑ€Ñ�ÐµÑ‚ Ð½Ðµ Ð¿Ñ€Ð¾Ñ�Ñ€Ð¾Ñ‡ÐµÐ½ Ð»Ð¸ Ð·Ð°Ð¿Ñ€Ð¾Ñ�.
	 */
	public boolean isInProgress()
	{
		if (_isCancelled)
			return false;
		if (_isDone)
			return false;
		if (_timeout == 0)
			return true;
		if (_timeout > System.currentTimeMillis())
			return true;
		return false;
	}

	/**
	 * ÐŸÑ€Ð¾Ð²ÐµÑ€Ñ�ÐµÑ‚ Ñ‚Ð¸Ð¿ Ð·Ð°Ð¿Ñ€Ð¾Ñ�Ð°.
	 */
	public boolean isTypeOf(L2RequestType type)
	{
		return _type == type;
	}

	/**
	 * ÐŸÐ¾Ð¼ÐµÑ‡Ð°ÐµÑ‚ ÑƒÑ‡Ð°Ñ�Ñ‚Ð½Ð¸ÐºÐ° ÐºÐ°Ðº Ñ�Ð¾Ð³Ð»Ð°Ñ�Ð¸Ð²ÑˆÐµÐ³Ð¾Ñ�Ñ�.
	 */
	public void confirm(Player player)
	{
		if (player == getRequestor())
			_isRequestorConfirmed = true;
		else if (player == getReciever())
			_isRecieverConfirmed = true;
	}
	/**
	 * ÐŸÑ€Ð¾Ð²ÐµÑ€Ñ�ÐµÑ‚ Ñ�Ð¾Ð³Ð»Ð°Ñ�Ð¸Ð»Ñ�Ñ� Ð»Ð¸ Ð¸Ð³Ñ€Ð¾Ðº Ñ� Ð·Ð°Ð¿Ñ€Ð¾Ñ�Ð¾Ð¼.
	 */
	public boolean isConfirmed(Player player)
	{
		if (player == getRequestor())
			return _isRequestorConfirmed;
		else if (player == getReciever())
			return _isRecieverConfirmed;
		return false; // WTF???
	}
}