package vulpoids.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin.InstallableItemDescriptionMode;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import vulpoids.campaign.VulpoidsCampaignPlugin;

public class VulpoidModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        
        Global.getSector().registerPlugin(new VulpoidsCampaignPlugin());

        final int VULPOID_PROD_BONUS = 1;
        final int VULPOID_PROD_AMOUNT = 3;
        
        final String VULPOID_PRODUCTION_BUFFER_KEY = "$vulpoidProductionBuffer";
        final String VULPOID_SHINY_PRODUCTION_BUFFER_KEY = "$vulpoidShinyProductionBuffer";
        final String VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY = "$vulpoidUnapplyTimestamp";
        final String VULPOID_PRODUCTION_VOLUME_KEY = "$vulpoidProductionVolume";
        final float VULPOIDS_PER_DAY_PER_PROD = 100f/30f;
        final float VULPOIDS_SHINY_PER_DAY_PER_PROD = 1f/30f;
        
        final int VULPOID_BROKEN_ORGANS = 3;

        ItemEffectsRepo.ITEM_EFFECTS.put("vulpoid_biofactory", new BaseInstallableItemEffect("vulpoid_biofactory") {
            public void apply(Industry industry) {
                industry.getSupplyBonus().modifyFlat(spec.getId(), VULPOID_PROD_BONUS, "Vulpoid workforce");
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), "vulpoids", VULPOID_PROD_AMOUNT-VULPOID_PROD_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));
                
                    // Vulpoid Production is handled in unapply, for clock reasons.
                    /*if (!industry.getMarket().getMemoryWithoutUpdate().contains(VULPOID_PRODUCTION_CLOCK_KEY)) {
                        industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_CLOCK_KEY, Global.getSector().getClock().getTimestamp());
                        industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_BUFFER_KEY, 0);
                        industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_SHINY_PRODUCTION_BUFFER_KEY, 0);
                    }
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_CLOCK_KEY, Global.getSector().getClock().getTimestamp());*/
                    
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_VOLUME_KEY, b.getSupply("vulpoids").getQuantity().getModifiedInt());
                }
            }
            public void unapply(Industry industry) {
                industry.getSupplyBonus().modifyFlat(spec.getId(), 0, Misc.ucFirst(spec.getName().toLowerCase()));
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), "vulpoids", -100, null);
                    
                    /*float days_passed = Global.getSector().getClock().getElapsedDaysSince(industry.getMarket().getMemoryWithoutUpdate().getLong(VULPOID_PRODUCTION_CLOCK_KEY));
                    int production_volume = b.getSupply("vulpoids").getQuantity().getModifiedInt();
                    float vulpoid_production_buffer = industry.getMarket().getMemoryWithoutUpdate().getFloat(VULPOID_PRODUCTION_BUFFER_KEY);
                    vulpoid_production_buffer += Math.abs(days_passed * VULPOIDS_PER_DAY_PER_PROD * production_volume);
                    int vulpoids_produced = (int)(vulpoid_production_buffer);
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_BUFFER_KEY, vulpoid_production_buffer - vulpoids_produced);
                    //float vulpoid_shiny_production_buffer = industry.getMarket().getMemoryWithoutUpdate().getFloat(VULPOID_SHINY_PRODUCTION_BUFFER_KEY);
                    //vulpoid_shiny_production_buffer = vulpoid_shiny_production_buffer + days_passed * VULPOIDS_SHINY_PER_DAY_PER_PROD * production_volume;
                    //int vulpoids_shiny_produced = (int)(vulpoid_shiny_production_buffer);
                    //industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_SHINY_PRODUCTION_BUFFER_KEY, vulpoid_shiny_production_buffer - vulpoids_shiny_produced);
                    if(vulpoids_produced !=  0) {
                        int[] test = new int[0];
                        test[vulpoids_produced] = 5;
                    }*/
                    // Checks that the tags are set, only false when installed for the first time.
                    if(industry.getMarket().getMemoryWithoutUpdate().contains(VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY)) {
                        // This code is to check that we applied before we last unapplied.
                        // That means that the item is still installed.
                        int production_volume = industry.getMarket().getMemoryWithoutUpdate().getInt(VULPOID_PRODUCTION_VOLUME_KEY);
                        if(production_volume > 0) {
                            float time_elapsed = Global.getSector().getClock().getElapsedDaysSince(industry.getMarket().getMemoryWithoutUpdate().getLong(VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY));
                            
                            float vulpoid_buffer = industry.getMarket().getMemoryWithoutUpdate().getFloat(VULPOID_PRODUCTION_BUFFER_KEY);
                            vulpoid_buffer += time_elapsed * VULPOIDS_PER_DAY_PER_PROD * production_volume;
                            float vulpoid_shiny_buffer = industry.getMarket().getMemoryWithoutUpdate().getFloat(VULPOID_SHINY_PRODUCTION_BUFFER_KEY);
                            vulpoid_shiny_buffer += time_elapsed * VULPOIDS_SHINY_PER_DAY_PER_PROD * production_volume;
                            
                            b.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("vulpoids", (int)vulpoid_buffer);
                            b.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("vulpoids_shiny", (int)vulpoid_shiny_buffer);
                            
                            industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_BUFFER_KEY, vulpoid_buffer - (int)vulpoid_buffer);
                            industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_SHINY_PRODUCTION_BUFFER_KEY, vulpoid_shiny_buffer - (int)vulpoid_shiny_buffer);
                        }
                    }
                    // First-time setup of the buffer.
                    else {
                        industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_BUFFER_KEY, 0);
                        industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_SHINY_PRODUCTION_BUFFER_KEY, 0);
                    }
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY, Global.getSector().getClock().getTimestamp());
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_VOLUME_KEY, 0);
                }
            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases light industry production by %s units. If the colony is a freeport, also produces %s Vulpoids per month for export.",
                // Note - units of production and units per month _appear_ to line up when using an econunit of 5. Total stockpile is 22 at 3 production, 25 at 4, 31 at 5, 37 at 6, and 43 at 7. (Note: Luddic Maj applies!)
                        pad, Misc.getHighlightColor(), 
                        "" + (int) VULPOID_PROD_BONUS, "" + (int) VULPOID_PROD_AMOUNT);
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        
        
        ItemEffectsRepo.ITEM_EFFECTS.put("vulpoid_biofactory_broken", new BaseInstallableItemEffect("vulpoid_biofactory_broken") {
            public void apply(Industry industry) {
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), Commodities.ORGANS, VULPOID_BROKEN_ORGANS, Misc.ucFirst(spec.getName().toLowerCase()));
                }
            }
            public void unapply(Industry industry) {
                industry.getSupplyBonus().modifyFlat(spec.getId(), 0, Misc.ucFirst(spec.getName().toLowerCase()));
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), Commodities.ORGANS, -100, null);
                }
            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Enables the production of %s units of harvested organs from light industry.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) VULPOID_BROKEN_ORGANS);
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });

        // Make illegal - Not needed now that I'm using the ai_cores demand_class.
        /*for(FactionAPI faction : Global.getSector().getAllFactions()) {
            if(faction.isIllegal("ai_cores")) {
                faction.makeCommodityIllegal("vulpoids");
            }
        }*/

        // The commodity is implemented with a demand_class of ai_cores so it can be sold to important people.
        // That comes with a nonecon tag though, which needs to be removed to let the colony actually produce them.
        //Global.getSector().getEconomy().getCommoditySpec("vulpoids").getTags().remove("nonecon");
        
        
        
        
    }

}