package l2jexpress.gameserver.model.instances;

import l2jexpress.gameserver.templates.npc.NpcTemplate;

public class NpcNotSayInstance extends NpcInstance
{
	public NpcNotSayInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
		setHasChatWindow(false);
	}
}
