package city.windmill.Plan;

import com.feed_the_beast.ftblib.lib.util.IWithID;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;

public enum  PlanPermission implements IWithID {
    PUBLIC_RW("public_rw"),
    PUBLIC_R("public_r"),
    TEAM_RW("team_rw"),
    TEAM_R("team_r"),
    PRIVATE("private");

    public static final NameMap<PlanPermission> NAME_MAP = NameMap.create(TEAM_R, values());

    private String id;
    PlanPermission(String i){
        id = i;
    }
    @Override
    public String getId() {
        return id;
    }
}
