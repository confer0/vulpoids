/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vulpoids.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import java.util.List;

public class AnimatedIllustrationUIPanelPlugin implements CustomUIPanelPlugin {
    
    int frame = 0;
    
    @Override
    public void positionChanged(PositionAPI papi) {
        
    }

    @Override
    public void renderBelow(float f) {
        
    }

    @Override
    public void render(float f) {
        float x = 990;//1344*1344f/1814f;  //1814
        float y = 520;//924*924f/1228f;  //1228
        float w = 480;
        float h = 300;
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
        
        int n_frames = 3;
        int frames_per_frame = 30;
        
        SpriteAPI sprite = Global.getSettings().getSprite("graphics/illustrations/laisa_hug"+((frame/frames_per_frame)+1)+".png");
        
        sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
        //sprite = Global.getSettings().getSprite("graphics/icons/cargo/vulpoid_admin_icon.png");
        //sprite.renderWithCorners(blX, blY, tlX, tlY, trX, trY, brX, brY);
        frame += 1;
        if (frame >= n_frames*frames_per_frame) frame = 0;
    }

    @Override
    public void advance(float f) {
        
    }

    @Override
    public void processInput(List<InputEventAPI> list) {
        
    }

    @Override
    public void buttonPressed(Object o) {
        
    }
    
}
