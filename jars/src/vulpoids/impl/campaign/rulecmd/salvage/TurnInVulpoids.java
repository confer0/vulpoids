package vulpoids.impl.campaign.rulecmd.salvage;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.CustomRepImpact;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActionEnvelope;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import static com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin.getEntityMemory;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import vulpoids.campaign.impl.items.VulpoidPlugin;
import vulpoids.impl.campaign.ids.Vulpoids;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceEventIntel;
import vulpoids.impl.campaign.intel.events.VulpoidAcceptanceProfectoFactor;

/**
 * NotifyEvent $eventHandle <params> 
 * 
 */
public class TurnInVulpoids extends BaseCommandPlugin {
	
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected PersonAPI person;
    protected FactionAPI faction;

    protected boolean buysAICores;
    protected float valueMult;
    protected float repMult;
    protected float acceptanceMult;

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {

            this.dialog = dialog;
            this.memoryMap = memoryMap;

            String command = params.get(0).getString(memoryMap);
            if (command == null) return false;

            memory = getEntityMemory(memoryMap);

            entity = dialog.getInteractionTarget();
            text = dialog.getTextPanel();
            options = dialog.getOptionPanel();

            playerFleet = Global.getSector().getPlayerFleet();
            playerCargo = playerFleet.getCargo();

            playerFaction = Global.getSector().getPlayerFaction();
            entityFaction = entity.getFaction();

            person = dialog.getInteractionTarget().getActivePerson();
            faction = person.getFaction();

            buysAICores = faction.getCustomBoolean("buysVulpoids") || faction.getCustomBoolean("buysAICores");
            
            if(faction.getCustom().has("vulpoidValueMult")) {
                valueMult = faction.getCustomFloat("vulpoidValueMult");
            } else {
                valueMult = faction.getCustomFloat("AICoreValueMult");
            }
            
            if(faction.getCustom().has("vulpoidRepMult")) {
                repMult = faction.getCustomFloat("vulpoidRepMult");
            } else {
                repMult = faction.getCustomFloat("AICoreRepMult");
            }
            
            if(faction.getCustom().has("vulpoidAccMult")) {
                acceptanceMult = faction.getCustomFloat("vulpoidAccMult");
            } else {
                acceptanceMult = 1f;
            }
            

            if(command.equals("selectVulpoids")) {
                    selectVulpoids();
            } else if (command.equals("playerHasVulpoids")) {
                    return playerHasVulpoids();
            } else if (command.equals("personCanAcceptVulpoids")) {
                    return personCanAcceptVulpoids();
            }

            return true;
    }

    // TODO - Change these?
    protected boolean personCanAcceptVulpoids() {
            if (person == null || !buysAICores) return false;

            return Ranks.POST_BASE_COMMANDER.equals(person.getPostId()) ||
                       Ranks.POST_STATION_COMMANDER.equals(person.getPostId()) ||
                       Ranks.POST_ADMINISTRATOR.equals(person.getPostId()) ||
                       Ranks.POST_OUTPOST_COMMANDER.equals(person.getPostId());
    }

    protected void selectVulpoids() {
        CargoAPI copy = Global.getFactory().createCargo(false);
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            SpecialItemSpecAPI spec = stack.getSpecialItemSpecIfSpecial();
            if(spec != null && spec.getId().equals(Vulpoids.SPECIAL_ITEM_DEFAULT)) {
                copy.addFromStack(stack);
            }
        }
        copy.sort();

