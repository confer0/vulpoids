package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.ids.Vulpoids;

public class ResetVulpoidPortrait extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        PersonAPI person;
        if (params.isEmpty()) {
            person = dialog.getInteractionTarget().getActivePerson();
        } else {
            Object o = params.get(0).getObject(memoryMap);
            if (o instanceof PersonAPI) person = (PersonAPI) o;
            else person = Global.getSector().getImportantPeople().getPerson((String)o);
        }
        if (person == null) return false;
        String portrait = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_PORTRAIT);
        if (portrait == null) return false;
        person.setPortraitSprite(portrait);
        return true;
    }
    
}
