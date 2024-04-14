package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.interactions.VulpoidChatLocalEntitiesDialogPlugin;

public class OpenVulpoidLocalEntitiesDialog extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        VulpoidChatLocalEntitiesDialogPlugin plugin = new VulpoidChatLocalEntitiesDialogPlugin();
        String triggerOnExit = params.get(0).getString(memoryMap);
        plugin.initWithBacktrack(dialog, triggerOnExit);
        dialog.setPlugin(plugin);
        return true;
    }
}