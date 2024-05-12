package vulpoids.impl.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.DelayedFleetEncounter;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseHubMission;
import static com.fs.starfarer.api.impl.campaign.missions.hub.BaseHubMission.getRoundNumber;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Map;
import org.lwjgl.util.vector.Vector2f;
import vulpoids.impl.campaign.econ.VulpoidPopulation;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidProductionMission extends HubMissionWithBarEvent implements EconomyTickListener, TooltipMakerAPI.TooltipCreator {

    public static float PROB_COMPLICATIONS = 0.5f;
    public static float PROB_COMPLIMENTS = 0.25f;

    public static float MISSION_DAYS = 120f;  //How long you have to start.
    public static int MISSION_MONTHS = (int) Math.round(MISSION_DAYS / 30f);
    public static int CONTRACT_DAYS = 365;  //The actual length of the contract
    public static int CONTRACT_MONTHS = (int) Math.round(CONTRACT_DAYS * 12f / 365f);
    public static int TIMEOUT_DAYS = 40;

    public static float REWARD_MULT_WHEN_PRODUCING_ALREADY = 0.2f;

    public static enum Stage {
        WAITING,
        PAYING,
        COMPLETED,
        FAILED,
    }

    public static class CheckPlayerProduction implements BaseHubMission.ConditionChecker {

        protected int quantity;

        public CheckPlayerProduction(int quantity) {
            this.quantity = quantity;
        }

        public boolean conditionsMet() {
            return isPlayerProducing(quantity);
        }
    }
    public static class CheckPlayerNotProducing implements BaseHubMission.ConditionChecker {
        protected int quantity;
        protected long lastProductionTimestamp = Global.getSector().getClock().getTimestamp();
        public CheckPlayerNotProducing(int quantity) {this.quantity = quantity;}
        public boolean conditionsMet() {
            if(isPlayerProducing(quantity)) {
                lastProductionTimestamp = Global.getSector().getClock().getTimestamp();
                return false;
            }
            return TIMEOUT_DAYS < Global.getSector().getClock().getElapsedDaysSince(lastProductionTimestamp);
        }
    }

    protected int needed;
    protected int monthlyPayment;
    protected int totalPayment;
    
    public int neededOverride;
    public int monthlyPaymentOverride;
    
    protected int monthsRemaining;
    protected String uid;
    
    protected String vulpoidName;  // For the bar event

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if(genRandom==null) genRandom = Misc.random;
        if(iconName==null) iconName = getSpec().getIconName();
        
        if (barEvent) {
            if(!Global.getSector().getMemoryWithoutUpdate().contains("$vulpoidsAccepted")) return false;
            setGiverRank(Ranks.CITIZEN);
            String post = pickOne(Ranks.POST_TRADER, Ranks.POST_COMMODITIES_AGENT, Ranks.POST_PORTMASTER,
                    Ranks.POST_MERCHANT, Ranks.POST_INVESTOR, Ranks.POST_EXECUTIVE,
                    Ranks.POST_SENIOR_EXECUTIVE);
            setGiverPost(post);
            if (post.equals(Ranks.POST_SENIOR_EXECUTIVE)) {
                setGiverImportance(pickHighImportance());
            } else {
                setGiverImportance(pickImportance());
            }
            setGiverTags(Tags.CONTACT_TRADE);
            findOrCreateGiver(createdAt, false, false);
        }

        PersonAPI person = getPerson();
        if (person == null) return false;
        if (!setPersonMissionRef(person, "$vulpProduction_ref")) return false;
        //if (barEvent) setGiverIsPotentialContactOnSuccess();
        
        MarketAPI market = getPerson().getMarket();
        if (market == null) return false;
        if (market.isPlayerOwned()) return false;
        if (market.getMemoryWithoutUpdate().contains("$vulpProductionActive")) return false;
        if (barEvent) {
            if (market.getFactionId().equals(Factions.LUDDIC_PATH)) return false;
            //if (market.getFactionId().equals(Factions.LUDDIC_CHURCH)) return false;
            if (market.getFactionId().equals(Factions.HEGEMONY)) return false;
        }
        
        int MIN_NEEDED = 3;
        int MAX_NEEDED = Math.min(6, market.getSize());
        if (neededOverride == 0) {
            if(!market.hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) needed = MIN_NEEDED;
            else {
                VulpoidPopulation plugin = (VulpoidPopulation) market.getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin();
                needed = plugin.getPopulation()+1;
                needed = Math.max(needed, MIN_NEEDED);
                if(needed > MAX_NEEDED) return false;
            }
        } else {
            needed = neededOverride;
        }
        
        
        float basePayment = 1000 + getSpec().getBasePrice() * 10;
        basePayment *= REWARD_MULT_WHEN_PRODUCING_ALREADY;
        
        
        if(monthlyPaymentOverride==0) {
            monthlyPayment = getRoundNumber(basePayment * needed);
        } else {
            monthlyPayment = monthlyPaymentOverride;
        }
        totalPayment = (int) Math.round(monthlyPayment * CONTRACT_MONTHS);
        if (barEvent && monthlyPayment <= 0) {
            return false;
        }

        setStartingStage(Stage.WAITING);
        setSuccessStage(Stage.COMPLETED);
        setFailureStage(Stage.FAILED);

        connectWithCustomCondition(Stage.WAITING, Stage.PAYING, new CheckPlayerProduction(needed));
        connectWithCustomCondition(Stage.PAYING, Stage.FAILED, new CheckPlayerNotProducing(needed));
        setTimeLimit(Stage.FAILED, MISSION_DAYS, null, Stage.PAYING);

        monthsRemaining = (int) CONTRACT_MONTHS;
        
        vulpoidName = Global.getSector().getFaction(Vulpoids.FACTION_EXODYNE).createRandomPerson().getNameString();
        
        return true;
    }

    @Override
    public void setCurrentStage(Object next, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.setCurrentStage(next, dialog, memoryMap);

        if (next == Stage.PAYING) {
            addPotentialContacts(dialog);
            if(!getPerson().getMarket().hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) getPerson().getMarket().addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
            getPerson().getMarket().getMemoryWithoutUpdate().set("$vulpProductionQuantity", needed);
        }
        if (next == Stage.FAILED) {
            // Unset this here, since you failed to provide enough to get the desired growth.
            getPerson().getMarket().getMemoryWithoutUpdate().unset("$vulpProductionQuantity");
        }
    }

    protected void updateInteractionDataImpl() {
        set("$vulpProduction_barEvent", isBarEvent());
        set("$vulpProduction_manOrWoman", getPerson().getManOrWoman());
        set("$vulpProduction_vulpoidName", vulpoidName);
        set("$vulpProduction_monthlyPayment", Misc.getWithDGS(monthlyPayment));
        //set("$vulpProduction_underworld", getPerson().hasTag(Tags.CONTACT_UNDERWORLD));
        set("$vulpProduction_totalPayment", Misc.getWithDGS(totalPayment));
        set("$vulpProduction_missionMonths", MISSION_MONTHS);
        set("$vulpProduction_needed", needed);
        //set("$vulpProduction_playerHasColony", !Misc.getPlayerMarkets(false).isEmpty());
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.WAITING) {
            info.addPara("Produce at least %s units of " + getSpec().getLowerCaseName() + " at "
                    + "a colony under your control.", opad, h, "" + needed);
            info.addPara("Once these terms are met, you will receive %s per month for the next "
                    + "cycle, for a total of %s, as long as production is maintained.", opad, h,
                    Misc.getDGSCredits(monthlyPayment),
                    Misc.getDGSCredits(totalPayment));
            if (!playerHasAColony()) {
                info.addPara("You will need to survey a suitable planet and establish a colony to complete "
                        + "this mission.", opad);
            }
        } else if (currentStage == Stage.PAYING) {
            info.addPara("You've met the initial terms of the contract to produce %s units of "
                    + getSpec().getLowerCaseName() + " at "
                    + "a colony under your control.", opad, h, "" + needed);
            info.addPara("As long these terms are met, you will receive %s per month over a cycle for "
                    + "a total payout of %s, assuming there is no interruption in production.", opad, h,
                    Misc.getDGSCredits(monthlyPayment),
                    Misc.getDGSCredits(totalPayment));
            info.addPara("Months remaining: %s", opad, h, "" + monthsRemaining);
            if (isPlayerProducing(needed)) {
                info.addPara("You are currently meeting the terms of the contract.",
                        Misc.getPositiveHighlightColor(), opad);
            } else {
                info.addPara("You are not currently meeting the terms of the contract.",
                        Misc.getNegativeHighlightColor(), opad);
            }
        } else if (currentStage == Stage.COMPLETED) {
            info.addPara("The contract is completed.", opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.WAITING) {
            info.addPara("Produce at least %s units of " + getSpec().getLowerCaseName() + " at "
                    + "a colony", pad, tc, h, "" + needed);
            return true;
        } else if (currentStage == Stage.PAYING) {
            info.addPara("Receiving %s per month", pad, tc, h, Misc.getDGSCredits(monthlyPayment));
            info.addPara("Months remaining: %s", 0f, tc, h, "" + monthsRemaining);
            if (isPlayerProducing(needed)) {
                info.addPara("Terms of contract met", tc, 0f);
            } else {
                info.addPara("Terms of contract not met",
                        Misc.getNegativeHighlightColor(), 0f);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "Vulpoid Production";
    }

    public static boolean playerHasAColony() {
        return !Misc.getPlayerMarkets(true).isEmpty();
    }

    public static boolean isPlayerProducing(int quantity) {
        for (MarketAPI market : Misc.getPlayerMarkets(true)) {
            CommodityOnMarketAPI com = market.getCommodityData(Vulpoids.CARGO_ITEM);
            if (com.getMaxSupply() >= quantity) return true;
        }
        return false;
    }
    
    protected CommoditySpecAPI getSpec() {return Global.getSettings().getCommoditySpec(Vulpoids.CARGO_ITEM);}

    @Override
    protected void notifyEnding() {
        super.notifyEnding();
        getPerson().getMarket().getMemoryWithoutUpdate().unset("$vulpProductionActive");
        //getPerson().getMarket().getMemoryWithoutUpdate().unset("$vulpProductionQuantity");  // Don't unset, so there's no risk of loosing a pop growth on margin.
        Global.getSector().getListenerManager().removeListener(this);
    }

    @Override
    public void acceptImpl(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.acceptImpl(dialog, memoryMap);

        Global.getSector().getListenerManager().addListener(this);
        uid = Misc.genUID();
        connectWithGlobalFlag(Stage.PAYING, Stage.COMPLETED, getCompletionFlag());
        getPerson().getMarket().getMemoryWithoutUpdate().set("$vulpProductionActive", true);
        
        if (rollProbability(PROB_COMPLICATIONS)) {
            DelayedFleetEncounter e = new DelayedFleetEncounter(genRandom, getMissionId());
            //e.setDelay(0f);
            e.setDelay(MISSION_DAYS * 0.5f);
            e.setLocationInnerSector(true, Factions.LUDDIC_PATH);
            //e.setEncounterInHyper();
            e.beginCreate();
            e.triggerCreateFleet(HubMissionWithTriggers.FleetSize.VERY_LARGE, HubMissionWithTriggers.FleetQuality.DEFAULT, Factions.LUDDIC_PATH, FleetTypes.PATROL_LARGE, new Vector2f());
            e.triggerSetAdjustStrengthBasedOnQuality(true, getQuality());
            e.triggerSetStandardAggroNonPirateFlags();
            e.triggerSetStandardAggroInterceptFlags();
            e.triggerSetFleetGenericHailPermanent("VulpProductionPatherHail");
            e.endCreate();
        }
        
        if (rollProbability(PROB_COMPLIMENTS)) {
            DelayedFleetEncounter e = new DelayedFleetEncounter(genRandom, getMissionId());
            //e.setDelay(0f);
            e.setDelay(MISSION_DAYS * 0.5f);
            e.setLocationInnerSector(true, getPerson().getMarket().getFactionId());
            //e.setEncounterInHyper();
            e.beginCreate();
            e.triggerCreateFleet(HubMissionWithTriggers.FleetSize.LARGER, HubMissionWithTriggers.FleetQuality.SMOD_1, getPerson().getMarket().getFactionId(), FleetTypes.TRADE_LINER, new Vector2f());
            //e.triggerSetAdjustStrengthBasedOnQuality(true, getQuality());
            e.triggerFleetAllowLongPursuit();
            e.triggerSetFleetAlwaysPursue();
            e.triggerOrderFleetInterceptPlayer();
            e.triggerSetFleetGenericHailPermanent("VulpProductionComplimentHail");
            e.endCreate();
        }
    }

    public String getCompletionFlag() {
        return "$" + getMissionId() + "_" + uid + "_completed";
    }

    public void reportEconomyTick(int iterIndex) {
        if (currentStage != Stage.PAYING) {
            return;
        }

        int numIter = (int) Global.getSettings().getFloat("economyIterPerMonth");

        MonthlyReport report = SharedData.getData().getCurrentReport();
        MonthlyReport.FDNode colonyNode = report.getNode(MonthlyReport.OUTPOSTS);
        MonthlyReport.FDNode paymentNode = report.getNode(colonyNode, getMissionId() + "_" + uid);
        paymentNode.income += monthlyPayment / numIter;
        paymentNode.name = getBaseName();
        //paymentNode.icon = Global.getSettings().getSpriteName("income_report", "generic_income");
        paymentNode.icon = getSpec().getIconName();
        paymentNode.tooltipCreator = this;
    }

    public void reportEconomyMonthEnd() {
        monthsRemaining--;
        //monthsRemaining = 0;
        if (monthsRemaining <= 0) {
            Global.getSector().getMemoryWithoutUpdate().set(getCompletionFlag(), true);
        }
    }

    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addSpacer(-10f);
        addDescriptionForNonEndStage(tooltip, getTooltipWidth(tooltipParam), 1000f);
    }

    public float getTooltipWidth(Object tooltipParam) {return 450;}

    public boolean isTooltipExpandable(Object tooltipParam) {return false;}

    protected String getMissionTypeNoun() {return "contract";}
}
