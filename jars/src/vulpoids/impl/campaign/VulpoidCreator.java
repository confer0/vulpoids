package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import java.util.Random;
import vulpoids.impl.campaign.ids.Vulpoids;

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
    
    public static PersonAPI createProfectoVulpoid(MarketAPI market) {
        PersonAPI person;
        if(market == null) person = createRandomVulpoid(null);
        else person = createVulpoid(market);
        //person.setName(Global.getSector().getPlayerFaction().createRandomPerson().getName());
        person.setName(Global.getSector().getFaction(Vulpoids.FACTION_EXODYNE).createRandomPerson().getName());
        person.setGender(FullName.Gender.FEMALE);
        person.getStats().setSkillLevel(Vulpoids.SKILL_ADMIN, 1);
        //person.getStats().setSkillLevel("vulpoid_luxury", 1);
        person.getStats().setSkillLevel(Vulpoids.SKILL_OFFICER, 1);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, 6);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, 2);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_SKILL_PICKS_PER_LEVEL, 10);
        person.setRankId(Vulpoids.RANK_PROFECTO);
        person.setPostId(null);
        person.getRelToPlayer().setRel(0.5f);
        
        
        return person;
    }
    
    public static PersonAPI createRandomVulpoid(MarketAPI market) {
        return createVulpoid(market, false, false);
    }
    
    public static PersonAPI createVulpoid(MarketAPI market, boolean force_nude, boolean force_suit) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        String portrait = getPortraitForMarket(market, force_nude, force_suit);
        person.setPortraitSprite(portrait);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, portrait);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_OFFICER_PORTRAIT, "graphics/portraits/vulpoid/spacer/military.png");
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_CARGO_ICON, getIcon(portrait));
        if(market != null) {
            person.setFaction(market.getFactionId());
        } else {
            person.setFaction(Factions.PLAYER);
        }
        person.setRankId(Vulpoids.RANK_SERVANT);
        person.setPostId(null);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_IS_VULPOID, true);
        person.getRelToPlayer().setRel(1);
        return person;
    }
    
    
    public static String getClimate(String portrait) {
        return portrait.split("/")[3]; // TODO
    }
    public static String setClimate(String portrait, String climate) {
        String[] split = portrait.split("/");
        String new_portrait = split[0];
        for(int i=1; i<split.length; i++) {
            if(i!=3) new_portrait += "/"+split[i];
            else new_portrait += "/"+climate;
        }
        return new_portrait;
    }
    public static void setDefaultClimate(PersonAPI person, String climate) {
        String portrait = setClimate(person.getPortraitSprite(), climate);
        person.setPortraitSprite(portrait);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, portrait);
    }
    
    public static String getIcon(String portrait) {
        switch(getClimate(portrait)) {
            case "terran": return "graphics/icons/cargo/vulpoids/vulpoid_shiny_terran.png";
            case "desert": return "graphics/icons/cargo/vulpoids/vulpoid_shiny_desert.png";
            case "arctic": return "graphics/icons/cargo/vulpoids/vulpoid_shiny_arctic.png";
            default: return "graphics/icons/cargo/vulpoids/vulpoid_shiny.png";
        }
    }
    
    public static boolean marketIsSuitMarket(MarketAPI market) {
        return market.getPlanetEntity() == null || !market.hasCondition(Conditions.HABITABLE);
    }
    
    public static String getPortraitForMarket(MarketAPI market, boolean force_nude, boolean force_suit) {
        String path = "graphics/portraits/vulpoid/";
        String expression = "default.png";
        if (!force_nude && force_suit) return path+"spacer/admin_no_atmos.png";
        String clothing = "/clothed/";
        if (force_nude) clothing = "/nude/";
        
        Random r = new Random();
        String[] climates = new String[]{"terran", "desert", "arctic"};
        String default_climate = climates[r.nextInt(climates.length)];
        
        if (market == null) return path+default_climate+clothing+expression;
        if (marketIsSuitMarket(market)) return path+"spacer/admin_no_atmos.png";
        return path+default_climate+clothing+expression;
        /*switch (market.getPlanetEntity().getSpec().getPlanetType()) {
            case "jungle": return path+"terran"+clothing+expression;
            case Planets.PLANET_TERRAN: return path+"terran"+clothing+expression;
            case Planets.DESERT: return path+"desert"+clothing+expression;
            case Planets.DESERT1: return path+"desert"+clothing+expression;
            case Planets.ARID: return path+"desert"+clothing+expression;
            case Planets.PLANET_WATER: return path+"terran"+clothing+expression;
            case Planets.TUNDRA: return path+"arctic"+clothing+expression;
            case Planets.PLANET_TERRAN_ECCENTRIC: return path+"arctic"+clothing+expression;
            default: return path+"terran"+clothing+expression;
        }*/
    }
}
