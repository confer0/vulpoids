package vulpoids.impl.campaign.procgen.themes;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import java.util.Random;

public class UtopiaDefenderPlugin extends BaseGenericPlugin implements SalvageDefenderModificationPlugin {

    @Override
    public float getStrength(SalvageGenFromSeed.SDMParams p, float strength, Random random, boolean withOverride) {return strength;}
    @Override
    public float getProbability(SalvageGenFromSeed.SDMParams p, float probability, Random random, boolean withOverride) {return probability;}
    @Override
    public float getQuality(SalvageGenFromSeed.SDMParams p, float quality, Random random, boolean withOverride) {return quality;}
    @Override
    public float getMaxSize(SalvageGenFromSeed.SDMParams p, float maxSize, Random random, boolean withOverride) {return maxSize;}
    @Override
    public float getMinSize(SalvageGenFromSeed.SDMParams p, float minSize, Random random, boolean withOverride) {return minSize;}
    @Override
    public void reportDefeated(SalvageGenFromSeed.SDMParams p, SectorEntityToken entity, CampaignFleetAPI fleet) {}
    
    
    @Override
    public void modifyFleet(SalvageGenFromSeed.SDMParams p, CampaignFleetAPI fleet, Random random, boolean withOverride) {
        Misc.addDefeatTrigger(fleet, "UtopiaTerraformerDefeated");

        fleet.setNoFactionInName(true);
        fleet.setName("Fourteenth Battlegroup Task Force");
        
        fleet.getFleetData().clear();
        fleet.getFleetData().setShipNameRandom(random);
        
        //Zig captain from TTBlackSite.
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Motes", "", FullName.Gender.ANY));
        person.setFaction(Factions.NEUTRAL);
        //person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "ziggurat_captain"));
        //person.setPortraitSprite(Global.getSettings().getSpriteName("misc", "default_portrait"));
        person.setPortraitSprite("graphics/portraits/portrait_hegemony06.png");
        person.setPersonality(Personalities.RECKLESS);
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId(null);
        person.getStats().setSkipRefresh(true);
        person.getStats().setLevel(10);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
        person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
        person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
        person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);  //Changed from energy weapon, obvs.
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
        person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
        person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
        person.getStats().setSkillLevel(Skills.NAVIGATION, 1);
        person.getStats().setSkipRefresh(false);
        
        FleetMemberAPI terraformer = fleet.getFleetData().addFleetMember("vulp_terraformer_Standard");
        terraformer.setCaptain(person);
        terraformer.setShipName("EUS Muscascus");
        ShipVariantAPI terraformerVariant = terraformer.getVariant().clone();
        terraformerVariant.setSource(VariantSource.REFIT);
        terraformerVariant.addTag(Tags.TAG_NO_AUTOFIT);
        terraformerVariant.addTag(Tags.SHIP_LIMITED_TOOLTIP);
        terraformerVariant.addTag(Tags.VARIANT_UNBOARDABLE);
        terraformer.setVariant(terraformerVariant, false, true);
        fleet.getFleetData().addFleetMember("legion_xiv_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("legion_xiv_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("onslaught_xiv_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("onslaught_xiv_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("dominator_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("dominator_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("dominator_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("falcon_xiv_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("falcon_xiv_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("enforcer_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("enforcer_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("enforcer_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("enforcer_XIV_Elite").setCaptain(person);
        fleet.getFleetData().addFleetMember("enforcer_XIV_Elite").setCaptain(person);
        
        fleet.setCommander(person);
        
        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
        }

        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            ShipVariantAPI v = curr.getVariant().clone();
            v.setSource(VariantSource.REFIT);
            curr.setVariant(v, false, false);
        }

        if (fleet.getInflater() instanceof DefaultFleetInflater) {
            DefaultFleetInflater dfi = (DefaultFleetInflater) fleet.getInflater();
            DefaultFleetInflaterParams dfip = (DefaultFleetInflaterParams)dfi.getParams();
            dfip.allWeapons = true;
            dfip.averageSMods = 5;
            dfip.quality = 2;
            fleet.inflateIfNeeded();
            fleet.setInflater(null);
        }

        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getVariant().setVariantDisplayName("Ancient");
            //curr.getVariant().addTag(Tags.TAG_RETAIN_SMODS_ON_RECOVERY);
            //if (curr.isCapital()) {
            //    curr.getVariant().addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
            //}
        }
    }
    
    
    @Override
    public int getHandlingPriority(Object params) {
        if (!(params instanceof SDMParams)) return 0;
        SDMParams p = (SDMParams) params;

        if (p.entity != null && p.entity.getMemoryWithoutUpdate().contains("$vulp_planetBivouac")) {
            return 2;
        }
        return -1;
    }
}
