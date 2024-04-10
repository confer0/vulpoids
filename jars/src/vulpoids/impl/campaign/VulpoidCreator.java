package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import java.util.Random;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidCreator {    
    
    public static PersonAPI createProfectoVulpoid() {
        PersonAPI person;
        person = createVulpoid();
        //person.setName(Global.getSector().getPlayerFaction().createRandomPerson().getName());
        person.setName(Global.getSector().getFaction(Vulpoids.FACTION_EXODYNE).createRandomPerson().getName());
        person.setGender(FullName.Gender.FEMALE);
        setPersonPortraitPropertyAtIndex(person, INDEX_CLOTHING, CLOTHING_CLOTHED);
        person.getStats().setSkillLevel(Vulpoids.SKILL_ADMIN, 1);
        //person.getStats().setSkillLevel("vulpoid_luxury", 1);
        person.getStats().setSkillLevel(Vulpoids.SKILL_OFFICER, 1);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, 6);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, 3);
        person.getMemoryWithoutUpdate().set(MemFlags.OFFICER_SKILL_PICKS_PER_LEVEL, 10);
        person.setRankId(Vulpoids.RANK_PROFECTO);
        person.setPostId(null);
        person.getRelToPlayer().setRel(0.5f);
        
        //person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, person.getPortraitSprite());
        //person.getMemoryWithoutUpdate().set(Vulpoids.KEY_OFFICER_PORTRAIT, "graphics/portraits/vulpoid/spacer/military.png");
        //person.getMemoryWithoutUpdate().set(Vulpoids.KEY_CARGO_ICON, getIcon(person.getPortraitSprite()));
        
        return person;
    }
    
    public static PersonAPI createVulpoid() {
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        String portrait = getRandomClimatePortrait(CLOTHING_NUDE);
        person.setPortraitSprite(portrait);
        person.setFaction(Factions.PLAYER);
        person.setRankId(Vulpoids.RANK_SERVANT);
        person.setPostId(null);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_IS_VULPOID, true);
        person.getRelToPlayer().setRel(1);
        return person;
    }
    
    public static final int INDEX_CLIMATE = 3;
    public static final int INDEX_CLOTHING = 4;
    public static final int INDEX_EXPRESSION = 5;
    
    public static final String CLIMATE_TERRAN = "terran";
    public static final String CLIMATE_DESERT = "desert";
    public static final String CLIMATE_ARCTIC = "arctic";
    public static final String CLIMATE_SUGAR = "sugar";
    public static final String CLIMATE_LAISA = "laisa";
    static String[] random_climates = new String[]{
        CLIMATE_TERRAN,
        CLIMATE_DESERT,
        CLIMATE_ARCTIC,
        CLIMATE_SUGAR,
    };
    
    public static final String CLOTHING_CLOTHED = "clothed";
    public static final String CLOTHING_NUDE = "nude";
    public static final String CLOTHING_SUIT = "spacer";
    
    public static final String EXPRESSION_ANGRY = "angry";
    public static final String EXPRESSION_BLUSH = "blush";
    public static final String EXPRESSION_BRUH = "bruh";
    public static final String EXPRESSION_CRY = "cry";
    public static final String EXPRESSION_DEFAULT = "default";
    public static final String EXPRESSION_FEAR = "fear";
    public static final String EXPRESSION_HELMET = "helmet";
    public static final String EXPRESSION_OFFICER = "military";
    public static final String EXPRESSION_FROZEN = "popsicle";
    
    
    public static String getIcon(String portrait) {
        return "graphics/icons/cargo/vulpoids/vulpoid_"+getPortraitPropertyAtIndex(portrait, INDEX_CLIMATE)+".png";
    }
    
    public static String getPortraitPropertyAtIndex(String portrait, int index) {
        return portrait.split("/")[index];
    }
    public static String setPortraitPropertyAtIndex(String portrait, int index, String property) {
        String[] split = portrait.split("/");
        String new_portrait = split[0];
        for(int i=1; i<split.length; i++) {
            if(i!=index) new_portrait += "/"+split[i];
            else new_portrait += "/"+property;
        }
        // Just making things easier for myself.
        // We can use this to set expressions without needing the .png extension.
        if(index==split.length-1 && !property.contains(".png")) new_portrait += ".png";
        return new_portrait;
    }
    public static void setPersonPortraitPropertyAtIndex(PersonAPI person, int index, String property) {
        person.setPortraitSprite(setPortraitPropertyAtIndex(person.getPortraitSprite(), index, property));
    }
    
    public static String getClimate(String portrait) {return getPortraitPropertyAtIndex(portrait, INDEX_CLIMATE);}
    public static String setClimate(String portrait, String climate) {
        String[] split = portrait.split("/");
        String new_portrait = split[0];
        for(int i=1; i<split.length; i++) {
            if(i!=3) new_portrait += "/"+split[i];
            else new_portrait += "/"+climate;
        }
        return new_portrait;
    }
    /*public static void setDefaultClimate(PersonAPI person, String climate) {
        String portrait = setClimate(person.getPortraitSprite(), climate);
        person.setPortraitSprite(portrait);
        person.getMemoryWithoutUpdate().set(Vulpoids.KEY_DEFAULT_PORTRAIT, portrait);
    }*/
    public static String getPersonDefaultExpression(PersonAPI person) {
        String default_expression = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_EXPRESSION);
        if(default_expression == null) default_expression = EXPRESSION_DEFAULT;
        return default_expression;
    }
    
    public static boolean marketIsSuitMarket(MarketAPI market) {
        return market.getPlanetEntity() == null || !market.hasCondition(Conditions.HABITABLE);
    }
    
    public static String getRandomClimatePortrait() {
        return getRandomClimatePortrait(CLOTHING_CLOTHED);
    }
    
    public static String getRandomClimatePortrait(String clothing) {
         String path = "graphics/portraits/vulpoid/";
         String expression = "/default.png";
         String climate = random_climates[new Random().nextInt(random_climates.length)];
         return path+climate+"/"+clothing+expression;
    }
    
    public static String getSpecificClimate(MarketAPI market) {
        if(market.hasCondition(Conditions.HOT) || market.hasCondition(Conditions.VERY_HOT)) return CLIMATE_DESERT;
        if(market.hasCondition(Conditions.COLD) || market.hasCondition(Conditions.VERY_COLD)) return CLIMATE_ARCTIC;
        return CLIMATE_TERRAN;
    }
}
