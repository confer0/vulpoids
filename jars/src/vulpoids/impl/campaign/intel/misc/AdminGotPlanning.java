package vulpoids.impl.campaign.intel.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class AdminGotPlanning extends FleetLogIntel {
    
    protected MarketAPI market;
    protected PersonAPI person;

    public AdminGotPlanning(MarketAPI market, PersonAPI person) {
            //super(null, planet);
            this.market = market;
            this.person = person;
            Global.getSector().addScript(this);

            setIcon("graphics/icons/skills/industrial_planning.png");
            setSound("ui_intel_vulpoid_admin_level_up");
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

        info.addPara("You've received a report from your administrator "+person.getNameString()+" on "+market.getName()+
                    " that she has improved her skillset.", opad);

        float days = getDaysSincePlayerVisible();
        if (days >= 1) {
            addDays(info, "ago.", days, tc, opad);
        }
    }
    
    @Override
    public String getName() {
        return "Administrator Gained Skill";
    }
    
}
