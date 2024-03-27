package vulpoids.impl.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import java.awt.Color;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.People;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.academy.GABaseMission;
import com.fs.starfarer.api.impl.campaign.missions.cb.CustomBountyCreator.CustomBountyData;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec.DropData;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VulpoidBiofactoryMission extends HubMissionWithSearch implements FleetEventListener {
    
    protected CustomBountyData data;
    
    //protected String completedKey = "$vulp_gotFactory";
    
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

        if (fleet.isStationMode()) {
            if (fleet.getFlagship() != null) return;
        } else {
            if (fleet.getFlagship() != null) return;
        }

        String id = getMissionId();
        Global.getSector().getMemoryWithoutUpdate().set(completedKey, true);
    }

    public static enum Stage {
            ACTIVE,
            COMPLETED,
    }

    protected PersonAPI baird;

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        
        completedKey = "$vulp_gotFactory";
        
        baird = getImportantPerson(People.BAIRD);
        if (baird == null) return false;

        setStartingStage(Stage.ACTIVE);
        //addSuccessStages(Stage.COMPLETED);
        setSuccessStage(Stage.COMPLETED);

        setStoryMission();

        //makeImportant(baird.getMarket(), null, Stage.ACTIVE);
        //setStageOnMemoryFlag(Stage.COMPLETED, baird.getMarket(), "$gaIntro_completed");

        setRepFactionChangesNone();
        setRepPersonChangesNone();
        
        connectWithMemoryFlag(Stage.ACTIVE, Stage.COMPLETED, Global.getSector().getMemoryWithoutUpdate(), completedKey);
        
        createFleet();

        return true;
    }

    protected void createFleet() {
        data = new CustomBountyData();
        data.difficulty = 1;

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
        triggerCreateFleet(size, quality, Factions.HEGEMONY, FleetTypes.PATROL_MEDIUM, data.system);
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
        triggerFleetForceAutofitOnAllShips();
        triggerFleetSetAllWeapons();
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
        
        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getVariant().addPermaMod(HullMods.AUTOMATED);
            curr.getVariant().setVariantDisplayName("Automated");
            curr.getVariant().addTag(Tags.TAG_AUTOMATED_NO_PENALTY);
            //curr.getVariant().addTag(Tags.VARIANT_UNRESTORABLE);
            //curr.getVariant().addTag(Tags.TAG_RETAIN_SMODS_ON_RECOVERY);
            curr.getVariant().addTag(Tags.UNRECOVERABLE);
            curr.getVariant().addTag(Tags.VARIANT_UNBOARDABLE);
        }
        
        //fleet.getCargo().addSpecial(new SpecialItemData("vulpoid_biofactory", null), 1);
        //fleet.getCargo().addCommodity("vulpoids", 10);
        DropData d = new DropData();
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
        fleet.addDropRandom(d);
        
        //addAutomated(fleet, "onslaught_xiv_Elite", null, Commodities.ALPHA_CORE, random);
        
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
    }
    
    public static void addAutomated(CampaignFleetAPI fleet, String variantId, String shipName, String aiCore, Random random) {
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);

        FleetMemberAPI member = fleet.getFleetData().addFleetMember(variantId);
        member.setId("xivtf_" + random.nextLong());

        //System.out.println("ID for " + variantId + ": " + member.getId());

        //member.setId("xivtf_" + random.nextLong());
        if (shipName != null) {
            member.setShipName(shipName);
        }
        if (aiCore != null) {
            PersonAPI person = plugin.createPerson(aiCore, fleet.getFaction().getId(), random);
            member.setCaptain(person);
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





