package vulpoids.impl.campaign.intel.events;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator;

public class VulpoidAcceptanceProductionMissionFactor extends BaseEventFactor {
    
    protected VulpoidAcceptanceEventIntel intel;
    protected int monthlyProgress;
    protected String name;
    protected String description;
    
    public VulpoidAcceptanceProductionMissionFactor(VulpoidAcceptanceEventIntel intel, int monthlyProgress, String name, String description) {
        this.intel = intel;
        this.monthlyProgress = monthlyProgress;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public int getProgress(BaseEventIntel intel) {
        return monthlyProgress;
    }
    
    
    @Override
    public TooltipCreator getMainRowTooltip() {
        return getMainRowTooltip(intel);
    }
    
    @Override
    public TooltipCreator getMainRowTooltip(final BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(description, 0f);
            }
        };
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        return name;
    }
}
