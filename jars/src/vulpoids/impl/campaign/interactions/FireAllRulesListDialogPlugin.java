
package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.Option;
import com.fs.starfarer.api.campaign.rules.RuleAPI;
import com.fs.starfarer.api.campaign.rules.RulesAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FireAllRulesListDialogPlugin extends RulePopulatedListDialogPlugin {
    
    String trigger;
    
    public FireAllRulesListDialogPlugin(String trigger) {
        this.trigger = trigger;
    }
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        RulesAPI rules = Global.getSector().getRules();
        Map<String, MemoryAPI> memoryMap = getMemoryMapCopy();
        List<RuleAPI> matchingRules = rules.getAllMatching(null, trigger, dialog, memoryMap);
        for (RuleAPI rule : matchingRules) {
            rule.runScript(dialog, memoryMap);
            for(Option option : rule.getOptions()) {
                String replacement_text = Misc.replaceTokensFromMemory(option.text, memoryMap);
                if(replacement_text!=null && !replacement_text.equals(option.text)) {
                    Option replacement_option = new Option();
                    replacement_option.text = replacement_text;
                    replacement_option.id = option.id;
                    replacement_option.order = option.order;
                    option = replacement_option;
                }
                entries.add(option);
                optionMemoryMaps.put(option, memoryMap);
            }
        }
        Collections.sort(entries,new Comparator<Object>() {
            @Override
            public int compare(Object ob1, Object ob2) {
                if(ob1 instanceof Option && ob2 instanceof Option) {
                    Option o1 = (Option) ob1;
                    Option o2 = (Option) ob2;
                    float d = o1.order - o2.order;
                    if (d<0) return -1;
                    if (d>0) return 1;
                }
                return 0;
            }
        });
    }
    
}
