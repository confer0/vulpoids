package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;

/**
 *	
 */
public class MakeVulpoidsIllegal extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        
        for(FactionAPI faction : Global.getSector().getAllFactions()) {
            if(faction.getCustomBoolean("vulpoidsAlwaysIllegal")) {
                faction.makeCommodityIllegal("vulpoids");
            } else if(!faction.getCustomBoolean("vulpoidsAlwaysLegal") && faction.isIllegal("ai_cores")) {
                //faction.makeCommodityIllegal("vulpoids_shiny");
            }
        }

        return true;
    }

}
