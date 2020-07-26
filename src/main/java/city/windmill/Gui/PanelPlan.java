package city.windmill.Gui;

import city.windmill.JustEnoughData;
import city.windmill.Plan.*;
import city.windmill.net.CreateObjBase;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Pattern;

public class PanelPlan extends Panel {
    private final ButtonAdd NewBtn;
    private final ButtonFile FileBtn;
    GuiPlan guiPlan;

    public PanelPlan(GuiPlan guiPlan) {
        super(guiPlan);
        this.guiPlan = guiPlan;
        NewBtn = new ButtonAdd(this) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiHelper.playClickSound();

                new GuiEditConfigValue("title", new ConfigString("", Pattern.compile("^.+$")), (value, set) ->
                {
                    guiPlan.openGui();

                    if (set)
                    {
                        MinecraftForge.EVENT_BUS.post(new ModifyEvent(guiPlan.getPlanFile().target,
                                EventBase.Source.GUI_FromClient,
                                guiPlan.getPlanFile(),
                                ModifyEvent.ModifyType.Create,
                                (toModify -> {
                                    Plan plan = new Plan((PlanFile) toModify);
                                    plan.name = value.getString();
                                    plan.owner = FMLClientHandler.instance().getClientPlayerEntity().getUniqueID();
                                    return plan;
                                })
                                ));
                    }
                }).openGui();
            }
        };
        FileBtn = new ButtonFile(this);
        FileBtn.setWidth(width);
        setPosAndSize(0, 1, 40, 0);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onFileChange(GuiPlan.ContentChangeEvent.CurFile event){
        updateFile(event.to);
        alignWidgets();
    }

    private void updateFile(PlanFile file){
        widgets.clear();
        add(FileBtn);
        for (Plan plan :
                file.plans) {
            add(new ButtonPlan(this, plan));
        }
        add(NewBtn);
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        super.draw(theme, x, y, w, h);
        GuiHelper.drawHollowRect(x, y, w, h, Color4I.GRAY, false);
    }

    @Override
    public void addWidgets() {
        updateFile(guiPlan.getPlanFile());
    }

    @Override
    public void alignWidgets() {
        setHeight(getGui().height - 2);
        align(WidgetLayout.VERTICAL);
    }
}
