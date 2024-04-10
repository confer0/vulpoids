package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class LaisaOfficer {
    
    public static float MIN_CREW_MULT = 0.5f;
    public static float REPAIR_RATE_BONUS = 100f;
    public static float CR_RECOVERY_BONUS = 100f;
    
    public static class Level1 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getMinCrewMod().modifyMult(id, MIN_CREW_MULT);
        }
        @Override
        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getMinCrewMod().unmodify(id);
        }
        @Override
        public String getEffectDescription(float level) {
            return "-" + (int)(MIN_CREW_MULT*100) + "% minimum crew required";
        }
        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }
        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
    
    public static class Level2 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getBaseCRRecoveryRatePercentPerDay().modifyPercent(id, CR_RECOVERY_BONUS);
        }
        @Override
        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getBaseCRRecoveryRatePercentPerDay().unmodify(id);
        }
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(CR_RECOVERY_BONUS) + "% daily CR recovery rate";
        }
        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }
        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
    
    public static class Level3 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            stats.getRepairRatePercentPerDay().modifyPercent(id, REPAIR_RATE_BONUS);
        }
        @Override
        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            stats.getRepairRatePercentPerDay().unmodify(id);
        }
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(REPAIR_RATE_BONUS) + "% daily repair rate";
        }
        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }
        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
}
