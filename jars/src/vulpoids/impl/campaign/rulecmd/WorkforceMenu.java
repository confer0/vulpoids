package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.WorkforceInteractionDialogPlugin;

public class WorkforceMenu extends BaseCommandPlugin {
    
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        final WorkforceInteractionDialogPlugin plugin = new WorkforceInteractionDialogPlugin();
        dialog.setPlugin(plugin);
        plugin.init(dialog);
        //dialog.setPlugin(prev_plugin);
        return true;
    }
    
}
