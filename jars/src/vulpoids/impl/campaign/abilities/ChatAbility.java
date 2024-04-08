package vulpoids.impl.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class ChatAbility extends BaseDurationAbility {
    @Override
    protected void activateImpl() {
        Global.getSector().getCampaignUI().addMessage("ActivateImpl");
        // PUT CODE HERE
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
