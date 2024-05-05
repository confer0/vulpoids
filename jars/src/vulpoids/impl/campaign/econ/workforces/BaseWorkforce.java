package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import vulpoids.impl.campaign.econ.VulpoidPopulation;
import vulpoids.impl.campaign.ids.Vulpoids;

public class BaseWorkforce extends BaseMarketConditionPlugin {
    
    public static final String VULP_POP_6 = "size 6+ Vulpoid population";
    public static final String POP_RATIO_1 = "at least 1:1 population ratio";
    
    @Override
    public void apply(String id) {
        market.getMemoryWithoutUpdate().set("$workforces", market.getMemoryWithoutUpdate().getInt("$workforces") + 1);
    }
    @Override
    public void unapply(String id) {
        market.getMemoryWithoutUpdate().set("$workforces", market.getMemoryWithoutUpdate().getInt("$workforces") - 1);
    }
    public boolean shouldApply() {
        return getUnmetRequirements().isEmpty();
    }
    public List<String> getUnmetRequirements() {
        return getUnmetRequirements(false);
    }
    public List<String> getUnmetRequirements(boolean for_placement) {
        ArrayList<String> unmet_requirements = new ArrayList();
        if(market.isPlanetConditionMarketOnly()) {
            unmet_requirements.add("inhabited");
        } else {
            int virtual = 0;
            if(for_placement) virtual = 1;
            if(market.getMemoryWithoutUpdate().getInt(Vulpoids.KEY_WORKFORCES) + virtual > market.getMemoryWithoutUpdate().getInt(Vulpoids.KEY_WORKFORCE_CAP)) unmet_requirements.add("sufficient workforce capacity");
            int vulp_pop = 0;
            if(market.hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) vulp_pop = ((VulpoidPopulation)market.getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin()).getPopulation();
            for (String requirement : getRequirements()) {
                switch(requirement) {
                    case VULP_POP_6: if(vulp_pop<6) {unmet_requirements.add(VULP_POP_6);} break;
                    case POP_RATIO_1: if(vulp_pop<market.getSize()) {unmet_requirements.add(POP_RATIO_1);} break;
                }
            }
        }
        return unmet_requirements;
    }
    public String[] getRequirements() {
        return new String[]{};
    }
    public boolean isAvailableToPlayer() {
        return true;  // Use this for unlockable or NPC-only workforces.
    }
    @Override
    public String getIconName() {
        if(shouldApply() || !market.isPlayerOwned()) return condition.getSpec().getIcon();
        return "graphics/icons/markets/workforce_confused.png";
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        if(!shouldApply()) {
            float opad = 10f;
            //tooltip.addPara("Due to a loss of population or some other factor, %s.", opad, Misc.getNegativeHighlightColor(), "our workforces are in disarray");
            //tooltip.addPara("%s", opad, Misc.getHighlightColor(), "Suspend some workforces to restore normal operation");
            tooltip.addPara("This workforce is unable to operate properly.", Misc.getNegativeHighlightColor(), opad);
            String list = "";
            for (String curr : getUnmetRequirements()) {
                curr = curr.trim();
                list += curr + ", ";
            }
            if (!list.isEmpty()) list = list.substring(0, list.length()-2);
            if (!list.isEmpty()) {
                tooltip.addPara("Unmet Requirements: %s", opad, Misc.getNegativeHighlightColor(), list);
            }
        }
    }
}
