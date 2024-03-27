package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;

public class VulpoidAdminPlugin implements AICoreAdminPlugin {

    public PersonAPI createPerson(String aiCoreId, String factionId, long seed) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setFaction(factionId);
        person.setAICoreId(aiCoreId);
        person.setName(new FullName("Vulpoid", "", FullName.Gender.ANY));
        person.setPortraitSprite("graphics/portraits/vulpoid.png");

        person.setRankId(null);
        person.setPostId(Ranks.POST_ADMINISTRATOR);

        person.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
        //person.getStats().setSkillLevel(Skills.HYPERCOGNITION, 1);
        person.getStats().setSkillLevel("vulpoid_brain", 1);

        return person;
    }
}