package l2jexpress.gameserver.model.entity.tournament.listener;

import l2jexpress.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.model.entity.tournament.ActiveBattleManager;
import l2jexpress.gameserver.model.entity.tournament.BattleInstance;

public class TournamentReceiveDamageListener implements OnCurrentHpDamageListener
{
	private final BattleInstance _battleInstance;

	public TournamentReceiveDamageListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
	{
		ActiveBattleManager.onReceivedDamage(_battleInstance, attacker, actor, damage);
	}
}
