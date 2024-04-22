package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class VulpoidTerraformer extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        
        switch(params.get(0).getString(memoryMap)) {
            case "giveIntel":
                new vulpoids.impl.campaign.intel.misc.UtopiaTerraformerIntel(null);
                return true;
            case "fleetDefeated":
                // Spawns the derelict Terraformer

                SectorEntityToken bivouac = Global.getSector().getEntityById("vulp_planetBivouac");

                ShipRecoverySpecial.PerShipData ship = new ShipRecoverySpecial.PerShipData("vulp_terraformer_Hull", ShipRecoverySpecial.ShipCondition.WRECKED, 0f);
                ship.shipName = "EUS Muscascus";
                DerelictShipEntityPlugin.DerelictShipData shipparams = new DerelictShipEntityPlugin.DerelictShipData(ship, false);
                CustomCampaignEntityAPI entity = (CustomCampaignEntityAPI) BaseThemeGenerator.addSalvageEntity(
                        bivouac.getContainingLocation(),
                        Entities.WRECK, Factions.NEUTRAL, shipparams);
                Misc.makeImportant(entity, "vulp_terraformer");
                Misc.makeImportant(bivouac, "vulp_terraformerplanet");
                entity.getMemoryWithoutUpdate().set("$vulp_terraformer", true);

                //entity.getLocation().x = bivouac.getLocation().x + (50f - (float) Math.random() * 100f);
                //entity.getLocation().y = bivouac.getLocation().y + (50f - (float) Math.random() * 100f);
                entity.setCircularOrbit(bivouac, 0, bivouac.getRadius()+50, 10);
                
                ShipRecoverySpecial.ShipRecoverySpecialData data = new ShipRecoverySpecial.ShipRecoverySpecialData(null);
                data.notNowOptionExits = true;
                data.noDescriptionText = true;
                DerelictShipEntityPlugin dsep = (DerelictShipEntityPlugin) entity.getCustomPlugin();
                ShipRecoverySpecial.PerShipData copy = (ShipRecoverySpecial.PerShipData) dsep.getData().ship.clone();
                copy.variant = Global.getSettings().getVariant(copy.variantId).clone();
                copy.variantId = null;
                copy.variant.addTag(Tags.SHIP_CAN_NOT_SCUTTLE);
                copy.variant.addTag(Tags.SHIP_UNIQUE_SIGNATURE);
                copy.condition = ShipCondition.PRISTINE;
                copy.variant.addPermaMod(HullMods.COMP_HULL);
                copy.variant.addPermaMod(HullMods.GLITCHED_SENSORS);
                copy.variant.addPermaMod(HullMods.MALFUNCTIONING_COMMS);
                data.addShip(copy);

                Misc.setSalvageSpecial(entity, data);
                
                return true;
        }
        return false;
    }
}
