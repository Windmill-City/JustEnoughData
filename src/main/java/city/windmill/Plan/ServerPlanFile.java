package city.windmill.Plan;

import java.io.File;

public class ServerPlanFile extends PlanFile{
    public ServerPlanFile(File folder) {
        super(folder);
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
