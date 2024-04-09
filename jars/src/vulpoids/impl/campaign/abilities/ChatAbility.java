package vulpoids.impl.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import vulpoids.impl.campaign.ProfectoInteractionDialogPlugin;

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
        tooltip.addTitle("Chat With Vulpoid");
        float pad = 10f;
        tooltip.addPara("Talk with a Vulpoid aboard your fleet.", pad);
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
