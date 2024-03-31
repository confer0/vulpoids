package vulpoids.impl.campaign.econ;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CommDirectoryEntryAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.impl.campaign.VulpoidCreator;

public class VulpoidCondition extends BaseMarketConditionPlugin implements MarketImmigrationModifier {

    public static float STABILITY = 1f;
    public static float IMMIGRATION_BASE = 10f;
    public static float ACCESS_BONUS = 10f;
    public static float INCOME_BONUS = 10f;

    public static String BONUS_NAME = "Vulpoids";
    
    PersonAPI vulpoid_comms;
    PersonAPI vulpoid_quartermaster;

    public void apply(String id) {
        market.addTransientImmigrationModifier(this);
        market.getStability().modifyFlat(id, STABILITY, BONUS_NAME);
        market.getAccessibilityMod().modifyPercent(id, ACCESS_BONUS, BONUS_NAME);
        market.getIncomeMult().modifyPercent(id, INCOME_BONUS, BONUS_NAME);
        
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
        market.getAccessibilityMod().unmodifyPercent(id);
        market.getIncomeMult().unmodifyPercent(id);
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
                    "While their limited supply means most cannot afford one for "+
                    "themselves, many people are still drawn in for tourism or "+
                    "work, and traders prefer to frequent ports catered by them.", opad);

            tooltip.addPara("%s stability", 
                    opad, Misc.getHighlightColor(),
                    "+" + (int)STABILITY);

            tooltip.addPara("%s population growth",
                    opad, Misc.getHighlightColor(), 
                    "+" + (int) getImmigrationBonus());
            
            tooltip.addPara("%s accessibility",
                    opad, Misc.getHighlightColor(), 
                    "+" + (int)ACCESS_BONUS + "%");
            
            tooltip.addPara("%s colony income",
                    opad, Misc.getHighlightColor(), 
                    "+" + (int)INCOME_BONUS + "%");
    }
}
