package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI.SkillLevelAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import java.util.Comparator;

public class VulpoidOfficer {
    static int MAX_LEVEL = 7;
    //static int MAX_ELITE = MAX_LEVEL-1;
    static int SKILL_PICKS = Integer.MAX_VALUE;
    public static class MaxLevel implements CharacterStatsSkillEffect {
        @Override
        public void apply(MutableCharacterStatsAPI stats, String id, float level) {
            if(Global.getSector()==null || Global.getSector().getPlayerFleet()==null) return;
            for(OfficerDataAPI officer : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
                if(officer.getPerson().getStats()==stats) {
                    officer.getPerson().getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, MAX_LEVEL);
                    return;
                }
            }
        }
        @Override
        public void unapply(MutableCharacterStatsAPI stats, String id) {}
        @Override
        public String getEffectDescription(float level) {return null;}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
    public static class SkillPicks implements CharacterStatsSkillEffect {
        @Override
        public void apply(MutableCharacterStatsAPI stats, String id, float level) {
            if(Global.getSector()==null || Global.getSector().getPlayerFleet()==null) return;
            for(OfficerDataAPI officer : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
                if(officer.getPerson().getStats()==stats) {
                    officer.getPerson().getMemoryWithoutUpdate().set(MemFlags.OFFICER_SKILL_PICKS_PER_LEVEL, SKILL_PICKS);
                    return;
                }
            }
        }
        @Override
        public void unapply(MutableCharacterStatsAPI stats, String id) {}
        @Override
        public String getEffectDescription(float level) {return null;}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
    
    public static class MakeSkillsElite implements CharacterStatsSkillEffect {
        @Override
        public void apply(MutableCharacterStatsAPI stats, String id, float level) {
            if(Global.getSector()==null || Global.getSector().getPlayerFleet()==null) return;
            for(OfficerDataAPI officer : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
                if(officer.getPerson().getStats()==stats) {
                    officer.getPerson().getMemoryWithoutUpdate().unset(MemFlags.OFFICER_MAX_ELITE_SKILLS); // Removing old flags
                    officer.getPerson().getMemoryWithoutUpdate().set(MemFlags.EXCEPTIONAL_SLEEPER_POD_OFFICER, true); // Stops the removal of Officer Training from messing with them, which could otherwise turn them into mercs.
                    for(SkillLevelAPI skill : stats.getSkillsCopy()) {
                        // impl/campaign/OfficerLevelupPluginImpl.pickLevelupSkillsV3 uses these functions to determine learnable skills.
                        if (!skill.getSkill().isCombatOfficerSkill()) continue;
                        if (skill.getSkill().hasTag(Skills.TAG_DEPRECATED)) continue;
                        if (skill.getSkill().hasTag(Skills.TAG_PLAYER_ONLY)) continue;
                        if(stats.getSkillLevel(skill.getSkill().getId())==1) stats.setSkillLevel(skill.getSkill().getId(), 2);
                    }
                    officer.getSkillPicks().sort(Comparator.comparing(String::toString));
                    return;
                }
            }
        }
        @Override
        public void unapply(MutableCharacterStatsAPI stats, String id) {}
        @Override
        public String getEffectDescription(float level) {return null;}
        @Override
        public String getEffectPerLevelDescription() {return null;}
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
    
    public static class SkillDesc extends BaseSkillEffectDescription implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string, float f) {}
        @Override
        public void unapply(MutableShipStatsAPI mssapi, ShipAPI.HullSize hs, String string) {}
        @Override
        public String getEffectDescription(float level) {return null;}
        @Override
        public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill, TooltipMakerAPI info, float width) {
            init(stats, skill);
            info.addPara("Officer can reach a maximum level of %s, learning %s skills.", 0f, hc, hc, ""+MAX_LEVEL, ""+(MAX_LEVEL-1));
            info.addPara(indent+"Leadership skills such as %s have no effect.", 0f, tc, hc, "Officer Training");
            info.addPara("Can pick any skill on level up.", 0f, hc, hc);
            info.addPara("Gained skills are automatically made elite.", 0f, hc, hc);
            info.addPara("Does not require SP to retrain skills or personality.", 0f, hc, hc);
            info.addPara(indent+"Speak with her using the %s ability to retrain!", 0f, tc, hc, "Chat");
        }
        @Override
        public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
    }
}
