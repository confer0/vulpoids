package vulpoids.impl.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import java.awt.Color;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.BaseFIDDelegate;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfigGen;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.SpecialItemOffer;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.historian.SpecialItemOfferCreator;
import com.fs.starfarer.api.impl.campaign.missions.cb.CustomBountyCreator.CustomBountyData;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager.RemnantFleetInteractionConfigGen;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipRecoverySpecialData;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;

public class VulpoidBiofactoryMission extends HubMissionWithSearch implements FleetEventListener {
    
    protected CustomBountyData data;
    protected PersonAPI flagship_captain;
    protected boolean gotDronesHint;
    
    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if (isDone() || result != null) return;
        if (this.data.fleet == fleet) {
            //String id = getMissionId();
            //getPerson().getMemoryWithoutUpdate().set("$" + id + "_failed", true);  //TODO
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (isDone() || result != null) return;
        // also credit the player if they're in the same location as the fleet and nearby
        float distToPlayer = Misc.getDistance(fleet, Global.getSector().getPlayerFleet());
        boolean playerInvolved = battle.isPlayerInvolved() || (fleet.isInCurrentLocation() && distToPlayer < 2000f);

        if (battle.isInvolved(fleet) && !playerInvolved) {
            boolean cancelBounty = (fleet.isStationMode() && fleet.getFlagship() == null) ||
                    (!fleet.isStationMode() && fleet.getFlagship() != null);
            if (cancelBounty) {
                //String id = getMissionId();
                //getPerson().getMemoryWithoutUpdate().set("$" + id + "_failed", true);  //TODO
                return;
            }
        }

        //CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (!playerInvolved || !battle.isInvolved(fleet) || battle.onPlayerSide(fleet)) {
            return;
        }
        
        
        fleet.getMemoryWithoutUpdate().set("$wasAttacked", true);
        fleet.getMemoryWithoutUpdate().set("$ignorePlayerCommRequests", true);
        if (fleet.getFlagship() != null) {
            if (fleet.getFlagship().getCaptain() != flagship_captain) {
                fleet.getMemoryWithoutUpdate().set("$geckDestroyed", true);
            }
            return;
        }
        

