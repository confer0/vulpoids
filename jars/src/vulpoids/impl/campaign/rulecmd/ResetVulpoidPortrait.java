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

public class ResetVulpoidPortrait extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        PersonAPI person;
        if (params.isEmpty()) {
            person = dialog.getInteractionTarget().getActivePerson();
        } else {
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
        }
        if (person == null) return false;
        VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_EXPRESSION, VulpoidCreator.EXPRESSION_DEFAULT);
        VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLOTHING, VulpoidCreator.CLOTHING_NUDE);
        return true;
    }
    
}
