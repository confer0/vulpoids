package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import java.util.List;
import java.util.Map;
import vulpoids.campaign.impl.items.VulpoidPlugin;

public class AddVulpoidFromImportant extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        
        String person_id = params.get(0).getString(memoryMap);
        PersonAPI person = Global.getSector().getImportantPeople().getPerson(person_id);
        
        TextPanelAPI text = dialog.getTextPanel();
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        
        SpecialItemData data = new SpecialItemData("special_vulpoid", VulpoidPlugin.personToJson(person));
        cargo.addSpecial(data, 1);
        AddRemoveCommodity.addItemGainText(data, 1, text);
        return true;
    }
    
}
