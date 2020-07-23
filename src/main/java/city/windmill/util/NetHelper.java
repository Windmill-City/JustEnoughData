package city.windmill.util;

import city.windmill.Plan.PlanFile;
import city.windmill.Plan.PlanObjectBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.HashSet;
import java.util.Set;

public class NetHelper {
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
