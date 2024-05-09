package vulpoids.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.RepairGantry;
import com.fs.starfarer.api.impl.campaign.SurveyPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TerraformingBays extends BaseHullMod {
    
    private static int cargoBonus = 2000;
    
    private static float salvageMag = 40f;
    private static float surveyMag = 40f;

    public static final float BATTLE_SALVAGE_MULT = .2f;
    public static final float MIN_CR = 0.1f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCargoMod().modifyFlat(id, cargoBonus);
        
        stats.getDynamic().getMod(Stats.SALVAGE_VALUE_MULT_MOD).modifyFlat(id, (Float) salvageMag * 0.01f);
        
        stats.getDynamic().getMod(Stats.getSurveyCostReductionId(Commodities.HEAVY_MACHINERY)).modifyFlat(id, surveyMag);
        stats.getDynamic().getMod(Stats.getSurveyCostReductionId(Commodities.SUPPLIES)).modifyFlat(id, surveyMag);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + cargoBonus;
        
        if (index == 1) return "" + ((Float)salvageMag).intValue() + "%";
        if (index == 2) return "" + (int) Math.round(BATTLE_SALVAGE_MULT * 100f) + "%";
        
        if (index == 3) return "" + ((Float)surveyMag).intValue();
        if (index == 4) return "" + (int) SurveyPluginImpl.MIN_SUPPLIES_OR_MACHINERY;

        return null;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        
        if (isForModSpec || ship == null) return;
        if (Global.getSettings().getCurrentState() == GameState.TITLE) return;
        
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        float fleetMod = RepairGantry.getAdjustedGantryModifier(fleet, null, 0f);
        float currShipMod = salvageMag * 0.01f;
        
        float fleetModWithOneMore = RepairGantry.getAdjustedGantryModifier(fleet, null, currShipMod);
        float fleetModWithoutThisShip = RepairGantry.getAdjustedGantryModifier(fleet, ship.getFleetMemberId(), 0f);
        
        tooltip.addPara("The total resource recovery bonus for your fleet is %s.", opad, h,
                "" + (int) Math.round(fleetMod * 100f) + "%");
        
        float cr = ship.getCurrentCR();
        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if (member.getId().equals(ship.getFleetMemberId())) {
                cr = member.getRepairTracker().getCR();
            }
        }
        
        if (cr < MIN_CR) {
            LabelAPI label = tooltip.addPara("This ship's combat readiness is below %s "
                    + "and the gantry can not be utilized. Bringing this ship into readiness "
                    + "would increase the fleetwide bonus to %s.",
                    opad, h,
                    "" + (int) Math.round(MIN_CR * 100f) + "%",
                    "" + (int) Math.round(fleetModWithOneMore * 100f) + "%");
            label.setHighlightColors(bad, h);
            label.setHighlight("" + (int) Math.round(MIN_CR * 100f) + "%", "" + (int) Math.round(fleetModWithOneMore * 100f) + "%");
        } else {
            if (fleetMod > currShipMod) {
                tooltip.addPara("Removing this ship would decrease it to %s.", opad, h,
                        "" + (int) Math.round(fleetModWithoutThisShip * 100f) + "%");
            }
        }

        tooltip.addPara("The fleetwide post-battle salvage bonus is %s.", opad, h,
                "" + (int) Math.round(getAdjustedGantryModifierForPostCombatSalvage(fleet) * 100f) + "%");
        
        int machinery = (int) Misc.getFleetwideTotalMod(fleet, Stats.getSurveyCostReductionId(Commodities.HEAVY_MACHINERY), 0, ship);
        int supplies = (int) Misc.getFleetwideTotalMod(fleet, Stats.getSurveyCostReductionId(Commodities.SUPPLIES), 0, ship);
        
        
        tooltip.addPara("The combined surveying equipment in your fleet reduces the survey cost by %s "
                + "supplies and %s heavy machinery.", opad, h, ""+supplies, ""+machinery
        );
    }
    
    public static float getAdjustedGantryModifierForPostCombatSalvage(CampaignFleetAPI fleet) {
        return RepairGantry.getAdjustedGantryModifier(fleet, null, 0) * BATTLE_SALVAGE_MULT;
    }
}
