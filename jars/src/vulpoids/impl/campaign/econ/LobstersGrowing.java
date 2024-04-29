
package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.impl.campaign.ids.Vulpoids;

public class LobstersGrowing extends BaseMarketConditionPlugin {
    
    static float DAYS_FOR_LOBSTERS = 365;
    float timer = DAYS_FOR_LOBSTERS;
    @Override
    public void advance(float amount) {
        if(hasBiofactory()) timer -= Misc.getDays(amount);
        if(timer<=0) timer = 0;
    }
    public boolean isFinished() {
        return timer==0;
    }
    
    protected boolean hasBiofactory() {
        Industry industry = market.getIndustry(Vulpoids.INDUSTRY_ORGANFARM);
        if(industry==null) industry = market.getIndustry(Vulpoids.INDUSTRY_BIOFACILITY);
        return industry!=null && industry.getSpecialItem()!=null && industry.getSpecialItem().getId().equals(Vulpoids.LOBSTER_BIOFORGE_ITEM);
    }
    
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(timer>0 && hasBiofactory()) {
            tooltip.addPara("%s days until population becomes sustainable", 10f, Misc.getHighlightColor(), (int)(timer)+"");
        } else if (timer>0) {
            tooltip.addPara("No production - growth stagnant", 10f);
        }
    }
    
    @Override
    public boolean isTransient() {return false;}
}
