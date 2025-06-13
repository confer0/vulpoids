
package vulpoids.impl.campaign.interactions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI.SkillLevelAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.impl.campaign.OfficerLevelupPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Skills;

public class VulpoidChatForgetSkillsDialogPlugin extends ListBasedInteractionDialogPlugin {
    
    // The dummy is needed to show only the relevant skills, because otherwise non-deletable skills and admin skills appear in the list.
    // But we retain the Person between each iteration, since the game politely greys out the deleted skills! :)
    PersonAPI dummyPerson;
    
    @Override
    protected void loadOptions() {
        super.loadOptions();
        PersonAPI person = dialog.getInteractionTarget().getActivePerson();
        if(dummyPerson==null) dummyPerson = Global.getFactory().createPerson();
        for(SkillLevelAPI skillLevel : person.getStats().getSkillsCopy()) {
            // impl/campaign/OfficerLevelupPluginImpl.pickLevelupSkillsV3 uses these functions to determine learnable skills.
            if (!skillLevel.getSkill().isCombatOfficerSkill()) continue;
            if (skillLevel.getSkill().hasTag(Skills.TAG_DEPRECATED)) continue;
            if (skillLevel.getSkill().hasTag(Skills.TAG_PLAYER_ONLY)) continue;
            if (skillLevel.getLevel() <= 0) continue;  // IDK if this actually comes up, not going to risk it.
            entries.add(skillLevel.getSkill());
            dummyPerson.getStats().setSkillLevel(skillLevel.getSkill().getId(), skillLevel.getLevel());
        }
    }
    
    @Override
    protected void addDescriptionText() {
        textPanel.addSkillPanel(dummyPerson, false);
    }
    @Override
    protected String getEntryLabel(Object entry) {
        return ((SkillSpecAPI)entry).getName();
    }

    @Override
    protected void selectEntry(Object entry) {
        SkillSpecAPI spec = (SkillSpecAPI) entry;
        PersonAPI person = dialog.getInteractionTarget().getActivePerson();
        person.getStats().setSkillLevel(spec.getId(), 0);
        dummyPerson.getStats().setSkillLevel(spec.getId(), 0);
        
        if(person.getStats().getLevel() == new OfficerLevelupPluginImpl().getMaxLevel(person)) {
            // Maxxed out officers apparently have zero XP. This fixes it.
            person.getStats().setXP(new OfficerLevelupPluginImpl().getXPForLevel(person.getStats().getLevel()));
        }
        person.getStats().setLevel(person.getStats().getLevel()-1);
        person.getStats().refreshCharacterStatsEffects();
        textPanel.addPara("1, 2, and... poof! "+person.getNameString()+" forgot "+spec.getName()+"!");
        loadOptions();
        optionSelected(null, OptionId.REFRESH);
    }

}
