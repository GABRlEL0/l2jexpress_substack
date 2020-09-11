package l2jexpress.gameserver.listener.actor;

import l2jexpress.gameserver.listener.CharListener;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;

public interface OnMagicHitListener extends CharListener
{
	public void onMagicHit(Creature actor, Skill skill, Creature caster);
}
