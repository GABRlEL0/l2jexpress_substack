package l2jexpress.gameserver.model.items.attachment;

import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Skill;

public interface FlagItemAttachment extends PickableAttachment
{
	//FIXME [l2jexpress_devs] may alter the listener Player
	void onLogout(Player player);
	//FIXME [l2jexpress_devs] may alter the listener Player
	void onDeath(Player owner, Creature killer);

	void onOutTerritory(Player player);
	
	boolean canAttack(Player player);

	boolean canCast(Player player, Skill skill);

	boolean canBeLost();

	boolean canBeUnEquiped();
}
