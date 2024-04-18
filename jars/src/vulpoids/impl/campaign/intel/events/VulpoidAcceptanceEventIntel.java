package vulpoids.impl.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Set;
import vulpoids.impl.campaign.ids.Vulpoids;

public class VulpoidAcceptanceEventIntel extends BaseEventIntel {
    public static int PROGRESS_MAX = 1000;
    public static int PROGRESS_SANCTIONS_START = 100;
    public static int PROGRESS_SANCTIONS_END = 500;
    public static int PROGRESS_KNIGHT_ATTACK = 700;
    public static int PROGRESS_KNIGHT_BOMBARD = 900;

    public static String KEY = "$vulpoidAcceptance_ref";
    
    // TODO - pushback per stage
    // Make sure there's a baseline level so it's always visible
    
    public static enum Stage {
        START,
        SANCTIONS_START,
        SANCTIONS_END,
        KNIGHT_ATTACK,
        KNIGHT_BOMBARD,
        SUCCESS,
    }
    
    
    public static void addFactorCreateIfNecessary(EventFactor factor, InteractionDialogAPI dialog) {
        if (get() == null) {
            new VulpoidAcceptanceEventIntel(null, false);
        }
        if (get() != null) {
            get().addFactor(factor, dialog);
        }
    }

    public static VulpoidAcceptanceEventIntel get() {
        return (VulpoidAcceptanceEventIntel) Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }


    public VulpoidAcceptanceEventIntel(TextPanelAPI text, boolean withIntelNotification) {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        setup();
        
        Global.getSector().getFaction(Factions.HEGEMONY).makeCommodityIllegal(Vulpoids.CARGO_ITEM);
        Global.getSector().getFaction(Factions.LUDDIC_CHURCH).makeCommodityIllegal(Vulpoids.CARGO_ITEM);
        Global.getSector().getFaction(Factions.LUDDIC_PATH).makeCommodityIllegal(Vulpoids.CARGO_ITEM);
        
        // now that the event is fully constructed, add it and send notification
        Global.getSector().getIntelManager().addIntel(this, !withIntelNotification, text);
    }

    protected void setup() {
        factors.clear();
        stages.clear();

        setMaxProgress(PROGRESS_MAX);

        addStage(Stage.START, 0);
        //addStage(Stage.SEND_MERC, PROGRESS_1, true, StageIconSize.MEDIUM);
        addStage(Stage.SANCTIONS_START, PROGRESS_SANCTIONS_START, true, StageIconSize.MEDIUM);
        addStage(Stage.SANCTIONS_END, PROGRESS_SANCTIONS_END, true, StageIconSize.MEDIUM);
        addStage(Stage.KNIGHT_ATTACK, PROGRESS_KNIGHT_ATTACK, true, StageIconSize.MEDIUM);
        addStage(Stage.KNIGHT_BOMBARD, PROGRESS_KNIGHT_BOMBARD, true, StageIconSize.MEDIUM);
        addStage(Stage.SUCCESS, PROGRESS_MAX, true, StageIconSize.LARGE);

        // not actualy repeatable since no way to reduce progress
        // but this will keep the icon and the stage description showing
        //getDataFor(Stage.SEND_MERC).isRepeatable = false;

        //addFactor(new TTCRCommerceRaidersDestroyedFactorHint());
        //addFactor(new TTCRTradeFleetsDestroyedFactorHint());
        //addFactor(new TTCRIndustryDisruptedFactorHint());
        
        addFactor(new VulpoidAcceptanceFactor(this));
        //addActivity(new VulpoidAcceptanceFactor(this), new VulpoidAcceptanceCause(this));
    }
    
    @Override
    public void setProgress(int progress) {
        if (progress >= PROGRESS_KNIGHT_BOMBARD) progress = PROGRESS_KNIGHT_BOMBARD;
        if (this.progress == progress) return;
        super.setProgress(progress);
    }
    

    protected Object readResolve() {
        return this;
    }


