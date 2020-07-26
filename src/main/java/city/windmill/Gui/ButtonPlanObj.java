package city.windmill.Gui;

import city.windmill.Plan.PlanObject;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;

public class ButtonPlanObj extends Button {
    public PlanObject planObj;
    public ButtonPlanObj(Panel panel, PlanObject planObj) {
        super(panel);
        this.planObj = planObj;
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        super.draw(theme, x, y, w, h);
    }

    @Override
    public void onClicked(MouseButton button){}
}
