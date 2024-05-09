package vulpoids.impl.campaign.intel.misc;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class VulpPopGrownIntel extends FleetLogIntel {

    protected MarketAPI market;
    protected int newSize;

    public VulpPopGrownIntel(MarketAPI market, int newSize) {
            //super(null, planet);
            this.market = market;
            this.newSize = newSize;
            Global.getSector().addScript(this);

            setIcon(Global.getSettings().getSpriteName("intel", "vulp_pop_growth"));
            setSound("ui_intel_vulpoid_pop_grown");
            setDuration(30f);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }



    public void createIntelInfo(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);

        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == IntelInfoPlugin.ListInfoMode.IN_DESC) initPad = opad;

        Color tc = getBulletColorForMode(mode);
        if (market != null) {
            bullet(info);
            info.addPara("Vulpoid population of " + market.getName() + " has increased to size "+newSize+".", tc, initPad);
            unindent(info);
        }
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color tc = Misc.getTextColor();
        float opad = 10f;

        info.addPara("Continuous production of Vulpoids has increased their population on "+market.getName()+" to size "+newSize+".", opad);

        float days = getDaysSincePlayerVisible();
        if (days >= 1) {
            addDays(info, "ago.", days, tc, opad);
        }
    }

    public String getName() {
        return "Vulpoid Population Increased";
    }
    
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return market.getPrimaryEntity();
    }
}
