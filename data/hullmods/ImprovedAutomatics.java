package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class ImprovedAutomatics extends BaseLogisticsHullMod {

	public static float CREW_MULT = 0.25f;
	public static float SUPPLY_USE_MULT = 2f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMinCrewMod().modifyMult(id, CREW_MULT);
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((1f - CREW_MULT) * 100f) + "%";
		if (index == 1) return "" + (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		return null;
	}

}