        //String id = getMissionId();
        //Global.getSector().getMemoryWithoutUpdate().set(completedKey, true);
        Global.getSector().getMemoryWithoutUpdate().set("$vulp_beatFactoryFleet", true);
    }

    public static enum Stage {
            ACTIVE,
            BOARDING,
            INTERROGATION,
            WRAPUP,
            COMPLETED,
            FAILED,
    }

    
    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        
        Global.getSector().getMemoryWithoutUpdate().set("$" + getMissionId() + "_ref", this);
        
        completedKey = Vulpoids.KEY_GOT_FACTORY;

        setStartingStage(Stage.ACTIVE);
        //addSuccessStages(Stage.COMPLETED);
        setSuccessStage(Stage.COMPLETED);
        setFailureStage(Stage.FAILED);

        setStoryMission();

        //makeImportant(baird.getMarket(), null, Stage.ACTIVE);
        //setStageOnMemoryFlag(Stage.COMPLETED, baird.getMarket(), "$gaIntro_completed");

        setRepFactionChangesNone();
        setRepPersonChangesNone();
        
        createFleet();
        
        //connectWithMemoryFlag(Stage.ACTIVE, Stage.COMPLETED, Global.getSector().getMemoryWithoutUpdate(), completedKey);
        connectWithMemoryFlag(Stage.ACTIVE, Stage.BOARDING, Global.getSector().getMemoryWithoutUpdate(), "$vulp_beatFactoryFleet");
        connectWithMemoryFlag(Stage.BOARDING, Stage.INTERROGATION, Global.getSector().getMemoryWithoutUpdate(), "$vulp_tookCaptain");
        connectWithMemoryFlag(Stage.INTERROGATION, Stage.WRAPUP, Global.getSector().getMemoryWithoutUpdate(), "$vulp_didInterrogationForMission");
        connectWithMemoryFlag(Stage.WRAPUP, Stage.COMPLETED, Global.getSector().getMemoryWithoutUpdate(), Vulpoids.KEY_GOT_FACTORY);
        connectWithMemoryFlag(Stage.BOARDING, Stage.FAILED, Global.getSector().getMemoryWithoutUpdate(), "$vulp_didBadEnding");

        return true;
    }

    protected void createFleet() {
        data = new CustomBountyData();
        data.difficulty = 10;

        //mission.setIconName("campaignMissions", "derelict_bounty");

        //mission.requireSystem(this);
        requireSystemTags(ReqMode.NOT_ANY, Tags.THEME_CORE, Tags.THEME_SPECIAL, Tags.HAS_CORONAL_TAP);
        preferSystemTags(ReqMode.NOT_ANY, Tags.THEME_REMNANT);
        requireSystemNotHasPulsar();
        //preferSystemBlackHoleOrNebula();
        preferSystemOnFringeOfSector();
        preferPlanetConditions(ReqMode.ALL, new String[]{"habitable"});
        preferPlanetUnsurveyed();
        preferPlanetWithoutRuins();

        PlanetAPI planet = pickPlanet();
        StarSystemAPI system = planet.getStarSystem();//pickSystem();
        data.system = system;

        FleetSize size = FleetSize.MEDIUM;

        if (data.difficulty <= 5) {
                size = FleetSize.MEDIUM;
        } else if (data.difficulty == 6) {
                size = FleetSize.LARGE;
        } else if (data.difficulty == 7) {
                size = FleetSize.LARGE;
        } else if (data.difficulty == 8) {
                size = FleetSize.VERY_LARGE;
        } else if (data.difficulty == 9) {
                size = FleetSize.HUGE;
        } else if (data.difficulty >= 10) {
                size = FleetSize.MAXIMUM;
        }

        beginStageTrigger(Stage.ACTIVE);
        triggerCreateFleet(size, FleetQuality.DEFAULT, Factions.REMNANTS, FleetTypes.PATROL_LARGE, data.system);
        //triggerSetFleetOfficers(oNum, oQuality);
        triggerAutoAdjustFleetSize(size, size.next());
        //triggerSetFleetFaction(Factions.REMNANTS);
        triggerSetFleetFaction(Vulpoids.FACTION_EXODYNE);
        //triggerFleetSetNoFactionInName();
        triggerFleetSetName("Research Flotilla");
        triggerFleetAddTags(Tags.NEUTRINO_HIGH);
        triggerMakeNonHostile();
        triggerMakeFleetIgnoreOtherFleets();
        triggerMakeFleetIgnoredByOtherFleets();
        triggerMakeNoRepImpact();
        //triggerDoNotShowFleetDesc();
        triggerPickLocationAroundEntity(planet, 0f);
        triggerSpawnFleetAtPickedLocation(null, null);
        triggerFleetSetPatrolActionText("waiting for contact");
        //triggerOrderFleetPatrol(data.system, true, Tags.JUMP_POINT, Tags.NEUTRINO, Tags.NEUTRINO_HIGH, Tags.GAS_GIANT);
        triggerOrderFleetPatrol(data.system, false, planet);
        triggerFleetOnlyEngageableWhenVisibleToPlayer();
        endTrigger();

        List<Abortable> before = new ArrayList(getChanges());
        List<CampaignFleetAPI> fleets = runStageTriggersReturnFleets(Stage.ACTIVE);
        //if (fleets.isEmpty()) return null;

        CampaignFleetAPI fleet = fleets.get(0);
        
        FleetMemberAPI member = fleet.getFleetData().addFleetMember("vulp_geck_Terraformer");
        member.setShipName("XBS Rellrait");
        
        flagship_captain = Global.getSector().getImportantPeople().getPerson(Vulpoids.PERSON_LAISA);
        member.setCaptain(flagship_captain);
        ShipVariantAPI v = member.getVariant().clone();
        v.setSource(VariantSource.REFIT);
        v.addTag(Tags.TAG_NO_AUTOFIT);
        v.addTag(Tags.SHIP_LIMITED_TOOLTIP);
        v.addTag(Tags.VARIANT_UNBOARDABLE);
        v.getHints().add(ShipHullSpecAPI.ShipTypeHints.CIVILIAN);  // I believe this makes it deploy last?
        //v.addTag(Tags.TAG_AUTOMATED_NO_PENALTY);
        v.addPermaMod("supercomputer", true);  //S-mod targeting supercomputer. Makes the boss a monster, but you loose it on recovery.
        v.removeMod(HullMods.ADVANCED_TARGETING_CORE);  // Removing this so it doesn't stack, it's effectively the replacement.
        member.setVariant(v, false, true);
        fleet.setCommander(flagship_captain);
        
        
        fleet.getFleetData().sort();
        
        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
        }
        
        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            v = curr.getVariant().clone();
            v.setSource(VariantSource.REFIT);
            curr.setVariant(v, false, false);
        }
        
        
        getChanges().add(new EntityAdded(fleet)); // so it's removed when the mission is abort()ed

        for (Abortable curr : getChanges()) {
            if (!before.contains(curr)) {
                data.abortWhenOtherVersionAccepted.add(curr);
            }
        }
        data.fleet = fleet;

        String id = getMissionId();
        data.fleet.addEventListener(this);
        makeImportant(data.fleet, "$" + id + "_target", Stage.ACTIVE);
        if (!data.fleet.getFaction().isNeutralFaction()) {
            addTag(data.fleet.getFaction().getId());
        }
        
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_INTERACTION_DIALOG_CONFIG_OVERRIDE_GEN,
                new VulpFIDConfig());
        
        Misc.addDefeatTrigger(fleet, "VulpoidBiofactoryFleetDefeated");
    }
    
    
    public static class VulpFIDConfig implements FIDConfigGen {
        public FIDConfig createConfig() {
            FIDConfig config = new FIDConfig();

            config.showTransponderStatus = false;
            config.showEngageText = false;
            config.alwaysPursue = false;
            config.dismissOnLeave = false;
            //config.lootCredits = false;
            config.withSalvage = true;
            //config.showVictoryText = false;
            config.printXPToDialog = true;

            config.noSalvageLeaveOptionText = "Continue";
//			config.postLootLeaveOptionText = "Continue";
//			config.postLootLeaveHasShortcut = false;

            config.delegate = new BaseFIDDelegate() {
                public void postPlayerSalvageGeneration(InteractionDialogAPI dialog, FleetEncounterContext context, CargoAPI salvage) {
                    new RemnantFleetInteractionConfigGen().createConfig().delegate.
                            postPlayerSalvageGeneration(dialog, context, salvage);
                }
                public void notifyLeave(InteractionDialogAPI dialog) {

                    SectorEntityToken other = dialog.getInteractionTarget();
                    if (!(other instanceof CampaignFleetAPI)) {
                        dialog.dismiss();
                        return;
                    }
                    CampaignFleetAPI fleet = (CampaignFleetAPI) other;

                    if (!fleet.isEmpty()) {
                        dialog.dismiss();
                        return;
                    }

                    //Global.getSector().getMemoryWithoutUpdate().set(DEFEATED_ZIGGURAT_KEY, true);
                    
                    PerShipData ship = new PerShipData("vulp_geck_Hull", ShipCondition.WRECKED, 0f);
                    ship.shipName = "XBS Rellrait";
                    DerelictShipData params = new DerelictShipData(ship, false);
                    CustomCampaignEntityAPI entity = (CustomCampaignEntityAPI) BaseThemeGenerator.addSalvageEntity(
                            fleet.getContainingLocation(),
                            Entities.WRECK, Factions.NEUTRAL, params);
                    //Misc.makeImportant(entity, "vulpFactoryShip");
                    entity.getMemoryWithoutUpdate().set("$vulpFactoryShip", true);
                    Global.getSector().getMemoryWithoutUpdate().set("$vulpFactoryShip", entity);
                    if(Global.getSector().getImportantPeople().getPerson(Vulpoids.PERSON_LAISA).getMemoryWithoutUpdate().contains("$talkedOnComms")) entity.getMemoryWithoutUpdate().set("$talkedOnComms", true);
                    
                    entity.getLocation().x = fleet.getLocation().x + (50f - (float) Math.random() * 100f);
                    entity.getLocation().y = fleet.getLocation().y + (50f - (float) Math.random() * 100f);

                    ShipRecoverySpecialData data = new ShipRecoverySpecialData(null);
                    data.notNowOptionExits = true;
                    data.noDescriptionText = true;
                    DerelictShipEntityPlugin dsep = (DerelictShipEntityPlugin) entity.getCustomPlugin();
                    PerShipData copy = (PerShipData) dsep.getData().ship.clone();
                    copy.variant = Global.getSettings().getVariant(copy.variantId).clone();
                    copy.variantId = null;
                    copy.variant.addTag(Tags.SHIP_CAN_NOT_SCUTTLE);
                    copy.variant.addTag(Tags.SHIP_UNIQUE_SIGNATURE);
                    copy.variant.clearHullMods();
                    data.addShip(copy);

                    Misc.setSalvageSpecial(entity, data);
                    
                    ((VulpoidBiofactoryMission)Global.getSector().getMemoryWithoutUpdate().get("$vulpFactory_ref")).makeImportant(entity, "$vulpFactoryShip", Stage.BOARDING);
                    ((VulpoidBiofactoryMission)Global.getSector().getMemoryWithoutUpdate().get("$vulpFactory_ref")).makeImportant(entity, "$vulpFactoryShip", Stage.INTERROGATION);
                    ((VulpoidBiofactoryMission)Global.getSector().getMemoryWithoutUpdate().get("$vulpFactory_ref")).makeImportant(entity, "$vulpFactoryShip", Stage.WRAPUP);

                    dialog.setInteractionTarget(entity);
                    RuleBasedInteractionDialogPluginImpl plugin = new RuleBasedInteractionDialogPluginImpl("VulpoidBiofactoryFleetDefeated");
                    dialog.setPlugin(plugin);
                    plugin.init(dialog);
                }

                public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                    bcc.aiRetreatAllowed = false;
                    bcc.objectivesAllowed = false;
                    bcc.fightToTheLast = true;
                    bcc.enemyDeployAll = true;
                }
            };
            return config;
        }
    }
    

    protected void updateInteractionDataImpl() {

    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        if (currentStage == Stage.ACTIVE) {
            if (data.system != null) {
                info.addPara("You've been given the location of a ship from the long-gone Exodyne "+
                        "Biotech corporation. You were told it contains valuable technology.", opad);
                info.addPara("The ship was reported to be in the " + data.system.getNameWithLowercaseType() + ".", opad);
                if(gotDronesHint) info.addPara("It is defended by a number AI droneships.", opad);
                if(data.fleet.getMemoryWithoutUpdate().getBoolean("$talkedOnComms")) info.addPara("Your attempts to communicate were rebuffed due to your lack of credentials. However the ship does appear to have a human captain, in contrast to the original brief.", opad);
            }
            //creator.addFleetDescription(info, width, height, this, data);
        } else if (currentStage == Stage.BOARDING) {
            if (data.system != null) {
                info.addPara("The ship and its escort are defeated. A boarding action will be "+
                        "required to locate any technology aboard that survived the engagement.", opad);
                info.addPara("The derelict is located in the " + data.system.getNameWithLowercaseType() + ".", opad);
            }
            //creator.addFleetDescription(info, width, height, this, data);
        } else if (currentStage == Stage.INTERROGATION) {
            info.addPara("You've successfully secured the vessel, but are unable to salvage it due "+
                    "to an anti-tampering system. Interrogate the captain to obtain the override codes.", opad);
            info.addPara("Use the 'Interrogate the Captain' button on your ability bar.", opad);
        } else if (currentStage == Stage.WRAPUP) {
            if (data.system != null) {
                info.addPara("You've obtained the override codes from Laisa, and can now safely retrieve "+
                        "the Vulpoid Bioforge from the wreck.", opad);
                info.addPara("The derelict is located in the " + data.system.getNameWithLowercaseType() + ".", opad);
            }
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        if (currentStage == Stage.ACTIVE) {
            if (data.system != null) {
                info.addPara("Target fleet is in the " + data.system.getNameWithLowercaseTypeShort() + ".", tc, pad);
                return true;
            }
        } else if (currentStage == Stage.BOARDING) {
            if (data.system != null) {
                info.addPara("Board the derelict vessel in the " + data.system.getNameWithLowercaseTypeShort() + ".", tc, pad);
                return true;
            }
        } else if (currentStage == Stage.INTERROGATION) {
            info.addPara("Interrogate the Exodyne captain.", tc, pad);
            return true;
        } else if (currentStage == Stage.WRAPUP) {
            if (data.system != null) {
                info.addPara("Reboard the derelict vessel in the " + data.system.getNameWithLowercaseTypeShort() + ".", tc, pad);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return super.getMapLocation(map);
    }

    @Override
    public String getBaseName() {
        return "The Pet Project";
    }

    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }
    
    
    @Override
    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = params.get(0).getString(memoryMap);
        switch (action) {
            case "giveDronesHint":
                gotDronesHint = true;
                return true;
            case "applyPunishment":
                Random random = new Random();
                float crMult = 1;
                for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getMembersWithFightersCopy()) {
                    float crLost = Math.min(member.getRepairTracker().getBaseCR(), member.getDeployCost() * 0.5f);
                    crLost += 0.01f * (float)random.nextInt(10);
                    crLost *= crMult;
                    if (crLost > 0) {
                        member.getRepairTracker().applyCREvent(-crLost, "Citadel explosion");
                        AddRemoveCommodity.addCRLossText(member, dialog.getTextPanel(), crLost);
                    }
                }   int n_crew_lost = 250;
                if(Global.getSector().getPlayerFleet().getCargo().getCrew() < 750) n_crew_lost = 1+(int)((1f/3f) * Global.getSector().getPlayerFleet().getCargo().getCrew());
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, n_crew_lost, dialog.getTextPanel());
                Global.getSector().getPlayerFleet().getCargo().removeCrew(n_crew_lost);
                Global.getSoundPlayer().playSound("gate_explosion", 1, 1, Global.getSoundPlayer().getListenerPos(), Misc.ZERO);
                CustomCampaignEntityAPI derelict_ship = (CustomCampaignEntityAPI)Global.getSector().getMemoryWithoutUpdate().get("$vulpFactoryShip");
                derelict_ship.setExpired(true);
                return true;
            case "generateEridaniBonus":
                SectorEntityToken entity = SpecialItemOfferCreator.createEntity(new Random());
                SpecialItemOffer offer = new SpecialItemOffer(entity, 2, Vulpoids.MANGONUT_TREE_ITEM);
                offer.init(dialog);
                return true;
            case "introduceToSector":
                new VulpoidAcceptanceEventIntel(null, true);
                
                Global.getSector().getPlayerFaction().addKnownIndustry(Vulpoids.INDUSTRY_VULPOIDAGENCY);
                String dealmakerParams = Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).getParams();
                dealmakerParams += ", "+Vulpoids.INDUSTRY_VULPOIDAGENCY;
                Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).setParams(dealmakerParams);
                
                return true;
        }
        return super.callEvent(ruleId, dialog, params, memoryMap);
    }
}





