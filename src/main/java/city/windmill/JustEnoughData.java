package city.windmill;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JEIPlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@JEIPlugin
@Mod(   modid = JustEnoughData.MODID,
        name = JustEnoughData.NAME,
        version = JustEnoughData.VERSION,
        dependencies = "required-after:ftblib;" +
                       "required-after:jei@[4.15.0,);" +
                       "required-after:forge@[14.23.5.2847,);")
public class JustEnoughData implements IModPlugin {

    public static final String MODID = "jeidata";
    public static final String NAME = "Just Enough Data";
    public static final String VERSION = "1.0";

    public static Logger logger;
    public static File configDir;

    public static IJeiRuntime JEIRuntime = null;

    @SidedProxy(clientSide = "city.windmill.ClientProxy", serverSide = "city.windmill.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static JustEnoughData instance;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        configDir = event.getModConfigurationDirectory();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void LoadComplete(FMLLoadCompleteEvent event){
        proxy.loadComplete(event);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIRuntime = jeiRuntime;
    }
}
