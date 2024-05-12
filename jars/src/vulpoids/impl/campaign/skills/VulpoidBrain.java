package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.misc.AdminGotPlanning;


public class VulpoidBrain {
    
    final static float ACCESS = 0.2f;
    final static float STABILITY_BONUS = 1;
    
    final static String SKILL_NAME = "Elevated Synapses";
    
    final static float ADMIN_CAP_BONUS = 0.5f;
    
    final static int DAYS_FOR_INDUSTRIAL = 365;
    
    public static class ChangePortrait implements MarketSkillEffect {
        @Override
        public void apply(MarketAPI market, String id, float level) {
            PersonAPI admin = market.getAdmin();
            admin.setPostId(Ranks.POST_ADMINISTRATOR);
            market.getMemoryWithoutUpdate().set(Vulpoids.KEY_MARKET_VULPOID_ADMIN, admin);
            market.getMemoryWithoutUpdate().set(Vulpoids.KEY_MARKET_VULPOID_ADMIN_TIMESTAMP, Global.getSector().getClock().getTimestamp());
            //market.getCommDirectory().addPerson(admin, 0);
            if(VulpoidCreator.marketIsSuitMarket(market)) {
                admin.setPortraitSprite(VulpoidCreator.setPortraitPropertyAtIndex(admin.getPortraitSprite(), VulpoidCreator.INDEX_CLOTHING, VulpoidCreator.CLOTHING_SUIT));
                admin.setPortraitSprite(VulpoidCreator.setPortraitPropertyAtIndex(admin.getPortraitSprite(), VulpoidCreator.INDEX_EXPRESSION, VulpoidCreator.EXPRESSION_HELMET));
            }
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            PersonAPI admin = (PersonAPI)market.getMemoryWithoutUpdate().get(Vulpoids.KEY_MARKET_VULPOID_ADMIN);
            if(admin != null) {
                //market.getCommDirectory().removePerson(admin);
                for(AdminData player_admin : Global.getSector().getCharacterData().getAdmins()) if(player_admin.getPerson().getId().equals(admin.getId())) admin=player_admin.getPerson();
                admin.setPortraitSprite(VulpoidCreator.setPortraitPropertyAtIndex(admin.getPortraitSprite(), VulpoidCreator.INDEX_CLOTHING, VulpoidCreator.CLOTHING_CLOTHED));
                admin.setPortraitSprite(VulpoidCreator.setPortraitPropertyAtIndex(admin.getPortraitSprite(), VulpoidCreator.INDEX_EXPRESSION, VulpoidCreator.EXPRESSION_DEFAULT));
                String default_post = admin.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_POST);
                if(default_post != null) admin.setPostId(default_post);
                else admin.setPostId(null);
                
                float days_passed = 0;
                if(market.getMemoryWithoutUpdate().contains(Vulpoids.KEY_MARKET_VULPOID_ADMIN_TIMESTAMP)) {
                    long start_timestamp = market.getMemoryWithoutUpdate().getLong(Vulpoids.KEY_MARKET_VULPOID_ADMIN_TIMESTAMP);
                    days_passed = Global.getSector().getClock().getElapsedDaysSince(start_timestamp);
                }
                float days_already_passed = 0;
                if(admin.getMemoryWithoutUpdate().contains(Vulpoids.KEY_ADMIN_XP_DAYS)) days_already_passed = admin.getMemoryWithoutUpdate().getFloat(Vulpoids.KEY_ADMIN_XP_DAYS);
                admin.getMemoryWithoutUpdate().set(Vulpoids.KEY_ADMIN_XP_DAYS, days_passed + days_already_passed);
                if(days_passed + days_already_passed >= DAYS_FOR_INDUSTRIAL && !admin.getStats().hasSkill(Skills.INDUSTRIAL_PLANNING)) {
                    admin.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
                    Global.getSector().getIntelManager().addIntel(new AdminGotPlanning(market, admin));
                }
            }
            market.getMemoryWithoutUpdate().unset(Vulpoids.KEY_MARKET_VULPOID_ADMIN);
            market.getMemoryWithoutUpdate().unset(Vulpoids.KEY_MARKET_VULPOID_ADMIN_TIMESTAMP);
        }
        @Override
        public String getEffectDescription(float level) {
            return null;
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
    
    public static class Level1 implements MarketSkillEffect {
        @Override
        public void apply(MarketAPI market, String id, float level) {}
        @Override
        public void unapply(MarketAPI market, String id) {}
        @Override
        public String getEffectDescription(float level) {
            return "Learns Industrial Planning after a year of service.";
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
            market.getAccessibilityMod().modifyFlat(id, ACCESS, SKILL_NAME);
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            market.getAccessibilityMod().unmodifyFlat(id);
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
    
    public static class Level3 implements MarketSkillEffect {
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
    
    public static class Level4 implements MarketSkillEffect {
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
