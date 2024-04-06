package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import java.util.List;
import java.util.Map;

public class ShowThirdPersonFromMemory extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if(dialog==null) return false;
        if(params.isEmpty()) return false;
        Object o = params.get(0).getObject(memoryMap);
        if (o instanceof PersonAPI) {
            PersonAPI person = (PersonAPI) o;
            dialog.getVisualPanel().showThirdPerson(person);
            return true;
        } else {
            return false;
        }
    }
}
