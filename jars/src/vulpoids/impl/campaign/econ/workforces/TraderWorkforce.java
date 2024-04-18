package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TraderWorkforce extends BaseWorkforce {
    final float INCOME_BONUS = 25f;
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getIncomeMult().modifyPercent(id, INCOME_BONUS, getName());
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getIncomeMult().unmodifyPercent(id);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Our Vulpoids are catering to visiting traders at the spaceports, offering free headpats and encouraging more trade.", opad);
            tooltip.addPara("%s colony income", opad, Misc.getHighlightColor(), "+" + (int)INCOME_BONUS + "%");
        }
    }
}
