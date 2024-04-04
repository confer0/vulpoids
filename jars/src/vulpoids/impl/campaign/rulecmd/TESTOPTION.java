package vulpoids.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import vulpoids.impl.campaign.AnimatedIllustrationUIPanelPlugin;
import vulpoids.impl.campaign.VulpoidRenderUIPanelPlugin;

/**
 * ShowImageVisual <category> <key>
 */
public class TESTOPTION extends BaseCommandPlugin {
    
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {

        /*String category = "illustrations";
        String key = null;

        if (params.size() <= 1) {
            key = params.get(0).string;
        } else {
            category = params.get(0).string;
            key = params.get(1).string;
        }

        SpriteAPI sprite = Global.getSettings().getSprite(category, key);
        dialog.getVisualPanel().showImagePortion(category, key, sprite.getWidth(), sprite.getHeight(), 0, 0, 480, 300);*/
        
        //VulpoidRenderUIPanelPlugin test = new VulpoidRenderUIPanelPlugin();
        AnimatedIllustrationUIPanelPlugin test = new AnimatedIllustrationUIPanelPlugin();
        dialog.getVisualPanel().showCustomPanel(0, 0, test);
        
        
        return true;
    }
}


