package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.econ.MonthlyReport.FDNode;
import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator;
import com.fs.starfarer.api.util.Misc;


public class VulpoidLuxury {
    
    final static int UPKEEP = 15000;
    
    public static class Level1 implements MarketSkillEffect {
        PersonAPI admin;
        
        public void apply(MarketAPI market, String id, float level) {
            if(market.isPlayerOwned()) {
                admin = market.getAdmin();
                MonthlyReport report = SharedData.getData().getCurrentReport();
                FDNode colonyNode = report.getNode(MonthlyReport.OUTPOSTS);
                FDNode adminNode = report.getNode(colonyNode, MonthlyReport.ADMIN);
                String name = "Profecto Vulpoid ("+market.getName()+")";
                FDNode stipendNode = report.getNode(adminNode, name);
                stipendNode.upkeep = UPKEEP;
                stipendNode.name = name;
                stipendNode.icon = admin.getPortraitSprite();
                stipendNode.tooltipCreator = new IncomeTooltip();
            }
        }

        public void unapply(MarketAPI market, String id) {
        }

        public String getEffectDescription(float level) {
            return "Requires a monthly upkeep of "+Misc.getDGSCredits(UPKEEP);
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return LevelBasedEffect.ScopeDescription.GOVERNED_OUTPOST;
        }
        
        private class IncomeTooltip implements TooltipCreator {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 450;
            }
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Monthly salary and expenses: %s", 
                        0f, Misc.getHighlightColor(), Misc.getDGSCredits(-15000));
                
                tooltip.addPara("Profecto Vulpoids have numerous expenses related to their security and comfort.", 10f);
            }
        }
    }

}
