package vulpoids.impl.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import java.awt.Color;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
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
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.People;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.academy.GABaseMission;
import com.fs.starfarer.api.impl.campaign.missions.cb.CustomBountyCreator.CustomBountyData;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec.DropData;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager.RemnantFleetInteractionConfigGen;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipRecoverySpecialData;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VulpoidBiofactoryMission extends HubMissionWithSearch implements FleetEventListener {
    
    protected CustomBountyData data;
    protected PersonAPI flagship_captain;
    
    
    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if (isDone() || result != null) return;
        if (this.data.fleet == fleet) {
            String id = getMissionId();
            getPerson().getMemoryWithoutUpdate().set("$" + id + "_failed", true);
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
                String id = getMissionId();
                getPerson().getMemoryWithoutUpdate().set("$" + id + "_failed", true);
                return;
            }
        }

        //CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (!playerInvolved || !battle.isInvolved(fleet) || battle.onPlayerSide(fleet)) {
            return;
        }
        
        
        
        if (fleet.getFlagship() != null) {
            if (fleet.getFlagship().getCaptain() != flagship_captain) {
                fleet.getMemoryWithoutUpdate().set("$geckDestroyed", true);
            }
            return;
        }
        

        String id = getMissionId();
        //Global.getSector().getMemoryWithoutUpdate().set(completedKey, true);
        Global.getSector().getMemoryWithoutUpdate().set("$vulp_beatFactoryFleet", true);
    }

    public static enum Stage {
            ACTIVE,
            BEATFLEET,
            COMPLETED,
    }

    
    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        
        completedKey = "$vulp_gotFactory";
        
        

        setStartingStage(Stage.ACTIVE);
        //addSuccessStages(Stage.COMPLETED);
        setSuccessStage(Stage.COMPLETED);

        setStoryMission();

        //makeImportant(baird.getMarket(), null, Stage.ACTIVE);
        //setStageOnMemoryFlag(Stage.COMPLETED, baird.getMarket(), "$gaIntro_completed");

        setRepFactionChangesNone();
        setRepPersonChangesNone();
        
        //connectWithMemoryFlag(Stage.ACTIVE, Stage.COMPLETED, Global.getSector().getMemoryWithoutUpdate(), completedKey);
        connectWithMemoryFlag(Stage.ACTIVE, Stage.BEATFLEET, Global.getSector().getMemoryWithoutUpdate(), "$vulp_beatFactoryFleet");
        connectWithMemoryFlag(Stage.BEATFLEET, Stage.COMPLETED, Global.getSector().getMemoryWithoutUpdate(), "$vulp_gotFactory");
        
        createFleet();

        return true;
    }

    protected void createFleet() {
        data = new CustomBountyData();
        data.difficulty = 10;

        //mission.setIconName("campaignMissions", "derelict_bounty");

        //mission.requireSystem(this);
        requireSystemTags(ReqMode.NOT_ANY, Tags.THEME_CORE);
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
        FleetQuality quality = FleetQuality.DEFAULT;
        OfficerQuality oQuality = OfficerQuality.AI_GAMMA;
        OfficerNum oNum = OfficerNum.ALL_SHIPS;

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
        triggerCreateFleet(size, quality, Factions.REMNANTS, FleetTypes.PATROL_LARGE, data.system);
        triggerSetFleetOfficers(oNum, oQuality);
        triggerAutoAdjustFleetSize(size, size.next());
        triggerSetRemnantConfigActive();
        triggerSetFleetFaction(Factions.DERELICT);
        triggerFleetSetName("Automated Colonial Fleet");
        triggerFleetAddTags(Tags.NEUTRINO_HIGH);
        triggerFleetAddCommanderSkill(Skills.DERELICT_CONTINGENT, 1);
        //triggerMakeHostileAndAggressive();
        triggerMakeNonHostile();
        triggerMakeFleetIgnoreOtherFleets();
        triggerMakeFleetIgnoredByOtherFleets();
        triggerMakeNoRepImpact();
        //mission.triggerSetFleetMemoryValue("$shownFleetDescAlready", true);
        triggerDoNotShowFleetDesc();
        //triggerFleetForceAutofitOnAllShips();
        //triggerFleetSetAllWeapons();
        //triggerPickLocationAtInSystemJumpPoint(data.system);
        triggerPickLocationAroundEntity(planet, 0f);
        triggerSpawnFleetAtPickedLocation(null, null);
        triggerFleetSetPatrolActionText("awaiting authorization codes");
        //triggerOrderFleetPatrol(data.system, true, Tags.JUMP_POINT, Tags.NEUTRINO, Tags.NEUTRINO_HIGH, Tags.GAS_GIANT);
        triggerOrderFleetPatrol(data.system, false, planet);

        //data.fleet = createFleet(mission, data);
        //if (data.fleet == null) return null;
        triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
        triggerFleetOnlyEngageableWhenVisibleToPlayer();
        endTrigger();

        List<Abortable> before = new ArrayList<Abortable>(getChanges());
        List<CampaignFleetAPI> fleets = runStageTriggersReturnFleets(Stage.ACTIVE);
        //if (fleets.isEmpty()) return null;

        CampaignFleetAPI fleet = fleets.get(0);
        
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);
        FleetMemberAPI member = fleet.getFleetData().addFleetMember("vulp_geck_Terraformer");
        member.setShipName("EBTS Rellrait");
        //member.setId("xivtf_" + random.nextLong());
        flagship_captain = plugin.createPerson(Commodities.ALPHA_CORE, fleet.getFaction().getId(), new Random());
        //person.setPortraitSprite("graphics/portraits/vulpoid.png");
        //person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        flagship_captain.getStats().setSkipRefresh(true);
        flagship_captain.getStats().setSkillLevel(Skills.CARRIER_GROUP, 1);
        flagship_captain.getStats().setSkillLevel(Skills.FIGHTER_UPLINK, 1);
        flagship_captain.getStats().setSkipRefresh(false);
        
        member.setCaptain(flagship_captain);
        ShipVariantAPI v = member.getVariant().clone();
        v.setSource(VariantSource.REFIT);
        v.addTag(Tags.TAG_NO_AUTOFIT);
        v.addTag(Tags.SHIP_LIMITED_TOOLTIP);
        v.addTag(Tags.VARIANT_UNBOARDABLE);
        //v.addTag(Tags.TAG_AUTOMATED_NO_PENALTY);
        v.addPermaMod("supercomputer", true);  //S-mod targeting supercomputer. Makes the boss a monster, but you loose it on recovery.
        member.setVariant(v, false, true);
        fleet.setCommander(flagship_captain);
        
        member = fleet.getFleetData().addFleetMember("odyssey_Balanced");
        member.setShipName("EBTS Artem");
        
        //addAutomated(fleet, "onslaught_xiv_Elite", null, Commodities.ALPHA_CORE, random);
        //addAutomated(fleet, "vulp_geck_terraformer", null, "vulpoids_shiny");
        //addAutomated(fleet, "odyssey_Balanced", null, Commodities.ALPHA_CORE);
        
        fleet.getFleetData().sort();
        
        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
        }
        
        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            v = curr.getVariant().clone();
            v.setSource(VariantSource.REFIT);
            curr.setVariant(v, false, false);
        }
        
        /*for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getVariant().addPermaMod(HullMods.AUTOMATED);
            curr.getVariant().setVariantDisplayName("Automated");
            curr.getVariant().addTag(Tags.TAG_AUTOMATED_NO_PENALTY);
            //curr.getVariant().addTag(Tags.VARIANT_UNRESTORABLE);
            //curr.getVariant().addTag(Tags.TAG_RETAIN_SMODS_ON_RECOVERY);
            curr.getVariant().addTag(Tags.UNRECOVERABLE);
            curr.getVariant().addTag(Tags.VARIANT_UNBOARDABLE);
        }*/
        
        /*DropData d = new DropData();
        d.chances = 1;
        d.addSpecialItem("vulpoid_biofactory", 1);
        fleet.addDropRandom(d);
        
        d = new DropData();
        d.chances = 1;
        d.value = 10000;
        d.addCommodity("vulpoids", 1);
        fleet.addDropValue(d);
        
        d = new DropData();
        d.chances = 1;
        d.addCommodity("vulpoids_shiny", 1);
        fleet.addDropRandom(d);*/
        
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
                new ZigFIDConfig());
        
        Misc.addDefeatTrigger(fleet, "VulpoidBiofactoryFleetDefeated");
    }
    
    public static void addAutomated(CampaignFleetAPI fleet, String variantId, String shipName, String aiCore/*, Random random*/) {
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);

        FleetMemberAPI member = fleet.getFleetData().addFleetMember(variantId);
        //member.setId("xivtf_" + random.nextLong());
        
        if (shipName != null) {
            member.setShipName(shipName);
        }
        if (aiCore != null) {
            PersonAPI person = plugin.createPerson(aiCore, fleet.getFaction().getId(), new Random());
            member.setCaptain(person);
        }
    }
    
    
    public static class ZigFIDConfig implements FIDConfigGen {
        public FIDConfig createConfig() {
            FIDConfig config = new FIDConfig();

            config.showTransponderStatus = false;
            config.showEngageText = false;
            config.alwaysPursue = true;
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
                    ship.shipName = "TTS Xenorphica";
                    DerelictShipData params = new DerelictShipData(ship, false);
                    CustomCampaignEntityAPI entity = (CustomCampaignEntityAPI) BaseThemeGenerator.addSalvageEntity(
                            fleet.getContainingLocation(),
                            Entities.WRECK, Factions.NEUTRAL, params);
                    Misc.makeImportant(entity, "ziggurat");
                    entity.getMemoryWithoutUpdate().set("$vulpFactoryShip", true);

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
                    data.addShip(copy);

                    Misc.setSalvageSpecial(entity, data);

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
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.ACTIVE) {
            if (data.system != null) {
                info.addPara("The target is located in the " + data.system.getNameWithLowercaseType() + ".", opad);
            }
            //creator.addFleetDescription(info, width, height, this, data);
        } else if (currentStage == Stage.BEATFLEET) {
            if (data.system != null) {
                info.addPara("TODO The target is located in the " + data.system.getNameWithLowercaseType() + ".", opad);
            }
            //creator.addFleetDescription(info, width, height, this, data);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.ACTIVE) {
            if (data.system != null) {
                if (data.system != null) {
                    info.addPara("Target is in the " + data.system.getNameWithLowercaseTypeShort() + "", tc, pad);
		}
                return true;
            }
        } else if (currentStage == Stage.BEATFLEET) {
            if (data.system != null) {
                if (data.system != null) {
                    info.addPara("TODO Target is in the " + data.system.getNameWithLowercaseTypeShort() + "", tc, pad);
		}
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
        return "Find the Biofactory";
    }

    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }

	
}





