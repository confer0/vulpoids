package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import vulpoids.impl.campaign.ids.Vulpoids;

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
        //person.getStats().setSkillLevel("vulpoid_luxury", 1);
        person.getStats().setSkillLevel("vulpoid_officer", 1);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, 6);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, 2);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_SKILL_PICKS_PER_LEVEL, 10);
        person.setRankId("vulp_profecto");
        
        person.getStats().setLevel(5);
        
        
        return person;
    }
    
    public static PersonAPI createRandomVulpoid(MarketAPI market) {
        return createVulpoid(market, false, false, true);
    }
    
    public static PersonAPI createVulpoid(MarketAPI market, boolean force_nude, boolean force_suit, boolean randomize_climate) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        String portrait = getPortraitForMarket(market, force_nude, force_suit, randomize_climate);
        person.setPortraitSprite(portrait);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, portrait);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_OFFICER_PORTRAIT, "graphics/portraits/vulpoid/spacer/military.png");
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_CARGO_ICON, getIcon(portrait));
        if(market != null) {
            person.setFaction(market.getFactionId());
        } else {
            person.setFaction(Factions.PLAYER);
        }
        person.setRankId("vulp_servant");
        person.setPostId(null);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_IS_VULPOID, true);
        person.getRelToPlayer().setRel(1);
        return person;
    }
    
    
    public static String getClimate(String portrait) {
        return portrait.split("/")[3]; // TODO
    }
    
    public static String getIcon(String portrait) {
        switch(getClimate(portrait)) {
            case "terran": return "graphics/icons/cargo/vulpoids/vulpoid_shiny_terran.png";
            case "desert": return "graphics/icons/cargo/vulpoids/vulpoid_shiny_desert.png";
            case "arctic": return "graphics/icons/cargo/vulpoids/vulpoid_shiny_arctic.png";
            default: return "graphics/icons/cargo/vulpoids/vulpoid_shiny.png";
        }
    }
    
    public static String getPortraitForMarket(MarketAPI market, boolean force_nude, boolean force_suit, boolean randomize_climate) {
        String path = "graphics/portraits/vulpoid/";
        String expression = "default.png";
        if (!force_nude && force_suit) return path+"spacer/admin_no_atmos.png";
        String clothing = "/clothed/";
        if (force_nude) clothing = "/nude/";
        
        String default_climate = "terran";
        if(randomize_climate) {
            Random r = new Random();
            String[] climates = new String[]{"terran", "desert", "arctic"};
            default_climate = climates[r.nextInt(climates.length)];
        }
        
        if (market == null) {
            return path+default_climate+clothing+expression;
        }
        
        if (market.getPlanetEntity() == null || !market.hasCondition(Conditions.HABITABLE)) {
            return path+"spacer/admin_no_atmos.png";
        } else {
            switch (market.getPlanetEntity().getSpec().getPlanetType()) {
                case "jungle": return path+"terran"+clothing+expression;
                case Planets.PLANET_TERRAN: return path+"terran"+clothing+expression;
                case Planets.DESERT: return path+"desert"+clothing+expression;
                case Planets.DESERT1: return path+"desert"+clothing+expression;
                case Planets.ARID: return path+"desert"+clothing+expression;
                case Planets.PLANET_WATER: return path+"terran"+clothing+expression;
                case Planets.TUNDRA: return path+"arctic"+clothing+expression;
                case Planets.PLANET_TERRAN_ECCENTRIC: return path+"arctic"+clothing+expression;
                default: return path+"terran"+clothing+expression;
            }
        }
    }
}
