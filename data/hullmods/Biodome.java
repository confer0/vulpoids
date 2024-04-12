package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class Biodome extends BaseLogisticsHullMod {

	public static int CREW_BONUS = 3000;
	public static int CARGO_BONUS = 2000;
	public static float FLUX_CAP_MULT = 1.5f;
	public static float FLUX_DIS_MULT = 0.8f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxCrewMod().modifyFlat(id, CREW_BONUS);
		stats.getCargoMod().modifyFlat(id, CARGO_BONUS);
		stats.getFluxDissipation().modifyMult(id, FLUX_DIS_MULT);
		stats.getFluxCapacity().modifyMult(id, FLUX_CAP_MULT);
		//if (stats.getVariant() != null && stats.getVariant().hasHullMod(HullMods.CIVGRADE) && !stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
		//	stats.getSuppliesPerMonth().modifyPercent(id, AdditionalBerthing.MAINTENANCE_PERCENT);
		//}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + CREW_BONUS;
		if (index == 1) return "" + CARGO_BONUS;
		if (index == 2) return "" + Math.round((FLUX_CAP_MULT-1)*100f)+"%";
		if (index == 3) return "" + Math.round((1-FLUX_DIS_MULT)*100f)+"%";
		return null;
	}

}




