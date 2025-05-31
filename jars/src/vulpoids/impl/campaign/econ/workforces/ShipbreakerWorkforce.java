
package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShipbreakerWorkforce extends BaseWorkforce {
    final float UPKEEP_MULT = 0.75f;
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            if(market.hasIndustry(Industries.POPULATION)) market.getIndustry(Industries.POPULATION).getUpkeep().modifyMult(id, UPKEEP_MULT, "Vulpoid Shipbreakers");
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        if(market.hasIndustry(Industries.POPULATION)) market.getIndustry(Industries.POPULATION).getUpkeep().unmodify(id);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            tooltip.addPara("%s Population & Infrastructure upkeep", opad, Misc.getHighlightColor(), (int)((UPKEEP_MULT-1)*100)+"%");
        }
    }
    public boolean isAvailableToPlayer() {
        return false;
    }
    public String[] getRequirements() {
        return new String[]{"established shipbreaking operations"};
    }
}
