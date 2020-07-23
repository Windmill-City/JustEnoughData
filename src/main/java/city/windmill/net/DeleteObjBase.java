package city.windmill.net;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import city.windmill.Plan.ModifyEvent;
import city.windmill.Plan.PlanObjectBase;
import city.windmill.util.NetHelper;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class DeleteObjBase extends MessageToServer {
    private int id;
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public DeleteObjBase() {
    }

    public DeleteObjBase(int id){
        this.id = id;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeInt(id);
    }

    @Override
    public void readData(DataIn data) {
        id = data.readInt();
    }

    @Override
    public void onMessage(EntityPlayerMP player) {
        MinecraftForge.EVENT_BUS.post(new ModifyEvent.FromClient(ClientProxy.remoteFile.get(id),
                ModifyEvent.ModifyType.Delete, (toModify) -> {
            toModify.deleteSelf();
            new DeleteObjBaseResponse(id).sendTo(NetHelper.getAccessiblePlayers(toModify));
            return toModify;
        }, player));
    }
}
