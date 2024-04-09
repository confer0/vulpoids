package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.VulpoidCreator;

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
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
            clothing = params.get(1).getString(memoryMap);
        }
        if (person == null) return false;
        VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLOTHING, clothing);
        return true;
    }
    
}
