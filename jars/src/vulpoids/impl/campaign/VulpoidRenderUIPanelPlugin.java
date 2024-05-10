package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.util.ArrayList;
import java.util.List;

public class VulpoidRenderUIPanelPlugin implements CustomUIPanelPlugin {
    
    protected List<UIComponentAPI> components = new ArrayList();
    
    PersonAPI person;
    PersonAPI second;
    PersonAPI third;
    
    @Override
    public void positionChanged(PositionAPI position) {
        
    }

    @Override
    public void renderBelow(float alphaMult) {
        
    }

    @Override
    public void render(float alphaMult) {
        /*float x = 1007;//1344*1344f/1814f;  //1814
        float y = 691;//924*924f/1228f;  //1228
        float w = 128;
        float h = 128;
        float cx = x+w/2;
        float cy = y+h/2;
        float blX = cx-w/2;
        float blY = cy-h/2;
        float tlX = cx-w/2;
        float tlY = cy+h/2;
        float trX = cx+w/2;
        float trY = cy+h/2;
        float brX = cx+w/2;
        float brY = cy-h/2;
        SpriteAPI sprite = Global.getSettings().getSprite("graphics/portraits/desert_fox.png");
        sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
        sprite = Global.getSettings().getSprite("graphics/icons/cargo/vulpoid_admin_icon.png");
        sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);*/
        
        for(UIComponentAPI component : components) {
            component.render(1f);
        }
    }

    @Override
    public void advance(float amount) {
        
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        
    }

    @Override
    public void buttonPressed(Object buttonId) {
        
    }
    
}
