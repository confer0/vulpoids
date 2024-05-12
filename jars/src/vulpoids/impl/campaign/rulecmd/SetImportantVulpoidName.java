package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;

public class SetImportantVulpoidName extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 2) {
            return false;
        }
        String person_id = params.get(0).getString(memoryMap);
        String firstname = params.get(1).getString(memoryMap);
        String lastname = "";
        if(params.size() >= 3) lastname = params.get(2).getString(memoryMap);
        PersonAPI person = Global.getSector().getImportantPeople().getPerson(person_id);
        if(person == null) return false;
        person.setName(new FullName(firstname, lastname, person.getGender()));
        return true;
    }
}
