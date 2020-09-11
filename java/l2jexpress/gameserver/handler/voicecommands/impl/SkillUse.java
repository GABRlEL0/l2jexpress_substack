package l2jexpress.gameserver.handler.voicecommands.impl;

import l2jexpress.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.network.serverpackets.Say2;
import l2jexpress.gameserver.network.serverpackets.components.ChatType;
import l2jexpress.gameserver.scripts.Functions;
import l2jexpress.gameserver.tables.SkillTable;

public class SkillUse extends Functions implements IVoicedCommandHandler
{
	private static final String[] _commandList = {};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		int skills = Integer.parseInt(args);

		Skill skill = SkillTable.getInstance().getInfo(skills, activeChar.getSkillLevel(Integer.valueOf(skills)));

		String sk = "/useskill " + skill.getName();
		Say2 cs = new Say2(activeChar.getObjectId(), ChatType.ALL, activeChar.getName(), sk);

		activeChar.setMacroSkill(skill);
		activeChar.sendPacket(cs);

		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}