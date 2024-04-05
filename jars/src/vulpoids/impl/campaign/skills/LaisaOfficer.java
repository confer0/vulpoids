package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;

public class LaisaOfficer {
    public static class Level1 implements CharacterStatsSkillEffect {
        @Override
        public void apply(MutableCharacterStatsAPI stats, String id, float level) {
            
        }
        @Override
        public void unapply(MutableCharacterStatsAPI stats, String id) {
            
        }
        @Override
        public String getEffectDescription(float level) {
            return "TODO";
        }
        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }
        @Override
        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return LevelBasedEffect.ScopeDescription.NONE;
        }
    }
}
