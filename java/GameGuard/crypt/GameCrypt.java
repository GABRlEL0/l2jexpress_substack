package GameGuard.crypt;

import GameGuard.GGConfig;
import GameGuard.crypt.impl.L2C;
import GameGuard.crypt.impl.L2S;
import GameGuard.crypt.impl.VMPC;

public class GameCrypt
{
	private Crypt _client;
	private Crypt _server;
	private boolean _isEnabled = false;
	private boolean _isProtected = false;

	public void setProtected(boolean state)
	{
		_isProtected = state;
	}

	public void setKey(byte[] key)
	{
		if (_isProtected)
		{
			_client = new VMPC();
			_client.setup(key, GGConfig.GUARD_CLIENT_CRYPT);
			_server = new L2S();
			_server.setup(key, null);
			_server = new VMPC();
			_server.setup(key, GGConfig.GUARD_SERVER_CRYPT);
		}
		else
		{
			_client = new L2C();
			_client.setup(key, null);
			_server = new L2S();
			_server.setup(key, null);
		}
	}

	public void decrypt(byte[] raw, int offset, int size)
	{
		if (_isEnabled)
			_client.crypt(raw, offset, size);
	}

	public void encrypt(byte[] raw, int offset, int size)
	{
		if (_isEnabled)
			_server.crypt(raw, offset, size);
		else
			_isEnabled = true;
	}
}