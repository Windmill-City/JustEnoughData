package city.windmill.net;

import city.windmill.CommonProxy;
import city.windmill.Plan.ModifyEvent;
import city.windmill.Plan.PlanObjectBase;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraftforge.common.MinecraftForge;

public class UpdateObjResponse extends MessageToClient {
    private int id;
    private PlanObjectBase base;
    private DataIn dataIn;
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public UpdateObjResponse(){}
    public UpdateObjResponse(PlanObjectBase base){
        id = base.id;
        this.base = base;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeInt(id);
        base.writeNetData(data);
    }

    @Override
    public void readData(DataIn data) {
        id = data.readInt();
        dataIn = data;
    }

    @Override
    public void onMessage() {
        PlanObjectBase base = CommonProxy.localFile.get(id);
        if(base != null)
            MinecraftForge.EVENT_BUS.post(new ModifyEvent.FromServer(base, ModifyEvent.ModifyType.Update, (toModify -> {
                toModify.readNetData(dataIn);
                return toModify;
            })));
    }
}
