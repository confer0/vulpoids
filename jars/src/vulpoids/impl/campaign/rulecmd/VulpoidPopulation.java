
package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
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
        }
        return false;
    }
    
    private vulpoids.impl.campaign.econ.VulpoidPopulation getPlugin(InteractionDialogAPI dialog) {
        if(!dialog.getInteractionTarget().getMarket().hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) return null;
        return (vulpoids.impl.campaign.econ.VulpoidPopulation) dialog.getInteractionTarget().getMarket().getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin();
    }
}
