package vulpoids.impl.campaign.econ.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import java.awt.Color;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.misc.ShinyProducedIntel;


public class OrganFarm extends BaseIndustry {
    
    public static float SHINY_VULPOIDS_PER_DAY = 0.5f/365f;
    protected float shiny_vulpoid_production_buffer = 0;
    protected final float PATHER_INTEREST = 2f;
    
    @Override
    public void advance(float amount) {
        super.advance(amount);

        if (special != null && Vulpoids.BIOFORGE_ITEM.equals(special.getId())) {
            float days = Misc.getDays(amount);
            shiny_vulpoid_production_buffer += days * SHINY_VULPOIDS_PER_DAY * Math.max(0, getSupply("vulpoids").getQuantity().getModifiedInt());
            while(shiny_vulpoid_production_buffer >= 1) {
                shiny_vulpoid_production_buffer -= 1;
                market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("vulpoids_shiny", 1);
                Global.getSector().getIntelManager().addIntel(new ShinyProducedIntel(market));
            }
        }
    }
    
    @Override
    public float getPatherInterest() {
        return PATHER_INTEREST + super.getPatherInterest();
    }
    
    @Override
    public void apply() {
        super.apply(true);
        
        int size = market.getSize();
        demand(Commodities.FOOD, size + 2);
        demand(Commodities.ORGANICS, size + 2);
        demand(Commodities.HEAVY_MACHINERY, size - 3);  // Same as Mining
        if("organfarms".equals(getId())) {
            supply(Commodities.ORGANS, size - 4);
        } else if("biofacility".equals(getId())) {
            supply(Commodities.ORGANS, size - 3);
            //supply(Commodities.DRUGS, size - 1);
        }
        
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.FOOD);
        applyDeficitToProduction(1, deficit, Commodities.ORGANS, Commodities.DRUGS);
        deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.FOOD, Commodities.HEAVY_MACHINERY);
        Pair<String, Integer> rare_deficit = getMaxDeficit(Commodities.RARE_METALS);
        if(rare_deficit.two > 0) {
            rare_deficit.two = 100;
            applyDeficitToProduction(1, rare_deficit, "vulpoids");
        } else {
            applyDeficitToProduction(1, deficit, "vulpoids");
        }
        
        
        if (!isFunctional()) {
            supply.clear();
        }
    }


    @Override
    public void unapply() {
        super.unapply();
    }
    
    
    public boolean hasBiofactory() {
        return getSpecialItem() != null && Vulpoids.BIOFORGE_ITEM.equals(getSpecialItem().getId());
    }
    public boolean isOrganFarmVulpBiofactory() {
        return "organfarms".equals(getId()) && hasBiofactory();
    }
    public boolean isBiofacilityAndNotUnlocked() {
        return "biofacility".equals(getId()) && !Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_gotFactory");
    }
    public boolean isBiofacilityAndAnotherExists() {
        if (!"biofacility".equals(getId())) return false;
        return getMarketWithOtherBiofacility() != null;
    }
    public MarketAPI getMarketWithOtherBiofacility() {
        for (MarketAPI world_market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (!world_market.getId().equals(market.getId())) {
                if (world_market.hasIndustry("biofacility")) return world_market;
                if (world_market.hasIndustry("organfarms") && world_market.getIndustry("organfarms").isUpgrading()) return world_market;
            }
        }
        return null;
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
                if(market.getSize() <= 3) return Global.getSettings().getSpriteName("industry", "biotechlowvulp");
                if(market.getSize() >= 6) return Global.getSettings().getSpriteName("industry", "biotechhighvulp");
                return Global.getSettings().getSpriteName("industry", "biotechmedvulp");
            }
            if(market.getSize() <= 3) return Global.getSettings().getSpriteName("industry", "biotechlow");
            if(market.getSize() >= 6) return Global.getSettings().getSpriteName("industry", "biotechhigh");
        }
        return super.getCurrentImage();
    }
    
    @Override
    public String getCurrentName() {
        if (isOrganFarmVulpBiofactory()) {
            return "Vulpoid Vatfarm";
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
                    "while cast-offs are reprocessed to supplement the baseline production of assorted biological goods.";
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
    
    @Override
    protected void applyImproveModifiers() {
        if (isImproved()) {
            getSupply(Commodities.ORGANS).getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            if ("biofacility".equals(getId())) {
                //getSupply(Commodities.DRUGS).getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            }
            if (hasBiofactory()) {
                getSupply("vulpoids").getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            }
        } else {
            getSupply(Commodities.ORGANS).getQuantity().unmodifyFlat(getModId(3));
            //getSupply(Commodities.DRUGS).getQuantity().unmodifyFlat(getModId(3));
            getSupply("vulpoids").getQuantity().unmodifyFlat(getModId(3));
        }
    }
    
    @Override
    public boolean isAvailableToBuild() {
        if(market.getSize() <= 3) return false;
        if (isBiofacilityAndNotUnlocked()) return false;
        if (isBiofacilityAndAnotherExists()) return false;
        return market.hasCondition(Conditions.HABITABLE);
    }
    
    @Override
    public String getUnavailableReason() {
        if (!super.isAvailableToBuild()) return super.getUnavailableReason();
        if (market.getSize() <= 3) return "Requires a larger population";
        if (isBiofacilityAndNotUnlocked()) return "Unknown technology";
        if (isBiofacilityAndAnotherExists()) return "Already built on "+getMarketWithOtherBiofacility().getName();
        return "Requires habitable conditions";
    }
    
    // We override this so the tooltip doesn't blab about the exact properties of the "speculative" upgrade.
    @Override
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
