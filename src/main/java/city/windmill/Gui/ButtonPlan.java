package city.windmill.Gui;

import city.windmill.Plan.Plan;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;

public class ButtonPlan extends Button {
    private final PanelPlan panelPlan;
    private final Plan plan;
    public ButtonPlan(PanelPlan panel, Plan plan) {
        super(panel);
        panelPlan = panel;
        this.plan = plan;
        setIcon(plan.icon);
        setTitle(plan.name);
    }

    @Override
    public void onClicked(MouseButton button) {
        panelPlan.guiPlan.setPlan(plan);
    }
}
