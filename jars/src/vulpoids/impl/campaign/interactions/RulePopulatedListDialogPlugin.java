package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.Option;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class RulePopulatedListDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    Map<Option, Map<String, MemoryAPI>> optionMemoryMaps;
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        optionMemoryMaps = new HashMap();
    }
    
    @Override
    protected Color getEntryColor(Object entry) {
        if(!(entry instanceof Option)) return Misc.getButtonTextColor();
        Option option = (Option) entry;
        if(optionMemoryMaps.get(option).get(MemKeys.LOCAL).contains("$optionColor:"+(option).id)) {
            String colorString =  optionMemoryMaps.get(option).get(MemKeys.LOCAL).getString("$optionColor:"+(option).id);
            String[] rgb = colorString.split(",");
            return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
        }
        return null;
    }
    
    @Override
    protected boolean getEntryEnabled(Object entry) {
        if(entry instanceof Option) {
            Option option = (Option) entry;
            return !optionMemoryMaps.get(option).get(MemKeys.LOCAL).getBoolean("$optionDisabled:"+(option).id);
        }
        return super.getEntryEnabled(entry);
    }
    
    @Override
    protected String getEntryConfirmation(Object entry) {
        if(!(entry instanceof Option)) return null;
        Option option = (Option) entry;
        if(optionMemoryMaps.get(option).get(MemKeys.LOCAL).contains("$optionConfirmation:"+(option).id)) {
            return optionMemoryMaps.get(option).get(MemKeys.LOCAL).getString("$optionConfirmation:"+(option).id);
        }
        return null;
    }
    
    @Override
    protected String getEntryTooltipString(Object entry) {
        if(!(entry instanceof Option)) return null;
        Option option = (Option) entry;
        if(optionMemoryMaps.get(option).get(MemKeys.LOCAL).contains("$optionTooltip:"+(option).id)) {
            return  optionMemoryMaps.get(option).get(MemKeys.LOCAL).getString("$optionTooltip:"+(option).id);
        }
        return null;
    }
    
    protected Map<String, MemoryAPI> getMemoryMapCopy() {
        Map<String, MemoryAPI> memoryMap = new HashMap();
        for(String key : conversationDelegate.getMemoryMap().keySet()) {
            memoryMap.put(key, conversationDelegate.getMemoryMap().get(key));
        }
        return memoryMap;
    }
    
    @Override
    protected String getEntryLabel(Object entry) {
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
            for(String key : optionMemoryMap.keySet()) {
                if(!memoryMap.containsKey(key)) {
                    memoryMap.put(key, optionMemoryMap.get(key));
                }
            }
            memoryMap.get(MemKeys.LOCAL).set("$option", option.id);
            FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
        }
    }
    
}
