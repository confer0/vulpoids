package vulpoids.impl.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LobsterBioforgeMission extends HubMissionWithSearch {
    
    static int NUM_ASTEROIDS = 6;
    
    protected int num_asteroids_explored = 0;
    
    public static enum Stage {
        OPIS,
        VOLTURN,
        GOT_FORGE,
    }
    
    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        Global.getSector().getMemoryWithoutUpdate().set("$" + getMissionId() + "_ref", this);
        
        setRepRewardPerson(0f);
        setRepRewardFaction(0f);
        
        addAsteroids();
        
        setStoryMission();
        
        setStartingStage(Stage.OPIS);
        setSuccessStage(Stage.GOT_FORGE);
        
        return true;
    }
    
    private void addAsteroids() {
        SectorEntityToken salus = getSalus();
        
        for(int i=0; i<NUM_ASTEROIDS; i++) {
            AsteroidAPI asteroid = salus.getStarSystem().addAsteroid(25f);
            asteroid.setFacing(new Random().nextFloat() * 360f);
            asteroid.setCircularOrbit(salus, i*360f/NUM_ASTEROIDS, 1100, 40);
            asteroid.setRotation(50);
            asteroid.setName("Opis Asteroid");
            asteroid.setCustomDescriptionId("opis_asteroid");
            asteroid.getMemoryWithoutUpdate().set("$vulp_OpisAsteroid", true);
            asteroid.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
            asteroid.addTag(Tags.HAS_INTERACTION_DIALOG);
        }
    }
    
    private SectorEntityToken getSalus() {
        SectorEntityToken salus = Global.getSector().getEntityById("salus");
        if(salus==null) salus = getVolturnInRandomSector();
        return salus;
    }
    
    private SectorEntityToken getVolturn() {
        SectorEntityToken volturn = Global.getSector().getEntityById("volturn");
        if(volturn==null) volturn = getVolturnInRandomSector();
        return volturn;
    }
    
    private SectorEntityToken getVolturnInRandomSector() {
        SectorEntityToken volturn = Global.getSector().getEntityById("volturn");
        // Random Sector support
        if(volturn==null) {
            // If it's got lobster pens, it's the one. Otherwise, first one with a water surface.
            for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                if(market.hasCondition(Conditions.VOLTURNIAN_LOBSTER_PENS)) {
                    volturn = market.getPrimaryEntity();
                    break;
                }
                if(volturn==null && market.hasCondition(Conditions.WATER_SURFACE)) {
                    volturn = market.getPrimaryEntity();
                }
            }
            // Pretty sure this shouldn't happen, but if it does, just pick something.
            if(volturn==null) volturn = Global.getSector().getEconomy().getMarketsCopy().get(0).getPrimaryEntity();
        }
        return volturn;
    }
    
    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        info.addPara("Your Profecto Vulpoids have discovered that the Volturnian Lobster was an Exodyne product, and the bioforge should still be somewhere in Volturn's oceans.", opad);
        if (currentStage == Stage.OPIS) {
            info.addPara("A record of the bioforge's location should be present on datacores on the moon Opis. Unfortunately, said moon is currently a ring of debris around Salus.", opad);
            info.addPara("Some larger fragments of the moon may still have intact enough facilities to obtain the data you need. Explore the ring system and look for large asteroids", opad);
        } else if (currentStage == Stage.VOLTURN) {
            info.addPara("You've managed to discover the location of the bioforge's subsea facility. You can send a team to retrieve it at any time.", opad);
            info.addPara("The Diktat will become aware of your operation as soon as you launch shuttles. Some marines will be required to protect your salvors from the Diktat's fast-response units.", opad);
        }
    }
    
    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        if(currentStage == Stage.OPIS) {
            info.addPara("Explore the Opis ring for any surviving records.", tc, pad);
            return true;
        }
        if(currentStage == Stage.VOLTURN) {
            info.addPara("Launch a raid on Volturn to extract the Bioforge.", tc, pad);
            return true;
        }
        return false;
    }
    
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if(currentStage == Stage.OPIS) return getSalus();
        if(currentStage == Stage.VOLTURN) return getVolturn();
        return super.getMapLocation(map);
    }
    
    @Override
    public String getBaseName() {
        return "Something Fishy";
    }
    
    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }
    
    @Override
    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = params.get(0).getString(memoryMap);
        switch(action) {
            case "isAsteroidNumber":
                return num_asteroids_explored+1 == params.get(1).getInt(memoryMap);
            case "exploreAsteroid":
                num_asteroids_explored++;
                return true;
            case "foundCoordinates":
                setCurrentStage(Stage.VOLTURN, dialog, memoryMap);
                getVolturn().getMarket().getMemoryWithoutUpdate().set("$vulp_lobsterBioforgeRaidTarget", true);
                return true;
            case "finish":
                setCurrentStage(Stage.GOT_FORGE, dialog, memoryMap);
                return true;
        }
        return super.callEvent(ruleId, dialog, params, memoryMap);
    }
}
