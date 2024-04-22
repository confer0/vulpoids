package vulpoids.impl.campaign.intel.events;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class VulpoidAcceptanceGenericOneTimeFactor extends BaseOneTimeFactor {
    protected String desc;
    protected String tooltipText;
    
    public VulpoidAcceptanceGenericOneTimeFactor(String desc, String tooltipText, int points) {
        super(points);
        this.desc = desc;
        this.tooltipText = tooltipText;
    }
    @Override
    public String getDesc(BaseEventIntel intel) {
        return desc;
    }
    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip() {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f);
            }
        };
    }
}
