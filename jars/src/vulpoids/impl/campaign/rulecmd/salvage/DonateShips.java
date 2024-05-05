
package vulpoids.impl.campaign.rulecmd.salvage;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class DonateShips extends BaseCommandPlugin {
    
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
        
        if (command.equals("hasFreeShip")) {
            return true;
        } else if (command.equals("selectShip")) {
            String fireOnExit = params.get(1).getString(memoryMap);
            boolean remove = false;
            if(params.size() >= 3) remove = params.get(2).getBoolean(memoryMap);
            String selection_text = null;
            if(params.size() >= 4) selection_text = params.get(3).getString(memoryMap);
            selectShip(fireOnExit, remove, selection_text);
        }
        return true;
    }
    
    public void selectShip(String fireOnExit, boolean remove, String selection_text) {
        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getFleetData().getMembersInPriorityOrder();
        if(selection_text == null) selection_text = "Select a Ship";
        final String final_selection_text = selection_text;
        final String final_fireOnExit = fireOnExit;
        
        dialog.showFleetMemberPickerDialog(final_selection_text, "Select", "Cancel", 3, 10, 64, true, false, members, new FleetMemberPickerListener() {
            @Override
            public void pickedFleetMembers(List<FleetMemberAPI> members) {
                if (members.isEmpty()) {
                    cancelledFleetMemberPicking();
                    return;
                }
                FireBest.fire(null, dialog, memoryMap, final_fireOnExit);
            }

            @Override
            public void cancelledFleetMemberPicking() {}
        });
        /*dialog.showCargoPickerDialog(final_selection_text, "Select", "Cancel", true, width, copy,
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
                                    playerCargo.removeItems(CargoAPI.CargoItemType.SPECIAL, stack.getSpecialDataIfSpecial(), 1);
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
                });*/
    }
}
