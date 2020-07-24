package city.windmill.net;

import city.windmill.CommonProxy;
import city.windmill.Plan.*;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class SendFullPlanToServer extends MessageToServer {
    private Plan plan;
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public SendFullPlanToServer(){}
    public SendFullPlanToServer(Plan plan){
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
        plan = new Plan(CommonProxy.localFile);
        plan.readNetData(data);
        for(PlanObjectBase base : plan.items){
            base.readNetData(data);
        }
    }

    @Override
    public void onMessage(EntityPlayerMP player) {
        plan.owner = player.getUniqueID();
        plan.permission = PlanPermission.PRIVATE;
        plan.id = CommonProxy.localFile.uniqueOrNewID(0);
        for(PlanObjectBase base : plan.items){
            base.id = CommonProxy.localFile.uniqueOrNewID(0);
        }
        plan.getPlanFile().plans.add(plan);
        plan.getPlanFile().refreshIdMap();
        new SendFullPlanToClient(plan).sendTo(player);
        MinecraftForge.EVENT_BUS.post(new FileEvent.Save(EventBase.Source.Other_FromClient, CommonProxy.DataDir));
    }
}
