package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class MinerWorkforce extends BaseWorkforce {
    final int PROD_BONUS = 1;
    final int STAB_PENALTY = -2;
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getStability().modifyFlat(id, STAB_PENALTY, getName());
            Industry industry = market.getIndustry(Industries.MINING);
            if(industry != null) industry.getSupplyBonusFromOther().modifyFlat(id, PROD_BONUS, getName());
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getStability().unmodify(id);
        Industry industry = market.getIndustry(Industries.MINING);
        if(industry != null) {
            industry.getSupplyBonusFromOther().unmodifyFlat(id);
        }
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Our Vulpoids labor alongside our miners. These fluffies yearn for the mines!", opad);
            tooltip.addPara("%s mining production", opad, Misc.getHighlightColor(), "+" + PROD_BONUS);
            tooltip.addPara("%s stability", opad, Misc.getNegativeHighlightColor(), "" + STAB_PENALTY);
        }
    }
}
