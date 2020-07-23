package city.windmill.Plan;

import city.windmill.JustEnoughData;
import city.windmill.net.CreateObjBase;
import city.windmill.net.DeleteObjBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RemotePlanFile extends PlanFile{
    public RemotePlanFile() {
        super(EventBase.Target.Remote);
        name = "RemotePlanFile";
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onModify_Remote(ModifyEvent event) {
        if(event.target.isRemote() && !event.isNetWork(null)) {
            switch (event.type) {
                case Update:
                    break;
                case Delete:
                    new DeleteObjBase(event.toModify.id).sendToServer();
                    JustEnoughData.logger.info("Send to server:Delete");
                    event.setResult(Event.Result.ALLOW);
                    break;
                case Create:
                    new CreateObjBase(event.modifyAction.onModify(event.toModify)).sendToServer();
                    JustEnoughData.logger.info("Send to server:Create");
                    event.setResult(Event.Result.ALLOW);
                    break;
            }
            event.setResult(Event.Result.ALLOW);
        }
    }
    @SubscribeEvent
    public void onModifyNetWork_FromServer(ModifyEvent.FromServer event) {
        super.onModify(event);
    }

    @Override
    @SubscribeEvent
    public void onSyncRead(SyncEvent.Read event) {
        super.onSyncRead(event);
        invalid = false;
        event.setResult(Event.Result.ALLOW);
    }
}
