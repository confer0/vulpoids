
package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import vulpoids.characters.VulpoidPerson;

public class VulpoidChatChangeOutfitDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    static Object DEFAULT_UNIFORM = new Object();
    
    String outfitOverride = null;
    
    @Override
    public void init(InteractionDialogAPI dialog) {
        super.init(dialog);
        if(dialog.getInteractionTarget().getActivePerson() instanceof VulpoidPerson person) {
            outfitOverride = person.getOutfitOverride();
            person.setOutfitOverride(null);
        }
    }
    protected void doOnLeave() {
        if(dialog.getInteractionTarget().getActivePerson() instanceof VulpoidPerson person) {
            person.setOutfitOverride(outfitOverride);
        }
    }
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        entries.add(DEFAULT_UNIFORM);
        for(String outfitId : VulpoidPerson.outfits.keySet()) {
            VulpoidPerson.OutfitData outfit = VulpoidPerson.outfits.get(outfitId);
            if(!outfit.pickable) continue;
            for(String outfitColorId : outfit.colors.keySet()) {
                if(outfit.colors.get(outfitColorId).pickable) {
                    entries.add(outfitId);
                    break;
                }
            }
        }
    }
    
    @Override
    protected void addDescriptionText() {}
    @Override
    protected String getEntryLabel(Object entry) {
        if(entry == DEFAULT_UNIFORM) return "Default uniform";
        if(entry instanceof String outfitId) return VulpoidPerson.outfits.get(outfitId).name;
        return null;
    }

    @Override
    protected void selectEntry(Object entry) {
        if(dialog.getInteractionTarget().getActivePerson() instanceof VulpoidPerson person) {
            VulpoidPerson.FurData furData = VulpoidPerson.furColors.get(person.getFurColor());
            if(entry == DEFAULT_UNIFORM) {
                person.setOutfit(furData.defaultUniform);
                return;
            }
            if(!(entry instanceof String)) return;
            String outfitId = (String) entry;
            if("dress".equals(outfitId) && furData.unique) {
                person.setOutfit(furData.defaultDress);
                return;
            }
            // All the unique stuff is above. From here, we either pick the only option, or open the color picker if there's multiple.
            VulpoidChatChangeOutfitColorDialogPlugin colorPlugin = new VulpoidChatChangeOutfitColorDialogPlugin(outfitId);
            colorPlugin.loadOptions();
            if(colorPlugin.entries.size()==1) person.setOutfit(outfitId+"/"+colorPlugin.entries.get(0));
            else {
                colorPlugin.init(dialog, this);
                dialog.setPlugin(colorPlugin);
                //colorPlugin.optionSelected(null, OptionId.REFRESH);
            }
        }
    }

}
