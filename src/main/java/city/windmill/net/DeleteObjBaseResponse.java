package city.windmill.net;

import city.windmill.ClientProxy;
import city.windmill.Plan.ModifyEvent;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraftforge.common.MinecraftForge;

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
    }

    @Override
    public void readData(DataIn data) {
        id = data.readInt();
    }

    @Override
    public void onMessage() {
        MinecraftForge.EVENT_BUS.post(new ModifyEvent.FromServer(ClientProxy.remoteFile.get(id),
                ModifyEvent.ModifyType.Delete, (toModify) -> {
            toModify.deleteSelf();
            return toModify;
        } ));
    }
}
