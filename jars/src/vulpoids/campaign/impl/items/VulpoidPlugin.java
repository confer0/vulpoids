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
            for(MutableCharacterStatsAPI.SkillLevelAPI skill : person.getStats().getSkillsCopy()) {
                if(skill.getLevel() > 0) skill_array.put(skill.getSkill().getId());
            }
            json.put("skills", skill_array);
            json.put("xp", person.getStats().getXP());
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
            // TODO MEMORY - Need to transfer over each key and expiry individually.
            if(json.has("firstname") && json.has("lastname")) person.setName(new FullName(json.getString("firstname"), json.getString("lastname"), FullName.Gender.FEMALE));
            if(json.has("portrait")) person.setPortraitSprite(json.getString("portrait"));
            if(json.has("postid")) person.setPostId(json.getString("postid"));
            if(json.has("rankid"))person.setRankId(json.getString("rankid"));
            if(json.has("relfloat"))person.getRelToPlayer().setRel(json.getLong("relfloat"));
            if(json.has("skills")) {
                JSONArray skill_array = json.getJSONArray("skills");
                for(int i=0; i<skill_array.length(); i++) {
                    person.getStats().setSkillLevel(skill_array.getString(i), 1);
                }
            }
            if(json.has("xp")) person.getStats().addXP(json.getLong("xp"));
            //for (String tag : (Set<String>)json.getJSONArray("tags")) person.addTag(tag);
            return person;
        }  catch(JSONException e) {
            throw new RuntimeException("Unable to parse Vulpoid person json ["+jsonStr+"]", e);
        }
    }
    
    final String[] random_assignments = new String[]{
        "Polishing some vibroknives.",
        "Degaussing the flux capacitors.",
        "Inspecting the life support.",
        "Conducting field manipulation research.",
        "Conducting particle physics research.",
        "Conducting biology research.",
        "Conducting industrial research.",
        "Conducting materials research.",
        "Brushing her fur.",
        "Relaxing.",
        "Slacking off.",
        "Distracting the crew.",
    };
    
    
    PersonAPI person;
    
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
        String person_sprite = person.getMemoryWithoutUpdate().getString("$vulp_cargoIcon");
        if(person_sprite != null) sprite = Global.getSettings().getSprite(person_sprite);
        //if(person.getPortraitSprite().contains("winter")) sprite = Global.getSettings().getSprite("graphics/icons/cargo/vulpoid_shiny_winter.png");
        //if(person.getPortraitSprite().contains("terran")) sprite = Global.getSettings().getSprite("graphics/icons/cargo/vulpoid_shiny_terran.png");
        //if(person.getPortraitSprite().contains("desert")) sprite = Global.getSettings().getSprite("graphics/icons/cargo/vulpoid_shiny_desert.png");
        
        //sprite.setAlphaMult(alphaMult);
        //sprite.setNormalBlend();
        sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
        if(Vulpoids.SPECIAL_ITEM_EMBARKED.equals(getId())) {
            sprite = Global.getSettings().getSprite("cargo", "vulp_embarked_icon");
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            
            if(disallowCycleReason()!=null) {
                sprite.renderWithCorners(blX-32, blY-64, tlX-32, tlY-64, trX-32, trY-64, brX-32, brY-64);
            }
        }
        if(Vulpoids.SPECIAL_ITEM_OFFICER.equals(getId())) {
            sprite = Global.getSettings().getSprite("cargo", "vulp_officer_icon");
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            
            if(disallowCycleReason()!=null) {
                sprite.renderWithCorners(blX-32, blY-64, tlX-32, tlY-64, trX-32, trY-64, brX-32, brY-64);
            }
        }
        else if(Vulpoids.SPECIAL_ITEM_ADMIN.equals(getId())) {
            sprite = Global.getSettings().getSprite("cargo", "vulp_admin_icon");
            sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
            if(disallowCycleReason()!=null) {
                sprite.renderWithCorners(blX-32, blY-64, tlX-32, tlY-64, trX-32, trY-64, brX-32, brY-64);
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
        
        Color pink = new Color(226, 143, 173);
        Color darkpink = new Color(255, 91, 165);
        //tooltip.addTitle(getName(), pink);
        
        /*String design = getDesignType();
        if (design != null) {
            Misc.addDesignTypePara(tooltip, design, 10f);
        }*/
        
        TooltipMakerAPI portrait = tooltip.beginImageWithText(person.getPortraitSprite(), 128, tooltip.getWidthSoFar(), false);
        String role = "";
        if (Vulpoids.SPECIAL_ITEM_EMBARKED.equals(getId())) role = "Passenger ";
        if (Vulpoids.SPECIAL_ITEM_OFFICER.equals(getId())) role = "Officer ";
        if (Vulpoids.SPECIAL_ITEM_ADMIN.equals(getId())) role = "Administrator ";
        portrait.addTitle(role+getName(), pink);
        portrait.addRelationshipBar(person, pad);
        if (null != getId()) switch (getId()) {
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
            default:
                break;
        }
        tooltip.addImageWithText(opad);
        
        
        tooltip.addSectionHeading("Assignment", pink, darkpink, Alignment.MID, opad);
        String assignment = disallowCycleReason();
        if (assignment != null) {
            tooltip.addPara(assignment, opad);
        } else {
            assignment = random_assignments[new Random(stack.hashCode()).nextInt(random_assignments.length)];
            tooltip.addPara(assignment, Misc.getGrayColor(), opad);
        }
        
        tooltip.addSectionHeading("Skills", pink, darkpink, Alignment.MID, opad);
        
        for (SkillLevelAPI skill : person.getStats().getSkillsCopy()) {
            if(skill.getLevel() > 0) {
                tooltip.beginImageWithText(skill.getSkill().getSpriteName(), 36).addPara(skill.getSkill().getName(), pad);
                tooltip.addImageWithText(opad);
            }
        }
        
        //tooltip.addSkillPanel(person, opad);
        
        /*if (!spec.getDesc().isEmpty()) {
            tooltip.addPara(spec.getDesc(), Misc.getTextColor(), opad);
        }

        addCostLabel(tooltip, opad, transferHandler, stackSource);

        tooltip.addPara("Right-click to integrate the " + getName() + " with your fleet", b, opad);
        switch(getId()) {
            case Vulpoids.SPECIAL_ITEM_DEFAULT: tooltip.addPara("IDLE", opad); break;
            case Vulpoids.SPECIAL_ITEM_OFFICER: tooltip.addPara("OFFICER", opad); break;
            case Vulpoids.SPECIAL_ITEM_ADMIN: tooltip.addPara("ADMIN", opad); break;
        }
        tooltip.addPara("Item Id: "+getId(), b, opad);
        tooltip.addPara("Item Spec Name: "+getSpec().getName(), b, opad);
        tooltip.addPara("Data: "+stack.getSpecialDataIfSpecial().getData(), b, opad);
        tooltip.addPara("Person ID: "+person.getId(), b, opad);
        tooltip.addPara("Person Name: "+person.getNameString(), b, opad);
        tooltip.addPara("Admin Number: "+person.getStats().getAdminNumber().getModifiedInt(), b, opad);
        
        //tooltip.addTitle("Skill Panel");
        //tooltip.addSkillPanel(person, opad);  // Doesn't seem to work?
        for (SkillLevelAPI skill : person.getStats().getSkillsCopy()) {
            if(skill.getLevel() > 0) tooltip.addImage(skill.getSkill().getSpriteName(), opad);
        }
        //tooltip.addImages(64, 64, opad, 64, strings);*/
        //tooltip.addPara(stack.getSpecialDataIfSpecial().getData(), b, opad);
        tooltip.addPara("Market value: %s", opad, Misc.getHighlightColor(), Misc.getDGSCredits(getPrice(null, null)));
        tooltip.addPara("Right-click to cycle jobs", Misc.getHighlightColor(), opad);
    }

    @Override
    public float getTooltipWidth() {
        return super.getTooltipWidth();
    }

    @Override
    public boolean isTooltipExpandable() {
        return false;
    }

    @Override
    public boolean hasRightClickAction() {
        return true;
    }
    
    
    private static boolean stacksHaveSamePerson(CargoStackAPI a, CargoStackAPI b) {
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
            stack.getCargo().addSpecial(new SpecialItemData(new_id, personToJson(person)), 1);
        } else {
            Global.getSector().getCampaignUI().getMessageDisplay().addMessage(disallowReason, Misc.getNegativeHighlightColor());
        }
    }
}



