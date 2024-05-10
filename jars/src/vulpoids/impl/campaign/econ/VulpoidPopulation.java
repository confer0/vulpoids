package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.econ.impl.VulpoidAgency;
import vulpoids.impl.campaign.econ.workforces.BaseWorkforce;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.misc.VulpPopGrownIntel;

public class VulpoidPopulation extends BaseMarketConditionPlugin implements MarketImmigrationModifier {
    
    private float population = 0;
    final float MIN_POPULATION = 1;
    final float MAX_POPULATION = 10; // Just in case.
    //private int last_reported_production = 0;
    
    private MutableStat workforceCap;
    
    final int MIN_POPULATION_FOR_WORKFORCE = 3;
    //final float POPULATION_WORKFORCE_MULT = 0.5f; // 3>0, 4>1, 5>1, 6>2, 7>3.
    final float POPULATION_WORKFORCE_MULT = 1; // 3>1, 4>2, 5>3, 6>4, 7>4.
    final int MAX_NATIVE_WORKFORCE_CAP = 4;
    
    final int MIN_AVAILABILITY = -6;  // 1 Vulpoid per 1M Humans
    final int MAX_AVAILABILITY = 2;  // 100 Vulpoids per 1 Human
    
    final int MIN_AVAILABILITY_FOR_STABILITY = -3;
    final float AVAILABILITY_STABILITY_MULT = 1;
    final float AVAILABILITY_STABILITY_MAX = 4;
    
    final int MIN_AVAILABILITY_FOR_GROWTH = -3;
    final float AVAILABILITY_GROWTH_MULT = 5;
    final float AVAILABILITY_GROWTH_MAX = 20;
    
    @Override
    public void init(MarketAPI market, MarketConditionAPI condition) {
        super.init(market, condition);
        advance(0);
    }
    
    @Override
    public void advance(float amount) {
        if(workforceCap==null) workforceCap = new MutableStat(0f);
        if(!market.getMemoryWithoutUpdate().contains(MemFlags.RECENTLY_BOMBARDED)) {
            float days = Misc.getDays(amount);
            int popCap = getPopCap();
            if (population < popCap - 2) {
                // Checking if pop is zero so we don't ping when installing the biofactory.
                if (population != 0) Global.getSector().getIntelManager().addIntel(new VulpPopGrownIntel(market, popCap - 2));
                population = popCap - 2;
            }
            double population_growth = days / 30f;  // Takes ~1 month to increase to -1.
            if(population >= popCap - 1) population_growth /= 10; // Takes ~1 year to increase to +0 from -1.
            if(population >= popCap) population_growth = 0;  // I can't believe I forgot this lol.
            if ((int)population < (int)(population+population_growth)) Global.getSector().getIntelManager().addIntel(new VulpPopGrownIntel(market, (int)population+1));
            population += population_growth;
            population = Math.min(population, MAX_POPULATION);
            population = Math.max(population, MIN_POPULATION);
        } else {
            population = Math.min(population, getPopCap());
        }
        int native_workforce_cap = (int)population - MIN_POPULATION_FOR_WORKFORCE + 1;
        native_workforce_cap = (int)(native_workforce_cap * POPULATION_WORKFORCE_MULT);
        native_workforce_cap = Math.max(native_workforce_cap, 0);
        native_workforce_cap = Math.min(native_workforce_cap, MAX_NATIVE_WORKFORCE_CAP);
        workforceCap.modifyFlat(condition.getId(), native_workforce_cap);
        market.getMemoryWithoutUpdate().set(Vulpoids.KEY_WORKFORCE_CAP, workforceCap.getModifiedInt());
        market.getMemoryWithoutUpdate().set(Vulpoids.KEY_VULPOID_POP_AMOUNT, population);
        market.getMemoryWithoutUpdate().set(Vulpoids.KEY_VULPS_FOR_NEXT_POP, Misc.getWithDGS((int)Math.pow(10, (int)(population+1))));
    }
    private int getPopCap() {
        int popCap = (int)MIN_POPULATION;
        Industry industry = market.getIndustry(Vulpoids.INDUSTRY_ORGANFARM);
        if (industry ==  null) industry = market.getIndustry(Vulpoids.INDUSTRY_BIOFACILITY);
        if(industry != null) popCap = Math.max(popCap, industry.getSupply("vulpoids").getQuantity().getModifiedInt());
        
        industry = market.getIndustry(Vulpoids.INDUSTRY_VULPOIDAGENCY);
        if(industry != null) {
            VulpoidAgency agency = (VulpoidAgency) industry;
            popCap = Math.max(popCap, agency.getVulpoidImport());
        }
        
        popCap = Math.max(popCap, market.getMemoryWithoutUpdate().getInt("$vulpProductionQuantity"));  // For NPC production missions
        
        // Can only support so many with modern infrastructure.
        // For every rich executive with 10 Vulpoids, there'll be at least 10 people with no more than 1.
        popCap = Math.min(popCap, market.getSize());
        
        return popCap;
    }
    
    
    
