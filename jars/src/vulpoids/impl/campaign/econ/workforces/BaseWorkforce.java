package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class BaseWorkforce extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        market.getMemoryWithoutUpdate().set("$workforces", market.getMemoryWithoutUpdate().getInt("$workforces") + 1);
    }
    @Override
    public void unapply(String id) {
        market.getMemoryWithoutUpdate().set("$workforces", market.getMemoryWithoutUpdate().getInt("$workforces") - 1);
    }
    protected boolean shouldApply() {
        return market.getMemoryWithoutUpdate().getInt("$workforces") <= market.getMemoryWithoutUpdate().getInt("$workforce_cap");
    }
    @Override
    public String getIconName() {
        if(shouldApply()) return condition.getSpec().getIcon();
        return "graphics/icons/markets/workforce_confusion.png";
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        if(!shouldApply()) {
            float opad = 10f;
            tooltip.addPara("Due to a loss of population or some other factor, %s.", opad, Misc.getNegativeHighlightColor(), "our workforces are in disarray");
            tooltip.addPara("%s", opad, Misc.getHighlightColor(), "Suspend some workforces to restore normal operation");
        }
    }
}
