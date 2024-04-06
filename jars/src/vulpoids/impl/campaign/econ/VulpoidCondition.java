package vulpoids.impl.campaign.econ;


import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.bases.LuddicPathCells;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.HashMap;
import vulpoids.impl.campaign.VulpoidCreator;

public class VulpoidCondition extends BaseMarketConditionPlugin implements MarketImmigrationModifier {

    public static float STABILITY = 1f;
    public static float IMMIGRATION_BASE = 10f;
    //public static float ACCESS_BONUS = 10f;
    //public static float INCOME_BONUS = 10f;
    
    public static float INCOME_BONUS_FOR_CUSTOMS_AGENT = 25f;
    public static int PROD_BONUS_FOR_HARD_LABOR = 1;
    public static int STAB_PENALTY_FOR_HARD_LABOR = -2;

    public final String BONUS_NAME = "Vulpoid population";
    
    PersonAPI vulpoid_comms;
    PersonAPI vulpoid_quartermaster;
    
    public final HashMap<String, String> TrainingProgramName = new HashMap<String, String>() {{
        put("customs_agent", "Vulpoid customs agents");
        put("security_fox", "Vulpoid security foxes");
        put("hard_labor", "Vulpoid hard labor");
    }};
    public String active_program;
    
    public void setParam(Object param) {
        active_program = (String)param;
    }
    
    public void apply(String id) {
        market.addTransientImmigrationModifier(this);
        market.getStability().modifyFlat(id, STABILITY, BONUS_NAME);
        
        String program_name = TrainingProgramName.get(active_program);
        if (active_program != null) {
            switch(active_program) {
                case "customs_agent":
                    market.getIncomeMult().modifyPercent(id, INCOME_BONUS_FOR_CUSTOMS_AGENT, program_name);
                    break;
                case "security_fox":
                    if(market.getCondition(Conditions.PATHER_CELLS) != null) ((LuddicPathCells)market.getCondition(Conditions.PATHER_CELLS).getPlugin()).getIntel().setSleeper(true);
                    break;
                case "hard_labor":
                    market.getStability().modifyFlat(id+"_hardLabor", STAB_PENALTY_FOR_HARD_LABOR, program_name);
                    Industry industry = market.getIndustry(Industries.MINING);
                    if(industry != null) industry.getSupplyBonusFromOther().modifyFlat(id, STABILITY, program_name);
                    break;
            }
        }
        
        if(vulpoid_comms == null) {
            vulpoid_comms = VulpoidCreator.createNudeVulpoid(market);
            vulpoid_comms.setId("vulpoid_rep");
            vulpoid_comms.setName(new FullName("Vulpoid Representative", "", FullName.Gender.FEMALE));
            vulpoid_comms.getMemoryWithoutUpdate().set("$isVulpoidRep", true);
        }
        market.getCommDirectory().addPerson(vulpoid_comms);
    }

    public void unapply(String id) {
        market.removeTransientImmigrationModifier(this);
        market.getStability().unmodify(id);
        market.getStability().unmodify(id+"_hardLabor");
        
        market.getIncomeMult().unmodifyPercent(id);
        Industry industry = market.getIndustry(Industries.MINING);
        if(industry != null) {
            industry.getSupplyBonusFromOther().unmodifyFlat(id);
        }
        
        if(vulpoid_comms != null) {
            market.getCommDirectory().removePerson(vulpoid_comms);
        }
        //Safeguard
        market.getCommDirectory().removeEntry(market.getCommDirectory().getEntryForPerson("vulpoid_rep"));
    }

    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        float bonus = getImmigrationBonus();
        incoming.add(Factions.INDEPENDENT, bonus);
        incoming.getWeight().modifyFlat(getModId(), bonus, BONUS_NAME);
    }
    
    public float getImmigrationBonus() {
        return IMMIGRATION_BASE * market.getSize();
    }
    
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
            super.createTooltipAfterDescription(tooltip, expanded);

            String name = market.getName();
            float opad = 10f;
            
            tooltip.addPara(name+" has a large population of Vulpoids, a cute and helpful artificial lifeform. "+
                    "Their presence drives immigration of people hoping for a more comforable life, "+
                    "and helps placate the more rowdy parts of society by overhelming cuteness.", opad);
            // I know what that might sound like. But I honestly wrote that with the idea of people being less aggressive when they're around.
            // -Cuteness bit might make it sound less "questionable".

            tooltip.addPara("%s population growth (based on colony size)",
                    opad, Misc.getHighlightColor(), 
                    "+" + (int) getImmigrationBonus());
            
            tooltip.addPara("%s stability", opad, Misc.getHighlightColor(), "+" + (int)STABILITY);
            
            if (active_program != null) {
                switch(active_program) {
                    case "customs_agent":
                        tooltip.addPara("The Vulpoids are catering to visiting traders at the spaceports, encouraging more trade.", opad);
                        tooltip.addPara("%s colony income", opad, Misc.getHighlightColor(), "+" + (int)INCOME_BONUS_FOR_CUSTOMS_AGENT + "%");
                        break;
                    case "security_fox":
                        tooltip.addPara("The Vulpoids are working with our security forces, helping to sniff out criminal elements.", opad);
                        tooltip.addPara("%s", opad, Misc.getHighlightColor(), "Pather activity suppressed");
                        break;
                    case "hard_labor":
                        tooltip.addPara("The Vulpoids are down in the mines, earning their keep with hard labor.\nThe foxgirls yearn for the mines!", opad);
                        tooltip.addPara("%s mining production", opad, Misc.getHighlightColor(), "+" + PROD_BONUS_FOR_HARD_LABOR);
                        tooltip.addPara("%s stability", opad, Misc.getNegativeHighlightColor(), "" + (int)STAB_PENALTY_FOR_HARD_LABOR);
                        break;
                    default:
                        tooltip.addPara("Unrecognized program: "+active_program, opad);
                        break;
                }
            } else {
                tooltip.addPara("The Vulpoids are not engaged in any specific training program.", opad);
            }
    }
}
