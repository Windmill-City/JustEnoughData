package city.windmill;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JEIPlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@JEIPlugin
@Mod(   modid = JustEnoughData.MODID,
        name = JustEnoughData.NAME,
        version = JustEnoughData.VERSION,
        dependencies = "required-after:jeresources@[0.9.2.60,);" +
                       "required-after:jei@[4.15.0,);" +
                       "required-after:forge@[14.23.5.2847,);",
        clientSideOnly = true)
public class JustEnoughData implements IModPlugin {

    public static final String MODID = "jeidata";
    public static final String NAME = "Just Enough Data";
    public static final String VERSION = "1.0";

    public static Logger logger;

    public static IJeiRuntime JEIRuntime = null;

    @Mod.Instance
    JustEnoughData instance;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void LoadComplete(FMLLoadCompleteEvent event){
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIRuntime = jeiRuntime;
    }
}
