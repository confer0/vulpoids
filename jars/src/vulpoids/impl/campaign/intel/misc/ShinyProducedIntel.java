package vulpoids.impl.campaign.intel.misc;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShinyProducedIntel extends FleetLogIntel {

    protected MarketAPI market;

    public ShinyProducedIntel(MarketAPI market) {
            //super(null, planet);
            this.market = market;
            Global.getSector().addScript(this);

            setIcon(Global.getSettings().getSpriteName("intel", "shiny_produced"));
            setSound("ui_intel_vulpoid_produced");
            setDuration(30f);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }
    
    
    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);

        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == ListInfoMode.IN_DESC) initPad = opad;

        Color tc = getBulletColorForMode(mode);
        if (market != null) {
            bullet(info);
            info.addPara("Occured at your colony on " + market.getName() + "", tc, initPad);
            unindent(info);
        }
    }
    
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color tc = Misc.getTextColor();
        float opad = 10f;

        info.addPara("You've received a report that the biofactory on "+market.getName()+
                    " has produced a Vulpoid with exceptional cognitive abilities. "+
                    // wording was a bit strange imo
                    "\nIt has been removed from the production line and transferred to your personal suite.", opad);
                    // i can imagine a poor fox being put into a fucking warehouse straight off the bat xd

        float days = getDaysSincePlayerVisible();
        if (days >= 1) {
            addDays(info, "ago.", days, tc, opad);
        }
    }
    
    @Override
    public String getName() {
        return "Profecto Vulpoid Produced";
    }
    
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return market.getPrimaryEntity();
    }
}
