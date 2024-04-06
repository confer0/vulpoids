package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class LaisaAdmin {
    
    final static float FLEET_SIZE = 20f;
    
    public static class Level1 implements MarketSkillEffect {
        @Override
        public void apply(MarketAPI market, String id, float level) {
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(id, FLEET_SIZE / 100f, "Predictive Checklists");
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(id);
        }
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)Math.round(FLEET_SIZE) + "% fleet size";
        }
        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }
        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }
}
