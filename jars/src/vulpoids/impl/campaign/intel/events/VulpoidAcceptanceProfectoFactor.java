package vulpoids.impl.campaign.intel.events;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator;
import com.fs.starfarer.api.util.Misc;

public class VulpoidAcceptanceProfectoFactor extends BaseOneTimeFactor {
    protected String desc;
    
    public VulpoidAcceptanceProfectoFactor(String desc, int points) {
        super(points);
        this.desc = desc;
    }
    @Override
    public String getDesc(BaseEventIntel intel) {
        return desc;
    }
    @Override
    public TooltipCreator getMainRowTooltip() {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Selling %s to important people will, one way or another, end up "+
                        "encouraging the implementation of more pro-Vulpoid policies, or otherwise "+
                        "result in actions that improve their public perception. More skilled "+
                        "Profectos will have a larger impact.",
                        0f, Misc.getHighlightColor(), "Profecto Vulpoids");
            }
        };
    }
}