    public int getPopulation() {return (int)population;}
    public void setPopulation(int population) {this.population=population;}
    public MutableStat getWorkforceCap() {return workforceCap;}
    public int getAvailability() {
        int availability = getPopulation()-market.getSize();
        availability = Math.max(availability, MIN_AVAILABILITY);
        availability = Math.min(availability, MAX_AVAILABILITY);
        return availability;
    }
    public float getAvailabilityStability() {
        float stability = getAvailability() - MIN_AVAILABILITY_FOR_STABILITY + 1;
        stability *= AVAILABILITY_STABILITY_MULT;
        stability = Math.max(stability, 0);
        stability = Math.min(stability, AVAILABILITY_STABILITY_MAX);
        return stability;
    }
    public float getAvailabilityGrowth() {
        if(!market.isPlayerOwned()) return 0;
        float growth = getAvailability() - MIN_AVAILABILITY_FOR_GROWTH + 1;
        growth *= AVAILABILITY_GROWTH_MULT;
        growth = Math.max(growth, 0);
        growth = Math.min(growth, AVAILABILITY_GROWTH_MAX);
        return growth * market.getSize();
    }
    
    
    
    
    @Override
    public void apply(String id) {
        // Fixing a weird bug where it could have a market that wasn't the original.
        // Probably the result of a mod conflict, but only visible here because it's non-transient. 
       if(market.getPrimaryEntity()!=null) market = market.getPrimaryEntity().getMarket();
        
        advance(0);
        if(!market.getMemoryWithoutUpdate().contains(Vulpoids.KEY_WORKFORCES)) market.getMemoryWithoutUpdate().set(Vulpoids.KEY_WORKFORCES, 0);
        market.getMemoryWithoutUpdate().set(Vulpoids.KEY_WORKFORCE_CAP, workforceCap.getModifiedInt());
        if(market.isPlayerOwned()) {
            PersonAPI vulpoid_comms = VulpoidCreator.createVulpoid();
            vulpoid_comms.setId("vulpoid_rep");
            vulpoid_comms.setName(new FullName("Vulpoid Representative", "", FullName.Gender.FEMALE));
            VulpoidCreator.setPersonPortraitPropertyAtIndex(vulpoid_comms, VulpoidCreator.INDEX_CLOTHING, VulpoidCreator.CLOTHING_CLOTHED);
            vulpoid_comms.getMemoryWithoutUpdate().set("$isVulpoidRep", true);
            market.getCommDirectory().addPerson(vulpoid_comms);
        }
        
        market.getStability().modifyFlat(id, getAvailabilityStability(), "Vulpoid Availability");
        market.addTransientImmigrationModifier(this);
    }

