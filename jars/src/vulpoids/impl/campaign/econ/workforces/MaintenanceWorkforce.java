package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class MaintenanceWorkforce extends BaseWorkforce {
    final int ACCESS_BONUS = 30;
    
    @Override
    public String[] getRequirements() {
        return new String[]{VULP_POP_6};
    }
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getAccessibilityMod().modifyFlat(id, ACCESS_BONUS/100f, getName());
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getAccessibilityMod().unmodifyFlat(id);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Our Vulpoids are assisting in orbital operations and stellar communications, greatly improving logistics.", opad);
            tooltip.addPara("%s colony accessibility", opad, Misc.getHighlightColor(), "+"+ACCESS_BONUS+"%");
        }
    }
}
