package city.windmill;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = JustEnoughData.MODID, value = Side.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if (ClientProxy.KEY_PlanGui.isPressed())
        {
            ClientProxy.guiPlan.openGui();
        }
    }

    @SubscribeEvent
    public static void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        ClientProxy.remoteFile.invalid = true;
        ClientProxy.guiPlan.setPlanFile(CommonProxy.localFile);
    }
}
