package city.windmill.Plan;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@Event.HasResult
public class ModifyEvent extends EventBase{
    public final PlanObjectBase toModify;
    public final ModifyType type;
    public final IModifyAction modifyAction;
    public PlanObjectBase ModifiedResult;
    public enum ModifyType{
        Create,
        Delete,
        Update
    }
    public interface IModifyAction {
        PlanObjectBase onModify(PlanObjectBase toModify);
    }

    public ModifyEvent(Target target, Source source, PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction) {
        super(target, source);
        this.toModify = toModify;
        this.type = type;
        this.modifyAction = modifyAction;
    }

    public ModifyEvent(Source source, PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction) {
        super(source);
        this.toModify = toModify;
        this.type = type;
        this.modifyAction = modifyAction;
    }

    public static class Local extends ModifyEvent {
        public Local(Source source, PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction) {
            super(Target.Local, source, toModify, type, modifyAction);
        }
    }

    public static class Remote extends ModifyEvent {
        public Remote(Source source, PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction) {
            super(Target.Remote, source, toModify, type, modifyAction);
        }
    }

    public static class FromServer extends ModifyEvent.Remote {
        public FromServer(PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction) {
            super(Source.NetWork_FromServer, toModify, type, modifyAction);
        }
    }

    public static class FromClient extends ModifyEvent.Local {
        public final EntityPlayer modifier;
        public FromClient(PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction, EntityPlayer modifier) {
            super(Source.NetWork_FromClient, toModify, type, modifyAction);
            this.modifier = modifier;
        }
    }

    public static class Gui extends ModifyEvent {
        public Gui(Target target, PlanObjectBase toModify, ModifyType type, IModifyAction modifyAction) {
            super(target, Source.GUI_FromClient, toModify, type, modifyAction);
        }
    }
}
