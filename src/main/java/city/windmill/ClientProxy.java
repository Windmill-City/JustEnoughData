package city.windmill;

import city.windmill.Gui.GuiPlan;
import city.windmill.Plan.PlanFile;
import city.windmill.Plan.RemotePlanFile;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {
    public static KeyBinding KEY_PlanGui;
    public static PlanFile remoteFile;
    public static GuiPlan guiPlan;
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.registerKeyBinding(KEY_PlanGui = new KeyBinding("Open Plan Gui", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, "Just Enough Data"));
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        super.loadComplete(event);
        remoteFile = new RemotePlanFile();
        remoteFile.invalid = true;
        guiPlan = new GuiPlan();
    }
}
