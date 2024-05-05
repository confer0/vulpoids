package vulpoids.impl.campaign.intel.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.DefenderDataOverride;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.StarSystemType;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.EntityLocation;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vulpoids.impl.campaign.procgen.themes.UtopiaDefenderPlugin;

public class UtopiaTerraformerIntel extends BaseIntelPlugin {
    
    protected StarSystemAPI system;
    protected PlanetAPI planet;
    
    boolean beatFleet = false;
    boolean exploredTerraformer = false;
    boolean repairedTerraformer = false;
    long terraformerRepairTimestamp;
    static int DAYS_TO_REPAIR = 120;
    boolean returnedToTerraformer = false;
    boolean exploredBivouac = false;

    public UtopiaTerraformerIntel(TextPanelAPI text) {
        Global.getSector().getGenericPlugins().addPlugin(new UtopiaDefenderPlugin());
        
        generate(Global.getSector());
        
        Global.getSector().getIntelManager().addIntel(this, false, text);
        Global.getSector().getMemoryWithoutUpdate().set("$vulp_terraformingIntel", this);
    }
    
    @Override
    public String getIcon() {
        return "graphics/icons/missions/dead_drop.png";
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_STORY);
        tags.add(Tags.INTEL_EXPLORATION);
        tags.add(Tags.INTEL_ACCEPTED);
        tags.add(Tags.INTEL_MISSIONS);
        return tags;
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_2;
    }
    @Override
    public String getSortString() {
        return "Lost in the Dark";
    }
    @Override
    public String getName() {
        if (isEnded() || isEnding()) {
            return "Lost in the Dark - Completed";
        }
        return "Lost in the Dark";
    }
    @Override
    public String getSmallDescriptionTitle() {
            return getName();
    }
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("Laisa has calculated the location of a unique vessel operated by Eridani-Utopia before the Collapse. "+
                "The Fourteenth Battlegroup attempted to escort it to the Persean Sector, but it was thrown off course "+
                "for an unknown reason and plummeted into the Orion-Perseus Abyss.", 10f);
        if(!beatFleet) {
            info.addPara("Warning: The location is deep in abyssal hyperspace.", Misc.getNegativeHighlightColor(), 10f);
        } else {
            if(!exploredTerraformer) info.addPara("The massive vessel has been disabled in battle, and awaits boarding.", 10f);
            else if(!repairedTerraformer) info.addPara("The vessel is secure, but will require extensive repair before it can be flown.", 10f);
            else {
                if(!terraformerIsRepaired()) info.addPara("The terraforming vessel is currently being repaired by the crew you "+
                        "stationed aboard. It's expected to take another "+(int)(DAYS_TO_REPAIR-Global.getSector().getClock().getElapsedDaysSince(terraformerRepairTimestamp))+
                        " days.", 10f);
                else if (!returnedToTerraformer) info.addPara("The repairs to the ship should be completed. You should return and recover the crew.", 10f);
            }
            if(!exploredBivouac) info.addPara("The terraformed moon's defenders have been disabled, opening it for exploration.", 10f);
        }
    }
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return system.getHyperspaceAnchor();
    }
    
    private boolean terraformerIsRepaired() {
        return repairedTerraformer && Global.getSector().getClock().getElapsedDaysSince(terraformerRepairTimestamp)>=DAYS_TO_REPAIR;
    }
    
    @Override
    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = params.get(0).getString(memoryMap);
        switch(action) {
            case "beatFleet":
                beatFleet = true;
                break;
            case "exploredTerraformer":
                exploredTerraformer = true;
                break;
            case "startedTerraformerRepair":
                repairedTerraformer = true;
                terraformerRepairTimestamp = Global.getSector().getClock().getTimestamp();
                break;
            case "returnedToTerraformer":
                returnedToTerraformer = true;
                break;
            case "exploredBivouac":
                exploredBivouac = true;
                break;
        }
        if(returnedToTerraformer && exploredBivouac) endAfterDelay();
        return true;
    }
    
    protected void generate(SectorAPI sector) {
        system = sector.createStarSystem("Deep Space");
        system.setType(StarSystemType.NEBULA);
        system.setName("Deep Space"); // to get rid of "Star System" at the end of the name
        //system.setType(StarSystemType.DEEP_SPACE);
        system.addTag(Tags.THEME_UNSAFE);
        system.addTag(Tags.THEME_HIDDEN);
        system.addTag(Tags.THEME_SPECIAL);


        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

        float w = Global.getSettings().getFloat("sectorWidth");
        float h = Global.getSettings().getFloat("sectorHeight");
        //system.getLocation().set(-w/2f + 2300f, -h/2f + 2100f);
        system.getLocation().set(-w/3f, -h/2f + 1000f);


        SectorEntityToken center = system.initNonStarCenter();

        system.setLightColor(new Color(100,100,100,255)); // light color in entire system, affects all entities
        center.addTag(Tags.AMBIENT_LS);
        
        PlanetAPI giant = system.addPlanet("vulp_planetThreshold", null, "Threshold", "ice_giant", 0, 450, 0, 0);
        giant.getMemoryWithoutUpdate().set("$vulp_planetThreshold", true);
        giant.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
        giant.getMarket().addCondition(Conditions.VERY_COLD);
        giant.getMarket().addCondition(Conditions.DARK);
        giant.getMarket().addCondition(Conditions.VOLATILES_TRACE);
        giant.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        giant.setOrbit(null);
        giant.setLocation(0, 0);
        
        float moon_angle = 215;
        float moon_radius = 100;
        float moon_orbit = 900;
        float moon_orbit_days = 50;
        
        planet = system.addPlanet("vulp_planetBivouac", giant, "Bivouac", Planets.PLANET_TERRAN_ECCENTRIC, moon_angle, moon_radius, moon_orbit, moon_orbit_days);
        planet.getMemoryWithoutUpdate().set("$vulp_planetBivouac", true);
        planet.setCustomDescriptionId("vulp_bivouac");
        planet.getMarket().addCondition(Conditions.HABITABLE);
        planet.getMarket().addCondition(Conditions.MILD_CLIMATE);
        //planet.getMarket().addCondition(Conditions.VERY_COLD);
        //planet.getMarket().addCondition(Conditions.DARK);
        planet.getMarket().addCondition(Conditions.FARMLAND_BOUNTIFUL);
        planet.getMarket().addCondition(Conditions.ORGANICS_PLENTIFUL);
        planet.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SPEC_ID_OVERRIDE, "red_planet");
        Misc.setDefenderOverride(planet, new DefenderDataOverride(Factions.HEGEMONY, 1f, 20, 20, 1));
        
        EntityLocation loc = new EntityLocation();
        //float radius = planet.getRadius() + 100f;
        loc.orbit = Global.getFactory().createCircularOrbit(giant, moon_angle, moon_orbit-moon_radius-100, moon_orbit_days);
        BaseThemeGenerator.AddedEntity lamp = BaseThemeGenerator.addNonSalvageEntity(system, loc, Entities.FUSION_LAMP, Factions.NEUTRAL);
        lamp.entity.getMemoryWithoutUpdate().set("$core_lampGlowColor", new Color(255,100,255,175));
        lamp.entity.getMemoryWithoutUpdate().set("$core_lampLightColor", new Color(255,100,255,175));
        lamp.entity.getMemoryWithoutUpdate().set("$vulp_bivouacLamp", true);
        
        SectorEntityToken lagrange = system.addCustomEntity(null,null, "sensor_array",Factions.NEUTRAL);
        lagrange.getMemoryWithoutUpdate().set("$objectiveNonFunctional", true);
        lagrange.setCircularOrbitPointingDown(giant, moon_angle+60, moon_orbit, moon_orbit_days);
        
        system.autogenerateHyperspaceJumpPoints(true, false);
    }
}
