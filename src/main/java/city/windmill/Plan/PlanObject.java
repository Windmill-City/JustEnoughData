package city.windmill.Plan;

import city.windmill.Gui.IPanelItem;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import net.minecraft.nbt.NBTTagCompound;

public abstract class PlanObject extends PlanObjectBase implements IPanelItem {
    //region Init required
    public final Plan plan;
    //endregion
    //region NeedToSave
    protected PlanObjectShape shape;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected PlanObject(Plan plan) {
        this.plan = plan;
    }

    //endregion
    public PlanObjectShape getShape(){
        return shape;
    }
    //region SaveToFile
    @Override
    public void writeData(NBTTagCompound nbt) {
        super.writeData(nbt);
        if(shape != PlanObjectShape.DEFAULT)
        nbt.setString("shape", shape.getId());
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("width", width);
        nbt.setInteger("height", height);
    }

    @Override
    public void readData(NBTTagCompound nbt) {
        super.readData(nbt);
        shape = nbt.hasKey("shape") ? PlanObjectShape.NAME_MAP.get(nbt.getString("shape")) : PlanObjectShape.DEFAULT;
        x = nbt.getInteger("x");
        y = nbt.getInteger("y");
        width = nbt.getInteger("width");
        height = nbt.getInteger("height");
    }
    //endregion
    //region partial updates
    @Override
    public void writeNetData(DataOut data) {
        super.writeNetData(data);
        PlanObjectShape.NAME_MAP.write(data, shape);
        data.writeVarInt(x);
        data.writeVarInt(y);
        data.writeVarInt(width);
        data.writeVarInt(height);
    }

    @Override
    public void readNetData(DataIn data) {
        super.readNetData(data);
        shape = PlanObjectShape.NAME_MAP.read(data);
        x = data.readVarInt();
        y = data.readVarInt();
        width = data.readVarInt();
        height = data.readVarInt();
    }
    //endregion
    //region BaseMethod
    @Override
    public void onCreated() {
        super.onCreated();
        getPlan().items.add(this);
    }

    @Override
    public Plan getPlan() {
        return plan;
    }

    @Override
    public int getParentID() {
        return getPlan().id;
    }

    @Override
    public PlanFile getPlanFile() {
        return getPlan().getPlanFile();
    }
    //endregion
    //region IPanelItem
    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }
    //endregion
}
