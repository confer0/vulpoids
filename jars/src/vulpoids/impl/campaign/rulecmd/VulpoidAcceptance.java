package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceGenericOneTimeFactor;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceProductionMissionFactor;
import vulpoids.impl.campaign.missions.VulpoidProductionMission;

public class VulpoidAcceptance extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String option = params.get(0).getString(memoryMap);
        switch(option) {
            case "officialStance":
                if (VulpoidAcceptanceEventIntel.get()==null) return false;
                return VulpoidAcceptanceEventIntel.get().getProgress() >= VulpoidAcceptanceEventIntel.PROGRESS_SANCTIONS_START;
            case "sanctionEnded":
                if (VulpoidAcceptanceEventIntel.get()==null) return false;
                return VulpoidAcceptanceEventIntel.get().getProgress() >= VulpoidAcceptanceEventIntel.PROGRESS_SANCTIONS_END;
            case "donate":
                int points = params.get(2).getInt(memoryMap);
                String desc = params.get(3).getString(memoryMap);
                String tooltip = params.get(4).getString(memoryMap);
                if(points>0) VulpoidAcceptanceEventIntel.addFactorCreateIfNecessary(new VulpoidAcceptanceGenericOneTimeFactor(desc, tooltip, points), dialog);
                // Rolls over to automatically add workforce in param 1.
            case "addWorkforce":
                String workforce = params.get(1).getString(memoryMap);
                if ("null".equals(workforce)) return true;
                MarketAPI market = dialog.getInteractionTarget().getMarket();
                if(!market.hasCondition(workforce)) market.addCondition(workforce);
                // Does not modify population size, since a production mission will add them over time.
                return true;
            case "productionContract":
                VulpoidProductionMission mission = new VulpoidProductionMission();
                int quantity = params.get(1).getInt(memoryMap);
                int monthlyPayment = params.get(2).getInt(memoryMap);
                PersonAPI person = dialog.getInteractionTarget().getActivePerson();
                if(person==null) {
                    person = dialog.getInteractionTarget().getFaction().createRandomPerson();
                    person.setPostId(Ranks.POST_CITIZEN);
                    person.setMarket(dialog.getInteractionTarget().getMarket());
                }
                mission.setPersonOverride(person);
                mission.neededOverride = quantity;
                mission.monthlyPaymentOverride = monthlyPayment;
                if (params.size() > 3) {
                    int monthlyProgress = params.get(3).getInt(memoryMap);
                    String name = "Vulpoid Production Mission";
                    if (params.size() > 4) name = params.get(4).getString(memoryMap);
                    String description = "You've been hired to produce Vulpoids for a prominent figure or group, and their influence is helping to drive public interest.";
                    if (params.size() > 5) description = params.get(5).getString(memoryMap);
                    mission.acceptanceFactor = new VulpoidAcceptanceProductionMissionFactor(VulpoidAcceptanceEventIntel.get(), monthlyProgress, name, description);
                }
                mission.createAndAbortIfFailed(dialog.getInteractionTarget().getMarket(), false);
                mission.accept(dialog, memoryMap);
                return true;
            case "resolveGilead":
                VulpoidAcceptanceEventIntel.get().resolveGileadEvent();
                return true;
            case "legalizeWithHeg":
                Global.getSector().getFaction(Factions.HEGEMONY).getIllegalCommodities().remove(Vulpoids.CARGO_ITEM);
                return true;
            case "hegLegalized":
                return !Global.getSector().getFaction(Factions.HEGEMONY).getIllegalCommodities().contains(Vulpoids.CARGO_ITEM);
            case "producingAtLeast":
                return Vulpoids.getVulpoidPeakProductionAmount() >= params.get(1).getInt(memoryMap);
        }
        return true;
    }

}
