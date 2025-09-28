
package vulpoids.impl.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.econ.MonthlyReport.FDNode;
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.util.Misc;
import vulpoids.characters.VulpoidPerson;

public class OfficerCostOverrideScript extends BaseCampaignEventListener implements EveryFrameScript {

    public OfficerCostOverrideScript() {
        super(true);
    }
    
    @Override
    public void reportEconomyTick(int iterIndex) {
        float numIter = Global.getSettings().getFloat("economyIterPerMonth");
        float f = 1f / numIter;
        
        MonthlyReport report = SharedData.getData().getCurrentReport();
        
        FDNode fleetNode = report.getNode(MonthlyReport.FLEET);
        FDNode officersNode = report.getNode(fleetNode, MonthlyReport.OFFICERS);
        for (OfficerDataAPI od : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
            if (od.getPerson() instanceof VulpoidPerson person) {
                FDNode oNode = report.getNode(officersNode, person.getId());
                oNode.upkeep -= f * Misc.getOfficerSalary(person);
                oNode.upkeep += f * person.getOfficerSalary();
            }
        }
        FDNode colonyNode = report.getColoniesNode();
        FDNode adminsNode = report.getNode(colonyNode, MonthlyReport.ADMIN);
        for (AdminData ad : Global.getSector().getCharacterData().getAdmins()) {
            if (ad.getPerson() instanceof VulpoidPerson person) {
                FDNode aNode = report.getNode(adminsNode, person.getId());
                float salaryMult = 1f;
                if (ad.getMarket() == null) {
                    salaryMult = Global.getSettings().getFloat("idleAdminSalaryMult");
                }
                aNode.upkeep -= f * salaryMult * Misc.getAdminSalary(person);
                aNode.upkeep += f * salaryMult * person.getAdminSalary();
            }
        }
    }
    
    
    @Override
    public boolean isDone() {return false;}
    @Override
    public boolean runWhilePaused() {return true;}
    @Override
    public void advance(float amount) {}

}
