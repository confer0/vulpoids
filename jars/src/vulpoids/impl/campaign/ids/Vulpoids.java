package vulpoids.impl.campaign.ids;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.InstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.DEALMAKER_INCOME_PERCENT_BONUS;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import vulpoids.campaign.impl.items.VulpoidPlugin;
import vulpoids.impl.campaign.econ.impl.VulpoidAgency;

public class Vulpoids {
    public static final String MOD_ID = "vulpoids";
    
    public static final String CARGO_ITEM = "vulpoids";
    public static final String MANGONUT_ITEM = "mangonuts";
    
    public static final String BIOFORGE_ITEM = "vulpoid_biofactory";
    public static final String CORRUPT_BIOFORGE_ITEM = "vulpoid_biofactory_broken";
    public static final String AIR_FILTER_ITEM = "air_filter_core";
    public static final String MIDAS_NANOFORGE_ITEM = "midas_nanoforge";
    public static final String MANGONUT_TREE_ITEM = "mangonut_tree";
    public static final String LOBSTER_BIOFORGE_ITEM = "lobster_bioforge";
    
    public static final String INDUSTRY_ORGANFARM = "organfarms";
    public static final String INDUSTRY_BIOFACILITY = "biofacility";
    public static final String INDUSTRY_VULPOIDAGENCY = "vulpoidagency";
    
    public static final String SPECIAL_ITEM_DEFAULT = "special_vulpoid";
    public static final String SPECIAL_ITEM_EMBARKED = "special_vulpoid_embarked";
    public static final String SPECIAL_ITEM_OFFICER = "special_vulpoid_officer";
    public static final String SPECIAL_ITEM_ADMIN = "special_vulpoid_admin";
    public static final String SPECIAL_ITEM_CODEX = "special_vulpoid_codex";
    
    public static final String ABILITY_CHAT = "vulpoid_chat";
    
    public static final String PERSON_LAISA = "laisa";
    public static final String PERSON_DUMMY_TERRAN = "vulp_dummy_terran";
    public static final String PERSON_DUMMY_DESERT = "vulp_dummy_desert";
    public static final String PERSON_DUMMY_ARCTIC = "vulp_dummy_arctic";
    
    public static final String FACTION_EXODYNE = "exodyne";
    
    public static final String KEY_IS_VULPOID = "$isVulpoid";
    //public static final String KEY_DEFAULT_PORTRAIT = "$vulp_defaultPortrait";
    //public static final String KEY_OFFICER_PORTRAIT = "$vulp_officerPortrait";
    public static final String KEY_DEFAULT_EXPRESSION = "$vulp_defaultExpression";
    public static final String KEY_DEFAULT_POST = "$vulp_defaultPost";
    //public static final String KEY_CARGO_ICON = "$vulp_cargoIcon";
    public static final String KEY_PROFECTO_ASSIGNMENT = "$vulp_profectoAssignment";
    
    public static final String KEY_MARKET_VULPOID_ADMIN = "$vulp_marketVulpoidAdmin";
    public static final String KEY_MARKET_VULPOID_ADMIN_TIMESTAMP = "$vulp_marketVulpoidAdminTimestamp";
    public static final String KEY_ADMIN_XP_DAYS = "$vulp_adminXpDays";
    
    public static final String KEY_RESEARCH_PROJECT = "$vulp_researchProject";
    public static final String KEY_RESEARCH_COMPLETION_DAY = "$vulp_researchCompletionDay";
    
    public static final String KEY_VULPOID_POP_AMOUNT = "$vulpoidPopulation";
    public static final String KEY_WORKFORCE_CAP = "$workforce_cap";
    public static final String KEY_WORKFORCES = "$workforces";
    public static final String KEY_VULPS_FOR_NEXT_POP = "$vulpsForNextPop";
    
    public static final String KEY_GOT_FACTORY = "$vulp_gotFactory";
    public static final String KEY_EXPORTED_VULPOIDS = "$exportedVulpoids";
    public static final String KEY_EXPORTED_MANGONUTS = "$exportedMangonuts";
    
    public static final String KEY_SELECTED_VULPOID = "$selectedVulpoid";
    public static final String KEY_SELECTED_VULPOID_NAME = "$selectedVulpoidName";
    
