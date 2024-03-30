package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.inspection.HegemonyInspectionIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;

/**
 *	
 */
public class MarketIsInspectionTarget extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        MarketAPI market = dialog.getInteractionTarget().getMarket();

        if (Global.getSector().getIntelManager().hasIntelOfClass(HegemonyInspectionIntel.class)) {
            for(IntelInfoPlugin inspectionIntel : Global.getSector().getIntelManager().getIntel(HegemonyInspectionIntel.class)) {
                if(((HegemonyInspectionIntel)inspectionIntel).getTarget() == market) {
                    return true;
                }
            }
        }
        

        return false;
    }

}
