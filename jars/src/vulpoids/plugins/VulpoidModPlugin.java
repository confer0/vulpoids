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
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        PersonAPI person;
        
        if (ip.getPerson(Vulpoids.PERSON_LAISA) == null) {
            person = VulpoidCreator.createProfectoVulpoid();
            person.setId(Vulpoids.PERSON_LAISA);
            person.setFaction(Vulpoids.FACTION_EXODYNE);
            person.setName(new FullName("Exodyne Captain", "", FullName.Gender.FEMALE));
            person.setRankId(null);
            person.setPostId(Ranks.POST_FLEET_COMMANDER);
            person.getRelToPlayer().setRel(-0.1f);
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLIMATE, VulpoidCreator.CLIMATE_LAISA);
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLOTHING, VulpoidCreator.CLOTHING_SUIT);
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_EXPRESSION, VulpoidCreator.EXPRESSION_HELMET);
            person.getStats().setSkillLevel(Vulpoids.SKILL_LAISA_ADMIN, 1);
            person.getStats().setSkillLevel(Vulpoids.SKILL_LAISA_OFFICER, 1);
            person.getStats().setLevel(8);
            person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
            person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            //person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 1);
            person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
            person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 1);
            person.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
            person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, 8);
            person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, 5);
            //person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, "graphics/portraits/vulpoid/laisa/clothed/default.png");
            //person.getMemoryWithoutUpdate().set(Vulpoids.KEY_OFFICER_PORTRAIT, "graphics/portraits/vulpoid/laisa/laisa_special_admiral.png");
            //person.getMemoryWithoutUpdate().set(Vulpoids.KEY_CARGO_ICON, "graphics/icons/cargo/vulpoids/vulpoid_laisa.png");
            ip.addPerson(person);
        }
        
        if(ip.getPerson(Vulpoids.PERSON_DUMMY_TERRAN) == null) {
            person = VulpoidCreator.createVulpoid();
            person.setId(Vulpoids.PERSON_DUMMY_TERRAN);
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLIMATE, VulpoidCreator.CLIMATE_TERRAN);
            ip.addPerson(person);
        }
        if(ip.getPerson(Vulpoids.PERSON_DUMMY_DESERT) == null) {
            person = VulpoidCreator.createVulpoid();
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLIMATE, VulpoidCreator.CLIMATE_DESERT);
            person.setId(Vulpoids.PERSON_DUMMY_DESERT);
            ip.addPerson(person);
        }
        if(ip.getPerson(Vulpoids.PERSON_DUMMY_ARCTIC) == null) {
            person = VulpoidCreator.createVulpoid();
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_CLIMATE, VulpoidCreator.CLIMATE_ARCTIC);
            person.setId(Vulpoids.PERSON_DUMMY_ARCTIC);
            ip.addPerson(person);
        }
        
        
        
        final int VULPOID_BROKEN_ORGANS = 2;

        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.BIOFORGE_ITEM, new BaseInstallableItemEffect(Vulpoids.BIOFORGE_ITEM) {
            public void apply(Industry industry) {
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    int size = b.getMarket().getSize();
                    if(Vulpoids.INDUSTRY_ORGANFARM.equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, -100, Misc.ucFirst(spec.getName().toLowerCase()));
                    } else if (Vulpoids.INDUSTRY_BIOFACILITY.equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, 1, "Vulpoid reprocessing");
                        //b.supply(spec.getId(), Commodities.DRUGS, 1, "Vulpoid reprocessing");
                    }
                    b.demand(spec.getId(), Commodities.RARE_METALS, 1, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), Vulpoids.CARGO_ITEM, size-3, Misc.ucFirst(spec.getName().toLowerCase()));
                
                    int supplied_amount = b.getSupply(Vulpoids.CARGO_ITEM).getQuantity().getModifiedInt();
                    if (supplied_amount > 0) {
                        Global.getSector().getMemoryWithoutUpdate().set(Vulpoids.KEY_EXPORTED_VULPOIDS, true);
                        for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                            market.addCondition(Vulpoids.CONDITION_VULPOID_DEMAND, supplied_amount);
                        }
                    }
                    
                    if(!industry.getMarket().hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION) && !b.getMarket().getMemoryWithoutUpdate().contains("$infinitelooppreventer")) {
                        b.getMarket().getMemoryWithoutUpdate().set("$infinitelooppreventer", true);
                        // Need to reapply to get the shortages to calculate properly.
                        // Otherwise we can end up starting off with an overly high pop size.
                        // Just reapplying without doing anything else would cause an infinite loop,
                        // hence the $infinitelooppreventer flags.
                        b.reapply();
                        industry.getMarket().addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
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
                    b.supply(spec.getId(), Vulpoids.CARGO_ITEM, -100, null);
                    b.demand(spec.getId(), Commodities.RARE_METALS, 0, null);
                    
                    for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                        market.removeCondition(Vulpoids.CONDITION_VULPOID_DEMAND);
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