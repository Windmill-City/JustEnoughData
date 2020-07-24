package city.windmill.net;

import city.windmill.CommonProxy;
import city.windmill.Plan.*;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

public class CreateObjBase extends MessageToServer {

    protected PlanObjectBase base;

    private PlanObjectType type;
    private int parent = 0;
    private NBTTagCompound nbt;

    public CreateObjBase() {
    }

    public CreateObjBase(PlanObjectBase base){
        this.base = base;
    }

    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    @Override
    public void writeData(DataOut data) {
        PlanObjectType.NAME_MAP.write(data, base.getObjectType());
        data.writeInt(base.getParentID());
        nbt = new NBTTagCompound();
        base.writeData(nbt);
        data.writeNBT(nbt);
    }

    @Override
    public void readData(DataIn data) {
        type = PlanObjectType.NAME_MAP.read(data);
        parent = data.readInt();
        nbt = data.readNBT();
    }

    @Override
    public void onMessage(EntityPlayerMP player) {
        PlanObjectBase parent = CommonProxy.localFile.get(this.parent);
        if(parent != null)
        MinecraftForge.EVENT_BUS.post(new ModifyEvent.FromClient(CommonProxy.localFile, ModifyEvent.ModifyType.Create, (toModify) -> {
            base = ((PlanFile)toModify).NewObjBase(type, parent, nbt);
            if(base instanceof Plan)
                ((Plan)base).owner = player.getUniqueID();
            new CreateObjBaseResponse(base).sendTo(JEIDataNetHandler.getAccessiblePlayers(base));
            return base;
        }, player));
    }
}
