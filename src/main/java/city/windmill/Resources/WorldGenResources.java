package city.windmill.Resources;

import java.util.LinkedList;
import java.util.List;

public class WorldGenResources implements IResourceProvider{
    List<IResource> resources = new LinkedList<>();

    public WorldGenResources(List<IResource> resources){
        this.resources = resources;
    }

    @Override
    public List<IResource> getResources() {
        return resources;
    }

    @Override
    public String getName() {
        return "World Gen";
    }
}
