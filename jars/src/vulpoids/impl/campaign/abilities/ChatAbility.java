package vulpoids.impl.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import vulpoids.impl.campaign.ProfectoInteractionDialogPlugin;
import vulpoids.impl.campaign.ids.Vulpoids;

public class ChatAbility extends BaseDurationAbility {
    @Override
    protected void activateImpl() {
        ProfectoInteractionDialogPlugin plugin = new ProfectoInteractionDialogPlugin();
        SectorEntityToken target = Global.getFactory().createJumpPoint("vulpoidconversation_dummyjumppoint", "DUMMY");
        Global.getSector().getCampaignUI().showInteractionDialog(plugin, target);
        
        // TODO - test this? Or is the current method good enough.
        //Global.getSector().getCampaignUI().getCurrentInteractionDialog().getVisualPanel().closeCoreUI();
    }
    
    @Override
    public boolean isUsable() {
        // TODO - require Vulpoids
        return true;
    }
    
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        float pad = 10f;
        if (Global.getSector().getMemoryWithoutUpdate().getBoolean(Vulpoids.KEY_GOT_FACTORY)) {
            tooltip.addTitle("Chat with a Vulpoid");
            tooltip.addPara("Talk with a Vulpoid aboard your fleet.", pad);
        } else {
            tooltip.addTitle("Interrogate the Captain");
            tooltip.addPara("You've captured the captain of the Exodyne ship. Now you just need to get your answers.", pad);
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
