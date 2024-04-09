package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import vulpoids.campaign.impl.items.VulpoidPlugin;
import vulpoids.impl.campaign.ids.Vulpoids;

public class ProfectoInteractionDialogPlugin implements InteractionDialogPlugin {
    
    /*private static enum OptionId {
        INIT,
        CHAT,
        SET_IDLE,
        SET_EMBARKED,
        SET_OFFICER,
        SET_ADMIN,
        LEAVE,
    }*/
    
    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;
    
    ArrayList<CargoStackAPI[]> pages;
    int page = 0;
    int MAX_ITEMS_PER_PAGE = 6;
    
    
    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        textPanel = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        
        page = 0;
        int vulps = 0;
        pages = new ArrayList();
        pages.add(new CargoStackAPI[MAX_ITEMS_PER_PAGE]);
        for(CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
            if(Vulpoids.CARGO_ITEM.equals(stack.getCommodityId())) {
                pages.get(0)[0] = stack;
                vulps++;
                break;
            }
        }
        for(CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
            if (stack.getPlugin() instanceof VulpoidPlugin) {
                if (pages.size() < 1+page) pages.add(new CargoStackAPI[MAX_ITEMS_PER_PAGE]);
                ((VulpoidPlugin)stack.getPlugin()).refreshPerson();  // This is actually here for when we return. It saves the updated person to the data.
                pages.get(page)[vulps%MAX_ITEMS_PER_PAGE] = stack;
                vulps++;
                if(vulps%MAX_ITEMS_PER_PAGE == 0) page++;
            }
        }
        page = 0;
        if(dialog.getInteractionTarget().getMemoryWithoutUpdate().contains("$vulpoidContactPanelPage")) {
            page = dialog.getInteractionTarget().getMemoryWithoutUpdate().getInt("$vulpoidContactPanelPage");
        } else {
            dialog.getInteractionTarget().getMemoryWithoutUpdate().set("$vulpoidContactPanelPage", 0);
        }
        
        
        visual.fadeVisualOut();
        dialog.getInteractionTarget().setActivePerson(null);
        
        optionSelected(null, "INIT");
    }

    @Override
    public void optionSelected(String text, Object optionData) {
        if (optionData == null) return;
        //if (text != null) dialog.addOptionSelectedText(optionData);
        if(optionData instanceof String) {
            switch((String)optionData) {
                case "INIT":
                    textPanel.clear();
                    options.clearOptions();
                    textPanel.addPara("You review the manifest of the Vulpoids aboard your fleet.");

                    for (int i=0; i<MAX_ITEMS_PER_PAGE; i++) {
                        CargoStackAPI stack = pages.get(page)[i];
                        if(stack!=null) {
                            if(stack.getPlugin() instanceof VulpoidPlugin) {
                                VulpoidPlugin plugin = ((VulpoidPlugin)stack.getPlugin());
                                options.addOption(plugin.getPerson().getNameString(), i, plugin.getColor(), null);
                            } else {
                                options.addOption("Vulpoid", i, new Color(161,118,86), null);
                            }
                        } else {
                            options.addOption("", i);
                            options.setEnabled(i, false);
                        }
                    }

                    options.addOption("Next", "NEXT");
                    if(page>=pages.size()-1) options.setEnabled("NEXT", false);
                    options.addOption("Prev", "PREV");
                    if(page<=0) options.setEnabled("PREV", false);

                    options.addOption("Leave", "LEAVE");
                    options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
                    break;
                case "NEXT":
                    page++;
                    dialog.getInteractionTarget().getMemoryWithoutUpdate().set("$vulpoidContactPanelPage", page);
                    optionSelected(null, "INIT");
                    break;
                case "PREV":
                    page--;
                    dialog.getInteractionTarget().getMemoryWithoutUpdate().set("$vulpoidContactPanelPage", page);
                    optionSelected(null, "INIT");
                    break;
                case "LEAVE":
                    dialog.dismiss();
                    Global.getSector().setPaused(true); // Doesn't work
                    break;
            }
        } else if (optionData instanceof Integer) {
            CargoStackAPI stack = pages.get(page)[(Integer)optionData];
            PersonAPI person;
            if(stack.getPlugin() instanceof VulpoidPlugin) {
                VulpoidPlugin plugin = ((VulpoidPlugin)stack.getPlugin());
                person = plugin.getPerson();
                person.getMemoryWithoutUpdate().set("$profectoIsAssigned", plugin.disallowCycleReason()!=null);
            } else {
                person = VulpoidCreator.createVulpoid();
                person.getMemoryWithoutUpdate().set(Vulpoids.KEY_PROFECTO_ASSIGNMENT, Vulpoids.CARGO_ITEM);
            }
            textPanel.clear();
            dialog.getInteractionTarget().setActivePerson(person);
            InteractionDialogPlugin chatDialog = new RuleBasedInteractionDialogPluginImpl("OpenVulpoidChatDialog");
            dialog.setPlugin(chatDialog);
            chatDialog.init(dialog);
            // Have to 'jiggle' it like this because otherwise the image doesn't load if you go to the same person twice.
            visual.showPersonInfo(person, true);
            visual.showPersonInfo(person, false);
        }
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {
        //TODO//options.addOptionTooltipAppender(optionData, optionTooltipCreator);
        if(optionData instanceof Integer) {
            CargoStackAPI stack = pages.get(page)[(Integer)optionData];
            options.addOptionTooltipAppender(optionData, new VulpoidTooltipCreator(stack));
        }
    }
    
    class VulpoidTooltipCreator implements OptionPanelAPI.OptionTooltipCreator {
        CargoStackAPI stack;
        public VulpoidTooltipCreator(CargoStackAPI stack) {
            this.stack = stack;
        }
        @Override
        public void createTooltip(TooltipMakerAPI tooltip, boolean hadOtherText) {
            if (stack!=null && stack.getPlugin() instanceof VulpoidPlugin) {
                ((VulpoidPlugin)stack.getPlugin()).createTooltip(tooltip, false, null, null);
            } else {
                TooltipMakerAPI portrait = tooltip.beginImageWithText("graphics/icons/cargo/vulpoids/vulpoid.png", 80, tooltip.getWidthSoFar(), false);
                portrait.addPara("Pull an ordinary Vulpoid out of storage.", 10f);
                tooltip.addImageWithText(10f);
            }
        }
        
    }
    
    @Override
    public void advance(float amount) {
        
    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {
        
    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }
}
