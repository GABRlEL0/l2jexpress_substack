package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;

public interface OnMagicUseListener extends CharListener
{
	public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt);
}
