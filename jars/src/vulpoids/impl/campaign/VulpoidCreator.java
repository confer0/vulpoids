package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VulpoidCreator {
    
    public static PersonAPI createVulpoid(MarketAPI market) {
        return createVulpoid(market, false, false, false);
    }
    
    public static PersonAPI createNudeVulpoid(MarketAPI market) {
        return createVulpoid(market, true, false, false);
    }
    
    public static PersonAPI createSuitedVulpoid(MarketAPI market) {
        return createVulpoid(market, false, true, false);
    }
    
    public static PersonAPI createPrefectoVulpoid(MarketAPI market) {
        PersonAPI person;
        if(market == null) person = createRandomVulpoid(null);
        else person = createVulpoid(market);
        person.setName(Global.getSector().getPlayerFaction().createRandomPerson().getName()); // TODO
        person.getStats().setSkillLevel("vulpoid_brain", 1);
        person.getStats().setSkillLevel("vulpoid_luxury", 1);
        person.setRankId("vulp_profecto");
        if(person.getPortraitSprite().contains("winter")) person.getMemoryWithoutUpdate().set("$vulp_cargoIcon", Global.getSettings().getSpriteName("cargo", "vulp_shiny_winter"));
        if(person.getPortraitSprite().contains("terran")) person.getMemoryWithoutUpdate().set("$vulp_cargoIcon", Global.getSettings().getSpriteName("cargo", "vulp_shiny_terran"));
        if(person.getPortraitSprite().contains("desert")) person.getMemoryWithoutUpdate().set("$vulp_cargoIcon", Global.getSettings().getSpriteName("cargo", "vulp_shiny_desert"));
        return person;
    }
    
    public static PersonAPI createRandomVulpoid(MarketAPI market) {
        return createVulpoid(market, false, false, true);
    }
    
    public static PersonAPI createVulpoid(MarketAPI market, boolean force_nude, boolean force_suit, boolean randomize_climate) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        person.setPortraitSprite(getPortraitForMarket(market, force_nude, force_suit, randomize_climate));
        if(market != null) {
            person.setFaction(market.getFactionId());
        } else {
            person.setFaction(Factions.PLAYER);
        }
        person.setRankId("vulp_servant");
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
    
    public static Map<String, String> icon_sprites = new HashMap<String, String>() {{
        put("terran", "graphics/icons/cargo/vulpoid_shiny_terran.png");
        put("desert", "graphics/icons/cargo/vulpoid_shiny_desert.png");
        put("winter", "graphics/icons/cargo/vulpoid_shiny_winter.png");
    }};
    
    
    public static String getPortraitForMarket(MarketAPI market, boolean force_nude, boolean force_suit, boolean randomize_climate) {
        
        Map<String, String> default_sprites_for_selection = default_sprites;
        Map<String, String> suit_sprites_for_selection = suited_sprites;
        if(force_suit) {
            default_sprites_for_selection = suited_sprites;
        }
        if(force_nude) {
            default_sprites_for_selection = nude_sprites;
            suit_sprites_for_selection = nude_sprites;
        }
        
        String default_climate = "terran";
        if(randomize_climate) {
            Random r = new Random();
            ArrayList<String> climates = new ArrayList(default_sprites.keySet());
            default_climate = climates.get(r.nextInt(climates.size()));
        }
        
        if (market == null) {
            return default_sprites_for_selection.get(default_climate);
        }
        
        if (market.getPlanetEntity() == null) {
            // Station
            return suit_sprites_for_selection.get(default_climate);
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
