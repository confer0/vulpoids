package vulpoids.impl.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.campaign.impl.items.VulpoidPlugin;
import vulpoids.impl.campaign.interactions.VulpoidChatTopDialogPlugin;
import vulpoids.impl.campaign.ids.Vulpoids;

public class ChatAbility extends BaseDurationAbility {
    @Override
    protected void activateImpl() {
        VulpoidChatTopDialogPlugin plugin = new VulpoidChatTopDialogPlugin();
        Misc.showRuleDialog(Global.getSector().getPlayerFleet(), "OpenInteractionDialog");
        InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
        plugin.init(dialog);
        dialog.setPlugin(plugin);
        // TODO - test this? Or is the current method good enough.
        //Global.getSector().getCampaignUI().getCurrentInteractionDialog().getVisualPanel().closeCoreUI();
    }
    
    @Override
    public boolean isUsable() {
        if (!Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_didInterrogation")) return true;
        for (CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
            if(Vulpoids.CARGO_ITEM.equals(stack.getCommodityId())) return true;
            if(stack.getPlugin() instanceof VulpoidPlugin) return true;
        }
        return false;
    }
    
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        float pad = 10f;
        if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_didInterrogation")) {
            tooltip.addTitle("Chat with a Vulpoid");
            tooltip.addPara("Talk with a Vulpoid aboard your fleet.", pad);
        } else {
            tooltip.addTitle("Interrogate the Captain");
            tooltip.addPara("You've captured the captain of the Exodyne ship. Now you just need to get your answers.", pad);
        }
        if(!isUsable()) {
            tooltip.addPara("There are no Vulpoids aboard your fleet.", Misc.getNegativeHighlightColor(), pad);
        }
    }
    
    @Override
    protected String getActivationText() {return null;}
    @Override
    protected String getDeactivationText() {return null;}
    @Override
    protected void applyEffect(float amount, float level) {}
    @Override
    protected void deactivateImpl() {}
    @Override
    protected void cleanupImpl() {}
    
}
