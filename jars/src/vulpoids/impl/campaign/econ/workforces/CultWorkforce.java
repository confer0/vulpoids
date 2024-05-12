package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class CultWorkforce extends BaseWorkforce {
    final int STAB_BONUS = 1;
    static float DAYS_PER_CHURCH_REP = 30;
    static RepLevel MAX_CHURCH_REP = RepLevel.FAVORABLE;
    float churchDays = 0;
    static float DAYS_PER_PATH_REP = 60;
    static RepLevel MAX_PATH_REP = RepLevel.SUSPICIOUS;
    float pathDays = 0;
    @Override
    public boolean isAvailableToPlayer() {
        return Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean("$unlockedVulpoidCults");
    }
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getStability().modifyFlat(id, STAB_BONUS, getName());
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getStability().unmodifyFlat(id);
    }
    @Override
    public void advance(float amount) {
        if(shouldApply() && market.isPlayerOwned()) {
            churchDays += Misc.getDays(amount);
            pathDays += Misc.getDays(amount);
            int churchDelta = (int)(churchDays / DAYS_PER_CHURCH_REP);
            int pathDelta = (int)(pathDays / DAYS_PER_PATH_REP);
            Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getRelToPlayer().adjustRelationship(churchDelta/100f, MAX_CHURCH_REP);
            Global.getSector().getFaction(Factions.LUDDIC_PATH).getRelToPlayer().adjustRelationship(pathDelta/100f, MAX_PATH_REP);
            churchDays -= churchDelta * DAYS_PER_CHURCH_REP;
            pathDays -= pathDelta * DAYS_PER_PATH_REP;
        }
    }
    @Override
    public boolean isTransient() {return false;}
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            tooltip.addPara("%s stability", opad, Misc.getHighlightColor(), "+"+STAB_BONUS);
            if(market.isPlayerOwned()) {
                float churchSignedMax = MAX_CHURCH_REP.getMax();
                if (MAX_CHURCH_REP.isNegative()) churchSignedMax = -1 * MAX_CHURCH_REP.getMin() - 0.01f;
                if(Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getRelToPlayer().getRel()>=churchSignedMax) {
                    tooltip.addPara("Cannot further increase Luddic Church reputation", opad);
                } else {
                    tooltip.addPara("Increases Luddic Church reputation every %s days", opad, Misc.getHighlightColor(), ""+(int)DAYS_PER_CHURCH_REP);
                }
                float pathSignedMax = MAX_CHURCH_REP.getMax();
                if (MAX_PATH_REP.isNegative()) pathSignedMax = -1 * MAX_PATH_REP.getMin() - 0.01f;
                if(Global.getSector().getFaction(Factions.LUDDIC_PATH).getRelToPlayer().getRel()>=pathSignedMax) {
                    tooltip.addPara("Cannot further increase Luddic Path reputation", opad);
                } else {
                    tooltip.addPara("Increases Luddic Path reputation every %s days", opad, Misc.getHighlightColor(), ""+(int)DAYS_PER_PATH_REP);
                }
            }
        }
    }
}
