package city.windmill.Plan;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

public class SyncEvent{
    public static class Write extends EventBase.FromServer{
        public final DataOut data;
        @Nullable
        public final EntityPlayer player;

        public Write(DataOut data, @Nullable EntityPlayer player) {
            this.data = data;
            this.player = player;
        }
    }

    public static class Read extends EventBase.FromClient{
        public final DataIn data;

        public Read(DataIn data) {
            this.data = data;
        }
    }
}
