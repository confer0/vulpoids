package vulpoids.campaign.impl.items;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI.SkillLevelAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vulpoids.impl.campaign.VulpoidCreator;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidPlugin extends BaseSpecialItemPlugin {
    
    public static String personToJson(PersonAPI person) {
        try {
            JSONObject json = new JSONObject();
            json.put("factionid", person.getFaction().getId());
            json.put("id", person.getId());
            JSONArray memory_key_array = new JSONArray();
            JSONArray memory_value_array = new JSONArray();
            JSONArray memory_expire_array = new JSONArray();
            for(String key : person.getMemoryWithoutUpdate().getKeys()) {
                memory_key_array.put(key);
                memory_value_array.put(person.getMemoryWithoutUpdate().get(key));
                memory_expire_array.put(person.getMemoryWithoutUpdate().getExpire(key));
            }
            json.put("memory_keys", memory_key_array);
            json.put("memory_values", memory_value_array);
            json.put("memory_expiries", memory_expire_array);
            //json.putOnce("memory", person.getMemoryWithoutUpdate());
            json.put("firstname", person.getName().getFirst());
            json.put("lastname", person.getName().getLast());
            json.put("portrait", person.getPortraitSprite());
            json.put("postid", person.getPostId());
            json.put("rankid", person.getRankId());
            json.put("relfloat", person.getRelToPlayer().getRel());
            //json.putOnce("stats", person.getStats());
            //json.putOnce("tags", person.getTags());
            JSONArray skill_array = new JSONArray();
            JSONArray skill_levels = new JSONArray();
            for(MutableCharacterStatsAPI.SkillLevelAPI skill : person.getStats().getSkillsCopy()) {
                if(skill.getLevel() > 0) {
                    skill_array.put(skill.getSkill().getId());
                    skill_levels.put((long)skill.getLevel());
                }
            }
            json.put("skills", skill_array);
            json.put("skill_levels", skill_levels);
            json.put("xp", person.getStats().getXP());
            json.put("bonusxp", person.getStats().getBonusXp());
            json.put("personality", person.getPersonalityAPI().getId());
            json.put("level", person.getStats().getLevel());
            return json.toString();
        } catch(JSONException e) {
            throw new RuntimeException("Unable to parse Vulpoid PersonAPI ["+person+"]", e);
        }
    }
    
    public static PersonAPI jsonToPerson(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            PersonAPI person = Global.getFactory().createPerson();
            if(json.has("factionid")) person.setFaction(json.getString("factionid"));
            if(json.has("id")) person.setId(json.getString("id"));
            if(json.has("memory_keys")) {
                JSONArray memory_key_array = json.getJSONArray("memory_keys");
                JSONArray memory_value_array = json.getJSONArray("memory_values");
                JSONArray memory_expire_array = json.getJSONArray("memory_expiries");
                for(int i=0; i<memory_key_array.length(); i++) {
                    person.getMemoryWithoutUpdate().set(memory_key_array.getString(i), memory_value_array.get(i), memory_expire_array.getLong(i));
                }
            }
            if(json.has("firstname") && json.has("lastname")) person.setName(new FullName(json.getString("firstname"), json.getString("lastname"), FullName.Gender.FEMALE));
            if(json.has("portrait")) person.setPortraitSprite(json.getString("portrait"));
            if(json.has("postid")) person.setPostId(json.getString("postid"));
            else person.setPostId(null);
            if(json.has("rankid"))person.setRankId(json.getString("rankid"));
            else person.setPostId(null);
            if(json.has("relfloat"))person.getRelToPlayer().setRel(json.getLong("relfloat"));
            if(json.has("skills")) {
                JSONArray skill_array = json.getJSONArray("skills");
                JSONArray skill_levels = null;
                if(json.has("skill_levels")) skill_levels = json.getJSONArray("skill_levels");
                for(int i=0; i<skill_array.length(); i++) {
                    int level = 1;
                    if(skill_levels!= null) level = (int) skill_levels.getLong(i);
                    person.getStats().setSkillLevel(skill_array.getString(i), level);
                }
            }
            if(json.has("xp")) person.getStats().addXP(json.getLong("xp"));
            if(json.has("bonusxp")) person.getStats().setBonusXp(json.getLong("bonusxp"));
            if(json.has("personality")) person.setPersonality(json.getString("personality"));
            if(json.has("level")) person.getStats().setLevel(json.getInt("level"));
            //for (String tag : (Set<String>)json.getJSONArray("tags")) person.addTag(tag);
            return person;
        }  catch(JSONException e) {
            throw new RuntimeException("Unable to parse Vulpoid person json ["+jsonStr+"]", e);
        }
    }
    
    final String[] random_assignments = new String[]{
        "Idle: Polishing some vibroknives.",
        "Idle: Degaussing the flux capacitors.",
        "Idle: Inspecting the life support.",
        "Idle: Conducting field manipulation research.",
        "Idle: Conducting particle physics research.",
        "Idle: Conducting biology research.",
        "Idle: Conducting industrial research.",
        "Idle: Conducting materials research.",
        "Idle: Brushing her fur.",
        "Idle: Relaxing.",
        "Idle: Slacking off.",
        "Idle: Distracting the crew.",
    };
    final String[] sleeper_assignments = new String[] {
        "Cryosleeping: Dreaming of synthetic sheep.",
    };
    
    
    PersonAPI person;
    public PersonAPI getPerson() {
        refreshPerson();  // Just to be safe
        return person;
    }
    
    public VulpoidPlugin() {
        super();
        //person = Global.getFactory().createPerson();
    }
    
    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        String jsonStr = stack.getSpecialDataIfSpecial().getData(); 
        if (jsonStr == null) {
            PersonAPI new_person = VulpoidCreator.createPrefectoVulpoid(null);
            jsonStr = personToJson(new_person);
            stack.getSpecialDataIfSpecial().setData(jsonStr);
        }
        person = jsonToPerson(jsonStr);
        
        
        String role_portrait = null;
        switch(getId()) {
            case Vulpoids.SPECIAL_ITEM_DEFAULT:
                role_portrait = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_PORTRAIT);
                break;
            case Vulpoids.SPECIAL_ITEM_EMBARKED:
                role_portrait = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_PORTRAIT);
                break;
            case Vulpoids.SPECIAL_ITEM_OFFICER:
                role_portrait = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_OFFICER_PORTRAIT);
                break;
            case Vulpoids.SPECIAL_ITEM_ADMIN:
                role_portrait = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_DEFAULT_PORTRAIT);
                break;
        }
        if (role_portrait != null) person.setPortraitSprite(role_portrait);
        
        if(Vulpoids.SPECIAL_ITEM_ADMIN.equals(getId())) {
            person.getMemoryWithoutUpdate().set("$ome_isAdmin", true);
            person.getMemoryWithoutUpdate().set("$ome_adminTier", 1);  // TODO - make smarter?
        } else {
            person.getMemoryWithoutUpdate().unset("$ome_isAdmin");
            person.getMemoryWithoutUpdate().unset("$ome_adminTier");
        }
        
        refreshPerson();
    }
    
    public void refreshPerson() {
        if(Global.getSector().getPlayerFleet() != null && getId().equals(Vulpoids.SPECIAL_ITEM_OFFICER)) {
            boolean found_match = false;
            for (OfficerDataAPI officer : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
                if (officer.getPerson().getId().equals(person.getId())) {
                    found_match = true;
                    person = officer.getPerson();
                    break;
                }
            }
            if (!found_match) {
                Global.getSector().getPlayerFleet().getFleetData().addOfficer(person);
            }
        }
        
        if(Global.getSector().getCharacterData() != null && getId().equals(Vulpoids.SPECIAL_ITEM_ADMIN)) {
            boolean found_match = false;
            for (AdminData admin : Global.getSector().getCharacterData().getAdmins()) {
                if (admin.getPerson().getId().equals(person.getId())) {
                    found_match = true;
                    person = admin.getPerson();
                    break;
                }
            }
            if (!found_match) {
                Global.getSector().getCharacterData().addAdmin(person);
            }
        }
    }
    
    
    
    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        //return super.getPrice(market, submarket);
        // Price is increased for every skill!
        // There's a baseline of four 'skills' that's just the headers, plus the two built-in.
        int price_per_skill = 7500;
        return price_per_skill * person.getStats().getSkillsCopy().size();
    }
    
    @Override
    public String getName() {
        refreshPerson();
        
        return person.getNameString();
    }
    
    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        float cx = x+w/2;
        float cy = y+h/2;
        float blX = cx-40;
        float blY = cy-40;
        float tlX = cx-40;
        float tlY = cy+40;
        float trX = cx+40;
        float trY = cy+40;
        float brX = cx+40;
        float brY = cy-40;

        // TODO - use a proper icon instead.
        //SpriteAPI sprite = Global.getSettings().getSprite(person.getPortraitSprite());
        SpriteAPI sprite = Global.getSettings().getSprite("cargo", "vulp_shiny");
        // TODO - this should be somewhere else.
        String person_sprite = person.getMemoryWithoutUpdate().getString(Vulpoids.KEY_CARGO_ICON);
        if(person_sprite != null) sprite = Global.getSettings().getSprite(person_sprite);
        //sprite.setAlphaMult(alphaMult);
        //sprite.setNormalBlend();
        sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
        if(Vulpoids.SPECIAL_ITEM_EMBARKED.equals(getId())) {
            sprite = Global.getSettings().getSprite("cargo", "vulp_embarked_icon");
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            
            if(disallowCycleReason()!=null) {
                sprite = Global.getSettings().getSprite("cargo", "vulp_lock_icon");
                sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            }
        }
        if(Vulpoids.SPECIAL_ITEM_OFFICER.equals(getId())) {
            sprite = Global.getSettings().getSprite("cargo", "vulp_officer_icon");
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            
            if(disallowCycleReason()!=null) {
                sprite = Global.getSettings().getSprite("cargo", "vulp_lock_icon");
                sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            }
        }
        else if(Vulpoids.SPECIAL_ITEM_ADMIN.equals(getId())) {
            sprite = Global.getSettings().getSprite("cargo", "vulp_admin_icon");
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            if(disallowCycleReason()!=null) {
                sprite = Global.getSettings().getSprite("cargo", "vulp_lock_icon");
                sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            }
        }
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        refreshPerson();

        float pad = 3f;
        float opad = 10f;
        float small = 5f;
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color b = Misc.getButtonTextColor();
        b = Misc.getPositiveHighlightColor();
        
        Color body_color;
        Color text_color;
        switch(getId()) {
            case Vulpoids.SPECIAL_ITEM_DEFAULT:
                body_color = new Color(127,127,127);
                text_color = new Color(255,255,255);
                break;
            case Vulpoids.SPECIAL_ITEM_EMBARKED:
                body_color = new Color(125,175,240);
                text_color = new Color(255,255,255);
                break;
            case Vulpoids.SPECIAL_ITEM_OFFICER:
                body_color = new Color(240,130,130);
                text_color = new Color(255,255,255);
                break;
            case Vulpoids.SPECIAL_ITEM_ADMIN:
                body_color = new Color(130,240,200);
                text_color = new Color(255,255,255);
                break;
            default:
                body_color = new Color(226, 143, 173);
                text_color = new Color(255,255,255);
                break;
        }
        
        
        TooltipMakerAPI portrait = tooltip.beginImageWithText(person.getPortraitSprite(), 128, tooltip.getWidthSoFar(), false);
        portrait.addTitle(getName(), body_color);
        portrait.addRelationshipBar(person, pad);
        switch (getId()) {
            case Vulpoids.SPECIAL_ITEM_DEFAULT:
                //portrait.addPara("Profecto Vulpoids are a rare, highly intelligent mutation. They retain a powerful innate "+
                //        "desire to help humans, though are self-aware enough to resist it. Considered beta-level AI, and "+
                //        "extremely valuable.", opad);
                portrait.addPara("Profecto Vulpoids are a rare, highly intelligent mutation. They're considered beta-level AI, "+
                        "yet are easily disguised as an ordinary Vulpoid. Important people would pay fortunes to own even one.", opad);
                break;
            case Vulpoids.SPECIAL_ITEM_EMBARKED:
                // TODO - Not really happy with this. Could be better.
                portrait.addPara("Profecto Vulpoids retain a powerful innate desire to help humans, though are self-aware "+
                        "enough to resist it. If one earns their trust, their psychology makes their loyalty unbreakable.", opad);
                break;
            case Vulpoids.SPECIAL_ITEM_OFFICER:
                portrait.addPara("Profecto Vulpoids can make for strong spacecraft commanders, able to think and act at speeds "+
                        "most humans cannot match without cybernetics. While they are not created with a captain's skillset, they "+
                        "learn quickly.", opad);
                break;
            case Vulpoids.SPECIAL_ITEM_ADMIN:
                portrait.addPara("Profecto Vulpoids make for natural colony administrators. Their inherent social and emotional "+
                        "awareness makes them capable negotiators and people-pleasers. Few humans can hope to match their "+
                        "aptitude.", opad);
                break;
        }
        tooltip.addImageWithText(opad);
        
        
        String assignment_title = "Assignment";
        switch(getId()) {
            case Vulpoids.SPECIAL_ITEM_DEFAULT: assignment_title = "No Assignment"; break;
            case Vulpoids.SPECIAL_ITEM_EMBARKED: assignment_title = "Embarked on Fleet"; break;
            case Vulpoids.SPECIAL_ITEM_OFFICER: assignment_title = "Serving as Officer"; break;
            case Vulpoids.SPECIAL_ITEM_ADMIN: assignment_title = "Serving as Administrator"; break;
        }
        tooltip.addSectionHeading(assignment_title, text_color, body_color, Alignment.MID, opad);
        if(!expanded) {
            String assignment_text = disallowCycleReason();
            if (assignment_text != null) {
                tooltip.addPara(assignment_text, opad);
            } else {
                if(Vulpoids.SPECIAL_ITEM_DEFAULT.equals(getId())) assignment_text = sleeper_assignments[new Random(stack.hashCode()).nextInt(sleeper_assignments.length)];
                else assignment_text = random_assignments[new Random(stack.hashCode()).nextInt(random_assignments.length)];
                tooltip.addPara(assignment_text, Misc.getGrayColor(), opad);
            }
        } else {
            switch(getId()) {
                case Vulpoids.SPECIAL_ITEM_DEFAULT: tooltip.addPara("Stored in suspended animation for commercial transport. Can be sold or stored, but will not escape if the fleet is destroyed.", opad); break;
                case Vulpoids.SPECIAL_ITEM_EMBARKED: tooltip.addPara("Formally embarked with an executive suite. Her escape pod will follow yours if the fleet is lost.", opad); break;
                case Vulpoids.SPECIAL_ITEM_OFFICER: tooltip.addPara("Available as an officer. Will expect pay - in credits, not just the usual headpats - even if not currently commanding a ship.", opad); break;
                case Vulpoids.SPECIAL_ITEM_ADMIN: tooltip.addPara("Available as an administrator. Will expect pay - in credits, not just the usual headpats - even if not currently administrating a colony.", opad); break;
            }
        }
        
        ArrayList<SkillLevelAPI> skills = new ArrayList(person.getStats().getSkillsCopy());
        Collections.sort(skills, new Comparator<SkillLevelAPI>() {
            @Override
            public int compare(SkillLevelAPI o1, SkillLevelAPI o2) {
                return (int)(o1.getSkill().getOrder() - o2.getSkill().getOrder());
            }
        });
        
        //BaseEventIntel :)
        
        TooltipMakerAPI skillTooltip = tooltip.beginSubTooltip(tooltip.getWidthSoFar());
        
        TooltipMakerAPI officerTooltip = skillTooltip.beginSubTooltip(tooltip.getWidthSoFar()/2-opad);
        Color highlight_color = text_color;
        if(Vulpoids.SPECIAL_ITEM_OFFICER.equals(getId())) highlight_color = Misc.getHighlightColor();
        officerTooltip.addSectionHeading("Combat Skills", highlight_color, body_color, Alignment.MID, 0);
        addSkillsToTooltip(officerTooltip, skills, expanded, true, false, opad);
        skillTooltip.endSubTooltip();
        
        TooltipMakerAPI adminTooltip = skillTooltip.beginSubTooltip(tooltip.getWidthSoFar()/2-opad);
        highlight_color = text_color;
        if(Vulpoids.SPECIAL_ITEM_ADMIN.equals(getId())) highlight_color = Misc.getHighlightColor();
        adminTooltip.addSectionHeading("Industrial Skills", highlight_color, body_color, Alignment.MID, 0);
        addSkillsToTooltip(adminTooltip, skills, expanded, false, true, opad);
        skillTooltip.endSubTooltip();
        
        float factorHeight = Math.max(adminTooltip.getHeightSoFar(), officerTooltip.getHeightSoFar());
        adminTooltip.setHeightSoFar(factorHeight);
        officerTooltip.setHeightSoFar(factorHeight);
        
        skillTooltip.addCustom(officerTooltip, 0);
	skillTooltip.addCustomDoNotSetPosition(adminTooltip).getPosition().rightOfTop(officerTooltip, opad);
        skillTooltip.setHeightSoFar(factorHeight);
        
        tooltip.endSubTooltip();
        tooltip.addCustom(skillTooltip, opad);
        
        tooltip.addPara("Market value: %s", opad, Misc.getHighlightColor(), Misc.getDGSCredits(getPrice(null, null)));
        tooltip.addPara("Right-click to cycle jobs", Misc.getHighlightColor(), opad);
    }
    
    private void addSkillsToTooltip(TooltipMakerAPI tooltip, ArrayList<SkillLevelAPI> skills, boolean expanded, boolean officer, boolean admin, float pad) {
        ArrayList<SkillLevelAPI> valid_skills = new ArrayList();
        ArrayList<SkillLevelAPI> elite_skills = new ArrayList();
        for (SkillLevelAPI skill : skills) {
            if ( skill.getLevel()>0 &&
                    ((officer && skill.getSkill().isCombatOfficerSkill()) ||
                    (admin && skill.getSkill().isAdminSkill())) ) {
                valid_skills.add(skill);
                if (skill.getLevel()>1) elite_skills.add(skill);
            }
        }
        if (valid_skills.isEmpty()) {
            tooltip.addPara("None", pad);
        } else {
            for (SkillLevelAPI skill : valid_skills) {
                TooltipMakerAPI image = tooltip.beginImageWithText(skill.getSkill().getSpriteName(), 36);
                if(elite_skills.contains(skill)) image.addPara("Elite "+skill.getSkill().getName(), Misc.getStoryOptionColor(), 0);
                else image.addPara(skill.getSkill().getName(), 0);
                tooltip.addImageWithText(pad);
            }
        }
    }

    @Override
    public float getTooltipWidth() {
        return super.getTooltipWidth();
    }

    @Override
    public boolean isTooltipExpandable() {
        return true;
    }

    @Override
    public boolean hasRightClickAction() {
        return true;
    }
    
    
    private static boolean stacksHaveSamePerson(CargoStackAPI a, CargoStackAPI b) {
        if(a.getPlugin() != b.getPlugin()) return false;
        try {
            JSONObject json_a = new JSONObject(a.getSpecialDataIfSpecial().getData());
            JSONObject json_b = new JSONObject(b.getSpecialDataIfSpecial().getData());
            return json_a.get("id").equals(json_b.get("id"));
        }  catch(JSONException e) {
            return false;
        }
    }
    
    private boolean isInPlayerCargo() {
        for(CargoStackAPI player_stack : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
            if (player_stack.isSpecialStack() && stacksHaveSamePerson(stack, player_stack)) {
                stack = player_stack; // It gets desynced when moved to another slot.
                return true;
            }
        }
        return false;
    }
    
    private String disallowCycleReason() {
        if (!isInPlayerCargo()) return "Not currently in your fleet.";
        
        if(getId().equals(Vulpoids.SPECIAL_ITEM_OFFICER)) {
            for (FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                if (ship.getCaptain()!=null && person.getId().equals(ship.getCaptain().getId())) return "Currently captaining the "+ship.getShipName()+".";
            }
        }
        
        if(getId().equals(Vulpoids.SPECIAL_ITEM_ADMIN)) {
            /*for (AdminData admin : Global.getSector().getCharacterData().getAdmins()) {
                if (person.getId().equals(admin.getPerson().getId())) {
                    if (admin.getMarket() != null) return "Currently administrating "+admin.getMarket().getName()+".";
                }
            }*/
            if (person.getMarket()!=null) return "Currently administrating "+person.getMarket().getName()+".";
        }
        
        return null;
    }
    
    @Override
    public boolean shouldRemoveOnRightClickAction() {
        refreshPerson();
        return disallowCycleReason()==null;
    }

    @Override
    public void performRightClickAction() {
        String disallowReason = disallowCycleReason();
        if (disallowReason==null) {
            String new_id = "";
            switch(getId()) {
                case Vulpoids.SPECIAL_ITEM_DEFAULT:
                    new_id = Vulpoids.SPECIAL_ITEM_EMBARKED;
                    break;
                case Vulpoids.SPECIAL_ITEM_EMBARKED:
                    new_id = Vulpoids.SPECIAL_ITEM_OFFICER;
                    break;
                case Vulpoids.SPECIAL_ITEM_OFFICER:
                    Global.getSector().getPlayerFleet().getFleetData().removeOfficer(person);
                    new_id = Vulpoids.SPECIAL_ITEM_ADMIN;
                    break;
                case Vulpoids.SPECIAL_ITEM_ADMIN:
                    Global.getSector().getCharacterData().removeAdmin(person);
                    new_id = Vulpoids.SPECIAL_ITEM_DEFAULT;
                    break;
            }
            Global.getSoundPlayer().playUISound("ui_cargo_crew", 1f, 1f);
            stack.getCargo().addSpecial(new SpecialItemData(new_id, personToJson(person)), 1);
        } else {
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(disallowReason, Misc.getNegativeHighlightColor());
        }
    }
}



