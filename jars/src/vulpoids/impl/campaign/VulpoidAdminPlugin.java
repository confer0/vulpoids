package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;

public class VulpoidAdminPlugin implements AICoreAdminPlugin {

    public PersonAPI createPerson(String aiCoreId, String factionId, long seed) {
        PersonAPI person = VulpoidCreator.createVulpoid(null);
        person.setFaction(factionId);
        person.setAICoreId(aiCoreId);
        person.setName(new FullName("Profecto Vulpoid", "", FullName.Gender.FEMALE));
        //person.setRankId(null);
        person.setPostId(Ranks.POST_ADMINISTRATOR);
        person.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
        person.getStats().setSkillLevel("vulpoid_brain", 1);
        person.getStats().setSkillLevel("vulpoid_luxury", 1);
        person.getMemoryWithoutUpdate().set("$isVulpoidAdmin", true);
        return person;
    }
}