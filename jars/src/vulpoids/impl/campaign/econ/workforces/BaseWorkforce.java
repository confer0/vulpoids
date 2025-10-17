package vulpoids.impl.campaign.econ.workforces;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.SharedUnlockData;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.econ.VulpoidPopulation;
import vulpoids.impl.campaign.ids.Vulpoids;

public class BaseWorkforce extends BaseMarketConditionPlugin {
    
    public static final String VULP_POP_6 = "size 6+ Vulpoid population";
    public static final String POP_RATIO_1 = "at least 1:1 population ratio";
    public static final String LUDDIC_MAJORITY = "luddic majority population";
    
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
                    case VULP_POP_6 -> {if(vulp_pop<6) unmet_requirements.add(VULP_POP_6);}
                    case POP_RATIO_1 -> {if(vulp_pop<market.getSize()) unmet_requirements.add(POP_RATIO_1);}
                    case LUDDIC_MAJORITY -> {if(!market.hasCondition(Conditions.LUDDIC_MAJORITY)) unmet_requirements.add(LUDDIC_MAJORITY);}
                }
            }
        }
        return unmet_requirements;
    }
    public String[] getRequirements() {
        return new String[]{};
    }
    // Use this to link specific entries to the industries - or worlds - they're associated with.
    // Remember to call super so the illustration links!
    public void linkCodexEntries() {
        CodexDataV2.makeRelated(
                CodexDataV2.getEntry(CodexDataV2.getConditionEntryId(condition.getId())),
                CodexDataV2.getEntry(CodexDataV2.getGalleryEntryId(getTooltipIllustrationId()))
        );
    }
    // Use this for unlockable or NPC-only workforces.
    public boolean isAvailableToPlayer() {return true;}
    @Override
    public String getIconName() {
        if(shouldApply() || !market.isPlayerOwned()) return condition.getSpec().getIcon();
        return "graphics/icons/markets/workforce_confused.png";
    }
    public String getTooltipIllustrationId() {return "vulpworkforce_placeholder";}
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        float opad = 10f;
        
        if (!Global.CODEX_TOOLTIP_MODE) {
            Color color = market.getTextColorForFactionOrPlanet();
            tooltip.addTitle(condition.getName(), color);
        }
        
        tooltip.addImage(Global.getSettings().getSpriteName("illustrations", getTooltipIllustrationId()), 10f);
        SharedUnlockData.get().reportPlayerAwareOfIllustration(getTooltipIllustrationId(), true);
        SharedUnlockData.get().saveIfNeeded();
        
        String text = condition.getSpec().getDesc();
        Map<String, String> tokens = getTokenReplacements();
        if (tokens != null) {
            for (String token : tokens.keySet()) {
                String value = tokens.get(token);
                text = text.replaceAll("(?s)\\" + token, value);
            }
        }

        if (!text.isEmpty()) {
            LabelAPI body = tooltip.addPara(text, opad);
            if (getHighlights() != null) {
                if (getHighlightColors() != null) {
                    body.setHighlightColors(getHighlightColors());
                } else {
                    body.setHighlightColor(Misc.getHighlightColor());
                }
                body.setHighlight(getHighlights());
            }
        }

        createTooltipAfterDescription(tooltip, expanded);
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        float opad = 10f;
        if(Global.CODEX_TOOLTIP_MODE) {
            String list = "";
            for (String curr : getRequirements()) {
                curr = curr.trim();
                list += curr + ", ";
            }
            if (!list.isEmpty()) list = list.substring(0, list.length()-2);
            if (!list.isEmpty()) {
                tooltip.addPara("Requirements: %s", opad, Misc.getTextColor(), list);
            } else {
                tooltip.addPara("Requirements: none", opad);
            }
        } else if(!shouldApply()) {
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
