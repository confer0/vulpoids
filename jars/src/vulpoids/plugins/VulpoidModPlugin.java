package vulpoids.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

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
import vulpoids.campaign.VulpoidsCampaignPlugin;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        
        Global.getSector().registerPlugin(new VulpoidsCampaignPlugin());
        
        
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        PersonAPI person;
        
        if (ip.getPerson(Vulpoids.PERSON_LAISA) == null) {
            person = VulpoidCreator.createPrefectoVulpoid(null);
            person.setId(Vulpoids.PERSON_LAISA);
            person.setFaction(Vulpoids.FACTION_EXODYNE);
            person.setName(new FullName("Laisa", "", FullName.Gender.FEMALE));
            person.setRankId(null);
            person.setPostId(Ranks.POST_FLEET_COMMANDER);
            person.getRelToPlayer().setRel(-0.3f);  // Inhospitable by default. Frankly, if she weren't a Vulpoid she'd be much angrier at you for blowing up her ship.
            person.setPortraitSprite("graphics/portraits/vulpoid/laisa/clothed/default.png");
            person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, "graphics/portraits/vulpoid/laisa/clothed/default.png");
            person.getMemoryWithoutUpdate().set(Vulpoids.KEY_OFFICER_PORTRAIT, "graphics/portraits/vulpoid/laisa/laisa_special_admiral.png");
            person.getMemoryWithoutUpdate().set(Vulpoids.KEY_CARGO_ICON, "graphics/icons/cargo/vulpoids/vulpoid_laisa.png");
            ip.addPerson(person);
        }
        
        
        
        
        /*person = VulpoidCreator.createVulpoid(null);
        person.setId("vulpoid_shiny_generic");
        person.setName(new FullName("Profecto Vulpoid", "", FullName.Gender.FEMALE));
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
        ip.addPerson(person);*/
        
        
        
        final int VULPOID_BROKEN_ORGANS = 2;

        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.BIOFORGE_ITEM, new BaseInstallableItemEffect(Vulpoids.BIOFORGE_ITEM) {
            public void apply(Industry industry) {
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    int size = b.getMarket().getSize();
                    if("organfarms".equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, -100, Misc.ucFirst(spec.getName().toLowerCase()));
                    } else if ("biofacility".equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, 1, "Vulpoid reprocessing");
                        //b.supply(spec.getId(), Commodities.DRUGS, 1, "Vulpoid reprocessing");
                    }
                    b.demand(spec.getId(), Commodities.RARE_METALS, 1, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), "vulpoids", size-3, Misc.ucFirst(spec.getName().toLowerCase()));
                
                    int supplied_amount = b.getSupply("vulpoids").getQuantity().getModifiedInt();
                    if (supplied_amount > 0) {
                        Global.getSector().getMemoryWithoutUpdate().set("$exportedVulpoids", true);
                        for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                            market.addCondition("vulpoid_demand", supplied_amount);
                        }
                    }
                    
                    if(!industry.getMarket().hasCondition("vulpoid_population") && !b.getMarket().getMemoryWithoutUpdate().contains("$infinitelooppreventer")) {
                        b.getMarket().getMemoryWithoutUpdate().set("$infinitelooppreventer", true);
                        // Need to reapply to get the shortages to calculate properly.
                        // Otherwise we can end up starting off with an overly high pop size.
                        // Just reapplying without doing anything else would cause an infinite loop,
                        // hence the $infinitelooppreventer flags.
                        b.reapply();
                        industry.getMarket().addCondition("vulpoid_population");
                        b.getMarket().getMemoryWithoutUpdate().unset("$infinitelooppreventer");
                    }
                }
            }
            public void unapply(Industry industry) {
                if (industry.getMarket().isPlanetConditionMarketOnly()) industry.getMarket().getMemoryWithoutUpdate().set("$hasDecivBiofactory", true);
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), Commodities.ORGANS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    //b.supply(spec.getId(), Commodities.DRUGS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), "vulpoids", -100, null);
                    b.demand(spec.getId(), Commodities.RARE_METALS, 0, null);
                    
                    for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                        market.removeCondition("vulpoid_demand");
                    }
                }
            }
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Enables Vulpoid production when supplied %s unit of transplutonics.",
                        pad, Misc.getHighlightColor(), 
                        "" + (int) 1);
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        
        
        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.CORRUPT_BIOFORGE_ITEM, new BaseInstallableItemEffect(Vulpoids.CORRUPT_BIOFORGE_ITEM) {
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases organ farm production by %s units.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) VULPOID_BROKEN_ORGANS);
            }
            public void apply(Industry industry) {
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