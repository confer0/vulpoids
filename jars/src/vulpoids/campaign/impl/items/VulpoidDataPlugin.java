package vulpoids.campaign.impl.items;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VulpoidDataPlugin extends BaseSpecialItemPlugin {
    
    String title;
    String author;
    String publicationDate;
    String description;
    
    static String DEFAULT_TITLE = "Unknown Work";
    static String DEFAULT_AUTHOR = "Unknown";
    static String DEFAULT_DATE = "Unknown";
    static String DEFAULT_DESCRIPTION = "The contents of this datapad are encrypted or corrupted, but are still valued by speculators and dataminers.";
    
    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        JSONObject json;
        try {
            json = new JSONObject(stack.getSpecialDataIfSpecial().getData());
        } catch (JSONException ex) {
            // If we get a JSONException, assume we're being passed the author name.
            json = new JSONObject();
            try {
                author = stack.getSpecialDataIfSpecial().getData();
                generateNewStory();
                json.put("title", title);
                json.put("author", author);
                json.put("publicationDate", Global.getSector().getClock().getDateString());
                json.put("description", description);
            } catch (JSONException ex1) {}
        }
        try {title = json.getString("title");} catch (JSONException ex) {title = DEFAULT_TITLE;}
        try {author = json.getString("author");} catch (JSONException ex) {author = DEFAULT_AUTHOR;}
        try {publicationDate = json.getString("publicationDate");} catch (JSONException ex) {publicationDate = DEFAULT_DATE;}
        try {description = json.getString("description");} catch (JSONException ex) {description = DEFAULT_DESCRIPTION;}
        stack.getSpecialDataIfSpecial().setData(json.toString());
    }
    
    private void generateNewStory() {
        int quality = Integer.parseInt(getSpec().getParams());
        Random random = new Random();
        try {
            JSONObject writerJson = Global.getSettings().loadJSON("data/strings/vulpoid_data_strings.json");
            JSONObject global = writerJson.getJSONObject("global");
            JSONArray subjects = writerJson.getJSONArray("subjects");
            JSONObject subject = subjects.getJSONObject(random.nextInt(subjects.length()));
            
            Iterator<String> keys = global.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                for(int i=0;i<global.getJSONArray(key).length();i++) subject.accumulate(key, global.getJSONArray(key).get(i));
            }
            
            // At this point, we've loaded every rule for the subject. We now need to eliminate any rule that isn't valid.
            // Rules are invalid for the following reasons:
            // -The rule's minquality is higher than the item quality.
            // -The rule's maxquality is lower than the item quality.
            // -The rule's priority is lower than that of another valid rule.
            Map<String, List<String>> rules = new HashMap();
            keys = subject.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if(subject.get(key) instanceof JSONArray) {
                    List<String> keyRules = new ArrayList();
                    int highestPriority = Integer.MIN_VALUE;
                    for(int i=0;i<subject.getJSONArray(key).length();i++) {
                        JSONObject obj = subject.getJSONArray(key).getJSONObject(i);
                        if(obj.has("minQuality") && obj.getInt("minQuality")>quality) continue;  //Quality too low
                        if(obj.has("maxQuality") && obj.getInt("maxQuality")<quality) continue;  //Quality too high
                        int objPriority = 0;
                        if(obj.has("priority")) objPriority = obj.getInt("priority");
                        if(objPriority<highestPriority) continue;  //Priority too low
                        if(objPriority>highestPriority) keyRules.clear();  //New highest priority - purge old options
                        highestPriority = objPriority;
                        int copiesToAdd = 1;
                        if(obj.has("weight")) copiesToAdd = obj.getInt("weight");
                        for(int j=0;j<copiesToAdd;j++) keyRules.add(obj.getString("string"));
                    }
                    rules.put(key, keyRules);
                }
            }
            
            Map<String, String> writerMap = new HashMap();
            for(String key : rules.keySet()) {
                writerMap.put(key, rules.get(key).get(random.nextInt(rules.get(key).size())));
            }
            
            writerMap.put("author", author);
            
            title = replaceTokens(writerMap.get("title"), writerMap);
            description = writerMap.get("description");
        } catch (IOException | JSONException ex) {
            title = "ERR";
            description = ""+ex;
        }
    }
    
    // https://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
    private String replaceTokens(String text, Map<String, String> map) {
        Pattern pattern = Pattern.compile("\\[(.+?)\\]");
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = map.get(matcher.group(1));
            builder.append(text.substring(i, matcher.start()));
            if (replacement == null)
                builder.append(matcher.group(0));
            else
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));
        return builder.toString();
    }
    
    @Override
    public String getName() {
        return title;
    }
    
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource, boolean useGray) {
        float opad = 10f;

        tooltip.addTitle(getName());
        
        tooltip.addPara("Author: %s", opad, Misc.getGrayColor(), Misc.getBasePlayerColor(), author);
        tooltip.addPara("Published %s", 0, Misc.getGrayColor(), Misc.getGrayColor(), publicationDate);
        
        tooltip.addPara(description, opad);

        addCostLabel(tooltip, opad, transferHandler, stackSource);
    }
}
