package l2jexpress.gameserver.skills.effects;

import l2jexpress.gameserver.model.Effect;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.stats.Env;

public class EffectUnAggro extends Effect
{
	public EffectUnAggro(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectUnAggro(Effect effect)
	{
		super(effect);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (_effected.isNpc())
			((NpcInstance) _effected).setUnAggred(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if (_effected.isNpc())
			((NpcInstance) _effected).setUnAggred(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}