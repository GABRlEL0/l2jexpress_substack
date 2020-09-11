package l2jexpress.gameserver.listener.actor.ai;

import l2jexpress.gameserver.ai.CtrlEvent;
import l2jexpress.gameserver.listener.AiListener;
import l2jexpress.gameserver.model.Creature;

public interface OnAiEventListener extends AiListener
{
	public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args);
}
