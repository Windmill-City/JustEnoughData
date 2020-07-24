package city.windmill.net;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import city.windmill.Plan.Plan;
import city.windmill.Plan.PlanFile;
import city.windmill.Plan.PlanObjectBase;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class SendFullPlanToClient  extends MessageToClient {
    private Plan plan;
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public SendFullPlanToClient(){}
    public SendFullPlanToClient(Plan plan){
        this.plan = plan;
    }

    @Override
    public void writeData(DataOut data) {
        plan.writeNetData(data);
        for(PlanObjectBase base : plan.items){
            base.writeNetData(data);
        }
    }

    @Override
    public void readData(DataIn data) {
        plan.readNetData(data);
        for(PlanObjectBase base : plan.items){
            base.readNetData(data);
        }
    }

    @Override
    public void onMessage() {
        plan.getPlanFile().plans.add(plan);
        plan.getPlanFile().refreshIdMap();
    }
}
