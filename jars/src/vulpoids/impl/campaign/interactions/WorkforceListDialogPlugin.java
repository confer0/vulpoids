
package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import vulpoids.impl.campaign.econ.workforces.BaseWorkforce;
import vulpoids.impl.campaign.ids.Vulpoids;

public class WorkforceListDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    MarketAPI market;
    
    @Override
    protected void loadOptions() {
        market = dialog.getInteractionTarget().getMarket();
        entries = new ArrayList();
        for (MarketConditionSpecAPI spec : Global.getSettings().getAllMarketConditionSpecs()) {
            if (!spec.hasTag(Vulpoids.CONDITION_WORKFORCE_TAG)) continue;
            BaseWorkforce b = getPluginForCondition(spec.getId());
            if (b.isAvailableToPlayer()) entries.add(spec.getId());
            else if (market.hasCondition(spec.getId())) entries.add(spec.getId());  // In case an update bars access
        }
    }
    private BaseWorkforce getPluginForCondition(String condition) {
        if(market.hasCondition(condition)) return (BaseWorkforce) market.getCondition(condition).getPlugin();
        
        MarketAPI dummy_market = Global.getFactory().createMarket(null, null, 0);
        dummy_market.addCondition(condition);
        BaseWorkforce b = (BaseWorkforce)dummy_market.getCondition(condition).getPlugin();
        b.init(market, null);
        return b;
    }
    
    @Override
    protected String getEntryLabel(Object entry) {
        if (market.hasCondition((String)entry)) {
            return "[X] "+Global.getSettings().getMarketConditionSpec((String)entry).getName();
        } else {
            return "[  ] "+Global.getSettings().getMarketConditionSpec((String)entry).getName();
        }
    }
    @Override
    protected Color getEntryColor(Object entry) {
        if (market.hasCondition((String)entry) && !getPluginForCondition((String)entry).getUnmetRequirements(false).isEmpty()) {
            return Misc.getNegativeHighlightColor();
        }
        return Misc.getButtonTextColor();
    }
    @Override
    protected String getEntryTooltipString(Object entry) {
        String tooltip = Global.getSettings().getMarketConditionSpec((String)entry).getDesc();
        BaseWorkforce condition_plugin = getPluginForCondition((String)entry);
        List<String> unmet_reqs = condition_plugin.getUnmetRequirements(false);
        if(!unmet_reqs.isEmpty()) {
            tooltip += "\n\nUnmet Requirements: ";
            for(String req : unmet_reqs) tooltip += req+", ";
            tooltip = tooltip.substring(0, tooltip.length() - 2);
        }
        return tooltip;
    }
    @Override
    protected boolean getEntryEnabled(Object entry) {
        if (market.hasCondition((String)entry)) return true;
        return getPluginForCondition((String)entry).getUnmetRequirements(true).isEmpty();
    }

    @Override
    protected void selectEntry(Object entry) {
        if(market.hasCondition((String)entry)) {
            market.removeCondition((String)entry);
        } else {
            market.addCondition((String)entry);
        }
        optionSelected(null, OptionId.REFRESH);
    }

}
