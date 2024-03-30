package vulpoids.impl.campaign.intel.misc;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShinyProducedIntel extends FleetLogIntel {

    protected MarketAPI market;

    public ShinyProducedIntel(MarketAPI market) {
            //super(null, planet);
            this.market = market;
            Global.getSector().addScript(this);

            setIcon(Global.getSettings().getSpriteName("intel", "shiny_produced"));
            setSound("ui_cargo_vulpoid_drop");
            setDuration(30f);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }



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
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        info.addPara("You've received a report that the biofactory on "+market.getName()+
                    " has produced a Vulpoid with anomalously strong cognitive ability. "+
                    "It has been removed from the production line and transferred to your personal storage.", opad);

        float days = getDaysSincePlayerVisible();
        if (days >= 1) {
            addDays(info, "ago.", days, tc, opad);
        }
    }

    public String getName() {
        return "Prefecto Vulpoid Produced";
    }

}
