package vulpoids.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;

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
import com.fs.starfarer.api.impl.campaign.econ.impl.BoostIndustryInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Pair;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.econ.FilteredAir;
import vulpoids.impl.campaign.econ.LobstersGrowing;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;

public class VulpoidModPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        PersonAPI person;
        
        String dealmakerParams = Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).getParams();
        if(!dealmakerParams.contains(Vulpoids.INDUSTRY_VULPOIDAGENCY) && Global.getSector().getPlayerFaction().knowsIndustry(Vulpoids.INDUSTRY_VULPOIDAGENCY)) {
            dealmakerParams += ", "+Vulpoids.INDUSTRY_VULPOIDAGENCY;
            Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).setParams(dealmakerParams);
        }
        
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
            VulpoidCreator.setPersonPortraitPropertyAtIndex(person, VulpoidCreator.INDEX_EXPRESSION, VulpoidCreator.EXPRESSION_OFFICER);
            person.getStats().setSkillLevel(Vulpoids.SKILL_ADMIN, 0);
            person.getStats().setSkillLevel(Vulpoids.SKILL_OFFICER, 0);
            person.getStats().setSkillLevel(Vulpoids.SKILL_LAISA_ADMIN, 1);
            person.getStats().setSkillLevel(Vulpoids.SKILL_LAISA_OFFICER, 1);
            person.getStats().setLevel(8);
            person.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
            person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            //person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 1);
            person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
            person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
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
                        //b.getSupply(Commodities.ORGANS).getQuantity().unmodify();
                        b.supply(spec.getId(), Commodities.ORGANS, -100, Misc.ucFirst(spec.getName().toLowerCase()));
                    } else if (Vulpoids.INDUSTRY_BIOFACILITY.equals(b.getId())) {
                        b.supply(spec.getId(), Commodities.ORGANS, 1, "Vulpoid reprocessing");
                        //b.supply(spec.getId(), Commodities.DRUGS, 1, "Vulpoid reprocessing");
                    }
                    b.demand(spec.getId(), Commodities.RARE_METALS, 1, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.supply(spec.getId(), Vulpoids.CARGO_ITEM, size-3, Misc.ucFirst(spec.getName().toLowerCase()));
                
                    int supplied_amount = b.getSupply(Vulpoids.CARGO_ITEM).getQuantity().getModifiedInt();
                    if (supplied_amount > 0 && VulpoidAcceptanceEventIntel.get()!=null && VulpoidAcceptanceEventIntel.get().getProgress()>=VulpoidAcceptanceEventIntel.PROGRESS_SANCTIONS_START) {
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
            @Override
            public void unapply(Industry industry) {
                if (industry.getMarket().isPlanetConditionMarketOnly()) industry.getMarket().getMemoryWithoutUpdate().set("$hasDecivBiofactory", true);
                if (industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(spec.getId(), Commodities.ORGANS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    //b.supply(spec.getId(), Commodities.DRUGS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.getSupply(Vulpoids.CARGO_ITEM).getQuantity().unmodify();
                    b.demand(spec.getId(), Commodities.RARE_METALS, 0, null);
                    
                    for(MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                        market.removeCondition(Vulpoids.CONDITION_VULPOID_DEMAND);
                    }
                }
            }
            @Override
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
            @Override
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases organ farm production by %s units.",
                        pad, Misc.getHighlightColor(),
                        "" + (int) VULPOID_BROKEN_ORGANS);
            }
            @Override
            public void apply(Industry industry) {
                industry.getSupply(Commodities.ORGANS).getQuantity().modifyFlat(spec.getId(), VULPOID_BROKEN_ORGANS, Misc.ucFirst(spec.getName().toLowerCase()));
                //industry.getDemand(Commodities.FOOD).getQuantity().modifyFlat(spec.getId(), VULPOID_BROKEN_ORGANS, Misc.ucFirst(spec.getName().toLowerCase()));
            }
            @Override
            public void unapply(Industry industry) {
                industry.getSupply(Commodities.ORGANS).getQuantity().unmodifyFlat(spec.getId());
                //industry.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(spec.getId());
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.AIR_FILTER_ITEM, new BaseInstallableItemEffect(Vulpoids.AIR_FILTER_ITEM) {
            float carryOverPollutionRemovalTimer=-1;
            @Override
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Improves local atmospheric conditions. Adds demand for %s units of volatiles and transplutonics.", pad, Misc.getHighlightColor(), "4");
            }
            @Override
            public void apply(Industry industry) {
                if(!industry.getMarket().hasCondition(Vulpoids.CONDITION_FILTERED_AIR)) industry.getMarket().addCondition(Vulpoids.CONDITION_FILTERED_AIR);
                FilteredAir plugin = (FilteredAir)industry.getMarket().getCondition(Vulpoids.CONDITION_FILTERED_AIR).getPlugin();
                if(carryOverPollutionRemovalTimer!=-1) plugin.setPollutionRemovalTimer(carryOverPollutionRemovalTimer);
                if(plugin.shouldRemovePollution()) industry.getMarket().removeCondition(Conditions.POLLUTION);
                if(industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.demand(9, Commodities.RARE_METALS, 4, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.demand(9, Commodities.VOLATILES, 4, Misc.ucFirst(spec.getName().toLowerCase()));
                    float rare_hazard = 1f - industry.getMarket().getCommodityData(Commodities.RARE_METALS).getAvailable() / (float)industry.getDemand(Commodities.RARE_METALS).getQuantity().getModifiedInt();
                    rare_hazard = Math.round(rare_hazard * 0.5f * 100f) / 100f;
                    float vol_hazard = 1f - industry.getMarket().getCommodityData(Commodities.VOLATILES).getAvailable() / (float)industry.getDemand(Commodities.VOLATILES).getQuantity().getModifiedInt();
                    vol_hazard = Math.round(vol_hazard * 0.5f * 100f) / 100f;
                    if(rare_hazard >= vol_hazard && rare_hazard > 0) {
                        industry.getMarket().getHazard().modifyFlat(spec.getId(), rare_hazard, Misc.ucFirst(spec.getName().toLowerCase()) + " transplutonic shortage");
                    } else if(vol_hazard > 0) {
                        industry.getMarket().getHazard().modifyFlat(spec.getId(), vol_hazard, Misc.ucFirst(spec.getName().toLowerCase()) + " volatiles shortage");
                    }
                }
            }
            @Override
            public void unapply(Industry industry) {
                industry.getMarket().getHazard().unmodifyFlat(spec.getId());
                if(industry.getMarket().hasCondition(Vulpoids.CONDITION_FILTERED_AIR)) {
                    carryOverPollutionRemovalTimer = ((FilteredAir)industry.getMarket().getCondition(Vulpoids.CONDITION_FILTERED_AIR).getPlugin()).getPollutionRemovalTimer();
                } else {
                    carryOverPollutionRemovalTimer = -1;
                }
                industry.getMarket().removeCondition(Vulpoids.CONDITION_FILTERED_AIR);
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        final int MIDAS_NANOFORGE_PROD = 3;
        final float MIDAS_NANOFORGE_QUALITY_BONUS = 0.5f;
        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.MIDAS_NANOFORGE_ITEM, new BaseInstallableItemEffect(Vulpoids.MIDAS_NANOFORGE_ITEM) {
            @Override
            public void apply(Industry industry) {
                industry.getSupplyBonus().modifyFlat(spec.getId(), MIDAS_NANOFORGE_PROD, Misc.ucFirst(spec.getName().toLowerCase()));
                industry.getMarket().getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD)
                        .modifyFlat("nanoforge", MIDAS_NANOFORGE_QUALITY_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));
                if(industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    int size = b.getMarket().getSize();
                    //b.demand(Commodities.METALS, 0);
                    //b.demand(Commodities.RARE_METALS, 0);
                    b.getDemand(Commodities.METALS).getQuantity().modifyMult(spec.getId(), 0);
                    b.getDemand(Commodities.RARE_METALS).getQuantity().modifyMult(spec.getId(), 0);
                    
                    b.demand(9, Commodities.ORE, size+3, Misc.ucFirst(spec.getName().toLowerCase()));
                    b.demand(9, Commodities.RARE_ORE, size+1, Misc.ucFirst(spec.getName().toLowerCase()));
                    Pair<String, Integer> deficit = b.getMaxDeficit(Commodities.ORE, Commodities.RARE_ORE);
                    int maxDeficit = size - 3;
                    if (deficit.two > maxDeficit) deficit.two = maxDeficit;
                    // Re-implementing applyDeficitToProduction since it's protected. :/
                    for (String commodity : new String[]{Commodities.HEAVY_MACHINERY, Commodities.SUPPLIES, Commodities.HAND_WEAPONS, Commodities.SHIPS}) {
			if (b.getSupply(commodity).getQuantity().isUnmodified()) continue;
			b.supply(9, commodity, -deficit.two, BaseIndustry.getDeficitText(deficit.one));
                    }
                }
            }
            @Override
            public void unapply(Industry industry) {
                industry.getSupplyBonus().modifyFlat(spec.getId(), 0, Misc.ucFirst(spec.getName().toLowerCase()));
                industry.getMarket().getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat("nanoforge");
                if(industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.getDemand(Commodities.METALS).getQuantity().unmodifyMult(spec.getId());
                    b.getDemand(Commodities.RARE_METALS).getQuantity().unmodifyMult(spec.getId());
                    b.demand(9, Commodities.ORE, 0, null);
                    b.demand(9, Commodities.RARE_ORE, 0, null);
                }
            }
            @Override
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                String heavyIndustry = "heavy industry ";
                if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST) {
                    heavyIndustry = "";
                }
                text.addPara(pre + "Increases ship and weapon production quality by %s. " +
                        "Increases " + heavyIndustry + "production by %s units." +
                        " On habitable worlds, causes pollution which becomes permanent. "+
                        "Demands ore instead of metal.",
                        pad, Misc.getHighlightColor(), 
                        "" + (int) Math.round(MIDAS_NANOFORGE_QUALITY_BONUS * 100f) + "%",
                        "" + (int) MIDAS_NANOFORGE_PROD);
            }
        });
        
        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.MANGONUT_TREE_ITEM, new BoostIndustryInstallableItemEffect(Vulpoids.MANGONUT_TREE_ITEM, 1, 0) {
            @Override
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data, 
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Increases farming production by %s unit. Enables mangonut production.",
                        pad, Misc.getHighlightColor(), ""+1);
            }
            @Override
            public void apply(Industry industry) {
                super.apply(industry);
                //industry.getSupply(Vulpoids.MANGONUT_ITEM).getQuantity().modifyFlat(spec.getId(), 1, Misc.ucFirst(spec.getName().toLowerCase()));
                if(industry instanceof BaseIndustry) {
                    BaseIndustry b = (BaseIndustry) industry;
                    b.supply(Vulpoids.MANGONUT_ITEM, 1);
                    Global.getSector().getMemoryWithoutUpdate().set(Vulpoids.KEY_EXPORTED_MANGONUTS, true);
                }
            }
            @Override
            public void unapply(Industry industry) {
                super.unapply(industry);
                industry.getSupply(Vulpoids.MANGONUT_ITEM).getQuantity().unmodify();
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
        
        ItemEffectsRepo.ITEM_EFFECTS.put(Vulpoids.LOBSTER_BIOFORGE_ITEM, new BaseInstallableItemEffect(Vulpoids.LOBSTER_BIOFORGE_ITEM) {
            @Override
            protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                    InstallableItemDescriptionMode mode, String pre, float pad) {
                text.addPara(pre + "Enables Volturnian lobster production.", pad);
            }
            @Override
            public void apply(Industry industry) {
                if(Vulpoids.INDUSTRY_ORGANFARM.equals(industry.getId())) {
                    industry.supply(spec.getId(), Commodities.ORGANS, -100, Misc.ucFirst(spec.getName().toLowerCase()));
                }
                
                if(!industry.getMarket().hasCondition(Conditions.VOLTURNIAN_LOBSTER_PENS)) {
                    PlanetAPI planet = industry.getMarket().getPlanetEntity();
                    if(planet!=null && (Planets.PLANET_TERRAN.equals(planet.getTypeId()) || Planets.PLANET_WATER.equals(planet.getTypeId()))) {
                        if(industry.getMarket().hasCondition(Vulpoids.CONDITION_LOBSTERS_GROWING)) {
                            LobstersGrowing plugin = (LobstersGrowing) industry.getMarket().getCondition(Vulpoids.CONDITION_LOBSTERS_GROWING).getPlugin();
                            if(plugin.isFinished()) {
                                industry.getMarket().addCondition(Conditions.VOLTURNIAN_LOBSTER_PENS);
                                industry.getMarket().removeCondition(Vulpoids.CONDITION_LOBSTERS_GROWING);
                            }
                        } else {
                            industry.getMarket().addCondition(Vulpoids.CONDITION_LOBSTERS_GROWING);
                        }
                    }
                }
            }
            @Override
            public void unapply(Industry industry) {
                industry.supply(spec.getId(), Commodities.ORGANS, 0, Misc.ucFirst(spec.getName().toLowerCase()));
                //industry.getMarket().removeCondition(Vulpoids.CONDITION_LOBSTERS_GROWING);
            }
            @Override
            public String[] getSimpleReqs(Industry industry) {
                return new String [] {ItemEffectsRepo.HABITABLE};
            }
        });
    }
}