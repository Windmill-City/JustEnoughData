package city.windmill.net;

import city.windmill.CommonProxy;
import city.windmill.JustEnoughData;
import city.windmill.Plan.*;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

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
        parent = base.getParentID();
        data.writeInt(parent);
        nbt = new NBTTagCompound();
        base.writeData(nbt);
        data.writeNBT(nbt);
        JustEnoughData.logger.debug(String.format("Send Create Message to Server, Type: %s Id: %d", base.getObjectType(), base.id));
    }

    @Override
    public void readData(DataIn data) {
        type = PlanObjectType.NAME_MAP.read(data);
        parent = data.readInt();
        nbt = data.readNBT();
        JustEnoughData.logger.debug(String.format("Received Client Create Message, Type: %s parent: %d", type, parent));
    }

    @Override
    public void onMessage(EntityPlayerMP player) {
        PlanObjectBase parent = CommonProxy.localFile.get(this.parent);
        if(parent != null) {
            ModifyEvent event = new ModifyEvent.FromClient(CommonProxy.localFile, ModifyEvent.ModifyType.Create, (toModify) -> {
                base = ((PlanFile) toModify).NewObjBase(type, parent, nbt);
                if (base instanceof Plan)
                    ((Plan) base).owner = player.getUniqueID();
                return base;
            }, player);
            if(!MinecraftForge.EVENT_BUS.post(event) && event.getResult() == Event.Result.ALLOW)
                new CreateObjBaseResponse(base).sendTo(JEIDataNetHandler.getAccessiblePlayers(base));
            else
                JustEnoughData.logger.warn(String.format("Player: %s try create obj: Type: %s Parent: %d , but event %s", player, type, this.parent,
                        event.isCanceled() ? "Canceled" : "Denied"));
        }
        else
            JustEnoughData.logger.warn(String.format("Player: %s try create obj: Type: %s Parent: %d , Parent not exist", player, type, this.parent));
    }
}
