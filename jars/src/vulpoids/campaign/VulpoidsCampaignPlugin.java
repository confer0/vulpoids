package vulpoids.campaign;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import vulpoids.impl.campaign.VulpoidAdminPlugin;

public class VulpoidsCampaignPlugin extends BaseCampaignPlugin {

    @Override
    public PluginPick<AICoreAdminPlugin> pickAICoreAdminPlugin(String commodityId) {
        if ("vulpoids_shiny".equals(commodityId)) {
            return new PluginPick<AICoreAdminPlugin>(new VulpoidAdminPlugin(), PickPriority.MOD_GENERAL);
        }
        return null;
    }
}