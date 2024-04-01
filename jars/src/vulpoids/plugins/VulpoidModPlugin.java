package vulpoids.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;

import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin.InstallableItemDescriptionMode;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import vulpoids.campaign.VulpoidsCampaignPlugin;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.intel.misc.ShinyProducedIntel;

public class VulpoidModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        
        Global.getSector().registerPlugin(new VulpoidsCampaignPlugin());
        
        
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        PersonAPI person;
        
        if (ip.getPerson("vulpoid_captain") == null) {
            person = VulpoidCreator.createVulpoid(null);
            person.setId("vulpoid_captain");
            person.setFaction(Factions.DERELICT);
            person.setName(new FullName("Biped Captain", "", FullName.Gender.FEMALE));
            person.setRankId(null);
            person.setPostId(Ranks.POST_FLEET_COMMANDER);
            person.getRelToPlayer().setRel(-0.3f);  // Inhospitable by default. Frankly, if she weren't a Vulpoid she'd be much angrier at you for blowing up her ship.
            person.setPortraitSprite("graphics/portraits/terran_fox.png");
            ip.addPerson(person);
        }
        
        
        
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_shiny_generic");
        person.setName(new FullName("Prefecto Vulpoid", "", FullName.Gender.FEMALE));
        person.setPortraitSprite("graphics/portraits/terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_desert_generic");
        person.setPortraitSprite("graphics/portraits/desert_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_terran_generic");
        person.setPortraitSprite("graphics/portraits/terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_winter_generic");
        person.setPortraitSprite("graphics/portraits/winter_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_space_desert_generic");
        person.setPortraitSprite("graphics/portraits/space_desert_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_space_terran_generic");
        person.setPortraitSprite("graphics/portraits/space_terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_space_winter_generic");
        person.setPortraitSprite("graphics/portraits/space_winter_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_nude_terran_generic");
        person.setPortraitSprite("graphics/portraits/nude_terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_barmaid");
        person.setPostId("vulp_barmaid");
        person.setPortraitSprite("graphics/portraits/nude_terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        

        final int VULPOID_PROD_AMOUNT = 3;
        
        //final String VULPOID_PRODUCTION_BUFFER_KEY = "$vulpoidProductionBuffer";
        final String VULPOID_SHINY_PRODUCTION_BUFFER_KEY = "$vulpoidShinyProductionBuffer";
        final String VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY = "$vulpoidUnapplyTimestamp";
        final String VULPOID_PRODUCTION_VOLUME_KEY = "$vulpoidProductionVolume";
        //final float VULPOIDS_PER_DAY_PER_PROD = 100f/30f;  // Baseline 100 per month
        final float VULPOIDS_SHINY_PER_DAY_PER_PROD = 0.5f/365f;  // Baseline 1.5 per year
        
        final int VULPOID_BROKEN_ORGANS = 2;

        ItemEffectsRepo.ITEM_EFFECTS.put("vulpoid_biofactory", new BaseInstallableItemEffect("vulpoid_biofactory") {
            public void apply(Industry industry) {
                
                industry.getMarket().addCondition("vulpoid_condition");
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    int size = b.getMarket().getSize();
                    if("organfarms".equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, -100, Misc.ucFirst(spec.getName().toLowerCase()));
                        //b.demand(spec.getId(), Commodities.HEAVY_MACHINERY, size-2, Misc.ucFirst(spec.getName().toLowerCase()));
                    } else if ("biofacility".equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, 1, "Vulpoid reprocessing");
                        b.supply(spec.getId(), Commodities.DRUGS, 1, "Vulpoid reprocessing");
                    }
                    b.demand(spec.getId(), Commodities.RARE_METALS, 1, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), "vulpoids", size-2, Misc.ucFirst(spec.getName().toLowerCase()));
                
                    int supplied_amount = b.getSupply("vulpoids").getQuantity().getModifiedInt();
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_VOLUME_KEY, supplied_amount);
                    
                    for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                        market.addCondition("vulpoid_demand", supplied_amount);
                    }
                }
            }
            public void unapply(Industry industry) {
                industry.getMarket().removeCondition("vulpoid_condition");
                if (industry.getMarket().isPlanetConditionMarketOnly()) industry.getMarket().getMemoryWithoutUpdate().set("$hasDecivBiofactory", true);
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), Commodities.ORGANS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), Commodities.DRUGS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), "vulpoids", -100, null);
                    //b.demand(spec.getId(), Commodities.HEAVY_MACHINERY, 0, null);
                    b.demand(spec.getId(), Commodities.RARE_METALS, 0, null);
                    
                    // Checks that the tags are set, only false when installed for the first time.
                    if(industry.getMarket().getMemoryWithoutUpdate().contains(VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY)) {
                        int production_volume = industry.getMarket().getMemoryWithoutUpdate().getInt(VULPOID_PRODUCTION_VOLUME_KEY);
                        if(production_volume > 0) {
                            float time_elapsed = Global.getSector().getClock().getElapsedDaysSince(industry.getMarket().getMemoryWithoutUpdate().getLong(VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY));
                            
                            float vulpoid_shiny_buffer = industry.getMarket().getMemoryWithoutUpdate().getFloat(VULPOID_SHINY_PRODUCTION_BUFFER_KEY);
                            vulpoid_shiny_buffer += time_elapsed * VULPOIDS_SHINY_PER_DAY_PER_PROD * production_volume;
                            
                            if (vulpoid_shiny_buffer >= 1) {
                                b.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("vulpoids_shiny", (int)vulpoid_shiny_buffer);
                                Global.getSector().getIntelManager().addIntel(new ShinyProducedIntel(b.getMarket()));
                            }
                            
                            industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_SHINY_PRODUCTION_BUFFER_KEY, vulpoid_shiny_buffer - (int)vulpoid_shiny_buffer);
                        }
                    }
                    // First-time setup of the buffer.
                    else {
                        //industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_BUFFER_KEY, 0);
                        industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_SHINY_PRODUCTION_BUFFER_KEY, 0);
                    }
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_LAST_UNAPPLY_TIMESTAMP_KEY, Global.getSector().getClock().getTimestamp());
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_VOLUME_KEY, 0);
                    
                    for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                        market.removeCondition("vulpoid_demand");
                    }
                }
            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Enables Vulpoid production. Demands %s unit of transplutonics to operate.",
                        pad, Misc.getHighlightColor(), 
                        "" + (int) 1);
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        
        
        ItemEffectsRepo.ITEM_EFFECTS.put("vulpoid_biofactory_broken", new BaseInstallableItemEffect("vulpoid_biofactory_broken") {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases organ farm production by %s units.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) VULPOID_BROKEN_ORGANS);
            }
            public void apply(Industry industry) {
                //super.apply(industry);
                industry.getSupply(Commodities.ORGANS).getQuantity().modifyFlat(spec.getId(), VULPOID_BROKEN_ORGANS, Misc.ucFirst(spec.getName().toLowerCase()));
                //industry.getDemand(Commodities.FOOD).getQuantity().modifyFlat(spec.getId(), VULPOID_BROKEN_ORGANS, Misc.ucFirst(spec.getName().toLowerCase()));
            }
            public void unapply(Industry industry) {
                industry.getSupply(Commodities.ORGANS).getQuantity().unmodifyFlat(spec.getId());
                //industry.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(spec.getId());
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        
    }
}