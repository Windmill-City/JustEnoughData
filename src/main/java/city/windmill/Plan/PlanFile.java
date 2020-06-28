package city.windmill.Plan;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class PlanFile extends PlanObjectBase {
    public final int FILE_VERSION = 1;
    //region NeedToSave
    public int version = FILE_VERSION;
    //endregion
    //region Init required
    public final File folder;
    //endregion
    //region runtime
    private final List<Plan> plans = new ArrayList<>();
    private final Int2ObjectOpenHashMap<PlanObjectBase> map = new Int2ObjectOpenHashMap<>();
    //endregion

    public PlanFile(File folder) {
        this.folder = folder;
    }

    @Nullable
    public final PlanObjectBase getBase(int id) {
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

    public final PlanObjectBase create(PlanObjectType type, int parent){
        switch (type){
            case PLAN:
                return new Plan(this);
            case PROVIDER:
                PlanObjectBase p = getBase(parent);
                if(p instanceof Plan){
                    return new Provider((Plan) p);
                }else{
                    throw new IllegalArgumentException("Parent Plan Not Found:" + parent);
                }
            case RESOURCE:
                PlanObjectBase p2 = getBase(parent);
                if(p2 instanceof Plan){
                    return new Resource((Plan) p2);
                }else{
                    throw new IllegalArgumentException("Parent Plan Not Found:" + parent);
                }
            default:
                throw new IllegalArgumentException("Unknown type:" + type.getId());
        }
    }

    @Nullable
    public final PlanObjectBase remove(int id){
        PlanObjectBase base = map.remove(id);
        return base;
    }

    public abstract boolean isClient();

    public void removeObj(int id){
        PlanObjectBase base = getBase(id);
        if(base != null){
            base.deleteChildren();
            base.deleteSelf();
            if(!isClient());
            //todo:sent to all
        }
    }

    public void updateObj(int id, DataIn data){
        PlanObjectBase base = getBase(id);
        if(base != null){
            base.readNetData(data);
            if(!isClient());
            //todo:sent to all
        }
    }
    //region load
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

    public void writeDataFull(File folder){
        NBTTagCompound nbtPlanFile = createIndex(plans);
        nbtPlanFile.setInteger("version", FILE_VERSION);
        NBTUtils.writeNBTSafe(getFile(), nbtPlanFile);
        for (Plan plan : plans) {
            if(plan.invalid)
                continue;
            NBTTagCompound nbtPlan = new NBTTagCompound();
            plan.writeData(nbtPlan);
            NBTUtils.writeNBTSafe(plan.getFile(), nbtPlan);
            for (PlanObjectBase base : plan.items) {
                if(base.invalid)
                    continue;
                NBTTagCompound nbtBase = new NBTTagCompound();
                base.writeData(nbtBase);
                NBTUtils.writeNBTSafe(base.getFile(), nbtBase);
            }
        }
    }

    public void readDataFull(File folder){
        NBTTagCompound nbtPlanFile = NBTUtils.readNBT(getFile());
        version = nbtPlanFile == null ? FILE_VERSION : nbtPlanFile.getInteger("version");
        plans.clear();
        //todo: create backup when version not equal
        for (int i : readIndex(nbtPlanFile)) {
            Plan plan = new Plan(this);
            plan.id = i;
            plans.add(plan);
            plan.readData(NBTUtils.readNBT(plan.getFile()));
        }
        refreshIdMap();
        for (Plan plan : plans)
        for (PlanObjectBase base : plan.items) {
            base.readData(NBTUtils.readNBT(base.getFile()));
        }
    }

    /**
     * get plans for player with permission check (readonly)
     * @param player null will write all plans
     */
    public List<Plan> getPlanByPlayer(@Nullable EntityPlayer player) {
        List<Plan> result = new LinkedList<>();
        if(player == null)
            return plans;
        for (Plan plan : plans) {
            if(plan.canAccess(player, true))
                result.add(plan);
        }
        return result;
    }

    public void writeNetDataFull(DataOut data, @Nullable EntityPlayer player){
        List<Plan> PlanList = getPlanByPlayer(player);
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

    public void readNetDataFull(DataIn data){
        plans.clear();
        for (int i = 0; i < data.readVarInt(); i++) {
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

    public int newID() {
        return checkOrNewID(0);
    }

    public int checkOrNewID(int id) {
        while (id == 0 || id == 1 || map.get(id) != null)
        {
            id = MathUtils.RAND.nextInt();
        }

        return id;
    }
    //endregion
    //region BaseMethods
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
    public File getFile() {
        return new File(getPlanFile().folder.getPath(), "PlanFile.nbt");
    }
    //endregion
}
