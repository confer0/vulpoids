package vulpoids.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.People;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

public class MovePersonFromMemoryToMarket extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.isEmpty()) return false;
        PersonAPI person = (PersonAPI) params.get(0).getObject(memoryMap);
        
        MarketAPI market;
        if(params.size() < 2) {
            market = dialog.getInteractionTarget().getMarket();
        } else {
            String marketId = params.get(1).getString(memoryMap);
            market = Global.getSector().getEconomy().getMarket(marketId);
            if (market == null && "ga_market".equals(marketId)) { // horrible hack from the original code, just in case.
                market = Global.getSector().getImportantPeople().getPerson(People.BAIRD).getMarket();
            }
        }
        if (market == null) return false;
        if (person == null) return false;
        Misc.moveToMarket(person, market, true);
        return true;
    }
}