package city.windmill.Plan;

import city.windmill.CommonProxy;
import city.windmill.JustEnoughData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LocalPlanFile extends PlanFile{

    public LocalPlanFile() {
        super(EventBase.Target.Local);
        name = "LocalPlanFile";
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onModify_Local(ModifyEvent event) {
        if(event.target.isLocal() && event.getResult() == Event.Result.DEFAULT) {
            super.onModify(event);
            MinecraftForge.EVENT_BUS.post(new FileEvent.Save(EventBase.Source.Other_FromClient, CommonProxy.DataDir));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void verifyFromClient(ModifyEvent.FromClient event){
        if(!canEdit(event.toModify, event.modifier))
            event.setResult(Event.Result.DENY);
    }

    @Override
    @SubscribeEvent
    public void onLoad(FileEvent.Load event) {
        super.onLoad(event);
        event.setResult(Event.Result.ALLOW);
    }

    @Override
    @SubscribeEvent
    public void onSave(FileEvent.Save event) {
        super.onSave(event);
        event.setResult(Event.Result.ALLOW);
    }

    @Override
    @SubscribeEvent
    public void onSyncWrite(SyncEvent.Write event) {
        super.onSyncWrite(event);
        event.setResult(Event.Result.ALLOW);
    }
}
