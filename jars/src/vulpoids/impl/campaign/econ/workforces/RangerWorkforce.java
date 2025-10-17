
package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class RangerWorkforce extends BaseWorkforce {
    final float HAB_BONUS = -0.125f;
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getHazard().modifyFlat(id, HAB_BONUS, "Vulpoid Rangers");
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getHazard().unmodify(id);
    }
    @Override
    public String getTooltipIllustrationId() {return "vulpworkforce_ranger";}
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            tooltip.addPara("%s hazard rating", opad, Misc.getHighlightColor(), (HAB_BONUS*100)+"%");
        }
    }
    @Override
    public boolean isAvailableToPlayer() {
        return false;
    }
    @Override
    public void linkCodexEntries() {
        super.linkCodexEntries();
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getPlanetEntryId(Planets.BARREN_DESERT)
            );
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getPlanetEntryId(Planets.DESERT)
            );
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getPlanetEntryId(Planets.DESERT1)
            );
    }
    public String[] getRequirements() {
        return new String[]{"failing pre-Collapse terraforming"};
    }
}
