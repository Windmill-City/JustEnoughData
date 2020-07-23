package city.windmill.Plan;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EventBase extends Event {
    public final Target target;
    public final Source source;

    public EventBase(Target target, Source source){
        this.target = target;
        this.source = source;
    }

    public EventBase(Source source){
        this.target = Target.Both;
        this.source = source;
    }

    public static class Local extends EventBase {
        public Local(Source source) {
            super(Target.Local, source);
        }
    }

    public static class Remote extends EventBase {
        public Remote(Source source) {
            super(Target.Remote, source);
        }
    }

    public static class FromServer extends Remote {
        public FromServer() {
            super(Source.NetWork_FromServer);
        }
    }

    public static class FromClient extends Local {
        public FromClient() {
            super(Source.NetWork_FromClient);
        }
    }

    public static class Gui extends EventBase {
        public Gui(Target target) {
            super(target, Source.GUI_FromClient);
        }
    }

    public enum Target{
        Local,
        Remote,
        Both;

        public boolean isLocal(){
            return this == Local;
        }

        public boolean isRemote(){
            return this == Remote;
        }
    }

    public enum Source{
        NetWork_FromServer,
        NetWork_FromClient,
        GUI_FromClient,
        Other_FromClient,
        Other_FromServer,
        Other_Local
    }

    public boolean acceptLocal(){
        return target == Target.Local || target == Target.Both;
    }

    public boolean acceptRemote(){
        return target == Target.Remote || target == Target.Both;
    }

    public boolean isNetWork(@Nullable Side acceptableSide){
        if(acceptableSide == null)
            return source == Source.NetWork_FromClient || source == Source.NetWork_FromServer;
        else
            switch (acceptableSide){
                case CLIENT:
                    return source == Source.NetWork_FromClient;
                case SERVER:
                    return source == Source.NetWork_FromServer;
                default:
                    throw new IllegalArgumentException("Unknown Side:" + acceptableSide.name());
            }
    }

    public boolean isSide(@Nonnull Side side){
        switch (side){
            case CLIENT:
                return source == Source.GUI_FromClient || source == Source.NetWork_FromClient || source == Source.Other_FromClient;
            case SERVER:
                return source == Source.NetWork_FromServer || source == Source.Other_FromServer;
            default:
                throw new IllegalArgumentException("Unknown Side:" + side.name());
        }
    }

    public boolean isGui(){
        return source == Source.GUI_FromClient;
    }
}
