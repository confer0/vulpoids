package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.characters.VulpoidPerson;

public class SetVulpoidProperty extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 2) return false;
        String property = params.get(0).getString(memoryMap);
        PersonAPI person;
        String param;
        if (params.size() == 2) {
            person = dialog.getInteractionTarget().getActivePerson();
            param = params.get(1).getString(memoryMap);
        } else {
            person = Global.getSector().getImportantPeople().getPerson(params.get(1).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(1).getObject(memoryMap);
            param = params.get(2).getString(memoryMap);
        }
        if(person instanceof VulpoidPerson vulpoidPerson) {
            vulpoidPerson.setArbitraryProperty(property, param);
        }
        return true;
    }
    
}
