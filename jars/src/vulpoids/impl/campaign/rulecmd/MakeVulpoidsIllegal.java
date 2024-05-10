package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;

/**
 *	
 */
public class MakeVulpoidsIllegal extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        
        new VulpoidAcceptanceEventIntel(null, true);
        
        Global.getSector().getPlayerFaction().addKnownIndustry(Vulpoids.INDUSTRY_VULPOIDAGENCY);
        String dealmakerParams = Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).getParams();
        dealmakerParams += ", "+Vulpoids.INDUSTRY_VULPOIDAGENCY;
        Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).setParams(dealmakerParams);
        
        return true;
    }

}
