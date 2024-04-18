
package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class CelebrityWorkforce extends BaseWorkforce implements MarketImmigrationModifier {
    final float INCOME_BONUS = 10f;
    final float IMMIGRATION_BASE = 5f;
    final int GOODS_DEMAND_INCREASE = 1;
    
    @Override
    public void apply(String id) {
        super.apply(id);
        if(shouldApply()) {
            market.getIncomeMult().modifyPercent(id, INCOME_BONUS, getName());
            market.addTransientImmigrationModifier(this);
            Industry population = market.getIndustry(Industries.POPULATION);
            if(population!=null) {
                population.getDemand(Commodities.DOMESTIC_GOODS).getQuantity().modifyFlat(id, GOODS_DEMAND_INCREASE, getName());
                population.getDemand(Commodities.LUXURY_GOODS).getQuantity().modifyFlat(id, GOODS_DEMAND_INCREASE, getName());
                population.getDemand(Commodities.DRUGS).getQuantity().modifyFlat(id, GOODS_DEMAND_INCREASE, getName());
            }
        }
    }
    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        float bonus = getImmigrationBonus();
        incoming.add(Factions.INDEPENDENT, bonus);
        incoming.getWeight().modifyFlat(getModId(), bonus, getName());
    }
    public float getImmigrationBonus() {
        return IMMIGRATION_BASE * market.getSize();
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getIncomeMult().unmodifyPercent(id);
        market.removeTransientImmigrationModifier(this);
        Industry population = market.getIndustry(Industries.POPULATION);
        if(population!=null) {
            population.getDemand(Commodities.DOMESTIC_GOODS).getQuantity().unmodifyFlat(id);
            population.getDemand(Commodities.LUXURY_GOODS).getQuantity().unmodifyFlat(id);
            population.getDemand(Commodities.DRUGS).getQuantity().unmodifyFlat(id);
        }
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        if(shouldApply()) {
            float opad = 10f;
            tooltip.addPara("%s colony income", opad, Misc.getHighlightColor(), "+" + (int)INCOME_BONUS + "%");
            tooltip.addPara("%s population growth (based on market size)", opad, Misc.getHighlightColor(), "+"+(int)getImmigrationBonus());
            tooltip.addPara("%s commercial goods demand", opad, Misc.getHighlightColor(), "+"+GOODS_DEMAND_INCREASE);
        }
    }
}
