package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.Option;
import com.fs.starfarer.api.campaign.rules.RuleAPI;
import com.fs.starfarer.api.campaign.rules.RulesAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.util.Misc;
import java.util.HashMap;
import java.util.Map;

public class VulpoidChatLocalEntitiesDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    float MAX_RANGE = 250f;
    Map<Option, Map<String, MemoryAPI>> optionMemoryMaps;
    
    @Override
    protected void loadOptions() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        LocationAPI location;
        RulesAPI rules = Global.getSector().getRules();
        optionMemoryMaps = new HashMap();
        
        if(Global.getSector().getPlayerFleet().isInHyperspace()) location = Global.getSector().getHyperspace();
        else location = Global.getSector().getPlayerFleet().getStarSystem();
        
        if (location != null) {  // Just to be safe
            // TODO - any way to sort by distance?
            for (SectorEntityToken entity : location.getAllEntities()) {
                float dist = Misc.getDistance(playerFleet, entity);
                dist -= playerFleet.getRadius();
                dist -= entity.getRadius();
                if(dist <= MAX_RANGE) {
                    Map<String, MemoryAPI> memoryMap = getMemoryForEntity(entity);
                    RuleAPI rule = rules.getBestMatching(null, "VulpoidChatSurroundingsOptions", dialog, memoryMap);
                    //if(rule != null && !entries.contains(rule)) entries.add(rule);
                    if(rule != null) {
                        for(Option option : rule.getOptions()) {
                            String replacement_text = Misc.replaceTokensFromMemory(option.text, memoryMap);
                            if(replacement_text!=null && !replacement_text.equals(option.text)) {
                                Option replacement_option = new Option();
                                replacement_option.text = replacement_text;
                                replacement_option.id = option.id;
                                replacement_option.order = option.order;
                                option = replacement_option;
                            }
                            if(!entries.contains(option)) {
                                entries.add(option);
                                optionMemoryMaps.put(option, memoryMap);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected Map<String, MemoryAPI> getMemoryForEntity(SectorEntityToken entity) {
        /*Map<String, MemoryAPI> memoryMap = new HashMap();
        memoryMap.put(MemKeys.LOCAL, entity.getMemory());
        if (entity.getFaction() != null) memoryMap.put(MemKeys.FACTION, entity.getFaction().getMemory());
        else memoryMap.put(MemKeys.FACTION, Global.getFactory().createMemory());
        memoryMap.put(MemKeys.GLOBAL, Global.getSector().getMemory());
        memoryMap.put(MemKeys.PLAYER, Global.getSector().getCharacterData().getMemory());
        if (entity.getMarket() != null) memoryMap.put(MemKeys.MARKET, entity.getMarket().getMemory());
        return memoryMap;*/
        Map<String, MemoryAPI> memoryMap = conversationDelegate.getMemoryMap();
        memoryMap.put("topic", entity.getMemory());
        if (entity.getFaction() != null) memoryMap.put("topicFaction", entity.getFaction().getMemory());
        else memoryMap.put("topicFaction", Global.getFactory().createMemory());
        if (entity.getMarket() != null) memoryMap.put("topicMarket", entity.getMarket().getMemory());
        return memoryMap;
    }
    
    @Override
    protected String getEntryLabel(Object entry) {
        //if(entry instanceof RuleAPI) return ((RuleAPI)entry).getOptions().get(0).text;
        if(entry instanceof Option) return ((Option)entry).text;
        return null;
    }
    
    @Override
    protected void selectEntry(Object entry) {
        if (entry instanceof Option) {
            delegated = true;
            options.clearOptions();
            conversationDelegate = new RuleBasedInteractionDialogPluginImpl();
            conversationDelegate.setEmbeddedMode(true);
            conversationDelegate.init(dialog);
            Option option = (Option) entry;
            Map<String, MemoryAPI> optionMemoryMap = optionMemoryMaps.get(option);
            Map<String, MemoryAPI> memoryMap = conversationDelegate.getMemoryMap();//optionMemoryMap.get((Option)entry);
            memoryMap.put("topic", optionMemoryMap.get("topic"));
            if(optionMemoryMap.containsKey("topicFaction")) memoryMap.put("topicFaction", optionMemoryMap.get("topicFaction"));
            if(optionMemoryMap.containsKey("topicMarket")) memoryMap.put("topicMarket", optionMemoryMap.get("topicMarket"));
            memoryMap.get(MemKeys.LOCAL).set("$option", option.id);
            FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
        }
    }
    
}
