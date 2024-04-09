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
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxCrewMod().modifyFlat(id, CREW_BONUS);
		stats.getCargoMod().modifyFlat(id, CARGO_BONUS);
		//if (stats.getVariant() != null && stats.getVariant().hasHullMod(HullMods.CIVGRADE) && !stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
		//	stats.getSuppliesPerMonth().modifyPercent(id, AdditionalBerthing.MAINTENANCE_PERCENT);
		//}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + CREW_BONUS;
		if (index == 1) return "" + CARGO_BONUS;
		return null;
	}

}




