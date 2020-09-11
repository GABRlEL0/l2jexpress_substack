package l2jexpress.gameserver.listener.actor.player;

import l2jexpress.gameserver.listener.PlayerListener;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Summon;

public interface OnPlayerSummonPetListener extends PlayerListener
{
	void onSummonPet(Player p0, Summon p1);
}
