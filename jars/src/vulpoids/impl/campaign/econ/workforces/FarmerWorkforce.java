
package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class FarmerWorkforce extends BaseWorkforce {
    final int PROD_BONUS = 2;
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            Industry industry = market.getIndustry(Industries.FARMING);
            if(industry==null) industry = market.getIndustry(Industries.AQUACULTURE);
            if(industry!=null) {
                industry.getSupplyBonusFromOther().modifyFlat(id, PROD_BONUS, getName());
            }
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        Industry industry = market.getIndustry(Industries.FARMING);
        if(industry==null) industry = market.getIndustry(Industries.AQUACULTURE);
        if(industry!=null) {
            industry.getSupplyBonusFromOther().unmodifyFlat(id);
        }
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            tooltip.addPara("%s farming production", opad, Misc.getHighlightColor(), "+" + PROD_BONUS);
        }
    }
}
