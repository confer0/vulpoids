package vulpoids.impl.campaign.intel.events;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidAcceptanceProductionFactor extends BaseEventFactor {
    
    protected VulpoidAcceptanceEventIntel intel;
    protected long seed;
    
    
    public VulpoidAcceptanceProductionFactor(VulpoidAcceptanceEventIntel intel) {
        this.intel = intel;
    }
    
    @Override
    public int getProgress(BaseEventIntel intel) {
        return getProgressForProduction(Vulpoids.getVulpoidPeakProductionAmount());
    }
    public int getProgressForProduction(int prod) {
        if(prod==0) return 0;
        if(prod>6) prod = 6;
        return (int) Math.pow(2, prod);
    }
    
    
    public TooltipCreator getMainRowTooltip() {
        return getMainRowTooltip(intel);
    }
    
    public TooltipCreator getMainRowTooltip(final BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Exporting Vulpoids onto the wider markets of the Sector will slowly improve "+
                        "their widespread acceptance. Even if most people don't have reliable access to them, "+
                        "the fact that wealthy people are eager to have them will steer public opinion in its "+
                        "own right.", 0f);
                int prod = Vulpoids.getVulpoidPeakProductionAmount();
                String s = "s";
                if (prod==1) s="";
                if (prod>=6) {
                    tooltip.addPara("You're currently producing %s unit"+s+" of Vulpoids.", 0f, Misc.getHighlightColor(), ""+prod);
                } else {
                    tooltip.addPara("You're currently producing %s unit"+s+" of Vulpoids. "+
                            "If production were increased, acceptence would rise to %s per month.",
                            0f, Misc.getHighlightColor(), ""+prod, "+"+getProgressForProduction(prod+1));
                }
            }
        };
    }
    
    @Override
    public String getProgressStr(BaseEventIntel intel) {
        if (getProgress(intel) <= 0) return "";
        return super.getProgressStr(intel);
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Vulpoid Exports";
    }

    @Override
    public Color getDescColor(BaseEventIntel intel) {
        if (getProgress(intel) > 0) return Misc.getTextColor();
        return Misc.getGrayColor();
    }
}
