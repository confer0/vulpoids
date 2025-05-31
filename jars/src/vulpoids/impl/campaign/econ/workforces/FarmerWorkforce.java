
package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.impl.campaign.ids.Vulpoids;

public class FarmerWorkforce extends BaseWorkforce {
    final int PROD_BONUS = 2;
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            Industry industry = Vulpoids.getFarming(market);
            if(industry!=null) {
                industry.getSupplyBonusFromOther().modifyFlat(id, PROD_BONUS, getName());
            }
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        Industry industry = Vulpoids.getFarming(market);
        if(industry!=null) {
            industry.getSupplyBonusFromOther().unmodifyFlat(id);
        }
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            tooltip.addPara("%s farming production", opad, Misc.getHighlightColor(), "+" + PROD_BONUS);
        }
    }
    @Override
    public void linkCodexEntries() {
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getIndustryEntryId(Industries.FARMING)
            );
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getIndustryEntryId(Industries.AQUACULTURE)
            );
        
        // Ashes Of The Domain
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getIndustryEntryId("monoculture")
            );
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getIndustryEntryId("artifarming")
            );
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getIndustryEntryId("subfarming")
            );
        CodexDataV2.makeRelated(
                    CodexDataV2.getConditionEntryId(condition.getId()),
                    CodexDataV2.getIndustryEntryId("fishery")
            );
    }
}
