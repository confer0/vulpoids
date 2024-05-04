package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class VulpoidAlphaCore {
    
    public static String SKILL_NAME = "Hypercognitive Assistant";
    
    public static float FLEET_SIZE = 20f;
    public static int DEFEND_BONUS = 50;
    
    public static float MIN_CREW_MULT = 0.5f;
    
    public static class Level1 extends BaseSkillEffectDescription implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            stats.getMinCrewMod().modifyMult(id, MIN_CREW_MULT);
        }
        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getMinCrewMod().unmodify(id);
        }
        @Override
        public String getEffectDescription(float level) {return null;}
        @Override
        public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill, TooltipMakerAPI info, float width) {
            init(stats, skill);
            float opad = 10f;
            Color c = Misc.getBasePlayerColor();
            info.addPara("Affects: %s", opad + 5f, Misc.getGrayColor(), c, "piloted ship");
            info.addPara("%s minimum crew required", opad, hc, hc, "-" + (int)(MIN_CREW_MULT*100) + "%");
        }
        public LevelBasedEffect.ScopeDescription getScopeDescription() {return LevelBasedEffect.ScopeDescription.PILOTED_SHIP;}
    }
    
    public static class Level2 extends BaseSkillEffectDescription implements MarketSkillEffect {
        @Override
        public void apply(MarketAPI market, String id, float level) {
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(id, FLEET_SIZE / 100f, SKILL_NAME);
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(id, 1f + DEFEND_BONUS * 0.01f, SKILL_NAME);
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(id);
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(id);
        }
        @Override
        public String getEffectDescription(float level) {return null;}
        public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill, TooltipMakerAPI info, float width) {
            init(stats, skill);
            float opad = 10f;
            Color c = Misc.getBasePlayerColor();
            info.addPara("Affects: %s", opad + 5f, Misc.getGrayColor(), c, "governed colony");
            info.addPara("%s fleet size", opad, hc, hc, "+"+(int)Math.round(FLEET_SIZE)+"%");
            info.addPara("%s effectiveness of ground defenses", 0f, hc, hc, "+"+(int)(DEFEND_BONUS)+"%");
        }
        @Override
        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return LevelBasedEffect.ScopeDescription.GOVERNED_OUTPOST;
        }
    }
    
    
    
}
