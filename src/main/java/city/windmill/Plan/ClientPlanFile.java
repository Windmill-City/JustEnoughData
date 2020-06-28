package city.windmill.Plan;

import java.io.File;

public class ClientPlanFile extends PlanFile{
    public ClientPlanFile(File folder) {
        super(folder);
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
