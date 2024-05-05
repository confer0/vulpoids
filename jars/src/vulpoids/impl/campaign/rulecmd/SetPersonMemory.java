package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.Misc.VarAndMemory;
import java.util.List;
import java.util.Map;

public class SetPersonMemory extends BaseCommandPlugin {
    
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if(params.size()<3) return false;
        PersonAPI person;
        String memflag;
        String vartype;
        int var_index;
        
        if(params.size()==3) {
            person = dialog.getInteractionTarget().getActivePerson();
            memflag = params.get(0).getString(memoryMap);
            vartype = params.get(1).getString(memoryMap);
            var_index = 2;
        } else {
            memflag = params.get(1).getString(memoryMap);
            vartype = params.get(2).getString(memoryMap);
            var_index = 3;
            person = Global.getSector().getImportantPeople().getPerson(params.get(0).getString(memoryMap));
            if(person==null) person = (PersonAPI) params.get(0).getObject(memoryMap);
        }
        switch(vartype) {
            case "boolean": person.getMemoryWithoutUpdate().set(memflag, params.get(var_index).getBoolean(memoryMap)); break;
            case "color": person.getMemoryWithoutUpdate().set(memflag, params.get(var_index).getColor(memoryMap)); break;
            case "float": person.getMemoryWithoutUpdate().set(memflag, params.get(var_index).getFloat(memoryMap)); break;
            case "int": person.getMemoryWithoutUpdate().set(memflag, params.get(var_index).getInt(memoryMap)); break;
            case "string": person.getMemoryWithoutUpdate().set(memflag, params.get(var_index).getString(memoryMap)); break;
            case "object": person.getMemoryWithoutUpdate().set(memflag, params.get(var_index).getObject(memoryMap)); break;
        }
        return true;
    }
}
