package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidResearch extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        switch(params.get(0).getString(memoryMap)) {
            case "setProject":
                String project = params.get(1).getString(memoryMap);
                int days = params.get(2).getInt(memoryMap);
                float completion_day = days + memoryMap.get(MemKeys.GLOBAL).getFloat("$daysSinceStart");
                memoryMap.get(MemKeys.LOCAL).set(Vulpoids.KEY_RESEARCH_PROJECT, project);
                memoryMap.get(MemKeys.LOCAL).set(Vulpoids.KEY_RESEARCH_COMPLETION_DAY, completion_day);
                return true;
            case "isComplete":
                if(!memoryMap.get(MemKeys.LOCAL).contains(Vulpoids.KEY_RESEARCH_PROJECT)) return false;
                return memoryMap.get(MemKeys.GLOBAL).getFloat("$daysSinceStart") >= memoryMap.get(MemKeys.LOCAL).getFloat(Vulpoids.KEY_RESEARCH_COMPLETION_DAY);
        }
        return false;
    }
}
