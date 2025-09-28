package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class ImprovedAutomatics extends BaseLogisticsHullMod {

	public static float CREW_PERC = -50f;
	public static float CREW_LOSS_MULT = 0.25f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMinCrewMod().modifyPercent(id, CREW_PERC);
		stats.getCrewLossMult().modifyMult(id, CREW_LOSS_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((CREW_PERC)) + "%";
		if (index == 1) return "" + (int)((1-CREW_LOSS_MULT) * 100f) + "%";
		return null;
	}

}




