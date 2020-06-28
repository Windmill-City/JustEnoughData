package city.windmill.Plan;

import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.misc.EnumPrivacyLevel;
import com.sun.istack.internal.NotNull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class Plan extends PlanObjectBase {
    //region Init required
    public final PlanFile planFile;
    //endregion
    //region NeedToSave
    public PlanPermission permission = PlanPermission.TEAM_R;
    public UUID owner;
    public boolean readOnly = false;

    public final List<PlanObjectBase> items = new ArrayList<>();
    //endregion
    //region runtime
    public Ticks baseUnit = Ticks.NO_TICKS;
    public final Map<Resource, Amount> generalInputs = new HashMap();
    public final Map<Resource, Amount> generalOutputs = new HashMap();
    //endregion
    //region constructor
    public Plan(PlanFile planFile) {
        this.planFile = planFile;
    }
    //endregion
    //region permission
    public boolean canAccess(@NotNull EntityPlayer player, boolean readOnly){
        //Universe should be loaded when calling this method
        ForgePlayer player1 = Universe.get().getPlayer(owner);
        ForgePlayer player2 = Universe.get().getPlayer(player);
        switch (permission){
            case PUBLIC_R:
                return player2.canInteract(player1, EnumPrivacyLevel.PUBLIC) && readOnly;
            case PUBLIC_RW:
                return player2.canInteract(player1, EnumPrivacyLevel.PUBLIC);
            case TEAM_R:
                return player2.canInteract(player1, EnumPrivacyLevel.TEAM) && readOnly;
            case TEAM_RW:
                return player2.canInteract(player1, EnumPrivacyLevel.TEAM);
            case PRIVATE:
                return player2.canInteract(player1, EnumPrivacyLevel.PRIVATE);
            default:
                JustEnoughData.logger.error("Unknown permission:" + permission.getId());
        }
        return false;
    }

    public boolean canEdit(@NotNull EntityPlayer player){
        return !readOnly && canAccess(player, false);
    }
    //endregion
    //region load
    //region SaveToFile
    @Override
    public void writeData(NBTTagCompound nbt) {
        super.writeData(nbt);
        nbt.setString("permission", permission.getId());
        nbt.setString("owner", owner.toString());
        nbt.setBoolean("readonly", readOnly);
        NBTTagList list = new NBTTagList();
        for (PlanObjectBase base :
                items) {
            NBTTagCompound data = new NBTTagCompound();
            data.setInteger("id", base.id);
            data.setString("type", base.getObjectType().getId());
            list.appendTag(data);
        }
        nbt.setTag("items", list);
    }

    @Override
    public void readData(NBTTagCompound nbt) {
        super.readData(nbt);
        permission = PlanPermission.NAME_MAP.get(nbt.getString("permission"));
        owner = UUID.fromString(nbt.getString("owner"));
        readOnly = nbt.getBoolean("readonly");

        NBTTagList list = nbt.getTagList("items", 10);
        for (NBTBase nbtbase :
                list) {
            NBTTagCompound data = (NBTTagCompound)nbtbase;
            int id = data.getInteger("id");
            PlanObjectType type = PlanObjectType.NAME_MAP.get(data.getString("type"));
            try{
                PlanObjectBase base = getPlanFile().create(type, this.id);
                base.id = id;
                items.add(base);
            }catch (IllegalArgumentException e){
                JustEnoughData.logger.error("Failed to load item:" + nbtbase.toString());
            }
        }
    }
    //endregion
    //region partial updates
    @Override
    public void writeNetData(DataOut data) {
        super.writeNetData(data);
        PlanPermission.NAME_MAP.write(data, permission);
        data.writeUUID(owner);
        data.writeBoolean(readOnly);

        data.writeVarInt(items.size());
        for (PlanObjectBase base :
                items) {
            data.writeInt(base.id);
            PlanObjectType.NAME_MAP.write(data, base.getObjectType());
        }
    }

    @Override
    public void readNetData(DataIn data) {
        super.readNetData(data);
        permission = PlanPermission.NAME_MAP.read(data);
        owner = data.readUUID();
        readOnly = data.readBoolean();

        items.clear();
        for (int i = 0; i < data.readVarInt(); i++) {
            int id = data.readInt();
            PlanObjectType type = PlanObjectType.NAME_MAP.read(data);
            //type should always valid
            PlanObjectBase base = getPlanFile().create(type, this.id);
            base.id = id;
            items.add(base);
        }
    }
    //endregion
    //endregion
    //region BaseMethods
    @Override
    public void deleteSelf() {
        deleteChildren();
        super.deleteSelf();
    }

    @Override
    public void deleteChildren() {
        for (PlanObjectBase base : items) {
            base.deleteChildren();
            base.deleteSelf();
        }
        super.deleteChildren();
    }

    @Override
    public PlanObjectType getObjectType() {
        return PlanObjectType.PLAN;
    }

    @Override
    public PlanFile getPlanFile() {
        return planFile;
    }

    @Override
    public Plan getPlan() {
        return this;
    }

    @Override
    public File getFile() {
        return new File(getPlanFile().folder.getPath(), "Plans/" + getCodeString(getPlan().id) + "/Plan.nbt");
    }
    //endregion
}