    public static final String CONDITION_VULPOID_DEMAND = "vulpoid_demand";
    public static final String CONDITION_VULPOID_POPULATION = "vulpoid_population";
    public static final String CONDITION_WORKFORCE_TAG = "vulpoid_workforce";
    public static final String CONDITION_VULPOID_BLOCKADE = "vulpoid_blockade";
    public static final String CONDITION_FILTERED_AIR = "filtered_air";
    public static final String CONDITION_LOBSTERS_GROWING = "lobsters_growing";
    
    public static final String SKILL_OFFICER = "vulpoid_officer";
    public static final String SKILL_ADMIN = "vulpoid_brain";
    public static final String SKILL_LAISA_OFFICER = "laisa_officer";
    public static final String SKILL_LAISA_ADMIN = "laisa_admin";
    
    public static final String RANK_SERVANT = "vulp_servant";
    public static final String RANK_PROFECTO = "vulp_profecto";
    
    public static final ArrayList<String> LOBSTER_SUPPORTING_WORLDS = new ArrayList<>() {{
        add(Planets.PLANET_TERRAN);
        add(Planets.PLANET_WATER);
        //add("jungle");  // Too hot
        //add(Planets.TUNDRA);  // Too cold
        //add(Planets.PLANET_TERRAN_ECCENTRIC);  // Not enough water
        
        // Unknown Skies
        add("US_continent");
        add("US_terran");
        add("US_water");
        add("US_waterB");
        add("US_waterAtoll");
        add("US_waterIsle");
        //add("US_waterHycean");  // Not habitable enough
    }};
    
    
    public static void updateVanillaItemsIfApplicable() {
        updateDealmaker();
    }
    private static void updateDealmaker() {
        if(CodexDataV2.hasUnlockedEntry(CodexDataV2.getIndustryEntryId(Vulpoids.INDUSTRY_VULPOIDAGENCY))) {
            String dealmakerParams = Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).getParams();
            
            CodexDataV2.makeRelated(CodexDataV2.getIndustryEntryId(Vulpoids.INDUSTRY_VULPOIDAGENCY), CodexDataV2.getItemEntryId(Items.DEALMAKER_HOLOSUITE));
            
            dealmakerParams += ", "+Vulpoids.INDUSTRY_VULPOIDAGENCY;
            Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).setParams(dealmakerParams);
            final InstallableItemEffect oldEffect = ItemEffectsRepo.ITEM_EFFECTS.get(Items.DEALMAKER_HOLOSUITE);
            ItemEffectsRepo.ITEM_EFFECTS.put(Items.DEALMAKER_HOLOSUITE, new BaseInstallableItemEffect(Items.DEALMAKER_HOLOSUITE) {
                @Override
                public void apply(Industry industry) {oldEffect.apply(industry);}
                @Override
                public void unapply(Industry industry) {oldEffect.unapply(industry);}
                @Override
                protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data, InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
                    oldEffect.addItemDescription(industry, text, data, mode);
                    if (text.getPrev() instanceof LabelAPI label && (
                            ("Colony's income increased by "+(int)DEALMAKER_INCOME_PERCENT_BONUS+"%.").equals(label.getText()) ||
                            (Misc.ucFirst(spec.getName().toLowerCase())+". Colony's income increased by "+(int)DEALMAKER_INCOME_PERCENT_BONUS+"%.").equals(label.getText())
                            )) {
                        label.setText("Colony's income increased by "+(int)DEALMAKER_INCOME_PERCENT_BONUS+"% in Commerce, or "+(int)VulpoidAgency.IMPROVE_BONUS+"% in a VDA.");
                        label.setHighlight((int)DEALMAKER_INCOME_PERCENT_BONUS+"%", (int)VulpoidAgency.IMPROVE_BONUS+"%");
                    } else {
                        text.addPara("In a Vulpoid Distribution Agency, income increased by %s", pad, Misc.getHighlightColor(), (int)VulpoidAgency.IMPROVE_BONUS+"%");
                    }
                }
            });
        }
    }
    
    public static boolean anyVulpoidWantsToTalk() {
        // Initial condition, calling to interrogate Laisa.
        if (!Global.getSector().getMemoryWithoutUpdate().contains("$vulp_didInterrogation")) return true;
        
        for (CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
            if (stack.getPlugin() instanceof VulpoidPlugin plugin) {
                if (vulpoidWantsToTalk(plugin)) return true;
            }
        }
        return false;
    }
    public static boolean vulpoidWantsToTalk(VulpoidPlugin plugin) {
        // Finished research project
        MemoryAPI memory = plugin.getPerson().getMemoryWithoutUpdate();
        if(memory.contains(Vulpoids.KEY_RESEARCH_PROJECT) && memory.contains(Vulpoids.KEY_RESEARCH_COMPLETION_DAY)) {
            // Originally used $global.daysSinceStart, but that's not actually set while in the overworld!
            // Turns out, CoreCampaignPluginImpl actually just sets it from this fella.
            // Don't know why we can't just store the timestamp in the clock, but as long as it works.
            if(PirateBaseManager.getInstance().getUnadjustedDaysSinceStart() >= memory.getFloat(Vulpoids.KEY_RESEARCH_COMPLETION_DAY)) return true;
        }
        return false;
    }
    
    public static Industry getFarming(MarketAPI market, boolean allowAquaculture) {
        for (String id : farmingIndustryIds) {
            if (market.hasIndustry(id)) return market.getIndustry(id);
        }
        if (allowAquaculture) {
            for (String id : aquacultureIndustryIds) {
                if (market.hasIndustry(id)) return market.getIndustry(id);
            }
        }
        return null;
    }
    public static ArrayList<String> aquacultureIndustryIds = new ArrayList<>() {{
        add(Industries.AQUACULTURE);
        
        // Ashes Of The Domain
        add("fishery");
        
        // Compatibility Failover
        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (spec.hasTag("aquaculture")) {
                if (!contains(spec.getId())) add(spec.getId());
            }
        }
    }};
    public static ArrayList<String> farmingIndustryIds = new ArrayList<>() {{
        add(Industries.FARMING);
        
        // Ashes Of The Domain
        add("monoculture");
        add("artifarming");
        add("subfarming");
        
        // Compatibility Failover
        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (spec.hasTag("farming")) {
                if (!contains(spec.getId())) add(spec.getId());
            }
        }
        // Apparently the vanilla aquaculture is tagged as pure farming lol.
        // Well, that's why we test.
        for (String aquaId : aquacultureIndustryIds) {
            remove(aquaId);
        }
    }};
    
    public static Industry getMining(MarketAPI market) {
        for (String id : miningIndustryIds) {
            if (market.hasIndustry(id)) return market.getIndustry(id);
        }
        return null;
    }
    public static ArrayList<String> miningIndustryIds = new ArrayList<>() {{
        add(Industries.MINING);
        
        // Ashes Of The Domain
        add("extractive");
        add("fracking");
        //add("sublimation");
        //add("benefication");
        add("mining_megaplex");
        
        // Compatibility Failover
        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (spec.hasTag("mining")) {
                if (!contains(spec.getId())) add(spec.getId());
            }
        }
    }};
    
    public static Industry getLightIndustry(MarketAPI market) {
        for (String id : lightIndustryIds) {
            if (market.hasIndustry(id)) return market.getIndustry(id);
        }
        return null;
    }
    public static ArrayList<String> lightIndustryIds = new ArrayList<>() {{
        add(Industries.LIGHTINDUSTRY);
        
        // Ashes Of The Domain
        add("lightproduction");
        add("hightech");
        add("druglight");
        add("consumerindustry");
        
        // Compatibility Failover
        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (spec.hasTag("lightindustry")) {
                if (!contains(spec.getId())) add(spec.getId());
            }
        }
    }};
    
    public static Industry getHeavyIndustry(MarketAPI market) {
        for (String id : heavyIndustryIds) {
            if (market.hasIndustry(id)) return market.getIndustry(id);
        }
        return null;
    }
    public static ArrayList<String> heavyIndustryIds = new ArrayList<>() {{
        add(Industries.HEAVYINDUSTRY);
        add(Industries.ORBITALWORKS);
        
        // Ashes Of The Domain
        add("supplyheavy");
        //add("weaponheavy");
        add("triheavy");
        add("hegeheavy");
        add("stella_manufactorium");
        
        // Compatibility Failover
        for (IndustrySpecAPI spec : Global.getSettings().getAllIndustrySpecs()) {
            if (spec.hasTag("heavyindustry")) {
                if (!contains(spec.getId())) add(spec.getId());
            }
        }
    }};
    
    public static int getVulpoidPeakProductionAmount() {
        int amount = 0;
        for (MarketAPI market : Misc.getPlayerMarkets(false)) {
            int prod = market.getCommodityData(Vulpoids.CARGO_ITEM).getMaxSupply();
            if (prod<=0) continue;
            amount = Math.max(amount, prod);
        }
        return amount;
    }
}
