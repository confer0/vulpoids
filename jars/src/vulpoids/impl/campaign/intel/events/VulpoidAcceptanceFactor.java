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

public class VulpoidAcceptanceFactor extends BaseEventFactor {
    
    protected VulpoidAcceptanceEventIntel intel;
    protected long seed;
    
    public VulpoidAcceptanceFactor(VulpoidAcceptanceEventIntel intel) {
        this.intel = intel;
    }
    
    public int getProgress(BaseEventIntel intel) {
        int progress = 0;
        for (MarketAPI market : Misc.getPlayerMarkets(false)) {
            int prod = market.getCommodityData(Vulpoids.CARGO_ITEM).getMaxSupply();
            if (prod<=0) continue;
            //score += market.getCommodityData(Vulpoids.CARGO_ITEM).getAvailableStat().getModifiedInt();
            //score += market.getCommodityData(Vulpoids.CARGO_ITEM).getMaxSupply();
            progress += prod;
        }
        return progress;
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
