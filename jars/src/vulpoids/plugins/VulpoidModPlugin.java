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
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import vulpoids.campaign.VulpoidsCampaignPlugin;
import vulpoids.impl.campaign.intel.misc.ShinyProducedIntel;

public class VulpoidModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        
        Global.getSector().registerPlugin(new VulpoidsCampaignPlugin());
        
        
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        PersonAPI person;
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_shiny_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Prefecto Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_desert_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/desert_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_terran_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_winter_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/winter_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_space_desert_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/space_desert_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_space_terran_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/space_terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_space_winter_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/space_winter_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_nude_terran_generic");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getRelToPlayer().setRel(1);
        person.setPortraitSprite("graphics/portraits/nude_terran_fox.png");
        ip.removePerson(person.getId());
        ip.addPerson(person);
        
        person = Global.getFactory().createPerson();
        person.setId("vulpoid_barmaid");
        person.setFaction(Factions.PLAYER);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setRankId("vulp_servant");
        person.setPostId("vulp_barmaid");
        person.getRelToPlayer().setRel(1);
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
                    if("organfarm".equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, -100, Misc.ucFirst(spec.getName().toLowerCase()));
                    }
                    b.supply(spec.getId(), "vulpoids", VULPOID_PROD_AMOUNT, Misc.ucFirst(spec.getName().toLowerCase()));
                
                    int supplied_amount = b.getSupply("vulpoids").getQuantity().getModifiedInt();
                    industry.getMarket().getMemoryWithoutUpdate().set(VULPOID_PRODUCTION_VOLUME_KEY, supplied_amount);
                    
                    for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                        market.addCondition("vulpoid_demand", supplied_amount);
                    }
                }
            }
            public void unapply(Industry industry) {
                industry.getMarket().removeCondition("vulpoid_condition");
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), Commodities.ORGANS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), "vulpoids", -100, null);
                    
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
                text.addPara(pre + "Converts organ vats into Vulpoid birthing tanks, producing %s units of vulpoids.",
                        pad, Misc.getHighlightColor(), 
                        "" + (int) VULPOID_PROD_AMOUNT);
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
                industry.getDemand(Commodities.FOOD).getQuantity().modifyFlat(spec.getId(), VULPOID_BROKEN_ORGANS, Misc.ucFirst(spec.getName().toLowerCase()));
            }
            public void unapply(Industry industry) {
                industry.getSupply(Commodities.ORGANS).getQuantity().unmodifyFlat(spec.getId());
                industry.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(spec.getId());
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        
    }
}