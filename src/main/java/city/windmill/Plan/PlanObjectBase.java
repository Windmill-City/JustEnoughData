package city.windmill.Plan;

import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.Bits;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.util.IWithID;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.io.File;

public abstract class PlanObjectBase implements IWithID {
    //region NeedToSave
    public Icon icon = Icon.EMPTY;
    public String name = "";
    public int id = 0;
    //endregion
    public boolean invalid = false;

    //region Handle id
    public static boolean isNull(@Nullable PlanObjectBase object) {
        return object == null || object.invalid;
    }

    public static int getID(@Nullable PlanObjectBase object) {
        return isNull(object) ? 0 : object.id;
    }

    public static String getCodeString(int id) {
        return String.format("%08x", id);
    }

    public static String getCodeString(@Nullable PlanObjectBase object) {
        return String.format("%08x", getID(object));
    }

    public final String getCodeString() {
        return getCodeString(id);
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }
    //endregion
    @Nullable
    public Plan getPlan(){ return null; }

    public int getParentID() {
        return 1;
    }

    public abstract PlanObjectType getObjectType();

    public abstract PlanFile getPlanFile();

    public void onCreated() {}

    public void deleteSelf() {
        invalid = true;
        getPlanFile().remove(id);
    }

    public void deleteChildren() {}

    @Nullable
    public File getFile() {
        return null;
    }
    //region SaveToFile
    public void writeData(NBTTagCompound nbt){
        if(!name.isEmpty())
            nbt.setString("Name", name);

        if(!icon.isEmpty())
            nbt.setString("Icon", icon.toString());
    }

    public void readData(NBTTagCompound nbt){
        name = nbt.getString("Name");
        icon = Icon.getIcon(nbt.getString("Icon"));
    }
    //endregion
    //region partial updates
    public void writeNetData(DataOut data) {
        int flags = 0;
        flags = Bits.setFlag(flags, 1, !name.isEmpty());
        flags = Bits.setFlag(flags, 2, !icon.isEmpty());
        data.writeVarInt(flags);
        if(!name.isEmpty())
            data.writeString(name);
        if(!icon.isEmpty())
            data.writeIcon(icon);
    }

    public void readNetData(DataIn data) {
        int flags = data.readVarInt();
        name = Bits.getFlag(flags, 1) ? data.readString() : "";
        icon = Bits.getFlag(flags, 2) ? data.readIcon() : Icon.EMPTY;
    }
    //endregion
    //region final Object methods
    @Override
    public final String toString() {
        return this.getCodeString();
    }

    @Override
    public final boolean equals(Object object) {
        return object == this;
    }

    @Override
    public final int hashCode() {
        return id;
    }
    //endregion
}
