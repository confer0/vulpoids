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

public class SetVulpoidClimate extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 1) return false;
        PersonAPI person;
        String climate;
        if (params.size() == 1) {
            person = dialog.getInteractionTarget().getActivePerson();
            climate = params.get(0).getString(memoryMap);
        } else {
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
            climate = params.get(1).getString(memoryMap);
        }
        VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLIMATE, climate);
        return true;
    }
    
}
