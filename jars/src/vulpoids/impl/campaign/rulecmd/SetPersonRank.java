package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SetPersonRank extends BaseCommandPlugin {
    
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(params.isEmpty()) return false;
        PersonAPI person;
        String rank;
        
        if(params.size()==1) {
            person = dialog.getInteractionTarget().getActivePerson();
            rank = params.get(0).getString(memoryMap);
        } else {
            rank = params.get(1).getString(memoryMap);
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
        }
        if("null".equals(rank)) rank=null;
        person.setRankId(rank);
        return true;
    }
}
