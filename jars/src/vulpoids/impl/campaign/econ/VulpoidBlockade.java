package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class VulpoidBlockade extends BaseMarketConditionPlugin {
    
    public static float ACCESSIBILITY_PENALTY = 0.5f;
    
    public void apply(String id) {
        market.getAccessibilityMod().modifyFlat(id, -ACCESSIBILITY_PENALTY, Misc.ucFirst(getName().toLowerCase()));
    }

    public void unapply(String id) {
        market.getAccessibilityMod().unmodifyFlat(id);
    }
    
    @Override
    public void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        float opad = 10f;
        tooltip.addPara("%s accessibility.", 
                opad, Misc.getHighlightColor(),
                "-" + (int)Math.round(ACCESSIBILITY_PENALTY * 100f) + "%");
    }
}
