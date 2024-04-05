package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class VulpoidOfficer {
    public static class Level1 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            
        }
        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            
        }

        @Override
        public String getEffectDescription(float level) {
            return "TODO - What should this do?";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
        /*@Override
        public void apply(MarketAPI market, String id, float level) {
            market.getAccessibilityMod().modifyFlat(id, ACCESS, SKILL_NAME);
            
            // Adding to comms, and giving the custom picture!
            admin = market.getAdmin();
            
            market.getCommDirectory().addPerson(admin, 0);
            market.getMemoryWithoutUpdate().set("$vulpoidAdmin", admin);
            admin.setPortraitSprite(VulpoidCreator.getPortraitForMarket(market, false, false, false));
            
        }
        @Override
        public void unapply(MarketAPI market, String id) {
            market.getAccessibilityMod().unmodifyFlat(id);
            
            // Comm Directory
            market.getMemoryWithoutUpdate().unset("$vulpoidAdmin");
            if(admin != null) {
                market.getCommDirectory().removePerson(admin);
                Global.getSector().getPlayerStats().getAdminNumber().unmodifyFlat(admin.getId());
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
            return ScopeDescription.PILOTED_SHIP;
        }*/
    }
}
