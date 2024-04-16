package vulpoids.impl.campaign.intel.events;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidAcceptanceCause {
    
    VulpoidAcceptanceEventIntel intel;
    
    public VulpoidAcceptanceCause(VulpoidAcceptanceEventIntel intel) {
        this.intel = intel;
    }
}
