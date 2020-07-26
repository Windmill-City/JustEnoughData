package city.windmill.Gui;

import city.windmill.Plan.*;
import city.windmill.util.Rect;
import city.windmill.util.Vec2d;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PanelViewPlan extends Panel {
    private final GuiPlan guiPlan;
    public boolean MouseDrag = false;
    public double zoom = 4;
    public PanelViewPlan(GuiPlan panel) {
        super(panel);
        guiPlan = panel;
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onPlanChange(GuiPlan.ContentChangeEvent.CurPlan event){
        updatePlan(event.to);
        alignWidgets();
        if(event.from != event.to)
            resetScroll();
    }

    //region world coordinate(W)
    //left top
    public Vec2d W_LT = new Vec2d();
    //right bottom
    public Vec2d W_RB = new Vec2d();
    public Rect W_Rect = new Rect();
    //region mouse
    public int getMouseWX(){
        return (int) ((getMouseX() + W_Center.x) / zoom);
    }
    public int getMouseWY(){
        return (int) ((getMouseY() + W_Center.y) / zoom);
    }
    //endregion
    public void updateRect(){
        W_LT.apply(Vec2d.MAX);
        W_RB.apply(Vec2d.MIN);
        Vec2d itemLT = new Vec2d(0, 0);
        Vec2d itemRB = new Vec2d(0, 0);
        for (Widget widget : widgets) {
            if(widget instanceof ButtonPlanObj){
                PlanObject planObj = ((ButtonPlanObj) widget).planObj;
                itemLT.x = planObj.getX() - planObj.getWidth() / 2D;
                itemLT.y = planObj.getY() - planObj.getHeight() / 2D;
                itemRB.x = planObj.getX() + planObj.getWidth() / 2D;
                itemRB.y = planObj.getY() + planObj.getHeight() / 2D;
            }else
                continue;
            W_LT.min_XY(itemLT);
            W_RB.max_XY(itemRB);
        }

        if(W_LT.equals(Vec2d.MAX)){
            W_LT.apply(Vec2d.ZERO);
            W_RB.apply(Vec2d.ZERO);
        }
        W_Rect.apply(W_LT, W_RB);
        A_LT.apply(W_LT.clone().scale(zoom));
        A_RB.apply(W_RB.clone().scale(zoom));
        A_Rect.apply(A_LT, A_RB);
        W_Center.x = getScrollX();
        W_Center.y = getScrollY();
        A_Center.x = (A_LT.x + A_RB.x) / 2D;
        A_Center.y = (A_LT.y + A_RB.y) / 2D;
    }
    //endregion
    //region absolute coordinate(A)
    //left top
    public Vec2d A_LT = new Vec2d();
    //right bottom
    public Vec2d A_RB = new Vec2d();
    //the W_Center when the A_Rect is at screen center
    public Vec2d A_Center = new Vec2d();
    //scrolled pos
    public Vec2d W_Center = new Vec2d();
    //zoomed rect
    public Rect A_Rect = new Rect();
    public int prevMouse_X, prevMouse_Y;
    //region mouse

    @Override
    public int getMouseX() {
        return super.getMouseX() - posX - width / 2;
    }

    @Override
    public int getMouseY() {
        return super.getMouseY() - posY - height / 2;
    }

    //endregion
    private int scroll = 0;

    public void resetScroll(){
        scrollTo(W_Center.apply(A_Center));
        scroll = 0;
        doZoom(0);
    }

    public void scrollTo(Vec2d vec2d){
        setScrollX(vec2d.x);
        setScrollY(vec2d.y);
    }
    public void doZoom(int scroll){
        this.scroll += scroll;
        double tmp = (Math.atan((this.scroll - 6.99) * 0.1) + 1.11) * 2;
        //+0.01 in case divide by 0
        tmp = MathHelper.clamp(tmp, Math.max(Math.min(Math.min(height / (W_Rect.height + 1), width / (W_Rect.width + 1)), 0.5D), 0.01), 4D);
        if(tmp != zoom){
            zoom = tmp;
            alignWidgets();
        }else
            this.scroll -= scroll;
    }

    public void doMouseDrag(int mouseX, int mouseY){
        if(MouseDrag) {
            W_Center.x = MathHelper.clamp(W_Center.x + (prevMouse_X - mouseX), -A_Rect.width / 2D + A_Center.x, A_Rect.width / 2D + A_Center.x);
            W_Center.y = MathHelper.clamp(W_Center.y + (prevMouse_Y - mouseY), -A_Rect.height / 2D + A_Center.y, A_Rect.height / 2D + A_Center.y);
            scrollTo(W_Center);
            prevMouse_X = mouseX;
            prevMouse_Y = mouseY;
        }
    }
    //endregion

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
    public void draw(Theme theme, int x, int y, int w, int h) {
        super.draw(theme, x, y, w, h);
        if(true) {
            //Panel border
            GuiHelper.drawHollowRect(x, y, width, height, Color4I.BLUE, false);
            //A_Rect
            GuiHelper.drawHollowRect((int) (-W_Center.x + x + A_Rect.left + w / 2D), (int) (-W_Center.y + y + A_Rect.top + h / 2D), (int) A_Rect.width, (int) A_Rect.height, Color4I.RED, false);
            //W_Rect
            GuiHelper.drawHollowRect((int) (x - W_Rect.width / 2D + width / 2D), (int) (y - W_Rect.height / 2D + height / 2D), (int) W_Rect.width, (int) W_Rect.height, Color4I.GRAY, false);
            int font_h = theme.getFontHeight();
            int dh = 0;
            theme.drawString(String.format("WX: %.2f WY: %.2f", W_Center.x, W_Center.y), x + width / 2, y + height / 2 + dh);dh += font_h;
            theme.drawString(String.format("AX: %.2f AY: %.2f", A_Center.x, A_Center.y), x + width / 2, y + height / 2 + dh);dh += font_h;
            theme.drawString(String.format("MX: %d MY: %d", getMouseX(), getMouseY()), x + width / 2, y + height / 2 + dh);dh += font_h;
            theme.drawString(String.format("MWX: %d MWY: %d", getMouseWX(), getMouseWY()), x + width / 2, y + height / 2 + dh);
        }
    }

    @Override
    public boolean mousePressed(MouseButton button) {
        if(super.mousePressed(button))
            return true;
        if(button.isLeft() && isMouseOver()){
            prevMouse_X = super.getMouseX();
            prevMouse_Y = super.getMouseY();
            MouseDrag = true;
            return true;
        }else if(button.isRight() && guiPlan.getPlan() != null){
            MinecraftForge.EVENT_BUS.post(new ModifyEvent(guiPlan.getPlanFile().target,
                    EventBase.Source.GUI_FromClient,
                    guiPlan.getPlan(),
                    ModifyEvent.ModifyType.Create,
                    (toModify -> {
                        Provider provider = new Provider((Plan) toModify);
                        provider.setX(getMouseWX());
                        provider.setY(getMouseWY());
                        return provider;
                    })));
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseButton button) {
        if(button.isLeft() && MouseDrag)
            MouseDrag = false;
        super.mouseReleased(button);
    }

    @Override
    public boolean scrollPanel(int scroll) {
        doZoom(scroll);
        return true;
    }

    @Override
    public void updateMouseOver(int mouseX, int mouseY) {
        super.updateMouseOver(mouseX, mouseY);
        doMouseDrag(mouseX, mouseY);
    }

    @Override
    public void addWidgets() {
        updatePlan(guiPlan.getPlan());
    }

    @Override
    public void alignWidgets() {
        setOffset(false);
        setPosAndSize(41, 1, getGui().width - 40, getGui().height - 2);
        updateRect();
        int w;
        int h;
        for (Widget item :
                widgets) {
            if(item instanceof ButtonPlanObj){
                PlanObject obj = ((ButtonPlanObj) item).planObj;
                w = obj.getWidth();
                h = obj.getHeight();
                double ax = (obj.getX() - w / 2.0) * zoom + width / 2D;
                double ay = (obj.getY() - h / 2.0) * zoom + height / 2D;
                item.setPosAndSize((int)ax, (int)ay, (int) (w * zoom), (int) (h * zoom));
            }
        }
    }
}
