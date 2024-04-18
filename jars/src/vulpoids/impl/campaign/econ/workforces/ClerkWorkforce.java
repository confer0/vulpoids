package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ClerkWorkforce extends BaseWorkforce {
    final float INCOME_MULT = 0.8f;
    final int BONUS_INDUSTRIES = 1;
    
    @Override
    public String[] getRequirements() {
        return new String[]{VULP_POP_6, POP_RATIO_1};
    }
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).modifyFlat(id, BONUS_INDUSTRIES);
            market.getIncomeMult().modifyMult(id, INCOME_MULT, getName());
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodifyFlat(id);
        market.getIncomeMult().unmodifyMult(id);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Our Vulpoids are working client service and basic white-collar jobs, freeing human workers for higher-skilled labor.", opad);
            tooltip.addPara("%s available industries", opad, Misc.getHighlightColor(), "+" + BONUS_INDUSTRIES);
            tooltip.addPara("%s colony income", opad, Misc.getNegativeHighlightColor(), "x" + INCOME_MULT);
        }
    }
}
