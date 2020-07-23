package city.windmill.Plan;

import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ImageIcon;
import com.feed_the_beast.ftblib.lib.util.IWithID;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import net.minecraft.util.ResourceLocation;

public final class PlanObjectShape extends Icon implements IWithID {
    public static final PlanObjectShape DEFAULT = new PlanObjectShape("default");
    public static final PlanObjectShape SQUARE = new PlanObjectShape("square");
    public static final PlanObjectShape HORNSQUARE = new PlanObjectShape("hornsquare");
    public static final PlanObjectShape WAVESQUARE = new PlanObjectShape("wavesquare");
    public static final PlanObjectShape CIRCLE = new PlanObjectShape("circle");

    public static final NameMap<PlanObjectShape> NAME_MAP = NameMap.create(DEFAULT, DEFAULT, SQUARE, HORNSQUARE, WAVESQUARE, CIRCLE);

    public final String id;
    public final ImageIcon background;
    public final ImageIcon outline;
    public final ImageIcon shape;

    public PlanObjectShape(String i){
        this.id = i;
        this.background = new ImageIcon(new ResourceLocation(JustEnoughData.MODID, "textures/shapes/" + this.id + "/background.png"));
        this.outline = new ImageIcon(new ResourceLocation(JustEnoughData.MODID, "textures/shapes/" + this.id + "/outline.png"));
        this.shape = new ImageIcon(new ResourceLocation(JustEnoughData.MODID, "textures/shapes/" + this.id + "/shape.png"));
    }
    @Override
    public void draw(int x, int y, int w, int h) {
        this.background.draw(x, y, w, h);
        this.outline.draw(x, y, w, h);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "plan_object_shape:" + this.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
