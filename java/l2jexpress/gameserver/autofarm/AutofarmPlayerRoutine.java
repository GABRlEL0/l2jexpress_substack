package l2jexpress.gameserver.autofarm;

import l2jexpress.gameserver.Config;
import l2jexpress.gameserver.ThreadPoolManager;
import l2jexpress.gameserver.cache.Msg;
import l2jexpress.gameserver.network.serverpackets.ItemList;
import l2jexpress.gameserver.network.serverpackets.PetItemList;
import l2jexpress.gameserver.network.serverpackets.SystemMessage;
import l2jexpress.gameserver.network.serverpackets.SystemMessage2;
import l2jexpress.gameserver.network.serverpackets.components.SystemMsg;
import l2jexpress.gameserver.model.quest.Quest;
import l2jexpress.gameserver.model.quest.QuestState;
import l2jexpress.commons.annotations.NotNull;
import l2jexpress.gameserver.geodata.GeoEngine;
import l2jexpress.gameserver.model.WorldRegion;
import l2jexpress.gameserver.model.Creature;
import l2jexpress.gameserver.model.Player;
import l2jexpress.gameserver.model.items.Inventory;
import l2jexpress.gameserver.model.Skill;
import l2jexpress.gameserver.templates.item.WeaponTemplate;
import l2jexpress.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2jexpress.gameserver.model.items.ItemInfo;
import l2jexpress.gameserver.model.items.ItemInstance;
import l2jexpress.gameserver.model.actor.instances.player.ShortCut;
import l2jexpress.gameserver.model.actor.instances.player.ShortCutList;
import l2jexpress.gameserver.model.Skill.SkillType;
import l2jexpress.gameserver.tables.SkillTable;
import l2jexpress.gameserver.model.GameObject;
import l2jexpress.commons.util.Rnd;
import l2jexpress.gameserver.taskmanager.AiTaskManager;
import l2jexpress.gameserver.ai.DefaultAI.Task;
import l2jexpress.gameserver.ai.DefaultAI.TaskType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutofarmPlayerRoutine {

    private final Player player;
    private Creature committedTarget = null;
    
    public AutofarmPlayerRoutine(Player player){
        this.player = player;
    }
    
    public void executeRoutine(){
    	System.out.println("Is Alive");
        checkSpoil();
        targetEligibleCreature();
        checkManaPots();
        checkHealthPots();
        attack();
        checkSpoil();
    }
    private void checkSpoil() {
    	
    }
    
    private void attack() {
    	
    }
    
    private void targetEligibleCreature() {
    	
    }
    
    private void checkManaPots() {
    	
    }
    
    private void checkHealthPots() {
    	
    } 
    
}