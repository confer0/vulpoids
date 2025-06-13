package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.interactions.FireAllRulesListDialogPlugin;
import vulpoids.impl.campaign.interactions.ListBasedInteractionDialogPlugin;
import vulpoids.impl.campaign.interactions.VulpoidChatChangeOutfitDialogPlugin;
import vulpoids.impl.campaign.interactions.VulpoidChatCurrentAffairsDialogPlugin;
import vulpoids.impl.campaign.interactions.VulpoidChatForgetSkillsDialogPlugin;
import vulpoids.impl.campaign.interactions.VulpoidChatLocalEntitiesDialogPlugin;

public class OpenListBasedDialog extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        ListBasedInteractionDialogPlugin plugin;
        String pluginPick = params.get(0).getString(memoryMap);
        String triggerOnExit = null;
        if(params.size()>=2) triggerOnExit = params.get(1).getString(memoryMap);
        boolean fireBestOnExit = false;
        if(params.size()>=3) fireBestOnExit = params.get(2).getBoolean(memoryMap);
        plugin = switch (pluginPick) {
            case "localEntities" -> new VulpoidChatLocalEntitiesDialogPlugin();
            case "currentAffairs" -> new VulpoidChatCurrentAffairsDialogPlugin();
            case "forgetSkills" -> new VulpoidChatForgetSkillsDialogPlugin();
            case "changeOutfit" -> new VulpoidChatChangeOutfitDialogPlugin();
            default -> new FireAllRulesListDialogPlugin(pluginPick);
        };
        
        plugin.initWithBacktrack(dialog, triggerOnExit, fireBestOnExit);
        dialog.setPlugin(plugin);
        return true;
    }
}