    @Override
    public void unapply(String id) {
        if(market.getCommDirectory().getEntryForPerson("vulpoid_rep")!=null) market.getCommDirectory().removeEntry(market.getCommDirectory().getEntryForPerson("vulpoid_rep"));
        market.removeTransientImmigrationModifier(this);
    }
    
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        float bonus = getAvailabilityGrowth();
        incoming.add(Factions.INDEPENDENT, bonus);
        incoming.getWeight().modifyFlat(getModId(), bonus, "Vulpoid Availability");
    }
    
    public void removeAllWorkforceConditions() {
        if(!market.isPlanetConditionMarketOnly()) {
            for(MarketConditionAPI condition : market.getConditions()) {
                if(condition.getPlugin() instanceof BaseWorkforce) {
                    market.removeCondition(condition.getId());
                }
            }
        }
    }
    
    
    @Override
    public String getIconName() {
        if(market.getMemoryWithoutUpdate().contains(MemFlags.RECENTLY_BOMBARDED)) return "graphics/icons/missions/tactical_bombardment.png";
        if(population < 2) return "graphics/icons/markets/vulp_pop_01.png";
        else if(population < 3) return "graphics/icons/markets/vulp_pop_02.png";
        else if(population < 4) return "graphics/icons/markets/vulp_pop_03.png";
        else if(population < 5) return "graphics/icons/markets/vulp_pop_04.png";
        else if(population < 6) return "graphics/icons/markets/vulp_pop_05.png";
        else if(population < 7) return "graphics/icons/markets/vulp_pop_06.png";
        else if(population < 8) return "graphics/icons/markets/vulp_pop_07.png";
        else if(population < 9) return "graphics/icons/markets/vulp_pop_08.png";
        else if(population < 10) return "graphics/icons/markets/vulp_pop_09.png";
        else return "graphics/icons/markets/vulp_pop_10.png";
    }
    
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        
        String name = market.getName();
        float opad = 10f;
        
        if(market.getMemoryWithoutUpdate().contains(MemFlags.RECENTLY_BOMBARDED)) {
            tooltip.addImage(Global.getSettings().getSpriteName("illustrations", "bombard_tactical_result"), opad);
            tooltip.addPara("\nA recent orbital bombardment has devastated the Vulpoid population. "+
                    "It will be take up to a month before the population can start to recover, assuming supply is still available.", opad);
            return;
        }
        
        
        if(population<=3) tooltip.addImage(Global.getSettings().getSpriteName("illustrations", "vulp_pop_low"), opad);
        else if(population>=6) tooltip.addImage(Global.getSettings().getSpriteName("illustrations", "vulp_pop_high"), opad);
        else tooltip.addImage(Global.getSettings().getSpriteName("illustrations", "vulp_pop_med"), opad);

        tooltip.addPara(name+" has a permanent population of Vulpoids, a cute and helpful artificial lifeform. "+
                "In sufficient quantities, their presence will drive immigration of people hoping for a more "+
                "comforable life, and help placate the more rowdy parts of society.", opad);
        
        
        tooltip.addPara("\nQuantity", market.getFaction().getBaseUIColor(), opad);
        
        int p = getPopulation();
        if(p < 2)       tooltip.addPara("Only a few dozen Vulpoids are permanently present here.", opad);
        else if(p < 3)  tooltip.addPara("A few hundred Vulpoids are present at any given time.", opad);
        else if(p < 4)  tooltip.addPara("Thousands of Vulpoids are present across "+name+".", opad);
        else if(p < 5)  tooltip.addPara("Tens of thousands of Vulpoids inhabit the cities of "+name+".", opad);
        else if(p < 6)  tooltip.addPara("Hundreds of thousands of Vulpoids occupy whole districts of "+name+".", opad);
        else if(p < 7)  tooltip.addPara("Millions of Vulpoids fill the cities of "+name+".", opad);
        else if(p < 8)  tooltip.addPara("Tens of millions of Vulpoids fill entire arcologies on "+name+".", opad);
        else if(p < 9)  tooltip.addPara("Hundreds of millions of Vulpoids live in seas of fluffy ears and wagging tails.", opad);
        else if(p < 10) tooltip.addPara("Billions of Vulpoids make entire oceans of fluffy affection, deep enough to drown in.", opad);
        else            tooltip.addPara("Tens of billions of Vulpoids live so densely on "+name+" that there's not a single place not smothered by eager adoration.", opad);
        
        int w = workforceCap.getModifiedInt();
        String w_s = "s";
        if (w==1) w_s = "";
        if(w > 0) tooltip.addPara("Supports %s Vulpoid workforce"+w_s, opad, Misc.getHighlightColor(), ""+w);
        
        
        tooltip.addPara("\nAvailability", market.getFaction().getBaseUIColor(), opad);
        
        switch(getAvailability()) {
            case -6:    tooltip.addPara("Vulpoids on "+name+" are one-in-a-million, with only the very richest posessing them.", opad); break;
            case -5:    tooltip.addPara("Only the upper echelons of "+name+"'s elites possess Vulpoids.", opad); break;
            case -4:    tooltip.addPara("Even among the wealthier citizens of "+name+", Vulpoids are a rarity.", opad); break;
            case -3:    tooltip.addPara("The wealthy citizens of "+name+" usually keep Vulpoids of their own.", opad); break;
            case -2:    tooltip.addPara("Anyone of means on "+name+" has at least one Vulpoid to their name.", opad); break;
            case -1:    tooltip.addPara("Vulpoids are common on "+name+", with over one in every dozen people having one.", opad); break;
            case 0:     tooltip.addPara("Nearly everyone on "+name+" has a Vulpoid to tend to their needs.", opad); break;
            case 1:     tooltip.addPara("Every citizen of "+name+" has a personal Vulpoid eager to be their best friend, and many have more than one.", opad); break;
            case 2:     tooltip.addPara("Every person on "+name+" has dozens of Vulpoids catering to their every whim, living a life of luxury unheard of in the Persean Sector.", opad); break;
        }
        
        if (getAvailabilityStability()>0) tooltip.addPara("%s stability", opad, Misc.getHighlightColor(), "+"+(int)getAvailabilityStability());
        if (getAvailabilityGrowth()>0) tooltip.addPara("%s population growth (based on market size)", opad, Misc.getHighlightColor(), "+"+(int)getAvailabilityGrowth());
        
        if(getPopulation() >= market.getSize()) {
            tooltip.addPara("\nThe Vulpoid population has matched the human population, and cannot be sustainably increased.", opad);
        } else if (getPopCap() <= getPopulation()) {
            tooltip.addPara("\nThe population has reached its maximum size for current production.", opad);
        } else {
            int progress_percent = (int)((population*100)%100);
            tooltip.addPara("\nProgress to next level: %s", opad, Misc.getHighlightColor(), progress_percent+"%");
        }
    }
    
    @Override
    public boolean isTransient() {return false;}
}
