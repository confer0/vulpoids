
package vulpoids.impl.campaign;

import com.fs.starfarer.ui.newui.HintPanel;
import vulpoids.characters.VulpoidPerson;

// Why the hint panel?
// Well, I _wanted_ to just implement UIComponentAPI.
// But there's some obfuscated 'b' class that the tooltip tries to cast it to.
// So I needed to extend a deobfuscated class that implemented 'b', and found this.
public class VulpoidPortraitCalculatorComponent extends HintPanel {
    VulpoidPerson vulpoid;
    
    public VulpoidPortraitCalculatorComponent(VulpoidPerson vulpoid) {
        this.vulpoid = vulpoid;
    }

    @Override
    public void render(float alphaMult) {
        // Do it three times to ensure that there's no flickering.
        vulpoid.getTooltipPortraitSprite();
        vulpoid.getTooltipPortraitSprite();
        vulpoid.getTooltipPortraitSprite();
    }

}
