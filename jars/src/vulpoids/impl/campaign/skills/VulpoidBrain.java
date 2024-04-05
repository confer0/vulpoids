package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.ids.Vulpoids;


public class VulpoidBrain {
    
    final static float ACCESS = 0.2f;
    final static float STABILITY_BONUS = 1;
    
    final static String SKILL_NAME = "Elevated Synapses";
    
    final static float ADMIN_CAP_BONUS = 0.5f;


    public static class Level1 implements MarketSkillEffect {
        PersonAPI admin;
        @Override
        public void apply(MarketAPI market, String id, float level) {
            market.getAccessibilityMod().modifyFlat(id, ACCESS, SKILL_NAME);
            admin = market.getAdmin();
            admin.setPostId(Ranks.POST_ADMINISTRATOR);
            market.getCommDirectory().addPerson(admin, 0);
            admin.setPortraitSprite(VulpoidCreator.getPortraitForMarket(market, false, false, false));
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            market.getAccessibilityMod().unmodifyFlat(id);
            if(admin != null) {
                market.getCommDirectory().removePerson(admin);
                // This doesn't work for some reason. Haven't been able to figure it out.
                String default_portrait = admin.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_PORTRAIT);
                if (default_portrait != null) admin.setPortraitSprite(default_portrait);
            }
        }
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)Math.round(ACCESS * 100f) + "% accessibility";
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

    public static class Level2 implements MarketSkillEffect {
        @Override
        public void apply(MarketAPI market, String id, float level) {
            market.getStability().modifyFlat(id, STABILITY_BONUS, SKILL_NAME);
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            market.getStability().unmodifyFlat(id);
        }
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)STABILITY_BONUS + " stability";
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
    
    public static class Level3 implements MarketSkillEffect {
        @Override
        public void apply(MarketAPI market, String id, float level) {
            Global.getSector().getPlayerStats().getAdminNumber().modifyFlat(market.getId(), ADMIN_CAP_BONUS, SKILL_NAME);
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            Global.getSector().getPlayerStats().getAdminNumber().unmodifyFlat(market.getId());
        }
        @Override
        public String getEffectDescription(float level) {
            return "+" + ADMIN_CAP_BONUS + " administrator capacity";
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
