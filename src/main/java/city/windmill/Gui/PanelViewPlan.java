package city.windmill.Gui;

import city.windmill.Plan.*;
import city.windmill.net.CreateObjBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PanelViewPlan extends Panel {
    private final GuiPlan guiPlan;
    public PanelViewPlan(GuiPlan panel) {
        super(panel);
        guiPlan = panel;
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onPlanChange(GuiPlan.ContentChangeEvent.CurPlan event){
        updatePlan(event.to);
        alignWidgets();
    }

    private void updatePlan(Plan plan){
        widgets.clear();
        if(plan != null){
            for(PlanObjectBase base : plan.items){
                switch (base.getObjectType()){
                    case RESOURCE:
                    case PROVIDER:
                        add(new ButtonPlanObj(this, (PlanObject) base));
                        break;
                }
            }
        }
    }

    @Override
    public void addWidgets() {
        updatePlan(guiPlan.getPlan());
    }

    @Override
    public void alignWidgets() {
        align(WidgetLayout.NONE);
        setPosAndSize(41, 1, getGui().width - 40, getGui().height - 2);
    }
}
