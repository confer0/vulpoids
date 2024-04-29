package vulpoids.impl.campaign.econ.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
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
    
    public static float LOBSTERS_PER_DAY = 50/30f;
    protected float lobster_production_buffer = 0;
    
    @Override
    public void advance(float amount) {
        super.advance(amount);

        if (special != null && market.isPlayerOwned()) {
            float days = Misc.getDays(amount);
            if(Vulpoids.BIOFORGE_ITEM.equals(special.getId())) {
                shiny_vulpoid_production_buffer += days * SHINY_VULPOIDS_PER_DAY * Math.max(0, getSupply(Vulpoids.CARGO_ITEM).getQuantity().getModifiedInt());
                while(shiny_vulpoid_production_buffer >= 1) {
                    shiny_vulpoid_production_buffer -= 1;
                    market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData(Vulpoids.SPECIAL_ITEM_DEFAULT, null), 1);
                    Global.getSector().getIntelManager().addIntel(new ShinyProducedIntel(market));
                }
            } else if (Vulpoids.LOBSTER_BIOFORGE_ITEM.equals(special.getId())) {
                lobster_production_buffer += days * LOBSTERS_PER_DAY * (market.getSize() - 2);
                if(lobster_production_buffer >= 1) {
                    market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity(Commodities.LOBSTER, (int)lobster_production_buffer);
                    lobster_production_buffer -= (int)lobster_production_buffer;
                }
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
        if(Vulpoids.INDUSTRY_ORGANFARM.equals(getId())) {
            supply(Commodities.ORGANS, size - 3);
        } else if(Vulpoids.INDUSTRY_BIOFACILITY.equals(getId())) {
            supply(Commodities.ORGANS, size - 2);
            //supply(Commodities.DRUGS, size - 1);
        }
        
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.FOOD);
        // Need to do this check so the deficit doesn't appear when production is meant to be disabled.
        if (!isOrganFarmVulpBiofactory()) applyDeficitToProduction(1, deficit, Commodities.ORGANS, Commodities.DRUGS);
        deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.FOOD, Commodities.HEAVY_MACHINERY);
        Pair<String, Integer> rare_deficit = getMaxDeficit(Commodities.RARE_METALS);
        if(rare_deficit.two > 0) {
            rare_deficit.two = 100;
            applyDeficitToProduction(1, rare_deficit, Vulpoids.CARGO_ITEM);
        } else {
            applyDeficitToProduction(1, deficit, Vulpoids.CARGO_ITEM);
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
    public boolean hasLobsterBioforge() {
        return getSpecialItem() != null && Vulpoids.LOBSTER_BIOFORGE_ITEM.equals(getSpecialItem().getId());
    }
    public boolean isOrganFarmVulpBiofactory() {
        return Vulpoids.INDUSTRY_ORGANFARM.equals(getId()) && hasBiofactory();
    }
    public boolean isOrganFarmLobsterBioforge() {
        return Vulpoids.INDUSTRY_ORGANFARM.equals(getId()) && hasLobsterBioforge();
    }
    public boolean isBiofacilityAndNotUnlocked() {
        return Vulpoids.INDUSTRY_BIOFACILITY.equals(getId()) && !Global.getSector().getMemoryWithoutUpdate().getBoolean(Vulpoids.KEY_GOT_FACTORY);
    }
    public boolean isBiofacilityAndAnotherExists() {
        if (!Vulpoids.INDUSTRY_BIOFACILITY.equals(getId())) return false;
        return getMarketWithOtherBiofacility() != null;
    }
    public MarketAPI getMarketWithOtherBiofacility() {
        for (MarketAPI world_market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (!world_market.getId().equals(market.getId())) {
                if (world_market.hasIndustry(Vulpoids.INDUSTRY_BIOFACILITY)) return world_market;
                if (world_market.hasIndustry(Vulpoids.INDUSTRY_ORGANFARM) && world_market.getIndustry(Vulpoids.INDUSTRY_ORGANFARM).isUpgrading()) return world_market;
            }
        }
        return null;
    }
    public boolean isBiofacilityVulpBiofactory() {
        return Vulpoids.INDUSTRY_BIOFACILITY.equals(getId()) && hasBiofactory();
    }
    public boolean isBiofacilityLobsterBioforge() {
        return Vulpoids.INDUSTRY_BIOFACILITY.equals(getId()) && hasLobsterBioforge();
    }
    
    
    @Override
    public String getCurrentImage() {
        if (isOrganFarmVulpBiofactory()) {
            return Global.getSettings().getSpriteName("industry", "organfarmvulp");
        }
        if (isOrganFarmLobsterBioforge()) {
            return Global.getSettings().getSpriteName("industry", "organfarmlobster");
        }
        // Note - Using size 4 as the small instead of 3, because they can only be built on 4+.
        if (Vulpoids.INDUSTRY_BIOFACILITY.equals(getId())) {
            String item = "";
            if (hasBiofactory()) {
                item = "vulp";
            } else if (hasLobsterBioforge()) {
                item = "lobster";
            }
            if(market.getSize() <= 4) return Global.getSettings().getSpriteName("industry", "biotechlow"+item);
            if(market.getSize() >= 6) return Global.getSettings().getSpriteName("industry", "biotechhigh"+item);
            return Global.getSettings().getSpriteName("industry", "biotechmed"+item);
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
        if (isOrganFarmLobsterBioforge()) {
            return "Lobster Aquariums";
        }
        if (isBiofacilityLobsterBioforge()) {
            return "Lobster Biofacility";
        }
        if (isBiofacilityAndNotUnlocked()) {
            return "Speculative Improvements";
        }
        return super.getCurrentName();
    }
    
    @Override
    protected String getDescriptionOverride() {
        if (isOrganFarmVulpBiofactory()) {
            return "With the power of a fully-operational bioforge, adorable fluffy friends can be printed at industrial scale."+
                    "The phrase 'how the sausage is made' is often used to describe operations, "+
                    "and many steps in the bioforging process do in fact have an unpleasant resemblance to sausage meat.";
        }
        if (isBiofacilityVulpBiofactory()) {
            return "The pinnacle of Exodyne Biotech's technological achievement. With a bioforge at its heart, "+
                    "this sleek and advanced megacomplex is capable of producing an endless flow of fluffy friends, "+
                    "while cast-offs are reprocessed to supplement the pre-existing organ production.";
        }
        if (isOrganFarmLobsterBioforge()) {
            return "A specialized bioforge enables the production of Volturnian lobsters outside of their homeworld. "+
                    "Even wetter and smellier than a traditional organ farming operation, these operations are best "+
                    "kept extremely far from inhabited areas.";
        }
        if (isBiofacilityLobsterBioforge()) {
            return "The sleekly-designed biofacility has been converted for use with an alternative bioforge. "+
                    "Growth vats have become makeshift aquariums to support the Volturnian lobsters churned "+
                    "out by the bioforge until they can fully develop.";
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
            if (Vulpoids.INDUSTRY_BIOFACILITY.equals(getId())) {
                //getSupply(Commodities.DRUGS).getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            }
            if (hasBiofactory()) {
                getSupply(Vulpoids.CARGO_ITEM).getQuantity().modifyFlat(getModId(3), 1, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
            }
        } else {
            getSupply(Commodities.ORGANS).getQuantity().unmodifyFlat(getModId(3));
            //getSupply(Commodities.DRUGS).getQuantity().unmodifyFlat(getModId(3));
            getSupply(Vulpoids.CARGO_ITEM).getQuantity().unmodifyFlat(getModId(3));
        }
    }
    
    @Override
    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Production increased by %s unit.", 0f, highlight, ""+1);
        } else {
            info.addPara("Increases production by %s unit.", 0f, highlight, ""+1);
        }
        info.addSpacer(opad);
        super.addImproveDesc(info, mode);
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
        if (Vulpoids.INDUSTRY_BIOFACILITY.equals(getId()) && !Global.getSector().getMemoryWithoutUpdate().getBoolean(Vulpoids.KEY_GOT_FACTORY)) {
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
