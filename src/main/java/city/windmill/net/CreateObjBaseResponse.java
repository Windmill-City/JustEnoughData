package city.windmill.net;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import city.windmill.Plan.ModifyEvent;
import city.windmill.Plan.PlanFile;
import city.windmill.Plan.PlanObjectBase;
import city.windmill.Plan.PlanObjectType;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

public class CreateObjBaseResponse extends MessageToClient {

    protected PlanObjectBase base;

    private PlanObjectType type;
    private int parent;
    private int id;
    private NBTTagCompound nbt;

    @Override
    public NetworkWrapper getWrapper() {
        return JEIDataNetHandler.GENERAL;
    }

    public CreateObjBaseResponse() {
    }

    public CreateObjBaseResponse(PlanObjectBase base){
        this.base = base;
    }

    @Override
    public void writeData(DataOut data) {
        PlanObjectType.NAME_MAP.write(data, base.getObjectType());
        data.writeInt(base.getParentID());
        data.writeInt(base.id);
        nbt = new NBTTagCompound();
        base.writeData(nbt);
        data.writeNBT(nbt);
    }

    @Override
    public void readData(DataIn data) {
        type = PlanObjectType.NAME_MAP.read(data);
        parent = data.readInt();
        id = data.readInt();
        nbt = data.readNBT();
    }

    @Override
    public void onMessage() {
        PlanObjectBase parent = CommonProxy.localFile.get(this.parent);
        if(parent != null)
            MinecraftForge.EVENT_BUS.post(new ModifyEvent.FromServer(ClientProxy.remoteFile, ModifyEvent.ModifyType.Create, (toModify) -> {
                base = ((PlanFile)toModify).NewObjBase(type, parent, nbt);
                return base;
            }));
    }
}
