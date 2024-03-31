package vulpoids.impl.campaign.econ.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import java.awt.Color;


public class OrganFarm extends BaseIndustry {

    public void apply() {
        super.apply(true);
        
        int size = market.getSize();
        demand(Commodities.ORGANICS, size + 1);
        demand(Commodities.FOOD, size);
        if("organfarms".equals(getId())) {
            supply(Commodities.ORGANS, size - 4);
        } else if("biofacility".equals(getId())) {
            supply(Commodities.ORGANS, size - 3);
            supply(Commodities.DRUGS, size - 1);
        }
        
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.FOOD);
        applyDeficitToProduction(1, deficit, Commodities.ORGANS, Commodities.DRUGS);
        if (!isFunctional()) {
            supply.clear();
        }
    }


    @Override
    public void unapply() {
        super.unapply();
    }
    
    
    public boolean hasBiofactory() {
        return getSpecialItem() != null && "vulpoid_biofactory".equals(getSpecialItem().getId());
    }
    public boolean isOrganFarmVulpBiofactory() {
        return "organfarms".equals(getId()) && hasBiofactory();
    }
    public boolean isBiofacilityAndNotUnlocked() {
        return "biofacility".equals(getId()) && !Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_gotFactory");
    }
    public boolean isBiofacilityVulpBiofactory() {
        return "biofacility".equals(getId()) && hasBiofactory();
    }
    
    
    @Override
    public String getCurrentImage() {
        if (isOrganFarmVulpBiofactory()) {
            return Global.getSettings().getSpriteName("industry", "organfarmvulp");
        }
        if ("biofacility".equals(getId())) {
            if (hasBiofactory()) {
                return Global.getSettings().getSpriteName("industry", "biotechvulp");
            }
            if(market.getSize() == 3) {
                return Global.getSettings().getSpriteName("industry", "biotechlow");
            }
            if(market.getSize() == 6) {
                return Global.getSettings().getSpriteName("industry", "biotechhigh");
            }
        }
        return super.getCurrentImage();
    }
    
    @Override
    public String getCurrentName() {
        if (isOrganFarmVulpBiofactory()) {
            return "Vulpoid Bioforge";
        }
        if (isBiofacilityVulpBiofactory()) {
            return "Vulpoid Biofacility";
        }
        if (isBiofacilityAndNotUnlocked()) {
            return "Speculative Improvements";
        }
        return super.getCurrentName();
    }
    
    @Override
    protected String getDescriptionOverride() {
        if (isOrganFarmVulpBiofactory()) {
            return "With the power of a fully-operational bioforge, living creatures can be printed as easily as spacecraft. "+
                    "The phrase 'how the sausage is made' is often used to describe operations, "+
                    "and many steps in the bioforging process do in fact have an unpleasant resemblance to sausage meat.";
        }
        if (isBiofacilityVulpBiofactory()) {
            return "The pinnacle of Exodyne Biotech's aspirations for the Sector. With a bioforge at its heart, "+
                    "this sleek and advanced megacomplex is capable of producing an endless flow of fluffy friends, "+
                    "all without compromising its baseline production levels of assorted biological goods.";
        }
        if (isBiofacilityAndNotUnlocked()) {
            return "Without Domain-era technology, this is advanced as is possible in the Sector.";
        }
        return super.getDescriptionOverride(); 
    }
    
    
    
    @Override
    public boolean canImprove() {
        return true;
    }
    
    protected void applyImproveModifiers() {
        if (isImproved()) {
            getSupply(Commodities.ORGANS).getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            if ("biofacility".equals(getId())) {
                getSupply(Commodities.DRUGS).getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            }
            if (hasBiofactory()) {
                getSupply("vulpoids").getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            }
        } else {
            getSupply(Commodities.ORGANS).getQuantity().unmodifyFlat(getModId(3));
            getSupply(Commodities.DRUGS).getQuantity().unmodifyFlat(getModId(3));
            getSupply("vulpoids").getQuantity().unmodifyFlat(getModId(3));
        }
    }
    
    @Override
    public boolean isAvailableToBuild() {
        if ("biofacility".equals(getId()) && !Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_gotFactory")) {
            return false;
        }
        return market.hasCondition(Conditions.HABITABLE);
    }
    
    public String getUnavailableReason() {
        if (!super.isAvailableToBuild()) return super.getUnavailableReason();
        return "Requires habitable conditions";
    }
    
    // We override this so the tooltip doesn't blab about the exact properties of the "speculative" upgrade.
    public void createTooltip(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
        if ("biofacility".equals(getId()) && !Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_gotFactory")) {
            currTooltipMode = mode;
            float opad = 10f;

            FactionAPI faction = market.getFaction();
            Color color = faction.getBaseUIColor();
            Color bad = Misc.getNegativeHighlightColor();

            String type = "";
            if (isIndustry()) type = " - Industry";
            if (isStructure()) type = " - Structure";

            tooltip.addTitle(getCurrentName() + type, color);

            String desc = spec.getDesc();
            String override = getDescriptionOverride();
            if (override != null) {
                    desc = override;
            }
            desc = Global.getSector().getRules().performTokenReplacement(null, desc, market.getPrimaryEntity(), null);

            tooltip.addPara(desc, opad);

            addRightAfterDescriptionSection(tooltip, mode);

            tooltip.addPara(getUnavailableReason(), bad, opad);
        } else {
            super.createTooltip(mode, tooltip, expanded);
        }
    }
    
}
