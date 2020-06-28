package city.windmill.net;

import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class MCreateObjBase extends MessageToServer {
    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    @Override
    public void onMessage(EntityPlayerMP player) {

    }
}
