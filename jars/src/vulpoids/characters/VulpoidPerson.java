package vulpoids.characters;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonalityAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.rpg.Person;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidPerson extends Person {
    public final String[] PORTRAITS = {"graphics/portraits/vulpoid/base1.png", "graphics/portraits/vulpoid/base2.png", "graphics/portraits/vulpoid/base3.png"};
    // Using this as a trick to allow three different portraits to be rendered in the same frame.
    public static int PORTRAIT_INDEX = 0;
    
    public static final String BACKGROUND_DEFAULT = "default";
    public static final String BACKGROUND_HABITABLE = "habitable";
    
    public static final String EXPRESSION_ANGRY = "angry";
    public static final String EXPRESSION_BLUSH = "blush";
    public static final String EXPRESSION_BRUH = "bruh";
    public static final String EXPRESSION_CRY = "cry";
    public static final String EXPRESSION_DEFAULT = "default";
    public static final String EXPRESSION_FEAR = "fear";
    public static final String EXPRESSION_HELMET = "helmet";
    
    public static final String OUTFIT_TOP = "top/default";
    public static final String OUTFIT_SPACER = "spacer/default";
    public static final String OUTFIT_CRYO = "cryosleep/default";
    
    public static HashMap<String, BackgroundData> backgrounds;
    public static HashMap<String, OutfitData> outfits;
    public static OutfitData.Outfit getOutfitData(String fullName) {
        if(!fullName.contains("/")) return null;
        return outfits.get(fullName.split("/")[0]).colors.get(fullName.split("/")[1]);
    }
    public static HashMap<String, FurData> furColors;
    public static void loadConfigs() {
        try {
            Iterator<String> keys;
            backgrounds = new HashMap<>();
            JSONObject backgroundJson = Global.getSettings().loadJSON("graphics/portraits/vulpoid/backgrounds/backgrounds.json");
            keys = backgroundJson.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                backgrounds.put(key, new BackgroundData(backgroundJson.getJSONObject(key)));
            }
            outfits = new HashMap<>();
            JSONObject outfitJson = Global.getSettings().loadJSON("graphics/portraits/vulpoid/outfits/outfits.json");
            keys = outfitJson.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                outfits.put(key, new OutfitData(outfitJson.getJSONObject(key)));
            }
            furColors = new HashMap<>();
            JSONObject furJson = Global.getSettings().loadJSON("graphics/portraits/vulpoid/furs/furs.json");
            keys = furJson.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                furColors.put(key, new FurData(furJson.getJSONObject(key)));
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException("Error loading Vulpoid portrait data:\n"+ex.getMessage());
        }
    }
    
    
    private String background;
    private String backgroundOverride = null;
    private String furColor;
    private String hair;
    private String outfit;
    private String outfitOverride = null;
    private String tooltipOutfit = null;
    private String expression = EXPRESSION_DEFAULT;
    private String tooltipExpression = null;
    
    public VulpoidPerson(boolean profecto) {
        super();
        setUpVulpoid(null);
        if(profecto) setUpProfecto();
    }
    public VulpoidPerson(String personalityId, boolean profecto) {
        super(personalityId);
        setUpVulpoid(null);
        if(profecto) setUpProfecto();
    }
    public VulpoidPerson(boolean profecto, String presetFurColor) {
        super();
        setUpVulpoid(presetFurColor);
        if(profecto) setUpProfecto();
    }
    public VulpoidPerson(String personalityId, boolean profecto, String presetFurColor) {
        super(personalityId);
        setUpVulpoid(presetFurColor);
        if(profecto) setUpProfecto();
    }
    
    private void setUpVulpoid(String presetFurColor) {
        setName(new FullName("Vulpoid", "", FullName.Gender.FEMALE));
        Random random = new Random();
        FurData furData;
        if(presetFurColor==null) {
            do {
                furColor = (String) furColors.keySet().toArray()[random.nextInt(furColors.size())];
                furData = furColors.get(furColor);
            } while(furData.unique);
        } else {
            furColor = presetFurColor;
            furData = furColors.get(furColor);
        }
        setBackground(furData.defaultBackground);
        setOutfit(furData.defaultDress);
        setHair(furData.hairstyles.get(random.nextInt(furData.hairstyles.size())));
        setFaction(Factions.PLAYER);
        setRankId(Vulpoids.RANK_SERVANT);
        setPostId(null);
        getMemoryWithoutUpdate().set(Vulpoids.KEY_IS_VULPOID, true);
        getRelToPlayer().setRel(1);
    }
    private void setUpProfecto() {
        setName(Global.getSector().getFaction(Vulpoids.FACTION_EXODYNE).createRandomPerson().getName());
        setGender(FullName.Gender.FEMALE);
        setOutfit(furColors.get(furColor).defaultUniform);
        
        getStats().setSkillLevel(Vulpoids.SKILL_ADMIN, 1);
        getStats().setSkillLevel(Vulpoids.SKILL_OFFICER, 1);
        getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, 7);
        getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, 6);
        getMemoryWithoutUpdate().set(MemFlags.OFFICER_SKILL_PICKS_PER_LEVEL, 10);
        setRankId(Vulpoids.RANK_PROFECTO);
        setPostId(null);
        getRelToPlayer().setRel(0.5f);
    }
    
    
    public void setBackground(String s) {background = s;}
    public String getBackground() {return background;}
    public void setBackgroundOverride(String s) {backgroundOverride = s;}
    public String getBackgroundOverride() {return backgroundOverride;}
    public void setFurColor(String s) {furColor = s;}
    public String getFurColor() {return furColor;}
    public void setHair(String s) {hair = s;}
    public String getHair() {return hair;}
    public void setOutfit(String s) {outfit = s;}
    public String getOutfit() {return outfit;}
    public void setOutfitOverride(String s) {outfitOverride = s;}
    public String getOutfitOverride() {return outfitOverride;}
    public void setTooltipOutfit(String s) {tooltipOutfit = s;}
    public String getTooltipOutfit() {return tooltipOutfit;}
    public void setExpression(String s) {expression = s;}
    public String getExpression() {return expression;}
    public void setTooltipExpression(String s) {tooltipExpression = s;}
    public String getTooltipExpression() {return tooltipExpression;}
    
    public void setArbitraryProperty(String property, String s) {
        switch(property) {
            case "background" -> setBackground(s);
            case "backgroundOverride" -> setBackgroundOverride(s);
            case "furColor" -> setFurColor(s);
            case "hair" -> setHair(s);
            case "outfit" -> setOutfit(s);
            case "outfitOverride" -> setOutfitOverride(s);
            case "tooltipOutfit" -> setTooltipOutfit(s);
            case "expression" -> setExpression(s);
            case "tooltipExpression" -> setTooltipExpression(s);
        }
    }
    
    public String getInventoryIcon() {
        String path = furColors.get(furColor).cargoIcon;
        try {Global.getSettings().loadTexture(path);}
        catch (Exception ex) {}
        return path;
    }
    public static String getCodexIcon() {return "graphics/icons/cargo/vulpoids/vulpoid_desert.png";}
    
    public String getTooltipPortraitSprite() {
        String trueExpression = expression;
        if(tooltipExpression!=null) expression = tooltipExpression;
        String trueOutfitOverride = outfitOverride;
        if(tooltipOutfit!=null) outfitOverride = tooltipOutfit;
        String sprite = getPortraitSprite();
        expression = trueExpression;
        outfitOverride = trueOutfitOverride;
        return sprite;
    }
    
    @Override
    public String getPortraitSprite() {
        // This cycling mechanism allows three unique sprites to be loaded at a time.
        // We need to do it asynchronously like this because we can't identify a Vulpoid in the second or third person dialog slot.
        String PORTRAIT = PORTRAITS[PORTRAIT_INDEX];
        PORTRAIT_INDEX += 1;
        PORTRAIT_INDEX %= PORTRAITS.length;
        
        // For some deranged reason, this is required in order to be able to properly assign market admins. O_o
        setPortraitSprite(PORTRAIT);
        
        
        String backgroundToUse = backgroundOverride;
        if(backgroundToUse==null) backgroundToUse = background;
        if(!backgrounds.containsKey(backgroundToUse)) backgroundToUse = BACKGROUND_DEFAULT; // Failsafe
        BufferedImage backgroundImage = loadAndGetBufferedImage(backgrounds.get(backgroundToUse).file);
        
        FurData furColorToUse = furColors.get(furColor);
        if(furColorToUse==null) furColorToUse = furColors.get("desert");
        
        String hairToUse = hair;
        if(!furColorToUse.hairstyles.contains(hair)) hairToUse = furColorToUse.hairstyles.get(0);
        BufferedImage hairImage = loadAndGetBufferedImage(furColorToUse.directory+"/hairs/"+hairToUse+".png");
        
        // No safeties on this one, if an invalid expression gets called I want to know.
        String expressionToUse = expression;
        if(Global.getSector().getCampaignUI().getCurrentInteractionDialog()==null && tooltipExpression!=null) expressionToUse = tooltipExpression;
        BufferedImage expressionImage = loadAndGetBufferedImage(furColorToUse.directory+"/expressions/"+expressionToUse+".png");
        
        String outfitToUse = outfitOverride;
        if(outfitToUse==null) outfitToUse = outfit;
        if(Global.getSector().getCampaignUI().getCurrentInteractionDialog()==null && tooltipOutfit!=null) outfitToUse = tooltipOutfit;
        OutfitData.Outfit outfitData = getOutfitData(outfitToUse);
        if(outfitData==null) outfitData = getOutfitData(OUTFIT_TOP);
        BufferedImage outfitImage = loadAndGetBufferedImage(outfitData.file);
        
        Graphics2D g2d = backgroundImage.createGraphics();
        g2d.drawImage(hairImage, null, 0, 0);
        g2d.drawImage(expressionImage, null, 0, 0);
        g2d.drawImage(outfitImage, null, 0, 0);
        
        writeBufferedImage(backgroundImage, PORTRAIT);
        
        
        return PORTRAIT;
    }
    
    public void applyColorMask(BufferedImage image, Color color) {
        for (int x=0; x<image.getWidth(); x++) {
            for (int y=0; y<image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y), true);
                int r = pixel.getRed()*color.getRed() / 255;
                int g = pixel.getGreen()*color.getGreen() / 255;
                int b = pixel.getBlue()*color.getBlue() / 255;
                int a = pixel.getAlpha();
                image.setRGB(x, y, new Color(r,g,b,a).getRGB());
            }
        }
    }
    
    
    
    public BufferedImage loadAndGetBufferedImage(String name) {
        try {Global.getSettings().loadTexture(name);}
        catch (Exception ex) {throw new RuntimeException("Failed to load Vulpoid portrait image: "+name);}
        return getBufferedImage(name);
    }
    
    /*
    Loads the given file path into a buffered image.
    If the image hasn't already been loaded by Starsector, it also loads it on the fly.
    So now we don't need to maintain a list in the settings file!
    */
    public BufferedImage getBufferedImage(String sprite_id) {
        SpriteAPI sprite = Global.getSettings().getSprite(sprite_id);
        int texture_id = sprite.getTextureId();
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_id);
        int format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        int channels = 4;
        if(format == GL11.GL_RGB) channels = 3;
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(width*height*channels);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, format, GL11.GL_UNSIGNED_BYTE, buffer);
        
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                int i = (x+y*width) * channels;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i+1) & 0xFF;
                int b = buffer.get(i+2) & 0xFF;
                int a = 255;
                if(channels==4) {
                    a = buffer.get(i+3) & 0xFF;
                }
                image.setRGB(x, y, (a<<24) | (r<<16) | (g<<8) | b);
            }
        }
        return image;
    }
    
    public void writeBufferedImage(BufferedImage image, String destination_sprite_id) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width*height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        ByteBuffer buffer2 = BufferUtils.createByteBuffer(width*height*4);
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int pixel = pixels[x+y*width];
                buffer2.put((byte) ((pixel>>16) & 0xFF));
                buffer2.put((byte) ((pixel>>8) & 0xFF));
                buffer2.put((byte) ((pixel) & 0xFF));
                buffer2.put((byte) ((pixel>>24) & 0xFF));
            }
        }
        buffer2.flip();
        int destination_id = Global.getSettings().getSprite(destination_sprite_id).getTextureId();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, destination_id);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer2);
    }
    
    
    
    
    String cachedPersonality = "steady";
    @Override
    public PersonalityAPI getPersonalityAPI() {
        PersonalityAPI test = super.getPersonalityAPI();
        // TODO - Figure out what causes this, and fix it
        if(test==null) {
            // I've got no idea why it's _this_ convoluted, but this is what's needed to preserve the personality between reloads.
            if(cachedPersonality==null) super.setPersonality("steady");
            else super.setPersonality(cachedPersonality);
        }
        return super.getPersonalityAPI();
    }
    @Override
    public void setPersonality(String string) {
        cachedPersonality = string;
        super.setPersonality(string);
    }
    
    
    public static class BackgroundData {
        public String name;
        public String file;
        public boolean pickable;
        public BackgroundData(JSONObject params) throws JSONException {
            name = params.getString("name");
            file = params.getString("file");
            pickable = params.getBoolean("pickable");
        }
    }
    public static class OutfitData {
        public String name;
        public boolean pickable = true;
        public HashMap<String, Outfit> colors;
        public OutfitData(JSONObject params) throws JSONException {
            name = params.getString("name");
            if(params.has("pickable")) pickable = params.getBoolean("pickable");
            JSONObject colorsJson = params.getJSONObject("colors");
            colors = new HashMap<>();
            Iterator<String> keys = colorsJson.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                colors.put(key, new Outfit(colorsJson.getJSONObject(key)));
            }
        }
        public static class Outfit {
            public String name;
            public Color color;
            public String file;
            public boolean pickable;
            public Outfit(JSONObject params) throws JSONException {
                name = params.getString("name");
                color = Color.decode(params.getString("color"));
                file = params.getString("file");
                pickable = params.getBoolean("pickable");
            }
        }
    }
    public static class FurData {
        public String name;
        public String directory;
        public String cargoIcon;
        public boolean unique;
        public String defaultBackground;
        public String defaultUniform;
        public String defaultDress;
        public ArrayList<String> hairstyles;
        public FurData(JSONObject params) throws JSONException {
            name = params.getString("name");
            directory = params.getString("directory");
            cargoIcon = params.getString("cargoIcon");
            unique = params.getBoolean("unique");
            defaultBackground = params.getString("defaultBackground");
            defaultUniform = params.getString("defaultUniform");
            defaultDress = params.getString("defaultDress");
            hairstyles = new ArrayList<>();
            JSONArray hairstylesJson = params.getJSONArray("hairstyles");
            for(int i=0;i<hairstylesJson.length();i++) hairstyles.add(hairstylesJson.getString(i));
        }
    }
}
