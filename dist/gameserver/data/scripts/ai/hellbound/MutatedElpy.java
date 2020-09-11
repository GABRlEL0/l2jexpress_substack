package ai.hellbound;

import l2jexpress.gameserver.ai.Fighter;
import l2jexpress.gameserver.instancemanager.naia.NaiaCoreManager;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.instances.NpcInstance;

public class MutatedElpy extends Fighter
{
    public MutatedElpy(NpcInstance actor) 
    {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    protected void onEvtDead(Creature killer)
    {
        NaiaCoreManager.launchNaiaCore();
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) 
    {
        NpcInstance actor = getActor();
        actor.doDie(attacker);
    }
}