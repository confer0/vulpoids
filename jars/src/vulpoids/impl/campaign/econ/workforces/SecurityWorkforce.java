package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.bases.LuddicPathCells;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class SecurityWorkforce extends BaseWorkforce {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            if(market.getCondition(Conditions.PATHER_CELLS) != null) ((LuddicPathCells)market.getCondition(Conditions.PATHER_CELLS).getPlugin()).getIntel().setSleeper(true);
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Our Vulpoids are working with our security forces, helping to \"sniff out\" criminal elements.", opad);
            tooltip.addPara("%s", opad, Misc.getHighlightColor(), "Pather activity suppressed");
        }
    }
}
