package city.windmill.Resources;

import java.util.List;

public interface IResourceProvider {
    /**
     * @return the resources this provider can provide
     */
    List<IResource> getResources();

    /**
     * @return the name of the provider
     */
    String getName();
}
