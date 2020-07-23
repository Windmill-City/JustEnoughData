package city.windmill;

import city.windmill.Plan.*;
import city.windmill.net.JEIDataNetHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.nio.file.Path;

import static city.windmill.JustEnoughData.configDir;

public class CommonProxy {
    public static Path DataDir;
    public static PlanFile localFile;

    public void preInit(FMLPreInitializationEvent event){
        JEIDataNetHandler.init();
        DataDir = configDir.toPath().resolve("JustEnoughData");
    }

    public void loadComplete(FMLLoadCompleteEvent event){
        localFile = new LocalPlanFile();
        MinecraftForge.EVENT_BUS.post(new FileEvent.Load(EventBase.Source.Other_Local, DataDir));
    }
}
