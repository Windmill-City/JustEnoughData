package city.windmill.Plan;

import city.windmill.JustEnoughData;
import city.windmill.net.DeleteObjBaseResponse;
import city.windmill.net.JEIDataNetHandler;
import city.windmill.net.SendFullPlanToClient;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.misc.EnumPrivacyLevel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;

@Mod.EventBusSubscriber(modid = JustEnoughData.MODID, value = Side.SERVER)
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
    public static class PlanPermissionChanged extends Event{
        public final Plan plan;
        public final PlanPermission from;
        public final PlanPermission to;
        public PlanPermissionChanged(Plan plan, PlanPermission from, PlanPermission to){
            this.plan = plan;
            this.from = from;
            this.to = to;
        }
    }
    @SubscribeEvent
    public static void onPermissionChanged(PlanPermissionChanged event){
        Plan dummy = new Plan(event.plan.getPlanFile());
        dummy.owner = event.plan.owner;
        dummy.permission = event.from;
        Set<EntityPlayerMP> AccessiblesPrev = JEIDataNetHandler.getAccessiblePlayers(dummy);
        Set<EntityPlayerMP> AccessiblesCur = JEIDataNetHandler.getAccessiblePlayers(event.plan);
        Set<EntityPlayerMP> ShouldRemove = new HashSet<>(AccessiblesPrev);
        ShouldRemove.removeAll(AccessiblesCur);
        Set<EntityPlayerMP> ShouldSendFullPlan = new HashSet<>(AccessiblesCur);
        ShouldSendFullPlan.removeAll(AccessiblesPrev);
        new DeleteObjBaseResponse(event.plan.id).sendTo(ShouldRemove);
        new SendFullPlanToClient(event.plan).sendTo(ShouldSendFullPlan);
    }

    public boolean canAccess(@Nonnull EntityPlayer player, boolean readOnly){
        //Universe should be loaded when calling this method
        ForgePlayer player1 = Universe.get().getPlayer(owner);
        ForgePlayer player2 = Universe.get().getPlayer(player);
        switch (permission){
            case PUBLIC_R:
                return player2.canInteract(player1, EnumPrivacyLevel.PUBLIC) && (readOnly || player2.canInteract(player1, EnumPrivacyLevel.PRIVATE));
            case PUBLIC_RW:
                return player2.canInteract(player1, EnumPrivacyLevel.PUBLIC);
            case TEAM_R:
                return player2.canInteract(player1, EnumPrivacyLevel.TEAM) && (readOnly || player2.canInteract(player1, EnumPrivacyLevel.PRIVATE));
            case TEAM_RW:
                return player2.canInteract(player1, EnumPrivacyLevel.TEAM);
            case PRIVATE:
                return player2.canInteract(player1, EnumPrivacyLevel.PRIVATE);
            default:
                JustEnoughData.logger.error("Unknown permission:" + permission.getId());
        }
        return false;
    }
    public boolean canEdit(@Nonnull EntityPlayer player){
        return !readOnly && canAccess(player, false);
    }
    //endregion
    //region load
    //region SaveToFile
    @Override
    public void writeData(NBTTagCompound nbt) {
        super.writeData(nbt);
        nbt.setString("permission", permission.getId());
        if(owner != null)
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
        if(nbt.hasKey("owner"))
            owner = UUID.fromString(nbt.getString("owner"));
        readOnly = nbt.getBoolean("readonly");

        NBTTagList list = nbt.getTagList("items", 10);
        for (NBTBase nbtbase :
                list) {
            NBTTagCompound data = (NBTTagCompound)nbtbase;
            int id = data.getInteger("id");
            PlanObjectType type = PlanObjectType.NAME_MAP.get(data.getString("type"));
            PlanObjectBase base = null;
            switch (type){
                case PROVIDER:
                    base = new Provider(this);
                    break;
                case RESOURCE:
                    base = new Resource(this);
                    break;
                default:
                    JustEnoughData.logger.error("Failed to load item:" + id + "which type is:" + type.toString());
                    continue;
            }
            base.id = id;
            items.add(base);
        }
    }
    //endregion
    //region ServerFile
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
        PlanPermission planPermission_new = PlanPermission.NAME_MAP.read(data);
        if(planPermission_new != permission){
            PlanPermissionChanged event = new PlanPermissionChanged(this, permission, planPermission_new);
            permission = planPermission_new;
            MinecraftForge.EVENT_BUS.post(event);
        }
        owner = data.readUUID();
        readOnly = data.readBoolean();


        int size = data.readVarInt();
        //Don't create new obj during update phrase
        if(items.size() > 0){
            if(size != items.size())
                JustEnoughData.logger.warn(String.format("Plan data has been loaded, but data from network seems not the as same as local.\n" +
                        "Network items.size() -> %d Local items.size() -> %d", size, items.size()));
            return;
        }
        for (int i = 0; i < size; i++) {
            int id = data.readInt();
            PlanObjectType type = PlanObjectType.NAME_MAP.read(data);
            //type should always valid
            PlanObjectBase base = null;
            switch (type){
                case PROVIDER:
                    base = new Provider(this);
                    break;
                case RESOURCE:
                    base = new Resource(this);
                    break;
                default:
                    JustEnoughData.logger.error("[Net]Failed to load item:" + id + "which type is:" + type.toString());
                    continue;
            }
            base.id = id;
            items.add(base);
        }
    }
    //endregion
    //endregion
    //region BaseMethods

    @Override
    public void onCreated() {
        super.onCreated();
        getPlanFile().plans.add(this);
    }

    @Override
    public void deleteSelf() {
        deleteChildren();
        super.deleteSelf();
        getPlanFile().plans.remove(this);
    }

    @Override
    public void deleteChildren() {
        for (PlanObjectBase base : items) {
            ModifyEvent event;
            event = new ModifyEvent(getPlanFile().target,
                    EventBase.Source.Other_Local,
                    base, ModifyEvent.ModifyType.Delete, (toModify) -> {
                toModify.deleteSelf();
                return toModify;
            });
            MinecraftForge.EVENT_BUS.post(event);
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
    public Path getFile() {
        return getPlanFile().saveFolder.resolve("Plans/" + getCodeString(getPlan().id) + "/" + "Plan.nbt");
    }
    //endregion
}
