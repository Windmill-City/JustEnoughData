package city.windmill.net;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import city.windmill.Plan.SyncEvent;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SyncServerFile extends MessageToClient {
    EntityPlayer player;
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public SyncServerFile() {
    }

    public SyncServerFile(EntityPlayer player){
        this.player = player;
    }

    @Override
    public void writeData(DataOut data) {
        MinecraftForge.EVENT_BUS.post(new SyncEvent.Write(data, player));
    }

    @Override
    public void readData(DataIn data) {
        MinecraftForge.EVENT_BUS.post(new SyncEvent.Read(data));
    }
}
