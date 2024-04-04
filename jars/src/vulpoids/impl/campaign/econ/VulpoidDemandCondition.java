package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Industries;

public class VulpoidDemandCondition extends BaseMarketConditionPlugin {
    
    public int demand_cap = 0;
    
    public void setParam(Object param) {
        if(param instanceof Integer) {
            demand_cap = (Integer)param;
        }
    }
    
    public void apply(String id) {
        int demand = Math.min(demand_cap, market.getSize()-3);
        if (market.hasIndustry(Industries.POPULATION)) market.getIndustry(Industries.POPULATION).getDemand("vulpoids").getQuantity().modifyFlat(id, demand, "Vulpoid Demand");
    }

    public void unapply(String id) {
        if (market.hasIndustry(Industries.POPULATION)) market.getIndustry(Industries.POPULATION).getDemand("vulpoids").getQuantity().unmodifyFlat(id);
    }
    
    @Override
    public boolean showIcon() {
        return false;
    }
}
