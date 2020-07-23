package city.windmill.Plan;

import city.windmill.CommonProxy;
import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class PlanFile extends PlanObjectBase {
    public final int FILE_VERSION = 1;
    //region NeedToSave
    public int version = FILE_VERSION;
    //endregion
    //region Init required
    //endregion
    //region runtime
    public Path saveFolder = Paths.get("./PlanData/");
    public final EventBase.Target target;
    public final List<Plan> plans = new ArrayList<>();
    private final Int2ObjectOpenHashMap<PlanObjectBase> map = new Int2ObjectOpenHashMap<>();

    protected PlanFile(EventBase.Target target) {
        this.target = target;
    }
    //endregion

    public PlanObjectBase NewObjBase(PlanObjectType type, PlanObjectBase parent, NBTTagCompound extraNbt){
        PlanObjectBase base;
        switch (type){
            case PLAN:
                base = new Plan(this);
                break;
            case RESOURCE:
                if(!(parent instanceof Plan) && !parent.invalid)
                    throw new IllegalArgumentException("Parent Invalid:" + parent.id);
                base = new Resource((Plan) parent);
                break;
            case PROVIDER:
                if(!(parent instanceof Plan) && !parent.invalid)
                    throw new IllegalArgumentException("Parent Invalid:" + parent.id);
                base = new Provider((Plan) parent);
                break;
            default:
                throw new IllegalArgumentException("Invalid type:" + type);
        }
        base.readData(extraNbt);
        return base;
    }

    public void onModify(ModifyEvent event){
        PlanObjectBase base = event.modifyAction.onModify(event.toModify);
        switch (event.type){
            case Create:
                base.onCreated();
                map.put(base.id, base);
                event.setResult(Event.Result.ALLOW);
                break;
            case Update:
                //just save
                event.setResult(Event.Result.ALLOW);
                break;
            case Delete:
                map.remove(base.id);
                event.setResult(Event.Result.ALLOW);
        }
        event.ModifiedResult = base;
    }

    @Nullable
    public final PlanObjectBase get(int id) {
        if (id == 0)
        {
            return null;
        }
        else if (id == 1)
        {
            return this;
        }

        PlanObjectBase object = map.get(id);
        return object == null || object.invalid ? null : object;
    }

    public static boolean canAccess(PlanObjectBase base, @Nullable EntityPlayer player, boolean readOnly){
        return base.getPlan() == null || base.getPlan().canAccess(player, readOnly);
    }

    public static boolean canEdit(PlanObjectBase base, @Nullable EntityPlayer player){
        return base.getPlan() == null || base.getPlan().canEdit(player);
    }

    //region save/load
    private NBTTagCompound createIndex(List<? extends PlanObjectBase> list) {
        int[] index = new int[list.size()];

        for (int i = 0; i < index.length; i++)
        {
            index[i] = list.get(i).id;
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setIntArray("index", index);
        return nbt;
    }
    private int[] readIndex(NBTTagCompound nbt) {
        return nbt == null ? new int[0] : nbt.getIntArray("index");
    }

    /**
     * get plans for player with permission check (readonly)
     * @param player null will return all plans
     */
    public List<Plan> getPlanByPlayer(@Nullable EntityPlayer player) {
        List<Plan> result = new LinkedList<>();
        if(player == null)
            return plans;
        for (Plan plan : plans) {
            if (canAccess(plan, player, true))
                result.add(plan);
        }
        return result;
    }

    public void onLoad(FileEvent.Load event){
        try {
            NBTTagCompound nbtPlanFile = NBTUtils.readNBT(event.dir.resolve(getFile()).toFile());
            version = nbtPlanFile == null ? FILE_VERSION : nbtPlanFile.getInteger("version");
            plans.clear();
            //todo: create backup when version not equal and try fix data
            for (int i : readIndex(nbtPlanFile)) {
                Plan plan = new Plan(this);
                plan.id = i;
                plans.add(plan);
                NBTTagCompound nbt = NBTUtils.readNBT(event.dir.resolve(plan.getFile()).toFile());
                if (nbt == null) {
                    JustEnoughData.logger.error(String.format("Missing Plan data, id: %d", plan.id));
                    plan.deleteSelf();
                    continue;
                }
                try {
                    plan.readData(nbt);
                } catch (Exception e) {
                    JustEnoughData.logger.error(String.format("Failed to read Plan data, id: %d", plan.id));
                    JustEnoughData.logger.error(e);
                    plan.deleteSelf();
                }
            }
            refreshIdMap();
            for (Plan plan : plans)
                for (PlanObjectBase base : plan.items) {
                    NBTTagCompound nbt = NBTUtils.readNBT(event.dir.resolve(base.getFile()).toFile());
                    if (nbt == null) {
                        JustEnoughData.logger.error(String.format("Missing item data, id: %d Plan: Name: %s --- id: %d", base.id, plan.name, plan.id));
                        base.deleteSelf();
                        map.remove(base.id);
                        continue;
                    }
                    try {
                        base.readData(nbt);
                    } catch (Exception e) {
                        JustEnoughData.logger.error(String.format("Failed to read item data, id: %d Plan: Name: %s --- id: %d", base.id, plan.name, plan.id));
                        JustEnoughData.logger.error(e);
                        base.deleteSelf();
                        map.remove(base.id);
                    }
                }
        }catch (Exception e){
            JustEnoughData.logger.error(String.format("Failed to load PlanFile, dir:%s", event.dir));
            JustEnoughData.logger.error(e);
            plans.clear();
            refreshIdMap();
        }
    }

    public void onSave(FileEvent.Save event){
        NBTTagCompound nbtPlanFile = createIndex(plans);
        nbtPlanFile.setInteger("version", FILE_VERSION);
        NBTUtils.writeNBTSafe(event.dir.resolve(getFile()).toFile(), nbtPlanFile);
        for (Plan plan : plans) {
            if(plan.invalid)
                continue;
            NBTTagCompound nbtPlan = new NBTTagCompound();
            plan.writeData(nbtPlan);
            NBTUtils.writeNBTSafe(event.dir.resolve(plan.getFile()).toFile(), nbtPlan);
            for (PlanObjectBase base : plan.items) {
                if(base.invalid)
                    continue;
                NBTTagCompound nbtBase = new NBTTagCompound();
                base.writeData(nbtBase);
                NBTUtils.writeNBTSafe(event.dir.resolve(base.getFile()).toFile(), nbtBase);
            }
        }
    }

    public void onSyncWrite(SyncEvent.Write event){
        DataOut data = event.data;
        List<Plan> PlanList = getPlanByPlayer(event.player);
        data.writeVarInt(PlanList.size());
        for (Plan plan : PlanList) {
            data.writeInt(plan.id);
            plan.writeNetData(data);
        }
        for (Plan plan : PlanList) {
            for (PlanObjectBase base : plan.items){
                base.writeNetData(data);
            }
        }
    }

    public void onSyncRead(SyncEvent.Read event){
        DataIn data = event.data;
        plans.clear();
        int size = data.readVarInt();
        for (int i = 0; i < size; i++) {
            Plan plan = new Plan(this);
            plan.id = data.readInt();
            plans.add(plan);
            plan.readNetData(data);
        }
        refreshIdMap();
        for (Plan plan : plans) {
            for (PlanObjectBase base : plan.items){
                base.readNetData(data);
            }
        }
    }

    //endregion
    //region id
    public void refreshIdMap(){
        map.clear();
        for (Plan plan : plans){
            map.put(plan.id, plan);
            for (PlanObjectBase base : plan.items){
                map.put(base.id, base);
            }
        }
    }

    public int uniqueOrNewID(int id) {
        while (id == 0 || id == 1 || map.get(id) != null)
        {
            id = MathUtils.RAND.nextInt();
        }

        return id;
    }
    //endregion
    //region BaseMethods

    @Override
    public void writeData(NBTTagCompound nbt) {
    }

    @Override
    public void readData(NBTTagCompound nbt) {
    }

    @Override
    public void writeNetData(DataOut data) {
    }

    @Override
    public void readNetData(DataIn data) {
    }

    @Override
    public PlanObjectType getObjectType() {
        return PlanObjectType.FILE;
    }

    @Override
    public PlanFile getPlanFile() {
        return this;
    }

    @Nullable
    @Override
    public Path getFile() {
        return saveFolder.resolve("PlanFile.nbt");
    }
    //endregion
}
