
package vulpoids.impl.campaign.econ.workforces;

public class EstateServantWorkforce extends BaseWorkforce {
    public boolean isAvailableToPlayer() {
        return false;
    }
    public String[] getRequirements() {
        return new String[]{"old-money aristocracy"};
    }
}
