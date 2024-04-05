package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SetVulpoidExpression extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 1) return false;
        PersonAPI person;
        String expression;
        if (params.size() == 1) {
            person = dialog.getInteractionTarget().getActivePerson();
            expression = params.get(0).getString(memoryMap);
        } else {
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            expression = params.get(1).getString(memoryMap);
        }
        if (person == null) return false;
        String[] portrait_slices = person.getPortraitSprite().split("/");
        String portrait = "";
        for(int i=0; i<portrait_slices.length-1; i++) {
            portrait += portrait_slices[i]+"/";
        }
        portrait += expression + ".png";
        person.setPortraitSprite(portrait);
        return true;
    }
    
}
