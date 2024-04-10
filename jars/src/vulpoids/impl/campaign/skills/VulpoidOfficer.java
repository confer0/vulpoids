package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class VulpoidOfficer {
    public static class Level1 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string, float f) {}
        @Override
        public void unapply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string) {}
        @Override
        public String getEffectDescription(float level) {return "Maximum officer level: 6";}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
    
    public static class Level2 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string, float f) {}
        @Override
        public void unapply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string) {}
        @Override
        public String getEffectDescription(float level) {return "Maximum elite skills: 3";}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
    
    public static class Level3 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string, float f) {}
        @Override
        public void unapply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string) {}
        @Override
        public String getEffectDescription(float level) {return "10 skill picks on levelling up";}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
    
    public static class Level4 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string, float f) {}
        @Override
        public void unapply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string) {}
        @Override
        public String getEffectDescription(float level) {return "Not affected by Officer Training or Cybernetic Augmentation";}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
}
