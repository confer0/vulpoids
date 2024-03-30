package vulpoids.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Stats;


public class VulpoidBrain {
    
    public static float ACCESS = 0.05f;//0.1f;
    public static float FLEET_SIZE = 10f;//20f;
    public static int DEFEND_BONUS = 25;//50;
    public static float STABILITY_BONUS = 1;
    
    private static String skill_name = "Vulpoid Brain";


    public static class Level1 implements MarketSkillEffect {
        PersonAPI admin;
        
        public void apply(MarketAPI market, String id, float level) {
            market.getAccessibilityMod().modifyFlat(id, ACCESS, skill_name);
            
            // Adding to comms, and giving the custom picture!
            admin = market.getAdmin();
            if(admin != null && "vulpoids_shiny".equals(admin.getAICoreId())) {
                market.getCommDirectory().addPerson(admin, 0);
                market.getMemoryWithoutUpdate().set("$vulpoidAdmin", admin);
                
                // Planet-specific picture.
                if (market.getPlanetEntity() == null) {
                    // Station
                    admin.setPortraitSprite("graphics/portraits/vulpoid_airless.png");
                } else {
                    //Planets
                    /*if (market.hasCondition(Conditions.NO_ATMOSPHERE)) {
                        admin.setPortraitSprite("graphics/portraits/vulpoid_airless.png");
                    } else if (market.hasCondition(Conditions.HOT) || market.hasCondition(Conditions.VERY_HOT)) {
                        admin.setPortraitSprite("graphics/portraits/vulpoid_hot.png");
                    } else if (market.hasCondition(Conditions.COLD) || market.hasCondition(Conditions.VERY_COLD)) {
                        admin.setPortraitSprite("graphics/portraits/vulpoid_cold.png");
                    } else if (market.hasCondition(Conditions.HABITABLE)) {
                        admin.setPortraitSprite("graphics/portraits/vulpoid_habitable.png");
                    } else {
                        admin.setPortraitSprite("graphics/portraits/vulpoid.png");
                    }*/
                    if (market.hasCondition(Conditions.HABITABLE)) {
                        switch (market.getPlanetEntity().getSpec().getPlanetType()) {
                            case "jungle":
                                admin.setPortraitSprite("graphics/portraits/terran_fox.png");
                                break;
                            case Planets.PLANET_TERRAN:
                                admin.setPortraitSprite("graphics/portraits/terran_fox.png");
                                break;
                            case Planets.DESERT:
                                admin.setPortraitSprite("graphics/portraits/desert_fox.png");
                                break;
                            case Planets.DESERT1:
                                admin.setPortraitSprite("graphics/portraits/desert_fox.png");
                                break;
                            case Planets.ARID:
                                admin.setPortraitSprite("graphics/portraits/desert_fox.png");
                                break;
                            case Planets.PLANET_WATER:
                                admin.setPortraitSprite("graphics/portraits/terran_fox.png");
                                break;
                            case Planets.TUNDRA:
                                admin.setPortraitSprite("graphics/portraits/winter_fox.png");
                                break;
                            case Planets.PLANET_TERRAN_ECCENTRIC:
                                admin.setPortraitSprite("graphics/portraits/winter_fox.png");
                                break;
                            default:
                                admin.setPortraitSprite("graphics/portraits/terran_fox.png");
                                break;
                        }
                    } else if (market.hasCondition(Conditions.VERY_HOT)) {
                        admin.setPortraitSprite("graphics/portraits/space_desert_fox.png");
                    } else if (market.hasCondition(Conditions.VERY_COLD)) {
                        admin.setPortraitSprite("graphics/portraits/space_winter_fox.png");
                    } else {
                        admin.setPortraitSprite("graphics/portraits/space_terran_fox.png");
                    }
                }
            }
        }

        public void unapply(MarketAPI market, String id) {
            market.getAccessibilityMod().unmodifyFlat(id);
            
            // Comm Directory
            market.getMemoryWithoutUpdate().unset("$vulpoidAdmin");
            if(admin != null) {
                market.getCommDirectory().removePerson(admin);
            }
        }

        public String getEffectDescription(float level) {
            return "+" + (int)Math.round(ACCESS * 100f) + "% accessibility";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }

    public static class Level2 implements MarketSkillEffect {
        public void apply(MarketAPI market, String id, float level) {
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(id, FLEET_SIZE / 100f, skill_name);
        }

        public void unapply(MarketAPI market, String id) {
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(id);
        }

        public String getEffectDescription(float level) {
            //return "" + (int)Math.round(FLEET_SIZE) + "% larger fleets";
            return "+" + (int)Math.round(FLEET_SIZE) + "% fleet size";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }

    public static class Level3 implements MarketSkillEffect {
        public void apply(MarketAPI market, String id, float level) {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(id, 1f + DEFEND_BONUS * 0.01f, skill_name);
        }

        public void unapply(MarketAPI market, String id) {
            //market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyPercent(id);
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(id);
        }

        public String getEffectDescription(float level) {
            return "+" + (int)(DEFEND_BONUS) + "% effectiveness of ground defenses";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }

    public static class Level4 implements MarketSkillEffect {
        public void apply(MarketAPI market, String id, float level) {
            market.getStability().modifyFlat(id, STABILITY_BONUS, skill_name);
        }

        public void unapply(MarketAPI market, String id) {
            market.getStability().unmodifyFlat(id);
        }

        public String getEffectDescription(float level) {
            return "+" + (int)STABILITY_BONUS + " stability";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }
    
}
