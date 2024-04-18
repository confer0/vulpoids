package vulpoids.impl.campaign.rulecmd.salvage;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import vulpoids.campaign.impl.items.VulpoidPlugin;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidPicker extends BaseCommandPlugin {
    
    protected SectorEntityToken entity;
    protected CargoAPI playerCargo;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;
        String command = params.get(0).getString(memoryMap);
        if(command==null) return false;
        entity = dialog.getInteractionTarget();
        playerCargo = Global.getSector().getPlayerFleet().getCargo();
        
        if (command.equals("hasVulpoid")) {
            return hasVulpoids();
        } else if (command.equals("selectVulpoid")) {
            String selection_text = null;
            if(params.size() >= 2) selection_text = params.get(1).getString(memoryMap);
            selectVulpoid(selection_text, false);
        } else if (command.equals("selectRemoveVulpoid")) {
            String selection_text = null;
            if(params.size() >= 2) selection_text = params.get(1).getString(memoryMap);
            selectVulpoid(selection_text, true);
        }
        return true;
    }
    
    
    public boolean hasVulpoids() {
        return !getVulpoids().isEmpty();
    }
    public CargoAPI getVulpoids() {
        CargoAPI copy = Global.getFactory().createCargo(false);
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            if (stack.isSpecialStack()) {
                String item_id = stack.getSpecialDataIfSpecial().getId();
                if(Vulpoids.SPECIAL_ITEM_DEFAULT.equals(item_id)) copy.addFromStack(stack);
            }
        }
        copy.sort();
        return copy;
    }

    public void selectVulpoid(String selection_text, boolean remove) {
        CargoAPI copy = getVulpoids();
        if(selection_text == null) selection_text = "Select a Vulpoid";
        final String final_selection_text = selection_text;
        final boolean final_remove = remove;

        final float width = 310f;
        dialog.showCargoPickerDialog(final_selection_text, "Select", "Cancel", true, width, copy,
                new CargoPickerListener() {
                    @Override
                    public void pickedCargo(CargoAPI cargo) {
                        if (cargo.isEmpty()) {
                            cancelledCargoSelection();
                            return;
                        }

                        cargo.sort();
                        for (CargoStackAPI stack : cargo.getStacksCopy()) {
                            if (stack.isSpecialStack() && stack.getPlugin() instanceof VulpoidPlugin) {
                                PersonAPI person = ((VulpoidPlugin)stack.getPlugin()).getPerson();
                                Global.getSector().getMemoryWithoutUpdate().set(Vulpoids.KEY_SELECTED_VULPOID, person);
                                Global.getSector().getMemoryWithoutUpdate().set(Vulpoids.KEY_SELECTED_VULPOID_NAME, person.getNameString());
                                if(final_remove) {
                                    playerCargo.removeItems(CargoItemType.SPECIAL, stack.getSpecialDataIfSpecial(), 1);
                                    AddRemoveCommodity.addItemLossText(stack.getSpecialDataIfSpecial(), 1, dialog.getTextPanel());
                                }
                                FireBest.fire(null, dialog, memoryMap, "VulpoidSelected");
                                break;
                            }
                        }
                    }
                    @Override
                    public void cancelledCargoSelection() {
                    }
                    @Override
                    public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {
                        panel.addPara(final_selection_text, 0f);
                        cargo.sort();
                        for(CargoStackAPI stack : cargo.getStacksCopy()) {
                            panel.addPara("Selected: %s", 10f, Misc.getHighlightColor(), stack.getDisplayName());
                            break;
                        }
                    }
                });
    }
}
