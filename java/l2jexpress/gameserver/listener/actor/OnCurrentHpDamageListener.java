package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;

public interface OnCurrentHpDamageListener extends CharListener
{
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill);
}
