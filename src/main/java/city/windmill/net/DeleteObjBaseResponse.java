package city.windmill.net;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import city.windmill.JustEnoughData;
import city.windmill.Plan.ModifyEvent;
import city.windmill.Plan.PlanObjectBase;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DeleteObjBaseResponse extends MessageToClient {
    private int id;

    public DeleteObjBaseResponse(){}

    public DeleteObjBaseResponse(int id){
        this.id = id;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    @Override
    public void writeData(DataOut data) {
        data.writeInt(id);
        JustEnoughData.logger.debug(String.format("Send Delete Message to Client, Id: %d", id));
    }

    @Override
    public void readData(DataIn data) {
        id = data.readInt();
        JustEnoughData.logger.debug(String.format("Receive Delete Message from Server, Id: %d", id));
    }

    @Override
    public void onMessage() {
        PlanObjectBase base = CommonProxy.localFile.get(id);
        if(base != null) {
            ModifyEvent event = new ModifyEvent.FromServer(base,
                    ModifyEvent.ModifyType.Delete, (toModify) -> {
                toModify.deleteSelf();
                return toModify;
            });
            if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() == Event.Result.ALLOW)
                new DeleteObjBaseResponse(id).sendTo(JEIDataNetHandler.getAccessiblePlayers(base));
            else
                JustEnoughData.logger.warn(String.format("Server try to Delete Id: %d, but event has %s", id, event.isCanceled() ? "Canceled" : "Denied"));
        }
        else
            JustEnoughData.logger.warn(String.format("Server try to Delete Id: %d, but it's not exist", id));
    }
}
