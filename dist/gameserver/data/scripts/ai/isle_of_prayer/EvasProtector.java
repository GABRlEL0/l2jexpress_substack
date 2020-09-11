package ai.isle_of_prayer;

import l2jexpress.gameserver.ai.DefaultAI;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.model.instances.NpcInstance;
import l2jexpress.gameserver.model.Skill;
import instances.CrystalCaverns;

public class EvasProtector extends DefaultAI
{
	public EvasProtector(NpcInstance actor)
	{
		super(actor);
		actor.setHasChatWindow(false);
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();

		CrystalCaverns refl = null;
		if (actor.getReflection() instanceof CrystalCaverns)
			refl = (CrystalCaverns) actor.getReflection();	
		if (refl != null)			
			if (skill.getSkillType() == Skill.SkillType.HEAL)
				refl.notifyProtectorHealed(actor);
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}