    @Override
    protected void notifyEnding() {
        super.notifyEnding();
    }

    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().getMemoryWithoutUpdate().unset(KEY);
    }
    
    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        if (addEventFactorBulletPoints(info, mode, isUpdate, tc, initPad)) {
            return;
        }
        Color h = Misc.getHighlightColor();
        if (isUpdate && getListInfoParam() instanceof EventStageData) {
            EventStageData esd = (EventStageData) getListInfoParam();
            // TODO
            
            switch((Stage)esd.id) {
                case SANCTIONS_START:
                    info.addPara("The Hegemony and Church have placed sanctions on Vulpoid manufacture.", tc, initPad);
                    break;
                case SANCTIONS_END:
                    info.addPara("Anti-Vulpoid sanctions have been lifted due to public protest.", tc, initPad);
                    break;
                case KNIGHT_ATTACK:
                    info.addPara("The Knights of Ludd have launched an fleet to eliminate you.", tc, initPad);
                    break;
                case KNIGHT_BOMBARD:
                    info.addPara("The Knights of Ludd are launching a saturation bombardment against you.", tc, initPad);
                    break;
                case SUCCESS:
                    info.addPara("Vulpoids have been embraced by the civilian population of the Sector.", tc, initPad);
                    break;
            }
        }
    }

    @Override
    public void addStageDescriptionText(TooltipMakerAPI info, float width, Object stageId) {
        float opad = 10f;
        float small = 0f;

        EventStageData stage = getDataFor(stageId);
        if (stage == null) return;

        if (isStageActive(stageId)) {
            addStageDesc(info, stageId, small, false);
        }
    }

    public FactionAPI getFaction() {
        return Global.getSector().getPlayerFaction();
    }


    public void addStageDesc(TooltipMakerAPI info, Object stageId, float initPad, boolean forTooltip) {
        float opad = 10f;
        
        Color heg = Global.getSector().getFaction(Factions.HEGEMONY).getBaseUIColor();
        Color lc = Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getBaseUIColor();
        Color pl = Global.getSector().getFaction(Factions.PERSEAN).getBaseUIColor();
        Color tt = Global.getSector().getFaction(Factions.TRITACHYON).getBaseUIColor();

        if (stageId == Stage.START) {
            LabelAPI label = info.addPara("Vulpoids have been reintroduced to the Sector. The "+
                    "Hegemony and Luddic Church have banned them as a matter of course, but it will take a continuous "+
                    "supply before other major factions feel the need to establish an official stance.", opad);
            label.setHighlight("Hegemony", "Luddic Church");
            label.setHighlightColors(heg, lc);
            info.addPara("It should be possible to drive their acceptance %s by seeking out parties who "+
                    "would be interested in utilizing Vulpoids for their own aims.", opad, Misc.getHighlightColor(), "more rapidly");
        } else if (stageId == Stage.SANCTIONS_START) {
            if(progress<PROGRESS_SANCTIONS_START) {
                LabelAPI label = info.addPara("Major factions will establish their official stance towards the manufacture and trade "+
                        "of Vulpoids. Factions that have already banned them are likely to seek some form of retributive action.", opad);
                label.setHighlight("retributive action");
                label.setHighlightColors(Misc.getNegativeHighlightColor());
            } else {
                LabelAPI label = info.addPara("The northern factions have spoken out in support of Vulpoids, each "+
                        "for their own reason. Parallels to the Second AI War were made, "+
                        "and the Hegemony has been pushed to not launch full-scale technological inspections.", opad);
                label.setHighlight("northern factions", "Hegemony");
                label.setHighlightColors(pl, heg);
                label = info.addPara("The Luddic Church's orthodoxy and the Ministry of Technology "+
                        "Standards still hold considerable sway in their respective factions, and your Vulpoid manufacturing facilities "+
                        "will be subject to major sanctions for the forseeable future.", opad);
                label.setHighlight("Luddic Church's", "Ministry of Technology Standards", "major sanctions");
                label.setHighlightColors(lc, heg, Misc.getNegativeHighlightColor());
                // TODO - 60% access penalty is what the League blockade uses.
            }
        } else if (stageId == Stage.SANCTIONS_END) {
            if(progress<PROGRESS_SANCTIONS_START) {
                info.addPara("Retalliatory action will only remain popular for so long. If public acceptance of Vulpoids improves "+
                        "to this point, most major factions are likely to abandon any overt attempts to hamper Vulpoid exports.", opad);
            } else if(progress<PROGRESS_SANCTIONS_END) {
                LabelAPI label = info.addPara("Internal tensions are on the rise within anti-Vulpoid factions. Hegemony veterans are "+
                        "protesting the idea that unintelligent fox maids are of a similar threat to the killing machines fielded "+
                        "in the AI Wars. In the Luddic Church, fringe factions are pushing messages of universal redemption "+
                        "in a rebellion against the orthodoxy.", opad);
                label.setHighlight("Hegemony", "Luddic Church");
                label.setHighlightColors(heg, lc);
                info.addPara("Although there are indications that these movements are being supported by %s, they still hold "+
                        "sway within their parent factions. Given time and support, they may force the adoption of more lenient "+
                        "policies.", opad, tt, "Tri-Tachyon");
                
            } else {
                /*info.addPara("Internal tensions have made it untenable for the Hegemony and Luddic Church to maintain their "+
                        "sanctions on your Vulpoid manufacturing facilities.", opad);*/
                
                info.addPara("The %s has ceded to internal pressures and reluctantly classified Vulpoids as delta-level AI. "+
                        "Their trade does remain prohibited in %s space, under allegations of security concerns.",
                        opad, heg, "Ministry of Technology Standards", "Hegemony");
                
                info.addPara("The %s's orthodoxy has struggled to deal with preachers who draw analogies between the Vulpoids' "+
                        "origins and humanity's own original sin. They've been forced to backpedal their no-redemption doctrine, "+
                        "though still insist that the abominations have no place on hallowed worlds.", opad, lc, "Luddic Church");
            }
        } else if (stageId == Stage.KNIGHT_ATTACK) {
            if (progress<PROGRESS_SANCTIONS_END) {
                info.addPara("Even if major powers are swayed by the will of the people, relatively smaller factions can remain "+
                        "committed for some time longer. Expect %s.", opad, Misc.getNegativeHighlightColor(), "further hostilities");
            } else if(progress<PROGRESS_KNIGHT_ATTACK) {
                LabelAPI label = info.addPara("Tension is building within the Knights of Ludd, who are dissatisfied with the Luddic Church's "+
                        "apparent inaction against the \"growing abominable threat\". While they are not technically empowered "+
                        "to act independently, you should anticipate the inevitable.", opad);
                label.setHighlight("Knights of Ludd", "Luddic Church's", "anticipate the inevitable");
                label.setHighlightColors(lc, lc, Misc.getNegativeHighlightColor());
            } else {
                LabelAPI label = info.addPara("Several rogue Luddic Knights have broken ranks and sallied forth to destroy you "+
                        "in space combat. This action is not sanctioned by the Luddic Church, and is representative of the growing "+
                        "schism in the Faith.", opad);
                label.setHighlight("Luddic Knights", "destroy you in space combat", "Luddic Church");
                label.setHighlightColors(lc, Misc.getNegativeHighlightColor(), lc);
            }
        } else if (stageId == Stage.KNIGHT_BOMBARD) {
            if(progress<PROGRESS_KNIGHT_ATTACK) {
                info.addPara("Tensions are likely to reach an eventual head. Any non-major factions that remain overtly hostile "+
                        "will begin to loose public support and draw the ire of their parent faction. It's quite likely "+
                        "that you'll be subject to one %s before they're finally cracked down upon.",
                        opad, Misc.getNegativeHighlightColor(), "final strike");
            } else if(progress<PROGRESS_KNIGHT_BOMBARD) {
                LabelAPI label = info.addPara("The unwavering Knights continue to butt heads with a Church that is increasingly struggling to "+
                        "maintain control over its own doctrine. Accusations of Tri-Tachyon interference are prevalent, but "+
                        "public opinion shifts nonetheless. They are likely to attempt a last-ditch attack on your facilities soon.", opad);
                label.setHighlight("Knights", "Church", "Tri-Tachyon", "last-ditch attack");
                label.setHighlightColors(lc, lc, tt, Misc.getNegativeHighlightColor());
            } else {
                LabelAPI label = info.addPara("The Knights have openly defied the Church and launched a fleet intent on performing a saturation "+
                        "bombardment of your Vulpoid production facility. This action goes strictly against Luddic "+
                        "doctrine. Once the dust has settled, you can likely sue for reparations.", opad);
                label.setHighlight("Knights", "Church", "saturation bombardment", "sue for reparations");
                label.setHighlightColors(lc, lc, Misc.getNegativeHighlightColor(), Misc.getHighlightColor());
            }
        } else if (stageId == Stage.SUCCESS) {
            info.addPara("It's almost impossible not to love a Vulpoid once you've actually met her. The hard "+
                    "part is getting people to overcome their prejudices long enough to do that.", opad);
        }
    }

    public TooltipCreator getStageTooltipImpl(Object stageId) {
        final EventStageData esd = getDataFor(stageId);

        if (esd != null && esd.id instanceof Stage) {
            return new BaseFactorTooltip() {
                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    float opad = 10f;
                    
                    // TODO
                    if (esd.id == Stage.SANCTIONS_START) {
                        tooltip.addTitle("Major faction retaliation");
                    } else if (esd.id == Stage.SANCTIONS_END) {
                        tooltip.addTitle("Major faction acceptance");
                    } else if (esd.id == Stage.KNIGHT_ATTACK) {
                        tooltip.addTitle("Zealous retaliation");
                    } else if (esd.id == Stage.KNIGHT_BOMBARD) {
                        tooltip.addTitle("Extremist attack");
                    } else if (esd.id == Stage.SUCCESS) {
                        tooltip.addTitle("Success!");
                    }

                    addStageDesc(tooltip, esd.id, opad, true);

                    esd.addProgressReq(tooltip, opad);
                }
            };
        }

        return null;
    }



    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("events", "vulpoidAcceptance_icon");
    }

    protected String getStageIconImpl(Object stageId) {
        EventStageData esd = getDataFor(stageId);
        if (esd == null || !(esd.id instanceof Stage)) return null;
        
        switch((Stage)esd.id) {
            case START: return getIcon();
            case SANCTIONS_START:
                return Global.getSettings().getSpriteName("events", "vulpoidAcceptance_sanctions");
            case SANCTIONS_END:
                if(progress<PROGRESS_SANCTIONS_START) return Global.getSettings().getSpriteName("events", "stage_unknown_neutral");
                else return Global.getSettings().getSpriteName("events", "vulpoidAcceptance_sanctionsLifted");
            case KNIGHT_ATTACK:
                if(progress<PROGRESS_SANCTIONS_END) return Global.getSettings().getSpriteName("events", "stage_unknown_bad");
                else return Global.getSettings().getSpriteName("events", "vulpoidAcceptance_knightAttack");
            case KNIGHT_BOMBARD:
                if(progress<PROGRESS_KNIGHT_ATTACK) return Global.getSettings().getSpriteName("events", "stage_unknown_bad");
                else return Global.getSettings().getSpriteName("events", "vulpoidAcceptance_knightBombardment");
            case SUCCESS:
                return Global.getSettings().getSpriteName("events", "vulpoidAcceptance_success");
            default: return null;
        }
    }


    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_COLONIES);
        tags.add(Factions.PLAYER);
        return tags;
    }

    @Override
    public Color getBarColor() {
        Color color = getFaction().getBaseUIColor();
        color = Misc.interpolateColor(color, Color.black, 0.25f);
        return color;
    }

    @Override
    public Color getBarProgressIndicatorColor() {
        return super.getBarProgressIndicatorColor();
    }

    @Override
    protected int getStageImportance(Object stageId) {
        return super.getStageImportance(stageId);
    }


    @Override
    protected String getName() {
        return "Vulpoid Acceptance";
    }



    @Override
    protected void advanceImpl(float amount) {
        super.advanceImpl(amount);
        
        // Note - this can't be in notifyStageReached because the bioforge can be moved around.
        if (progress >= PROGRESS_SANCTIONS_START && progress < PROGRESS_SANCTIONS_END) {
            for (MarketAPI market : Misc.getPlayerMarkets(false)) {
                Industry industry = market.getIndustry(Vulpoids.INDUSTRY_ORGANFARM);
                if(industry==null) industry = market.getIndustry(Vulpoids.INDUSTRY_BIOFACILITY);
                if(industry!=null && industry.getSpecialItem()!=null && industry.getSpecialItem().getId().equals(Vulpoids.BIOFORGE_ITEM)) {
                    if(!market.hasCondition(Vulpoids.CONDITION_VULPOID_BLOCKADE)) market.addCondition(Vulpoids.CONDITION_VULPOID_BLOCKADE);
                }
            }
        } else {
            for (MarketAPI market : Misc.getPlayerMarkets(false)) {
                market.removeCondition(Vulpoids.CONDITION_VULPOID_BLOCKADE);
            }
        }
    }


    @Override
    protected void notifyStageReached(EventStageData stage) {
        //applyFleetEffects();

        /*if (stage.id == Stage.SEND_MERC) {
            sendBountyHunters();
        }

        if (stage.id == Stage.SUCCESS) {
            TriTachyonHostileActivityFactor.setPlayerCounterRaidedTriTach();
            endAfterDelay();
        }*/
    }

    /*protected String getSoundForStageReachedUpdate(Object stageId) {
//		if (stageId == Stage.SEND_MERC) {
//			return "ui_learned_ability";
//		}
            return super.getSoundForStageReachedUpdate(stageId);
    }*/

    /*@Override
    protected String getSoundForOneTimeFactorUpdate(EventFactor factor) {
            return null;
    }*/
    
}
