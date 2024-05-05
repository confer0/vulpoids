package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.RuleBasedDialog;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventPlugin;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import org.lwjgl.input.Keyboard;

public abstract class ListBasedInteractionDialogPlugin implements InteractionDialogPlugin, RuleBasedDialog {
    
    private static enum OptionId {
        INIT,
        REFRESH,
        NEXT,
        PREV,
        LEAVE
    }
    
    protected InteractionDialogAPI dialog;
    protected TextPanelAPI textPanel;
    protected OptionPanelAPI options;
    protected VisualPanelAPI visual;
    
    protected InteractionDialogPlugin backtrack;
    protected boolean fireBestOnExit;
    protected String triggerOnExit;
    
    boolean delegated = false;
    protected RuleBasedInteractionDialogPluginImpl conversationDelegate;
    Object lastNonRuleOption;
    
    //ArrayList<Object[]> pages;
    ArrayList<Object> entries;
    int ITEMS_PER_PAGE = 6;
    int page = 0;
    //int MAX_ITEMS_PER_PAGE = 6;
    
    public void init(InteractionDialogAPI dialog, InteractionDialogPlugin backtrack) {
        this.backtrack = backtrack;
    }
    
    
    @Override
    public void reinit(boolean withContinueOnRuleFound) {
        loadOptions();
        if(!this.equals(dialog.getPlugin())) {
            dialog.setPlugin(this);
            //delegated = false;
            //optionSelected(null, lastNonRuleOption);
        } else {
            delegated = false;
            optionSelected(null, OptionId.INIT);
        }
    }
    
    public void initWithBacktrack(InteractionDialogAPI dialog, String triggerOnExit, boolean fireBestOnExit) {
        this.backtrack = dialog.getPlugin();
        this.triggerOnExit = triggerOnExit;
        this.fireBestOnExit = fireBestOnExit;
        init(dialog);
    }
    
    @Override
    public void init(InteractionDialogAPI dialog) {
        if(!delegated) {
            this.dialog = dialog;
            textPanel = dialog.getTextPanel();
            options = dialog.getOptionPanel();
            visual = dialog.getVisualPanel();

            conversationDelegate = new RuleBasedInteractionDialogPluginImpl();
            conversationDelegate.setEmbeddedMode(true);
            conversationDelegate.init(dialog);

            loadOptions();

            optionSelected(null, OptionId.INIT);
        }
    }
    
    protected void loadOptions() {entries = new ArrayList();}
    
    protected void addDescriptionText() {}
    protected abstract String getEntryLabel(Object entry);
    protected Color getEntryColor(Object entry) {return Misc.getButtonTextColor();}
    protected String getEntryTooltipString(Object entry) {return null;}
    protected boolean getEntryEnabled(Object entry) {return true;}
    protected String getEntryConfirmation(Object entry) {return null;}
    
    protected abstract void selectEntry(Object entry);
    
    protected String getLeaveOptionText() {return "Back";}
    protected void doOnLeave() {}
    
    @Override
    public void optionSelected(String text, Object optionData) {
        if (optionData == null) return;
        
        if(delegated && optionData instanceof String) {
            conversationDelegate.optionSelected(text, optionData);
            return;
        }
        
        if (!delegated) {
            if(text!=null && !(optionData instanceof OptionId)) dialog.addOptionSelectedText(optionData);
            lastNonRuleOption = optionData;
            
            if(optionData instanceof OptionId) {
                switch((OptionId)optionData) {
                    case INIT:
                        //textPanel.clear();
                        addDescriptionText();
                    case REFRESH:
                        options.clearOptions();
                        //for (int i=0; i<MAX_ITEMS_PER_PAGE; i++) {
                        for (int i=page*ITEMS_PER_PAGE; i<(page+1)*ITEMS_PER_PAGE; i++) {
                            //Object entry = pages.get(page)[i];
                            if(i<entries.size()) {
                                Object entry = entries.get(i);
                                Color c = getEntryColor(entry);
                                if(c!=null) {
                                    options.addOption(getEntryLabel(entry), i, getEntryColor(entry), getEntryTooltipString(entry));
                                } else {
                                    options.addOption(getEntryLabel(entry), i, getEntryTooltipString(entry));
                                }
                                options.setEnabled(i, getEntryEnabled(entry));
                                String conf = getEntryConfirmation(entry);
                                if(conf!=null) options.addOptionConfirmation(i, conf, "Yes", "Never mind");
                            } else {
                                options.addOption("", i);
                                options.setEnabled(i, false);
                            }
                        }
                        options.addOption("Next", OptionId.NEXT);
                        //if(page>=pages.size()-1) options.setEnabled(OptionId.NEXT, false);
                        if(page>=(entries.size()*1f/ITEMS_PER_PAGE)-1) options.setEnabled(OptionId.NEXT, false);
                        options.addOption("Prev", OptionId.PREV);
                        if(page<=0) options.setEnabled(OptionId.PREV, false);
                        options.addOption(getLeaveOptionText(), OptionId.LEAVE);
                        options.setShortcut(OptionId.LEAVE, Keyboard.KEY_ESCAPE, false, false, false, true);
                        break;
                    case NEXT:
                        page++;
                        optionSelected(null, OptionId.REFRESH);
                        break;
                    case PREV:
                        page--;
                        optionSelected(null, OptionId.REFRESH);
                        break;
                    case LEAVE:
                        doOnLeave();
                        if(backtrack == null || backtrack == this) dialog.dismiss();
                        else {
                            if(backtrack instanceof ListBasedInteractionDialogPlugin) {
                                //((RuleBasedDialog)backtrack).reinit(false);
                                dialog.setPlugin(backtrack);
                                if(triggerOnExit!=null) ((ListBasedInteractionDialogPlugin)backtrack).doBacktrackToHere(triggerOnExit, fireBestOnExit);
                                else ((ListBasedInteractionDialogPlugin)backtrack).reinit(false);
                            }
                            //else dialog.setPlugin(backtrack); backtrack.init(dialog);
                        }
                        break;

                }
            } else if (optionData instanceof Integer) {
                //Object entry = pages.get(page)[(Integer)optionData];
                Object entry = entries.get((Integer)optionData);
                selectEntry(entry);
            }
        }
    }
    
    public void doBacktrackToHere(String trigger, boolean fireBest) {
        options.clearOptions();
        if(fireBest) conversationDelegate.fireBest(trigger);
        else conversationDelegate.fireAll(trigger);
    }
    
    @Override
    public void optionMousedOver(String optionText, Object optionData) {
        if(delegated) conversationDelegate.optionMousedOver(optionText, optionData);
    }
    
    @Override
    public void advance(float amount) {}
    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {}
    @Override
    public Object getContext() {return null;}
    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return conversationDelegate == null ? null : conversationDelegate.getMemoryMap();
    }
    
    @Override
    public void notifyActivePersonChanged() {
        if (conversationDelegate != null) {
            conversationDelegate.notifyActivePersonChanged();
        }
    }
    @Override
    public void setActiveMission(CampaignEventPlugin mission) {
        if (mission == null) {
            conversationDelegate.getMemoryMap().remove(MemKeys.MISSION);
        } else {
            MemoryAPI memory = mission.getMemory();
            if (memory != null) {
                conversationDelegate.getMemoryMap().put(MemKeys.MISSION, memory);
            } else {
                conversationDelegate.getMemoryMap().remove(MemKeys.MISSION);
            }
        }
    }

    public void updateMemory() {
        if (conversationDelegate != null) {
            conversationDelegate.updateMemory();
        }
    }
}
