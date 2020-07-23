package city.windmill.Plan;

import java.nio.file.Path;

public class FileEvent extends EventBase{
    public final Action action;
    public final Path dir;
    public enum Action{
        Load,
        Save
    }

    public FileEvent(Source source, Action action, Path dir) {
        super(Target.Local, source);
        this.action = action;
        this.dir = dir;
    }

    public static class Load extends FileEvent{
        public Load(Source source, Path dir) {
            super(source, Action.Load, dir);
        }
    }

    public static class Save extends FileEvent{
        public Save(Source source, Path dir) {
            super(source, Action.Save, dir);
        }
    }
}
