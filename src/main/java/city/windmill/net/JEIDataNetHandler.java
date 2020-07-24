package city.windmill.net;

import city.windmill.JustEnoughData;
import city.windmill.Plan.PlanFile;
import city.windmill.Plan.PlanObjectBase;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.HashSet;
import java.util.Set;

public class JEIDataNetHandler {
    static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(JustEnoughData.MODID);

    public static void init(){
        GENERAL.register(new CreateObjBase());
        GENERAL.register(new CreateObjBaseResponse());
        GENERAL.register(new DeleteObjBase());
        GENERAL.register(new DeleteObjBaseResponse());
        GENERAL.register(new SyncServerFile());
    }

    public static Set<EntityPlayerMP> getAccessiblePlayers(PlanObjectBase base){
        HashSet<EntityPlayerMP> players = new HashSet<>();
        for(EntityPlayerMP player :
                FMLServerHandler.instance().getServer().getPlayerList().getPlayers()){
            if(PlanFile.canAccess(base, player, true))
                players.add(player);
        }
        return players;
    }
}
