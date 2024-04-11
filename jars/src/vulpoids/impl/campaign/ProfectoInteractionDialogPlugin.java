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
import com.fs.starfarer.api.util.Misc;
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
    
    ArrayList<Object[]> pages;
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
        pages.add(new Object[MAX_ITEMS_PER_PAGE]);
        if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_didInterrogation")) {
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
                    ((VulpoidPlugin)stack.getPlugin()).setToDefaultExpression();  // This, on the other hand, is for seeing their faces.
                    pages.get(page)[vulps%MAX_ITEMS_PER_PAGE] = stack;
                    vulps++;
                    if(vulps%MAX_ITEMS_PER_PAGE == 0) page++;
                }
            }
            page = 0;
        } else {
            // Story option! Only Laisa
            pages.get(0)[0] = Vulpoids.PERSON_LAISA;
        }
        if(dialog.getInteractionTarget().getMemoryWithoutUpdate().contains("$vulpoidContactPanelPage")) {
            page = dialog.getInteractionTarget().getMemoryWithoutUpdate().getInt("$vulpoidContactPanelPage");
        } else {
            dialog.getInteractionTarget().getMemoryWithoutUpdate().set("$vulpoidContactPanelPage", 0);
        }
        
        
        visual.fadeVisualOut();
        visual.finishFadeFast();  // Otherwise you can see the expressions change.
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
                    if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_didInterrogation")) {
                        textPanel.addPara("You review the manifest of the Vulpoids aboard your fleet.");
                    } else {
                        textPanel.addPara("You review the brig records for prisoners of interest from the Exodyne ship.");
                    }
                    
                    for (int i=0; i<MAX_ITEMS_PER_PAGE; i++) {
                        Object entry = pages.get(page)[i];
                        if(entry==null) {
                            options.addOption("", i);
                            options.setEnabled(i, false);
                        } else if (entry instanceof CargoStackAPI) {
                            // Regular inventory items.
                            CargoStackAPI stack = (CargoStackAPI) entry;
                            if(stack.getPlugin() instanceof VulpoidPlugin) {
                                VulpoidPlugin plugin = ((VulpoidPlugin)stack.getPlugin());
                                options.addOption(plugin.getPerson().getNameString(), i, plugin.getColor(), null);
                            } else {
                                options.addOption("Vulpoid", i, new Color(161,118,86), null);
                            }
                        } else if (entry instanceof String) {
                            // Important person by ID.
                            String person_name = Global.getSector().getImportantPeople().getPerson((String)entry).getNameString();
                            options.addOption(person_name, i, Misc.getHighlightColor(), "Speak with "+person_name);
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
                    for (Object[] page1 : pages) {
                        for (Object entry : page1) {
                            // Resetting back to the defaults, so we don't carry over expression changes from the conversation.
                            if(entry instanceof CargoStackAPI) {
                                CargoStackAPI stack = (CargoStackAPI) entry;
                                if(stack.getPlugin() instanceof VulpoidPlugin) ((VulpoidPlugin)stack.getPlugin()).resetClothingAndExpressions();
                            }
                        }
                    }
                    dialog.dismiss();
                    Global.getSector().setPaused(true); // Doesn't work
                    break;

            }
        } else if (optionData instanceof Integer) {
            Object entry = pages.get(page)[(Integer)optionData];
            PersonAPI person = null;
            if (entry instanceof String) {
                person = Global.getSector().getImportantPeople().getPerson(Vulpoids.PERSON_LAISA);
                person.getMemoryWithoutUpdate().set("$importantCall", true, 0);
            } else if (entry instanceof CargoStackAPI) {
                CargoStackAPI stack = (CargoStackAPI) entry;
                if(stack.getPlugin() instanceof VulpoidPlugin) {
                    VulpoidPlugin plugin = ((VulpoidPlugin)stack.getPlugin());
                    person = plugin.getPerson();
                    person.getMemoryWithoutUpdate().set("$profectoIsAssigned", plugin.disallowCycleReason()!=null);
                } else {
                    person = VulpoidCreator.createVulpoid();
                    person.getMemoryWithoutUpdate().set(Vulpoids.KEY_PROFECTO_ASSIGNMENT, Vulpoids.CARGO_ITEM);
                }
            }
            if (person != null) {
                textPanel.clear();
                dialog.getInteractionTarget().setActivePerson(person);
                InteractionDialogPlugin chatDialog = new RuleBasedInteractionDialogPluginImpl("OpenVulpoidChatDialog");
                dialog.setPlugin(chatDialog);
                chatDialog.init(dialog);
                // Have to 'jiggle' it like this because otherwise the image doesn't load if you go to the same person twice.
                visual.showPersonInfo(person, true);
                visual.showPersonInfo(person, false);
            } else {
                textPanel.addPara("An issue has occured. Please document what happened and contact the mod developer.");
            }
        }
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {
        //TODO//options.addOptionTooltipAppender(optionData, optionTooltipCreator);
        if(optionData instanceof Integer) {
            Object entry = pages.get(page)[(Integer)optionData];
            if (entry instanceof CargoStackAPI) options.addOptionTooltipAppender(optionData, new VulpoidTooltipCreator((CargoStackAPI)entry));
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
