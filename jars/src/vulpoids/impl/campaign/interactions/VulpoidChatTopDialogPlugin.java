package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import vulpoids.campaign.impl.items.VulpoidPlugin;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidChatTopDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_didInterrogation")) {
            for(CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
                if(Vulpoids.CARGO_ITEM.equals(stack.getCommodityId())) {
                    //pages.get(0)[0] = stack;
                    entries.add(stack);
                    break;
                }
            }
            for(CargoStackAPI stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
                if (stack.getPlugin() instanceof VulpoidPlugin) {
                    //if (pages.size() < 1+p) pages.add(new CargoStackAPI[MAX_ITEMS_PER_PAGE]);
                    ((VulpoidPlugin)stack.getPlugin()).refreshPerson();  // This is actually here for when we return. It saves the updated person to the data.
                    ((VulpoidPlugin)stack.getPlugin()).setToDefaultExpression();  // This, on the other hand, is for seeing their faces.
                    //pages.get(p)[vulps%MAX_ITEMS_PER_PAGE] = stack;
                    entries.add(stack);
                    //if(vulps%MAX_ITEMS_PER_PAGE == 0) p++;
                }
            }
        } else {
            // Story option! Only Laisa
            //pages.get(0)[0] = Vulpoids.PERSON_LAISA;
            entries.add(Vulpoids.PERSON_LAISA);
        }
        if(dialog.getInteractionTarget().getMemoryWithoutUpdate().contains("$vulpoidContactPanelPage")) {
            page = dialog.getInteractionTarget().getMemoryWithoutUpdate().getInt("$vulpoidContactPanelPage");
        } else {
            dialog.getInteractionTarget().getMemoryWithoutUpdate().set("$vulpoidContactPanelPage", 0);
        }
        
        
        visual.fadeVisualOut();
        visual.finishFadeFast();  // Otherwise you can see the expressions change.
        dialog.getInteractionTarget().setActivePerson(null);
    }
    
    @Override
    protected void addDescriptionText() {
        if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$vulp_didInterrogation")) {
            textPanel.addPara("You review the manifest of the Vulpoids aboard your fleet.");
        } else {
            textPanel.addPara("You review the brig records for prisoners of interest from the Exodyne ship.");
        }
    }
    @Override
    protected String getEntryLabel(Object entry) {
        if (entry instanceof CargoStackAPI) {
            CargoStackAPI stack = (CargoStackAPI) entry;
            if(stack.getPlugin() instanceof VulpoidPlugin) {
                VulpoidPlugin plugin = ((VulpoidPlugin)stack.getPlugin());
                return plugin.getPerson().getNameString();
            } else {
                return "Vulpoid";
            }
        } else if (entry instanceof String) {
            return Global.getSector().getImportantPeople().getPerson((String)entry).getNameString();
        }
        return null;
    }
    @Override
    protected Color getEntryColor(Object entry) {
        if (entry instanceof CargoStackAPI) {
            CargoStackAPI stack = (CargoStackAPI) entry;
            if(stack.getPlugin() instanceof VulpoidPlugin) {
                VulpoidPlugin plugin = ((VulpoidPlugin)stack.getPlugin());
                MemoryAPI memory = plugin.getPerson().getMemoryWithoutUpdate();
                if(memory.contains(Vulpoids.KEY_RESEARCH_PROJECT) && memory.contains(Vulpoids.KEY_RESEARCH_COMPLETION_DAY)) {
                    if(Global.getSector().getMemoryWithoutUpdate().getFloat("$daysSinceStart") >= memory.getFloat(Vulpoids.KEY_RESEARCH_COMPLETION_DAY)) return Misc.getHighlightColor();
                }
                return plugin.getColor();
            } else {
                return new Color(161,118,86);
            }
        } else if (entry instanceof String) {
            return Misc.getHighlightColor();
        }
        return null;
    }
    @Override
    protected String getEntryTooltipString(Object entry) {
        if (entry instanceof String) return "Speak with "+getEntryLabel(entry);
        return null;
    }
    @Override
    protected String getLeaveOptionText() {
        return "Leave";
    }
    @Override
    protected void doOnLeave() {
        /*for (Object[] page1 : pages) {
            for (Object entry : page1) {
                // Resetting back to the defaults, so we don't carry over expression changes from the conversation.
                if(entry instanceof CargoStackAPI) {
                    CargoStackAPI stack = (CargoStackAPI) entry;
                    if(stack.getPlugin() instanceof VulpoidPlugin) ((VulpoidPlugin)stack.getPlugin()).resetClothingAndExpressions();
                }
            }
        }*/
        for (Object entry : entries) {
            // Resetting back to the defaults, so we don't carry over expression changes from the conversation.
            if(entry instanceof CargoStackAPI) {
                CargoStackAPI stack = (CargoStackAPI) entry;
                if(stack.getPlugin() instanceof VulpoidPlugin) ((VulpoidPlugin)stack.getPlugin()).resetClothingAndExpressions();
            }
        }
    }
    
    @Override
    protected void selectEntry(Object entry) {
        PersonAPI person = null;
        if (entry instanceof String) {
            person = Global.getSector().getImportantPeople().getPerson((String)entry);
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
            delegated = true;
            options.clearOptions();
            conversationDelegate = new RuleBasedInteractionDialogPluginImpl();
            conversationDelegate.setEmbeddedMode(true);
            conversationDelegate.init(dialog);
            //textPanel.clear();
            dialog.getInteractionTarget().setActivePerson(person);
            conversationDelegate.notifyActivePersonChanged();
            if(!conversationDelegate.fireBest("OpenVulpoidChatDialog")) {
                dialog.getInteractionTarget().setActivePerson(null);
                conversationDelegate.notifyActivePersonChanged();
                delegated = false;
            }
            //InteractionDialogPlugin chatDialog = new RuleBasedInteractionDialogPluginImpl("OpenVulpoidChatDialog");
            //dialog.setPlugin(chatDialog);
            //chatDialog.init(dialog);
            
            
            // Have to 'jiggle' it like this because otherwise the image doesn't load if you go to the same person twice.
            visual.hideFirstPerson();
            //visual.showPersonInfo(person, true);
            visual.showPersonInfo(person, false);
        } else {
            textPanel.addPara("An issue has occured. Please document what happened and contact the mod developer.");
        }
    }
    
    @Override
    public void optionMousedOver(String optionText, Object optionData) {
        super.optionMousedOver(optionText, optionData);
        if(optionData instanceof Integer) {
            //Object entry = pages.get(page)[(Integer)optionData];
            Object entry = entries.get((Integer)optionData);
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
}
