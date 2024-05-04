package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SetPersonSkillLevel extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(params.isEmpty()) return false;
        PersonAPI person;
        String skill;
        int level;
        if(params.size()==2) {
            person = dialog.getInteractionTarget().getActivePerson();
            skill = params.get(0).getString(memoryMap);
            level = params.get(1).getInt(memoryMap);
        } else {
            skill = params.get(1).getString(memoryMap);
            level = params.get(2).getInt(memoryMap);
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
        }
        person.getStats().setSkillLevel(skill, level);
        return true;
    }
}
