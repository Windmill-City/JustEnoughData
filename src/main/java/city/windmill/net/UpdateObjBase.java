package city.windmill.net;

import city.windmill.CommonProxy;
import city.windmill.Plan.ModifyEvent;
import city.windmill.Plan.PlanObjectBase;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class UpdateObjBase extends MessageToServer {
    private int id;
    private PlanObjectBase base;
    private DataIn dataIn;
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public UpdateObjBase(){}
    public UpdateObjBase(PlanObjectBase base){
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
    public void onMessage(EntityPlayerMP player) {
        PlanObjectBase base = CommonProxy.localFile.get(id);
        if(base != null)
        MinecraftForge.EVENT_BUS.post(new ModifyEvent.FromClient(base, ModifyEvent.ModifyType.Update, (toModify -> {
            toModify.readNetData(dataIn);
            new UpdateObjResponse(toModify).sendTo(JEIDataNetHandler.getAccessiblePlayers(toModify));
            return toModify;
        }), player));
    }
}
