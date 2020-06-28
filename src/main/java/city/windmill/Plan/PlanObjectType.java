package city.windmill.Plan;

import com.feed_the_beast.ftblib.lib.util.IWithID;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;

import java.util.function.Predicate;

public enum PlanObjectType implements IWithID, Predicate<PlanObjectBase> {
    NULL("null", 1),
    PROVIDER("provider", 2),
    RESOURCE("resource", 4),
    PLAN("plan", 8),
    FILE("file", 16);

    private final String Id;
    private final int Flag;
    public static final NameMap<PlanObjectType> NAME_MAP = NameMap.create(NULL, values());

    PlanObjectType(String id, int flag){
        Id = id;
        Flag = flag;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public boolean test(PlanObjectBase planObjectBase) {
        return (planObjectBase == null ? NULL : planObjectBase.getObjectType()) == this;
    }
}
