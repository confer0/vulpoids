package vulpoids.impl.campaign.intel.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
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
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Set;
import vulpoids.impl.campaign.procgen.themes.UtopiaDefenderPlugin;

public class UtopiaTerraformerIntel extends BaseIntelPlugin {
    
    public static enum Stage {
        TALK_TO_PILOT,
        GO_TO_PLANET,
        DONE,
    }
    
    protected StarSystemAPI system;
    protected PlanetAPI planet;

    protected Stage stage;

    public UtopiaTerraformerIntel(TextPanelAPI text) {
        Global.getSector().getGenericPlugins().addPlugin(new UtopiaDefenderPlugin());
        
        generate(Global.getSector());
        
        Global.getSector().getIntelManager().addIntel(this, false, text);
    }
    
    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "red_planet");
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

    public String getSortString() {
        return "Red Planet";
    }

    public String getName() {
        if (isEnded() || isEnding()) {
            return "Red Planet - Completed";
        }
        return "Red Planet";
    }

    public String getSmallDescriptionTitle() {
            return getName();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return system.getHyperspaceAnchor();
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
        planet.setCustomDescriptionId("limbo_hades");
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
        
        system.autogenerateHyperspaceJumpPoints(true, false);
    }
}
