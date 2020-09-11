package l2jexpress.gameserver.listener.actor.ai;

import l2jexpress.gameserver.ai.CtrlIntention;
import l2jexpress.gameserver.listener.AiListener;
import l2jexpress.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener
{
	public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}
