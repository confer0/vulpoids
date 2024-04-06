package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SetVulpoidClothing extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 1) return false;
        PersonAPI person;
        String clothing;
        if (params.size() == 1) {
            person = dialog.getInteractionTarget().getActivePerson();
            clothing = params.get(0).getString(memoryMap);
        } else {
            Object o = params.get(0).getObject(memoryMap);
            if (o instanceof PersonAPI) person = (PersonAPI) o;
            else person = Global.getSector().getImportantPeople().getPerson((String)o);
            clothing = params.get(1).getString(memoryMap);
        }
        if (person == null) return false;
        String[] portrait_slices = person.getPortraitSprite().split("/");
        String portrait = "";
        for(int i=0; i<portrait_slices.length; i++) {
            if(i!=portrait_slices.length-2) portrait += portrait_slices[i]+"/";
            else portrait += clothing+"/";
        }
        portrait = portrait.substring(0, portrait.length()-1); // Cutting off the last slash.
        person.setPortraitSprite(portrait);
        return true;
    }
    
}
