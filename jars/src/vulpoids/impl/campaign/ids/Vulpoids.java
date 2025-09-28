package vulpoids.impl.campaign.ids;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.InstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.DEALMAKER_INCOME_PERCENT_BONUS;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
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
    
    
    public static void updateVanillaItemsIfApplicable() {
        String dealmakerParams = Global.getSettings().getSpecialItemSpec(Items.DEALMAKER_HOLOSUITE).getParams();
        if(!dealmakerParams.contains(Vulpoids.INDUSTRY_VULPOIDAGENCY) && Global.getSector().getPlayerFaction().knowsIndustry(Vulpoids.INDUSTRY_VULPOIDAGENCY)) {
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
    
    public static Industry getFarming(MarketAPI market) {
        Industry industry = null;
        industry = market.getIndustry(Industries.FARMING);
        if(industry==null) industry = market.getIndustry(Industries.AQUACULTURE);
        
        // Ashes Of The Domain
        if(industry==null) industry = market.getIndustry("monoculture");
        if(industry==null) industry = market.getIndustry("artifarming");
        if(industry==null) industry = market.getIndustry("subfarming");
        if(industry==null) industry = market.getIndustry("fishery");
        
        return industry;
    }
    
    public static Industry getMining(MarketAPI market) {
        Industry industry = null;
        industry = market.getIndustry(Industries.MINING);
        
        // Ashes Of The Domain
        if(industry==null) industry = market.getIndustry("extractive");
        if(industry==null) industry = market.getIndustry("fracking");
        if(industry==null) industry = market.getIndustry("sublimation");
        if(industry==null) industry = market.getIndustry("benefication");
        
        return industry;
    }
    
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
