package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;

public class FilteredAir extends BaseMarketConditionPlugin {
    
    static String[] suppressableConditions = new String[]{
        Conditions.POLLUTION,
        Conditions.EXTREME_WEATHER,
        Conditions.TOXIC_ATMOSPHERE
    };
    static float HAZARD_IF_COMFORTABLE = -0.25f;
    
    static float DAYS_TO_REMOVE_POLLUTION = 365;
    float pollutionRemovalTimer = DAYS_TO_REMOVE_POLLUTION;
    @Override
    public void advance(float amount) {
        //if(pollutionRemovalTimer==0 && market.hasCondition(Conditions.POLLUTION)) pollutionRemovalTimer = DAYS_TO_REMOVE_POLLUTION;
        pollutionRemovalTimer -= Misc.getDays(amount);
        if(pollutionRemovalTimer<=0) pollutionRemovalTimer = 0;
        if(!market.hasCondition(Conditions.POLLUTION)) pollutionRemovalTimer = 0;
    }
    public float getPollutionRemovalTimer() {return pollutionRemovalTimer;}
    public void setPollutionRemovalTimer(float amount) {pollutionRemovalTimer=amount;}
    public boolean shouldRemovePollution() {
        return pollutionRemovalTimer==0;
    }
    
    @Override
    public void apply(String id) {
        boolean nothingSuppressed = true;
        for (String suppressedCondition : getSuppressedConditions()) {
            nothingSuppressed = false;
            market.suppressCondition(suppressedCondition);
        }
        if(nothingSuppressed) {
            market.getHazard().modifyFlat(id, HAZARD_IF_COMFORTABLE, getName());
        }
    }
    
    @Override
    public void unapply(String id) {
        market.getHazard().unmodifyFlat(id);
        for (String suppressedCondition : getSuppressedConditions()) {
            market.unsuppressCondition(suppressedCondition);
        }
    }
    public List<String> getSuppressedConditions() {
        List<String> conds = new ArrayList();
        for (String suppressedCondition : suppressableConditions) {
            if(market.hasCondition(suppressedCondition)) {
                conds.add(suppressedCondition);
            }
        }
        return conds;
    }
    
    @Override
    public String getIconName() {
        if(getSuppressedConditions().isEmpty()) {
            if(market.hasCondition(Conditions.MILD_CLIMATE)) return "graphics/icons/markets/terraformer_idyllic.png";
            else return "graphics/icons/markets/terraformer_improvement.png";
        }
        return "graphics/icons/markets/terraformer_supression.png";
    }
    @Override
    public String getName() {
        if(getSuppressedConditions().isEmpty()) {
            if(market.hasCondition(Conditions.MILD_CLIMATE)) return "Edenic Climate";
            else return "Fine-Tuned Climate";
        }
        return condition.getSpec().getName();
    }
    
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        
        List<String> conds = new ArrayList();
        for (String suppressedCondition : getSuppressedConditions()) {
            conds.add(Global.getSettings().getMarketConditionSpec(suppressedCondition).getName());
        }
        if(conds.isEmpty()) {
            if(market.hasCondition(Conditions.MILD_CLIMATE)) {
                String climateSpecificBlurb = "and dazzling auroras dance in the night sky.";
                if(market.hasCondition(Conditions.HOT)) climateSpecificBlurb = "and the heat is always dry and breezy.";
                if(market.hasCondition(Conditions.COLD)) climateSpecificBlurb = "and snow always falls light and puffy.";
                tooltip.addPara("The already-beautiful conditions have been further refined into an edenic state. Every breath of clean air is invigorating, puffy white clouds self-arrange into playful shapes, "+climateSpecificBlurb, 10f);
            } else {
                tooltip.addPara("The planet's weather is being carefully nurtured into smooth and delicate patterns like those of Gilead or old Mairaath.", 10f);
            }
            tooltip.addPara("%s hazard rating", 10f, Misc.getHighlightColor(), (int)(HAZARD_IF_COMFORTABLE * 100f) + "%");
        } else {
            tooltip.addPara("The climate manipulation systems are currently working to suppress negative atmospheric conditions.", 10f);
            tooltip.addPara("Countering the effects of " + Misc.getAndJoined(conds) + ".", 10f);
        }
        if(pollutionRemovalTimer>0 && market.hasCondition(Conditions.POLLUTION)) {
            tooltip.addPara("%s days until pollution removal", 10f, Misc.getHighlightColor(), (int)(pollutionRemovalTimer)+"");
        }
    }
    
    @Override
    public boolean isTransient() {return false;}
}
