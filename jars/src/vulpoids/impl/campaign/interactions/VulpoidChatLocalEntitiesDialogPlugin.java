package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.Option;
import com.fs.starfarer.api.campaign.rules.RuleAPI;
import com.fs.starfarer.api.campaign.rules.RulesAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VulpoidChatLocalEntitiesDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    float MAX_RANGE = 250f;
    Map<Option, Map<String, MemoryAPI>> optionMemoryMaps;
    Map<Option, SectorEntityToken> optionFirstEntity;
    
    @Override
    protected void loadOptions() {
        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        LocationAPI location;
        RulesAPI rules = Global.getSector().getRules();
        optionMemoryMaps = new HashMap();
        optionFirstEntity = new HashMap();
        
        if(Global.getSector().getPlayerFleet().isInHyperspace()) location = Global.getSector().getHyperspace();
        else location = Global.getSector().getPlayerFleet().getStarSystem();
        
        if (location != null) {  // Just to be safe
            // TODO - any way to sort by distance?
            
            List<Pair<SectorEntityToken, Float>> entitiesInRange = new ArrayList();
            
            for (SectorEntityToken entity : location.getAllEntities()) {
                if(!(entity instanceof CampaignTerrainAPI)) {  // Because we add it later
                    float dist = Misc.getDistance(playerFleet, entity);
                    dist -= playerFleet.getRadius();
                    dist -= entity.getRadius();
                    if (dist<=MAX_RANGE) entitiesInRange.add(new Pair(entity, dist));
                }
            }
            Collections.sort(entitiesInRange,new Comparator<Pair<SectorEntityToken, Float>>() {
                @Override
                public int compare(Pair<SectorEntityToken, Float> p1, Pair<SectorEntityToken, Float> p2) {
                    float d = p1.two-p2.two;
                    if (d<0) return -1;
                    if (d>0) return 1;
                    return 0;
                }
            });
            // TODO - consider sub-sorting these terrains?
            // Want them to always be on top, but if there's more than one it should sort by relative distance.
            for (CampaignTerrainAPI terrain : location.getTerrainCopy()) {
                if (terrain.getPlugin().containsEntity(playerFleet)) entitiesInRange.add(0, new Pair(terrain, 0));
            }
            
            for (Pair<SectorEntityToken, Float> p : entitiesInRange) {
                SectorEntityToken entity = p.one;
                Map<String, MemoryAPI> memoryMap = getMemoryForEntity(entity);
                RuleAPI rule = rules.getBestMatching(null, "VulpoidChatSurroundingsOptions", null, memoryMap);
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
                        boolean optionIsUnique = true;
                        for(Object entry : entries) {
                            Option existingOption = (Option) entry;
                            if(existingOption.id.equals(option.id) &&
                                    existingOption.order==option.order &&
                                    existingOption.text.equals(option.text)) {
                                optionIsUnique = false;
                                break;
                            }
                        }
                        if(optionIsUnique) {
                            entries.add(option);
                            optionMemoryMaps.put(option, memoryMap);
                            optionFirstEntity.put(option, entity);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected Color getEntryColor(Object entry) {
        if(!(entry instanceof Option) || optionFirstEntity.get((Option)entry)==null) {
            return Misc.getButtonTextColor();
        } else {
            SectorEntityToken entity = optionFirstEntity.get((Option)entry);
            if(!entity.getFaction().getId().equals(Factions.NEUTRAL)) return entity.getFaction().getBaseUIColor();
            if(entity instanceof PlanetAPI) return ((PlanetAPI)entity).getSpec().getIconColor();
            if(entity instanceof CampaignTerrainAPI) return ((CampaignTerrainAPI)entity).getPlugin().getNameColor();
            //return entity.getLightColor();
            return entity.getIndicatorColor();
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
        Map<String, MemoryAPI> memoryMap = new HashMap();//conversationDelegate.getMemoryMap();
        for(String key : conversationDelegate.getMemoryMap().keySet()) {
            memoryMap.put(key, conversationDelegate.getMemoryMap().get(key));
        }
        memoryMap.put("topic", entity.getMemory());
        if (entity.getFaction() != null) memoryMap.put("topicFaction", entity.getFaction().getMemory());
        else memoryMap.put("topicFaction", Global.getFactory().createMemory());
        if (entity.getMarket() != null) memoryMap.put("topicMarket", entity.getMarket().getMemory());
        
        if(entity instanceof CampaignTerrainAPI) memoryMap.get("topic").set("$terrainName", ((CampaignTerrainAPI)entity).getPlugin().getTerrainName(), 0);
        
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
