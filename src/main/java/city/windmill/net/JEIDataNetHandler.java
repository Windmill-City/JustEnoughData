package city.windmill.net;

import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class JEIDataNetHandler {
    static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(JustEnoughData.MODID);

    public static void init(){
        GENERAL.register(new CreateObjBase());
        GENERAL.register(new CreateObjBaseResponse());
        GENERAL.register(new DeleteObjBase());
        GENERAL.register(new DeleteObjBaseResponse());
        GENERAL.register(new SyncServerFile());
    }
}
