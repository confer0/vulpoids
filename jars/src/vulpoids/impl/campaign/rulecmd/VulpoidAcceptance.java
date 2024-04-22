package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.econ.VulpoidPopulation;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceGenericOneTimeFactor;

public class VulpoidAcceptance extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String option = params.get(0).getString(memoryMap);
        switch(option) {
            case "officialStance":
                return VulpoidAcceptanceEventIntel.get().getProgress() >= VulpoidAcceptanceEventIntel.PROGRESS_SANCTIONS_START;
            case "donate":
                String workforce = params.get(1).getString(memoryMap);
                int points = params.get(2).getInt(memoryMap);
                String desc = params.get(3).getString(memoryMap);
                String tooltip = params.get(4).getString(memoryMap);
                MarketAPI market = dialog.getInteractionTarget().getMarket();
                if(!market.hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) market.addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
                VulpoidPopulation plugin = ((VulpoidPopulation)market.getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin());
                if(plugin.getPopulation()<=3) plugin.setPopulation(3);
                if(!market.hasCondition(workforce)) market.addCondition(workforce);
                if(points>0) VulpoidAcceptanceEventIntel.addFactorCreateIfNecessary(new VulpoidAcceptanceGenericOneTimeFactor(desc, tooltip, points), dialog);
                return true;
            case "resolveGilead":
                VulpoidAcceptanceEventIntel.get().resolveGileadEvent();
                return true;
        }
        return true;
    }

}
