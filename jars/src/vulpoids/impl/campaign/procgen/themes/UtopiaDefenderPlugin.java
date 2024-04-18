package vulpoids.impl.campaign.procgen.themes;

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
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
        fleet.setName("Remnants of Task Force Safeguard");

        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);

        fleet.getFleetData().clear();
        fleet.getFleetData().setShipNameRandom(random);


        FleetMemberAPI member = fleet.getFleetData().addFleetMember("legion_xiv_Automated");
        //member.setShipName("HSS Sentinel");
        member.setId("xivtf_" + random.nextLong());
        PersonAPI person = plugin.createPerson(Commodities.ALPHA_CORE, fleet.getFaction().getId(), random);
        person.getStats().setSkipRefresh(true);
        person.getStats().setSkillLevel(Skills.CARRIER_GROUP, 1);
        person.getStats().setSkillLevel(Skills.FIGHTER_UPLINK, 1);
        person.getStats().setSkipRefresh(false);

        member.setCaptain(person);
        ShipVariantAPI v = member.getVariant().clone();
        v.setSource(VariantSource.REFIT);
        v.addTag(Tags.TAG_NO_AUTOFIT);
        v.addTag(Tags.TAG_AUTOMATED_NO_PENALTY);
        member.setVariant(v, false, true);
        fleet.setCommander(person);

        /*addAutomated(fleet, "onslaught_xiv_Elite", null, Commodities.ALPHA_CORE, random);

        addAutomated(fleet, "dominator_XIV_Elite", null, Commodities.BETA_CORE, random);
        addAutomated(fleet, "eagle_xiv_Elite", null, Commodities.BETA_CORE, random);
        addAutomated(fleet, "falcon_xiv_Elite", null, Commodities.BETA_CORE, random);
        addAutomated(fleet, "falcon_xiv_Escort", null, Commodities.BETA_CORE, random);

        addAutomated(fleet, "enforcer_XIV_Elite", null, Commodities.GAMMA_CORE, random);
        addAutomated(fleet, "enforcer_XIV_Elite", null, Commodities.GAMMA_CORE, random);
        addAutomated(fleet, "enforcer_XIV_Elite", null, Commodities.GAMMA_CORE, random);*/


        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            //makeAICoreSkillsGoodForLowTech(curr, true);
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
        }

        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            v = curr.getVariant().clone();
            v.setSource(VariantSource.REFIT);
            curr.setVariant(v, false, false);
        }

        if (fleet.getInflater() instanceof DefaultFleetInflater) {
            DefaultFleetInflater dfi = (DefaultFleetInflater) fleet.getInflater();
            DefaultFleetInflaterParams dfip = (DefaultFleetInflaterParams)dfi.getParams();
            dfip.allWeapons = true;
            dfip.averageSMods = 3;
            dfip.quality = 0.4f;

            // what a HACK
            DModManager.assumeAllShipsAreAutomated = true;
            fleet.inflateIfNeeded();
            fleet.setInflater(null);
            DModManager.assumeAllShipsAreAutomated = false;
        }

        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getVariant().addPermaMod(HullMods.AUTOMATED);
            curr.getVariant().setVariantDisplayName("Automated");
            curr.getVariant().addTag(Tags.TAG_AUTOMATED_NO_PENALTY);
            curr.getVariant().addTag(Tags.VARIANT_UNRESTORABLE);
            curr.getVariant().addTag(Tags.TAG_RETAIN_SMODS_ON_RECOVERY);
            if (curr.isCapital()) {
                curr.getVariant().addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
            }
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
