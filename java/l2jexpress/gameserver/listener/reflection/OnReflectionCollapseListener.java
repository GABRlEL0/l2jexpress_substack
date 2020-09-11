package l2jexpress.gameserver.listener.reflection;

import l2jexpress.commons.listener.Listener;
import l2jexpress.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	public void onReflectionCollapse(Reflection reflection);
}
