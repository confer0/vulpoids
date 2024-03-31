package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import java.util.HashMap;
import java.util.Map;

public class VulpoidCreator {
    
    public static PersonAPI createVulpoid(MarketAPI market) {
        return createVulpoid(market, false, false);
    }
    
    public static PersonAPI createNudeVulpoid(MarketAPI market) {
        return createVulpoid(market, true, false);
    }
    
    public static PersonAPI createSuitedVulpoid(MarketAPI market) {
        return createVulpoid(market, false, true);
    }
    
    public static PersonAPI createVulpoid(MarketAPI market, boolean force_nude, boolean force_suit) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setPortraitSprite(getPortraitForMarket(market, force_nude, force_suit));
        if(market != null) {
            person.setFaction(market.getFactionId());
        }
        person.setRankId(null);
        person.setPostId(null);
        person.getMemoryWithoutUpdate().set("$isVulpoid", true);
        person.getRelToPlayer().setRel(1);
        return person;
    }
    
    public static Map<String, String> default_sprites = new HashMap<String, String>() {{
        put("terran", "graphics/portraits/terran_fox.png");
        put("desert", "graphics/portraits/desert_fox.png");
        put("winter", "graphics/portraits/winter_fox.png");
    }};
    public static Map<String, String> nude_sprites = new HashMap<String, String>() {{
        put("terran", "graphics/portraits/nude_terran_fox.png");
        put("desert", "graphics/portraits/nude_desert_fox.png");
        put("winter", "graphics/portraits/nude_winter_fox.png");
    }};
    public static Map<String, String> suited_sprites = new HashMap<String, String>() {{
        put("terran", "graphics/portraits/space_terran_fox.png");
        put("desert", "graphics/portraits/space_desert_fox.png");
        put("winter", "graphics/portraits/space_winter_fox.png");
    }};
    
    
    public static String getPortraitForMarket(MarketAPI market, boolean force_nude, boolean force_suit) {
        
        Map<String, String> default_sprites_for_selection = default_sprites;
        Map<String, String> suit_sprites_for_selection = suited_sprites;
        if(force_suit) {
            default_sprites_for_selection = suited_sprites;
        }
        if(force_nude) {
            default_sprites_for_selection = nude_sprites;
            suit_sprites_for_selection = nude_sprites;
        }
        
        if (market == null) {
            return default_sprites_for_selection.get("terran");
        }
        
        if (market.getPlanetEntity() == null) {
            // Station
            return suit_sprites_for_selection.get("terran");
        } else {
            //Planets
            if (market.hasCondition(Conditions.HABITABLE)) {
                switch (market.getPlanetEntity().getSpec().getPlanetType()) {
                    case "jungle":
                        return default_sprites_for_selection.get("terran");
                    case Planets.PLANET_TERRAN:
                        return default_sprites_for_selection.get("terran");
                    case Planets.DESERT:
                        return default_sprites_for_selection.get("desert");
                    case Planets.DESERT1:
                        return default_sprites_for_selection.get("desert");
                    case Planets.ARID:
                        return default_sprites_for_selection.get("desert");
                    case Planets.PLANET_WATER:
                        return default_sprites_for_selection.get("terran");
                    case Planets.TUNDRA:
                        return default_sprites_for_selection.get("winter");
                    case Planets.PLANET_TERRAN_ECCENTRIC:
                        return default_sprites_for_selection.get("winter");
                    default:
                        return default_sprites_for_selection.get("terran");
                }
            } else if (market.hasCondition(Conditions.VERY_HOT)) {
                return suit_sprites_for_selection.get("desert");
            } else if (market.hasCondition(Conditions.VERY_COLD)) {
                return suit_sprites_for_selection.get("winter");
            } else {
                return suit_sprites_for_selection.get("terran");
            }
        }
    }
}
