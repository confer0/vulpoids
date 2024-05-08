
package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidPopulation extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        switch(params.get(0).getString(memoryMap)) {
            case "set":
                if(getPlugin(dialog)==null) dialog.getInteractionTarget().getMarket().addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
                getPlugin(dialog).setPopulation(params.get(1).getInt(memoryMap));
                return true;
            case "setAtLeast":
                if(getPlugin(dialog)==null) dialog.getInteractionTarget().getMarket().addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
                int population = Math.max(params.get(1).getInt(memoryMap), getPlugin(dialog).getPopulation());
                getPlugin(dialog).setPopulation(population);
                return true;
            case "increase":
                getPlugin(dialog).setPopulation(getPlugin(dialog).getPopulation()+1);
                return true;
            case "decrease":
                getPlugin(dialog).setPopulation(getPlugin(dialog).getPopulation()-1);
                return true;
            case "getUpgradeableColony":
                final SectorEntityToken playerFleet = Global.getSector().getPlayerFleet();
                List<SectorEntityToken> entities = playerFleet.getContainingLocation().getAllEntities();
                Collections.sort(entities,new Comparator<SectorEntityToken>() {
                    @Override
                    public int compare(SectorEntityToken e1,SectorEntityToken e2) {
                        float d = (Misc.getDistance(e1, playerFleet)-e1.getRadius()) - (Misc.getDistance(e2, playerFleet)-e2.getRadius());
                        if (d<0) return -1;
                        if (d>0) return 1;
                        return 0;
                    }
                });
                for(SectorEntityToken entity: entities) {
                    if(Misc.getDistance(entity, playerFleet)>250f+entity.getRadius()+playerFleet.getRadius()) return false;
                    if(entity.getMarket()!=null && entity.getMarket().isPlayerOwned()) {
                        if(entity.getMarket().getMemoryWithoutUpdate().contains(MemFlags.RECENTLY_BOMBARDED)) return false;
                        int vulpsToUpgrade = 50;
                        if(entity.getMarket().hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) {
                            vulpoids.impl.campaign.econ.VulpoidPopulation cond = (vulpoids.impl.campaign.econ.VulpoidPopulation) entity.getMarket().getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin();
                            if(entity.getMarket().getSize() <= cond.getPopulation()) return false;
                            vulpsToUpgrade = (int) Math.pow(10, cond.getPopulation()+1);
                        }
                        memoryMap.get(MemKeys.LOCAL).set("$vulp_upgradeableMarket", entity.getMarket(), 0);
                        memoryMap.get(MemKeys.LOCAL).set("$vulp_upgradeableMarketName", entity.getMarket().getName(), 0);
                        memoryMap.get(MemKeys.LOCAL).set("$vulp_upgradeableMarketRequirements", Misc.getWithDGS(vulpsToUpgrade), 0);
                        return true;
                    }
                }
                return false;
            case "vulp_doColonyUpgrade":
                MarketAPI market = (MarketAPI) memoryMap.get(MemKeys.LOCAL).get("$vulp_upgradeableMarket");
                if(!market.hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) {
                    market.addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
                } else {
                    vulpoids.impl.campaign.econ.VulpoidPopulation cond = (vulpoids.impl.campaign.econ.VulpoidPopulation) market.getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin();
                    cond.setPopulation(cond.getPopulation()+1);
                }
                return true;
        }
        return false;
    }
    
    private vulpoids.impl.campaign.econ.VulpoidPopulation getPlugin(InteractionDialogAPI dialog) {
        if(!dialog.getInteractionTarget().getMarket().hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) return null;
        return (vulpoids.impl.campaign.econ.VulpoidPopulation) dialog.getInteractionTarget().getMarket().getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin();
    }
}
