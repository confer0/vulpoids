package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.Option;
import com.fs.starfarer.api.campaign.rules.RuleAPI;
import com.fs.starfarer.api.campaign.rules.RulesAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VulpoidChatLocalEntitiesDialogPlugin extends RulePopulatedListDialogPlugin {
    
    float MAX_RANGE = 250f;
    Map<Option, SectorEntityToken> optionFirstEntity;
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        optionFirstEntity = new HashMap();
        
        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        LocationAPI location;
        RulesAPI rules = Global.getSector().getRules();
        
        if(Global.getSector().getPlayerFleet().isInHyperspace()) location = Global.getSector().getHyperspace();
        else location = Global.getSector().getPlayerFleet().getStarSystem();
        
        if (location != null) {
            
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
                            rule.runScript(dialog, memoryMap);
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
        Color c = super.getEntryColor(entry);
        if(c!=null) return c;
        SectorEntityToken entity = optionFirstEntity.get((Option)entry);
        if(!entity.getFaction().getId().equals(Factions.NEUTRAL)) return entity.getFaction().getBaseUIColor();
        if(entity instanceof PlanetAPI) return ((PlanetAPI)entity).getSpec().getIconColor();
        if(entity instanceof CampaignTerrainAPI) return ((CampaignTerrainAPI)entity).getPlugin().getNameColor();
        //return entity.getLightColor();
        //return entity.getIndicatorColor();
        return null;
    }
    
    protected Map<String, MemoryAPI> getMemoryForEntity(SectorEntityToken entity) {
        Map<String, MemoryAPI> memoryMap = getMemoryMapCopy();
        memoryMap.put("topic", entity.getMemory());
        if (entity.getFaction() != null) memoryMap.put("topicFaction", entity.getFaction().getMemory());
        else memoryMap.put("topicFaction", Global.getFactory().createMemory());
        if (entity.getMarket() != null) memoryMap.put("topicMarket", entity.getMarket().getMemory());
        
        if(entity instanceof CampaignTerrainAPI) memoryMap.get("topic").set("$terrainName", ((CampaignTerrainAPI)entity).getPlugin().getTerrainName(), 0);
        
        return memoryMap;
    }
}
