package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import java.util.List;
import java.util.Map;

public class SetPersonFaction extends BaseCommandPlugin {
	
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if(params.isEmpty()) return false;
        PersonAPI person;
        String faction;
        
        if(params.size()==1) {
            person = dialog.getInteractionTarget().getActivePerson();
            faction = params.get(0).getString(memoryMap);
        } else {
            faction = params.get(1).getString(memoryMap);
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
        }
        person.setFaction(faction);
        return true;
    }
}
