package vulpoids.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;


public class ShowVulpAdminCrying extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        if (!(dialog.getInteractionTarget().getMarket().getMemoryWithoutUpdate().contains("$vulpoidAdmin"))) return false;
        
        PersonAPI admin = (PersonAPI) dialog.getInteractionTarget().getMarket().getMemoryWithoutUpdate().get("$vulpoidAdmin");
        String portraitSprite = admin.getPortraitSprite();
        portraitSprite = portraitSprite.substring(0, portraitSprite.length()-4) + "_cry.png";
        PersonAPI dummy_admin = Global.getFactory().createPerson();
        dummy_admin.setPortraitSprite(portraitSprite);
        dummy_admin.setName(admin.getName());
        dummy_admin.setFaction(admin.getFaction().getId());
        dummy_admin.setPostId(admin.getPostId());
        dummy_admin.setRankId(admin.getRankId());
        dummy_admin.getRelToPlayer().setRel(0.70f);
        
        dialog.getVisualPanel().showPersonInfo(dummy_admin);
        
        return true;
    }
    
}
