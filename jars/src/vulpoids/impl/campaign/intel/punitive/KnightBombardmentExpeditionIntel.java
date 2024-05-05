
package vulpoids.impl.campaign.intel.punitive;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.punitive.PunitiveExpeditionIntel;
import static com.fs.starfarer.api.impl.campaign.intel.punitive.PunitiveExpeditionIntel.BUTTON_AVERT;
import com.fs.starfarer.api.impl.campaign.intel.punitive.PunitiveExpeditionManager;
import com.fs.starfarer.api.impl.campaign.intel.raid.ActionStage;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;

public class KnightBombardmentExpeditionIntel extends PunitiveExpeditionIntel {

    public KnightBombardmentExpeditionIntel(FactionAPI faction, MarketAPI from, MarketAPI target, float expeditionFP, float orgDur, PunitiveExpeditionManager.PunExGoal goal, Industry targetIndustry, PunitiveExpeditionManager.PunExReason bestReason) {
        super(faction, from, target, expeditionFP, orgDur, goal, targetIndustry, bestReason);
        advanceImpl(10);
    }
    
    @Override
    protected void notifyEnding() {
        super.notifyEnding();
        boolean bombardmentOccurred = false;
        for (RaidStage stage : stages) {
            if (stage instanceof ActionStage && stage.getStatus() == RaidStageStatus.SUCCESS) {
                bombardmentOccurred = true;
                break;
            }
        }
        VulpoidAcceptanceEventIntel.get().resolveKnightBombardment(bombardmentOccurred);
    }
    
    @Override
    public CampaignFleetAPI spawnFleet(RouteManager.RouteData route) {
        CampaignFleetAPI fleet = super.spawnFleet(route);
        fleet.setFaction(Factions.KOL, true);
        return fleet;
    }
    @Override
    public String getIcon() {
        return Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getCrest();
    }
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;

        info.addImage(getFactionForUIColors().getLogo(), width, 128, opad);

        addInitialDescSection(info, opad);
        
        info.addPara("This expedition is being conducted in retalliation for the production of Vulpoids, and for the "+
                "'spiritual corruption' that they have inflicted upon the Sector.", opad);

        if (outcome == null) {
            addStandardStrengthComparisons(info, target, targetFaction, goal != PunitiveExpeditionManager.PunExGoal.BOMBARD, goal == PunitiveExpeditionManager.PunExGoal.BOMBARD, "expedition", "expedition's");
        }

        info.addSectionHeading("Status",
                faction.getBaseUIColor(), faction.getDarkUIColor(), Alignment.MID, opad);

        for (RaidStage stage : stages) {
            stage.showStageInfo(info);
            if (getStageIndex(stage) == failStage) break;
        }

        if (getCurrentStage() == 0 && !isFailed()) {
            FactionAPI pf = Global.getSector().getPlayerFaction();
            ButtonAPI button = info.addButton("Avert", BUTTON_AVERT, 
                    pf.getBaseUIColor(), pf.getDarkUIColor(),
                    (int)(width), 20f, opad * 2f);
            button.setShortcut(Keyboard.KEY_T, true);
        }

        if (!from.getFaction().isHostileTo(targetFaction) && !isFailed()) {
            LabelAPI label = info.addPara("This operation is being carried out in defiance of the orders of the " +
                    "Luddic Church. Defeating it will not result in a reputation reduction, and you can sue for reparations " +
                    "regardless of the outcome.", Misc.getGrayColor(), opad);
            label.setHighlight("Luddic Church", "sue for reparations");
            label.setHighlightColors(faction.getBaseUIColor(), Misc.getHighlightColor());
        }
    }
    @Override
    public void applyRepPenalty() {}
}
