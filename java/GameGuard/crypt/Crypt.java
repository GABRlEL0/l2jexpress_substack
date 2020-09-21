package GameGuard.crypt;

public abstract interface Crypt
{
	public abstract void setup(byte[] rnd_key, byte[] client_server_key);
	public abstract void crypt(byte[] raw, int offset, int size);
}