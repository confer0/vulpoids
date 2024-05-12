package vulpoids.impl.campaign;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.DevMenuOptions;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.EndConversation;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.econ.workforces.*;


public class WorkforceInteractionDialogPlugin implements InteractionDialogPlugin {
    
    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;
    
    private MarketAPI market;
    
    Map<BaseWorkforce, Boolean> plugins;
    List<String> conditions;
    
    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        textPanel = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        market = dialog.getInteractionTarget().getMarket();
        
        conditions = new ArrayList();
        for (MarketConditionSpecAPI spec : Global.getSettings().getAllMarketConditionSpecs()) {
            try {
                Class conditionClass = Class.forName(spec.getScriptClass());
                if(conditionClass!=null && BaseWorkforce.class.isAssignableFrom(conditionClass)) {
                    try {
                        BaseWorkforce b = (BaseWorkforce)conditionClass.newInstance();
                        if(b.isAvailableToPlayer()) conditions.add(spec.getId());
                        else if(market.hasCondition(spec.getId())) conditions.add(spec.getId());  // In case an update bars access
                    } catch (InstantiationException | IllegalAccessException ex) {}
                }
            } catch (ClassNotFoundException ex) {}
        }
        
        dialog.setOptionOnEscape("Leave", "LEAVE");

        optionSelected(null, "INIT");
    }

    @Override
    public void optionSelected(String text, Object optionData) {
        if (optionData == null) return;
        String option = (String) optionData;
        if (text != null) {
            dialog.addOptionSelectedText(option);
        }
        switch(option) {
            case "INIT":
                textPanel.addPara("Vulpoids, while not particularly intelligent on their own, "+
                        "nonetheless take well to training. We can teach our Vulpoids to operate "+
                        "as part of a specialized workforce, providing various benefits for the "+
                        "colony.\n"+
                        "In most cases, the limiting factor will be the number of Vulpoids we can manage to train.");
            case "HUB":
                options.clearOptions();
                for (String condition : conditions) {
                    BaseWorkforce condition_plugin = getPluginForCondition(condition);
                    String tickbox = "[  ] ";
                    Color option_color = Misc.getButtonTextColor();
                    boolean enabled = true;
                    String tooltip = Global.getSettings().getMarketConditionSpec(condition).getDesc();
                    if(market.hasCondition(condition)) {
                        tickbox = "[X] ";
                        List<String> unmet_reqs = condition_plugin.getUnmetRequirements(false);
                        if(!unmet_reqs.isEmpty()) {
                            option_color = Misc.getNegativeHighlightColor();
                            tooltip += "\n\nUnmet Requirements: ";
                            for(String req : unmet_reqs) tooltip += req+", ";
                            tooltip = tooltip.substring(0, tooltip.length() - 2);
                        }
                    } else{
                        List<String> unmet_reqs = condition_plugin.getUnmetRequirements(true);
                        if(!unmet_reqs.isEmpty()) {
                            enabled = false;
                            tooltip += "\n\nUnmet Requirements: ";
                            for(String req : unmet_reqs) tooltip += req+", ";
                            tooltip = tooltip.substring(0, tooltip.length() - 2);
                        }
                    }
                    options.addOption(tickbox+Global.getSettings().getMarketConditionSpec(condition).getName(), condition, option_color, tooltip);
                    options.setEnabled(condition, enabled);
                }
                
                options.addOption("Leave", "LEAVE", null);
		options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
		
		if (Global.getSettings().isDevMode()) {
                    DevMenuOptions.addOptions(dialog);
		}
                break;
            case "LEAVE":
                Map<String, MemoryAPI> memoryMap = new HashMap();
                MemoryAPI memory = dialog.getInteractionTarget().getMemory();

                memoryMap.put(MemKeys.LOCAL, memory);
                if (dialog.getInteractionTarget().getFaction() != null) {
                        memoryMap.put(MemKeys.FACTION, dialog.getInteractionTarget().getFaction().getMemory());
                } else {
                        memoryMap.put(MemKeys.FACTION, Global.getFactory().createMemory());
                }
                memoryMap.put(MemKeys.GLOBAL, Global.getSector().getMemory());
                memoryMap.put(MemKeys.PLAYER, Global.getSector().getCharacterData().getMemory());

                if (dialog.getInteractionTarget().getMarket() != null) {
                        memoryMap.put(MemKeys.MARKET, dialog.getInteractionTarget().getMarket().getMemory());
                }
                RuleBasedInteractionDialogPluginImpl plugin = new RuleBasedInteractionDialogPluginImpl();
                plugin.init(dialog);
                dialog.setPlugin(plugin);
                //FireAll.fire(null, dialog, memoryMap, "PopulateOptions");
                new EndConversation().execute(null, dialog, new ArrayList(), memoryMap);
                break;
            default:
                // We assume that the options are correctly enabled/disabled, so just blindly un/apply them.
                if(market.hasCondition(option)) {
                    market.removeCondition(option);
                } else {
                    market.addCondition(option);
                }
                optionSelected(option, "HUB");
        }
    }
    
    private BaseWorkforce getPluginForCondition(String condition) {
        if(market.hasCondition(condition)) return (BaseWorkforce) market.getCondition(condition).getPlugin();
        String class_name = Global.getSettings().getMarketConditionSpec(condition).getScriptClass();
        try {
            Class c = Class.forName(class_name);
            BaseWorkforce b = (BaseWorkforce) c.newInstance();
            b.init(market, null);
            return b;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {
        
    }

    @Override
    public void advance(float amount) {
        
    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {
        
    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }
    
}
