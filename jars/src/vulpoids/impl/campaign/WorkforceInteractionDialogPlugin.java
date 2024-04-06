package vulpoids.impl.campaign;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.DevMenuOptions;
import java.util.Map;
import vulpoids.impl.campaign.econ.workforces.*;


public class WorkforceInteractionDialogPlugin implements InteractionDialogPlugin {
    
    private static enum OptionId {
        INIT,
        ADD_STABLE_CONFIRM,
        ADD_STABLE_DESCRIBE,
        //SCAN_BlACK_HOLE,
        DUMP_PLANETKILLER,
        DUMP_PLANETKILLER_ON_SECOND_THOUGHT,
        DUMP_PLANETKILLER_CONT_1,
        ADD_STABLE_NEVER_MIND,
        LEAVE,
    }
    
    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;
    
    private MarketAPI market;
    
    Map<BaseWorkforce, Boolean> plugins;
    String[] conditions = new String[] {
        "vulpoid_traders",
        "vulpoid_security",
        "vulpoid_miners",
        "vulpoid_clerks",
        "vulpoid_maintenance",
        "vulpoid_servants",
    };
    /*Class[] test = new Class[]{
        TraderWorkforce.class,
        SecurityWorkforce.class,
        MinerWorkforce.class,
        ClerkWorkforce.class,
        MaintenanceWorkforce.class,
        ServantWorkforce.class,
    };*/
    
    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        textPanel = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        market = dialog.getInteractionTarget().getMarket();

        //visual.setVisualFade(0.25f, 0.25f);

        /*if (planet.getCustomInteractionDialogImageVisual() != null) {
                visual.showImageVisual(planet.getCustomInteractionDialogImageVisual());
        } else {
                if (!Global.getSettings().getBoolean("3dPlanetBGInInteractionDialog")) {
                        visual.showPlanetInfo(planet);
                }
        }*/
        
        dialog.setOptionOnEscape("Leave", OptionId.LEAVE);

        optionSelected(null, OptionId.INIT);
    }

    @Override
    public void optionSelected(String text, Object optionData) {
        if (optionData == null) return;
        OptionId option = (OptionId) optionData;
        if (text != null) {
            dialog.addOptionSelectedText(option);
        }
        switch(option) {
            case INIT:
                options.clearOptions();
                for (String condition : conditions) {
                    BaseWorkforce test = getPluginForCondition(condition);
                    String reqs = "";
                    for (String req : test.getUnmetRequirements()) {
                        reqs += req + ", ";
                    }
                    textPanel.addPara(condition + ": " + reqs);
                }
                
                options.addOption("Leave", OptionId.LEAVE, null);
		options.setShortcut(OptionId.LEAVE, Keyboard.KEY_ESCAPE, false, false, false, true);
		
		if (Global.getSettings().isDevMode()) {
                    DevMenuOptions.addOptions(dialog);
		}
                break;
            case LEAVE:
                //Global.getSector().setPaused(false);
                dialog.dismiss();
                break;
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
