package scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin.InstallableItemDescriptionMode;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

import com.fs.starfarer.api.campaign.FactionAPI;

public class VulpoidModPlugin extends BaseModPlugin
{


	public void onGameLoad(boolean newGame) {
		
		final int VULPOID_PROD_BONUS = 1;
		final int VULPOID_PROD_AMOUNT = 3;
		
		ItemEffectsRepo.ITEM_EFFECTS.put("vulpoid_biofactory", new BaseInstallableItemEffect("vulpoid_biofactory") {
			public void apply(Industry industry) {
				industry.getSupplyBonus().modifyFlat(spec.getId(), VULPOID_PROD_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));
				if (industry instanceof BaseIndustry) {
					BaseIndustry b = (BaseIndustry) industry;
					b.supply(spec.getId(), "vulpoids", VULPOID_PROD_AMOUNT-VULPOID_PROD_BONUS, Misc.ucFirst(spec.getName().toLowerCase()));
				}
			}
			public void unapply(Industry industry) {
				industry.getSupplyBonus().modifyFlat(spec.getId(), 0, Misc.ucFirst(spec.getName().toLowerCase()));
				if (industry instanceof BaseIndustry) {
					BaseIndustry b = (BaseIndustry) industry;
					b.supply(spec.getId(), "vulpoids", 0, null);
				}
			}
			
			protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
												  InstallableItemDescriptionMode mode, String pre, float pad) {
				text.addPara(pre + "Increases light industry production by %s units. If the colony is a freeport, also produces %s Vulpoids per month for export.",
				// Note - units of production and units per month _appear_ to line up when using an econunit of 5. Total stockpile is 22 at 3 production, 25 at 4, 31 at 5, 37 at 6, and 43 at 7. (Note: Luddic Maj applies!)
						pad, Misc.getHighlightColor(), 
						"" + (int) VULPOID_PROD_BONUS, "" + (int) VULPOID_PROD_AMOUNT);
			}
			@Override
			public String[] getSimpleReqs(Industry industry) {
				return new String [] {ItemEffectsRepo.HABITABLE};
			}
		});
		
		// Make illegal - Not needed now that I'm using the ai_cores demand_class.
		/*for(FactionAPI faction : Global.getSector().getAllFactions()) {
			if(faction.isIllegal("ai_cores")) {
				faction.makeCommodityIllegal("vulpoids");
			}
		}*/
		
		// The commodity is implemented with a demand_class of ai_cores so it can be sold to important people.
		// That comes with a nonecon tag though, which needs to be removed to let the colony actually produce them.
		Global.getSector().getEconomy().getCommoditySpec("vulpoids").getTags().remove("nonecon");
		
	}
	
}