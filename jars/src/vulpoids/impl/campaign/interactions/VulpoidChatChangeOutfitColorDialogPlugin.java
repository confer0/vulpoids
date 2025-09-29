
package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.characters.PersonAPI;
import java.awt.Color;
import vulpoids.characters.VulpoidPerson;

public class VulpoidChatChangeOutfitColorDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    String outfitId;
    
    public VulpoidChatChangeOutfitColorDialogPlugin(String outfitId) {
        super();
        this.outfitId = outfitId;
    }
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        VulpoidPerson.OutfitData outfitData = VulpoidPerson.outfits.get(outfitId);
        for(String outfitColorId : outfitData.colors.keySet()) {
            if(outfitData.colors.get(outfitColorId).pickable) {
                entries.add(outfitColorId);
            }
        }
    }
    
    @Override
    protected void addDescriptionText() {}
    @Override
    protected String getEntryLabel(Object entry) {
        return(VulpoidPerson.outfits.get(outfitId).colors.get((String)entry).name);
    }
    @Override
    protected Color getEntryColor(Object entry) {
        return(VulpoidPerson.outfits.get(outfitId).colors.get((String)entry).color);
    }
    @Override
    protected boolean doAddSelectedOptionToDialog(Object entry) {return false;}

    @Override
    protected void selectEntry(Object entry) {
        PersonAPI personAPI = dialog.getInteractionTarget().getActivePerson();
        if(personAPI instanceof VulpoidPerson person) {
            person.setOutfit(outfitId+"/"+(String)entry);
        }
    }

}
