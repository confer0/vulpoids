package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ServantWorkforce extends BaseWorkforce {
    final int STABILITY_BONUS = 4;
    final int DRUG_DEMAND_REDUCTION = 1;
    
    @Override
    public String[] getRequirements() {
        return new String[]{POP_RATIO_1};
    }
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getStability().modifyFlat(id, STABILITY_BONUS, getName());
            market.getIndustry(Industries.POPULATION).getDemand(Commodities.DRUGS).getQuantity().modifyFlat(id, -DRUG_DEMAND_REDUCTION, getName());
            Industry mining = market.getIndustry(Industries.MINING);
            if(mining!=null) mining.getDemand(Commodities.DRUGS).getQuantity().modifyFlat(id, -DRUG_DEMAND_REDUCTION, getName());
        }
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getStability().unmodifyFlat(id);
        Industry population = market.getIndustry(Industries.POPULATION);
        if(population!=null) population.getDemand(Commodities.DRUGS).getQuantity().unmodifyFlat(id);
        Industry mining = market.getIndustry(Industries.MINING);
        if(mining!=null) mining.getDemand(Commodities.DRUGS).getQuantity().unmodifyFlat(id);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Our Vulpoids are distributed across households and public spaces, ensuring the entire population is attended to.", opad);
            tooltip.addPara("%s stability", opad, Misc.getHighlightColor(), "+"+STABILITY_BONUS);
            tooltip.addPara("%s recreational drugs demand", opad, Misc.getHighlightColor(), "-"+DRUG_DEMAND_REDUCTION);
        }
    }
}
