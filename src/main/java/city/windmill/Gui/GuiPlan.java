package city.windmill.Gui;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import city.windmill.Plan.ModifyEvent;
import city.windmill.Plan.Plan;
import city.windmill.Plan.PlanFile;
import city.windmill.Plan.SyncEvent;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiPlan extends GuiBase {
    public PanelPlan panelPlan = new PanelPlan(this);
    public PanelViewPlan panelViewPlan = new PanelViewPlan(this);
    @Nonnull
    private PlanFile curFile = CommonProxy.localFile;
    @Nullable
    private Plan curPlan;

    public GuiPlan(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nullable
    public Plan getPlan(){
        return curPlan;
    }

    public void setPlan(Plan plan){
        if(!MinecraftForge.EVENT_BUS.post(new ContentChangeEvent.CurPlan(curPlan, plan))){
            curPlan = plan;
        }
    }

    @Nonnull
    public PlanFile getPlanFile(){
        return curFile;
    }

    public void setPlanFile(PlanFile file){
        if(!MinecraftForge.EVENT_BUS.post(new ContentChangeEvent.CurFile(curFile, file)) && file != curFile){
            curFile = file;
            setPlan(curFile.plans.isEmpty() ? null : curFile.plans.get(0));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onSync(SyncEvent.Read event){
        if(event.getResult() == Event.Result.ALLOW)
            setPlanFile(ClientProxy.remoteFile);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onModified(ModifyEvent event){
        if(event.getResult() == Event.Result.ALLOW){
            if(curFile == event.toModify)
                setPlanFile(curFile);
            else if(event.ModifiedResult != null && curPlan == event.ModifiedResult.getPlan())
                setPlan(curPlan);
        }
    }

    @Override
    public boolean drawDefaultBackground() {
        return false;
    }

    @Override
    public void addWidgets() {
        panelPlan.addWidgets();
        panelViewPlan.addWidgets();

        add(panelPlan);
        add(panelViewPlan);
    }

    @Override
    public boolean keyPressed(int key, char keyChar) {
        if(ClientProxy.KEY_PlanGui.getKeyCode() == key) {
            closeGui();
            return true;
        }
        return super.keyPressed(key, keyChar);
    }

    @Override
    public void alignWidgets() {
        panelPlan.alignWidgets();
        panelViewPlan.alignWidgets();
    }

    @Override
    public boolean onInit() {
        MinecraftForge.EVENT_BUS.post(new ContentChangeEvent.CurFile(curFile, curFile));
        return setFullscreen();
    }

    @Cancelable
    public static class ContentChangeEvent extends Event{
        public static class CurPlan extends ContentChangeEvent{
            @Nullable
            public final Plan from;
            @Nullable
            public final Plan to;

            public CurPlan(@Nullable Plan from, @Nullable Plan to) {
                this.from = from;
                this.to = to;
            }
        }

        public static class CurFile extends ContentChangeEvent{
            @Nonnull
            public final PlanFile from;
            @Nonnull
            public final PlanFile to;

            public CurFile(@Nonnull PlanFile from, @Nonnull PlanFile to) {
                this.from = from;
                this.to = to;
            }
        }
    }
}
