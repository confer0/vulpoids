
package vulpoids.campaign.listeners;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyPlayerHostileActListener;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import vulpoids.impl.campaign.econ.VulpoidPopulation;
import vulpoids.impl.campaign.ids.Vulpoids;

public class SaturationBombardmentListener implements ColonyPlayerHostileActListener {

    @Override
    public void reportRaidForValuablesFinishedBeforeCargoShown(InteractionDialogAPI dialog, MarketAPI market, MarketCMD.TempData actionData, CargoAPI cargo) {}

    @Override
    public void reportRaidToDisruptFinished(InteractionDialogAPI dialog, MarketAPI market, MarketCMD.TempData actionData, Industry industry) {}

    @Override
    public void reportTacticalBombardmentFinished(InteractionDialogAPI dialog, MarketAPI market, MarketCMD.TempData actionData) {}

    @Override
    public void reportSaturationBombardmentFinished(InteractionDialogAPI dialog, MarketAPI market, MarketCMD.TempData actionData) {
        if(!market.hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) return;
        ((VulpoidPopulation)market.getCondition(Vulpoids.CONDITION_VULPOID_POPULATION).getPlugin()).onSaturationBombardment();
    }

}
