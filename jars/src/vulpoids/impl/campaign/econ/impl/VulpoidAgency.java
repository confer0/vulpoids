
package vulpoids.impl.campaign.econ.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.DEALMAKER_INCOME_PERCENT_BONUS;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import java.awt.Color;
import vulpoids.impl.campaign.econ.VulpoidPopulation;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidAgency extends BaseIndustry {
    
    int vulpImport;
    
    
    public static float IMPROVE_BONUS = 25f;
    
    @Override
    public void apply() {
        SpecialItemData cachedSpecial = special;
        special = null;
        super.apply(true);
        special = cachedSpecial;
        
        int vulpDemand = market.getSize();
        /*if(market.getIndustry(Industries.POPULATION)!=null) {
            vulpDemand = market.getIndustry(Industries.POPULATION).getDemand(Vulpoids.CARGO_ITEM).getQuantity().getModifiedInt();
        } else {
            vulpDemand = market.getSize()-2;
        }*/
        if(vulpDemand<1) vulpDemand = 1;
        
        demand(Vulpoids.CARGO_ITEM, vulpDemand);
        
        vulpImport = vulpDemand;
        Pair<String, Integer> deficit = getMaxDeficit(Vulpoids.CARGO_ITEM);
        vulpImport -= deficit.two;
        if(!isFunctional()) vulpImport = 0;
        
        if(vulpImport>0 && !market.hasCondition(Vulpoids.CONDITION_VULPOID_POPULATION)) market.addCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
        // TODO - Require some amount of satisfaction?
        if(getPopPlugin()!=null) getPopPlugin().getWorkforceCap().modifyFlat(getId(), 1);
        
        if(getSpecialItem()!=null && getSpecialItem().getId().equals(Items.DEALMAKER_HOLOSUITE)) {
            market.getIncomeMult().modifyPercent(getId(), DEALMAKER_INCOME_PERCENT_BONUS, Misc.ucFirst(Global.getSettings().getSpecialItemSpec(getSpecialItem().getId()).getName().toLowerCase())+" (VDA)");
        }
    }
    
    @Override
    public void unapply() {
        SpecialItemData cachedSpecial = special;
        special = null;
        super.unapply();
        special = cachedSpecial;
        
        if(getPopPlugin()!=null) getPopPlugin().getWorkforceCap().unmodifyFlat(getId());
        
        market.getIncomeMult().unmodifyPercent(getId());
    }
    
    public int getVulpoidImport() {
        return vulpImport;
    }
    
    @Override
    public boolean isAvailableToBuild() {
        return Global.getSector().getPlayerFaction().knowsIndustry(getId());
    }
    @Override
    public boolean showWhenUnavailable() {
        return Global.getSector().getPlayerFaction().knowsIndustry(getId());
    }
    
    
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        tooltip.addPara("Imports Vulpoids to increase population", opad);
        tooltip.addPara("Grants %s additional Vulpoid workforce", opad, h, ""+1);
    }
    
    
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }
        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Grants %s additional workforce.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", ""+1);
            tooltip.addImageWithText(opad);
            return;
        }
        tooltip.addPara(pre + "Reduces upkeep cost by %s. Grants %s additional workforce.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", ""+1);
    }

    protected void addBetaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Beta-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Beta-level AI core. ";
        }
        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s.", opad, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%");
            tooltip.addImageWithText(opad);
            return;
        }
        tooltip.addPara(pre + "Reduces upkeep cost by %s.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION);
    }
    protected void addGammaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        String pre = "Gamma-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Gamma-level AI core. ";
        }
        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "No effect.", opad);
            tooltip.addImageWithText(opad);
            return;
        }
        tooltip.addPara(pre + "No effect.", opad);
    }
    
    @Override
    protected void updateAICoreToSupplyAndDemandModifiers() {}
    
    @Override
    protected void applyAlphaCoreModifiers() {
        //demandReduction.modifyFlat(getModId(0), -1, "Alpha Core");
        if(getPopPlugin()!=null) getPopPlugin().getWorkforceCap().modifyFlat(getModId(1), 1);
    }
    @Override
    protected void applyNoAICoreModifiers() {
        if(getPopPlugin()!=null) getPopPlugin().getWorkforceCap().unmodifyFlat(getModId(1));
    }
    
    @Override
    public boolean canImprove() {
        return true;
    }
    @Override
    protected void applyImproveModifiers() {
        /*if (isImproved()) {
            market.getIncomeMult().modifyPercent(getModId(2), IMPROVE_BONUS,
                    getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
        } else {
            market.getIncomeMult().unmodifyPercent(getModId(2));
        }*/
        if (isImproved()) {
            if(getPopPlugin()!=null) getPopPlugin().getWorkforceCap().modifyFlat(getModId(2), 1);
        } else {
            if(getPopPlugin()!=null) getPopPlugin().getWorkforceCap().unmodifyFlat(getModId(2));
        }
    }
    @Override
    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        /*float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        float a = IMPROVE_BONUS;
        String aStr = "" + (int)Math.round(a * 1f) + "%";
        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Colony income increased by %s.", 0f, highlight, aStr);
        } else {
            info.addPara("Increases colony income by %s.", 0f, highlight, aStr);
        }
        info.addSpacer(opad);
        super.addImproveDesc(info, mode);*/
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Grants %s additional workforce.", 0f, highlight, ""+1);
        } else {
            info.addPara("Grants %s additional workforce.", 0f, highlight, ""+1);
        }
        info.addSpacer(opad);
        super.addImproveDesc(info, mode);
    }
    
    private VulpoidPopulation getPopPlugin() {
        MarketConditionAPI cond = market.getCondition(Vulpoids.CONDITION_VULPOID_POPULATION);
        if(cond==null) return null;
        return (VulpoidPopulation) cond.getPlugin();
    }
}
