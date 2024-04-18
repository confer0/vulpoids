package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;

public class FilteredAir extends BaseMarketConditionPlugin {
    
    String[] suppressedConditions = new String[]{
        Conditions.POLLUTION,
        Conditions.EXTREME_WEATHER,
        Conditions.TOXIC_ATMOSPHERE
    };
    float HAZARD_IF_COMFORTABLE = -0.25f;
    
    float DAYS_TO_REMOVE_POLLUTION = 5;
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
        return pollutionRemovalTimer<=0;
    }
    
    public void apply(String id) {
        boolean nothingSuppressed = true;
        for (String suppressedCondition : suppressedConditions) {
            if(market.hasCondition(suppressedCondition)) {
                nothingSuppressed = false;
                market.suppressCondition(suppressedCondition);
            }
        }
        if(nothingSuppressed) {
            market.getHazard().modifyFlat(id, HAZARD_IF_COMFORTABLE, getName());
        }
    }
    
    public void unapply(String id) {
        market.getHazard().unmodifyFlat(id);
        for (String suppressedCondition : suppressedConditions) {
            if(market.hasCondition(suppressedCondition)) {
                market.unsuppressCondition(suppressedCondition);
            }
        }
    }
    
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        
        List<String> conds = new ArrayList();
        for (String suppressedCondition : suppressedConditions) {
            if(market.hasCondition(suppressedCondition)) conds.add(suppressedCondition);
        }
        if(conds.isEmpty()) {
            tooltip.addPara("%s hazard rating", 10f, Misc.getHighlightColor(), (int)(HAZARD_IF_COMFORTABLE * 100f) + "%");
        } else {
            tooltip.addPara("Countering the effects of " + Misc.getAndJoined(conds) + ".", 10f);
        }
        if(pollutionRemovalTimer>0 && market.hasCondition(Conditions.POLLUTION)) {
            tooltip.addPara("%s days until pollution removal", 10f, Misc.getHighlightColor(), (int)(pollutionRemovalTimer)+"");
        }
    }
    
    @Override
    public boolean isTransient() {return false;}
}
