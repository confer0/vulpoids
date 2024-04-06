package vulpoids.impl.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.econ.workforces.BaseWorkforce;
import vulpoids.impl.campaign.intel.misc.VulpPopGrownIntel;

public class VulpoidPopulation extends BaseMarketConditionPlugin {
    
    private float population = 0;
    final float MIN_POPULATION = 1;
    final float MAX_POPULATION = 10; // Just in case.
    //private int last_reported_production = 0;
    
    private int workforce_cap = 0;
    final int MIN_POPULATION_FOR_WORKFORCE = 3;
    final float POPULATION_WORKFORCE_MULT = 0.5f; // 3>0, 4>1, 5>1, 6>2, 7>3.
    final int MAX_WORKFORCE_CAP = 4;
    
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
            if ((int)population < (int)(population+population_growth)) Global.getSector().getIntelManager().addIntel(new VulpPopGrownIntel(market, (int)population+1));
            population += population_growth;
            population = Math.min(population, MAX_POPULATION);
            population = Math.max(population, MIN_POPULATION);
        } else {
            population = getPopCap() - 2;
        }
        workforce_cap = (int)population - MIN_POPULATION_FOR_WORKFORCE + 1;
        workforce_cap = (int)(workforce_cap * POPULATION_WORKFORCE_MULT);
        workforce_cap = Math.max(workforce_cap, 0);
        workforce_cap = Math.min(workforce_cap, MAX_WORKFORCE_CAP);
        market.getMemoryWithoutUpdate().set("$workforce_cap", workforce_cap);
    }
    private int getPopCap() {
        try {
            Industry industry = market.getIndustry("organfarms");
            if (industry ==  null) industry = market.getIndustry("biofacility");
            return industry.getSupply("vulpoids").getQuantity().getModifiedInt();
        } catch(NullPointerException e) {
            return 0;
        }
    }
    
    
    
    public int getPopulation() {return (int)population;}
    public int getWorkforceCap() {return workforce_cap;}
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
        float growth = getAvailability() - MIN_AVAILABILITY_FOR_GROWTH + 1;
        growth *= AVAILABILITY_GROWTH_MULT;
        growth = Math.max(growth, 0);
        growth = Math.min(growth, AVAILABILITY_GROWTH_MAX);
        return growth * market.getSize();
    }
    
    
    
    
    @Override
    public void apply(String id) {
        advance(0);
        if(!market.getMemoryWithoutUpdate().contains("$workforces")) market.getMemoryWithoutUpdate().set("$workforces", 0);
        market.getMemoryWithoutUpdate().set("$workforce_cap", workforce_cap);
        PersonAPI vulpoid_comms = VulpoidCreator.createNudeVulpoid(market);
        vulpoid_comms.setId("vulpoid_rep");
        vulpoid_comms.setName(new FullName("Vulpoid Representative", "", FullName.Gender.FEMALE));
        vulpoid_comms.getMemoryWithoutUpdate().set("$isVulpoidRep", true);
        market.getCommDirectory().addPerson(vulpoid_comms);
    }

    @Override
    public void unapply(String id) {
        market.getCommDirectory().removeEntry(market.getCommDirectory().getEntryForPerson("vulpoid_rep"));
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
        
        int w = getWorkforceCap();
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
                                        // Mentioning hundreds is overkill imho. Tweaked case 1 and 2 desc a bit
        }
        
        if (getAvailabilityStability()>0) tooltip.addPara("%s stability", opad, Misc.getHighlightColor(), "+"+(int)getAvailabilityStability());
        if (getAvailabilityGrowth()>0) tooltip.addPara("%s population growth (based on market size)", opad, Misc.getHighlightColor(), "+"+(int)getAvailabilityGrowth());
        
        
        if(market.getMemoryWithoutUpdate().contains(MemFlags.RECENTLY_BOMBARDED)) {
            tooltip.addPara("\nA recent orbital bombardment has devastated the Vulpoid population. "+
                    "It will be take up to a month before the population can start to recover, assuming producion is still available.", opad);
        }
        else if (getPopCap() > 0) {
            if (getPopCap() <= getPopulation()) {
                tooltip.addPara("\nThe population has reached its maximum size for current production.", opad);
            } else {
                int progress_percent = (int)((population*100)%100);
                tooltip.addPara("\nProgress to next level: %s", opad, Misc.getHighlightColor(), progress_percent+"%");
            }
        }
    }
    
    @Override
    public boolean isTransient() {return false;}
}