        final float width = 310f;
        dialog.showCargoPickerDialog("Select Vulpoids to turn in", "Confirm", "Cancel", true, width, copy, new CargoPickerListener() {
            public void pickedCargo(CargoAPI cargo) {
                if (cargo.isEmpty()) {
                    cancelledCargoSelection();
                    return;
                }

                cargo.sort();
                for (CargoStackAPI stack : cargo.getStacksCopy()) {
                    playerCargo.removeItems(stack.getType(), stack.getData(), stack.getSize());
                    if (stack.isCommodityStack()) { // should be always, but just in case
                        int num = (int) stack.getSize();
                        AddRemoveCommodity.addCommodityLossText(stack.getCommodityId(), num, text);

                        String key = "$turnedIn_" + stack.getCommodityId();
                        int turnedIn = faction.getMemoryWithoutUpdate().getInt(key);
                        faction.getMemoryWithoutUpdate().set(key, turnedIn + num);

                        String key2 = "$turnedIn_allVulpoids";
                        int turnedIn2 = faction.getMemoryWithoutUpdate().getInt(key2);
                        faction.getMemoryWithoutUpdate().set(key2, turnedIn2 + num);
                    }
                }

                float bounty = computeCreditValue(cargo);
                float repChange = computeReputationValue(cargo);

                if (bounty > 0) {
                    playerCargo.getCredits().add(bounty);
                    AddRemoveCommodity.addCreditsGainText((int)bounty, text);
                }

                if (repChange >= 1f) {
                    CustomRepImpact impact = new CustomRepImpact();
                    impact.delta = repChange * 0.01f;
                    Global.getSector().adjustPlayerReputation(
                            new RepActionEnvelope(RepActions.CUSTOM, impact, null, text, true), faction.getId());

                    // Vulpoids give the full rep impact for the person.
                    impact.delta *= 1.0f;//0.25f;
                    if (impact.delta >= 0.01f) {
                        Global.getSector().adjustPlayerReputation(
                                new RepActionEnvelope(RepActions.CUSTOM, impact, null, text, true), person);
                    }
                }
                
                // Vulpoid Acceptance
                int acceptance = (int)computeAcceptanceValue(cargo);
                if(acceptance>0) {
                    VulpoidAcceptanceEventIntel.addFactorCreateIfNecessary(new VulpoidAcceptanceProfectoFactor("Profecto sale to "+person.getNameString(), acceptance), dialog);
                }
                
                FireBest.fire(null, dialog, memoryMap, "VulpoidsTurnedIn");
            }
            public void cancelledCargoSelection() {
            }
            public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {

                float bounty = computeCreditValue(combined);
                float repChange = computeReputationValue(combined);

                float pad = 3f;
                float opad = 10f;

                panel.setParaFontOrbitron();
                panel.addPara(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor(), 1f);
                //panel.addTitle(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor());
                //panel.addPara(faction.getDisplayNameLong(), faction.getBaseUIColor(), opad);
                //panel.addPara(faction.getDisplayName() + " (" + entity.getMarket().getName() + ")", faction.getBaseUIColor(), opad);
                panel.setParaFontDefault();

                panel.addImage(faction.getLogo(), width * 1f, 3f);


                //panel.setParaFontColor(Misc.getGrayColor());
                //panel.setParaSmallInsignia();
                //panel.setParaInsigniaLarge();
                panel.addPara("Compared to dealing with other factions, turning Vulpoids in to " +
                        faction.getDisplayNameLongWithArticle() + " " +
                        "will result in:", opad);
                panel.beginGridFlipped(width, 1, 40f, 10f);
                //panel.beginGrid(150f, 1);
                panel.addToGrid(0, 0, "Bounty value", "" + (int)(valueMult * 100f) + "%");
                panel.addToGrid(0, 1, "Reputation gain", "" + (int)(repMult * 100f) + "%");
                panel.addGrid(pad);

                panel.addPara("If you turn in the selected Vulpoids, you will receive a %s bounty " +
                        "and your standing with " + faction.getDisplayNameWithArticle() + " will improve by %s points.",
                        opad * 1f, Misc.getHighlightColor(),
                        Misc.getWithDGS(bounty) + Strings.C,
                        "" + (int) repChange);

            }
        });
    }

    protected float computeCreditValue(CargoAPI cargo) {
        float bounty = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            if (stack.getPlugin() instanceof VulpoidPlugin) {
                bounty += ((VulpoidPlugin)stack.getPlugin()).getPrice(null, null);
            }
        }
        bounty *= valueMult;
        return bounty;
    }

    protected float computeReputationValue(CargoAPI cargo) {
        float rep = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            if (stack.getPlugin() instanceof VulpoidPlugin) {
                rep += ((VulpoidPlugin)stack.getPlugin()).getTurnInRep();
            }
        }
        rep *= repMult;
        return rep;
    }
    
    protected float computeAcceptanceValue(CargoAPI cargo) {
        int acc = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            if (stack.getPlugin() instanceof VulpoidPlugin) {
                acc += ((VulpoidPlugin)stack.getPlugin()).getTurnInRep();
            }
        }
        acc *= 10;  // Note: gets 90 off of Laisa - good! Forces you to actually produce Vulpoids before people regulate.
        acc *= acceptanceMult;
        return acc;
    }
    
    protected boolean playerHasVulpoids() {
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            SpecialItemSpecAPI spec = stack.getSpecialItemSpecIfSpecial();
            if(spec != null && spec.getId().equals(Vulpoids.SPECIAL_ITEM_DEFAULT)) {
                return true;
            }
        }
        return false;
    }
}

