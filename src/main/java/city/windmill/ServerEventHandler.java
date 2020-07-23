package city.windmill;

import city.windmill.net.SyncServerFile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = JustEnoughData.MODID, value = Side.SERVER)
public class ServerEventHandler {
    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event){
        new SyncServerFile(event.player).sendTo((EntityPlayerMP) event.player);
    }
}
