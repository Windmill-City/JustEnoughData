package city.windmill.Resources;

import mezz.jei.api.gui.IDrawable;

import java.util.List;

public interface IResource {
    /**
     * @return the providers which can provide this resource
     */
    List<IResourceProvider> getProviders();

    /**
     * @return the icon of the resource
     */
    IDrawable getIcon();

    /**
     * @return the resource's instance
     */
    Object getResource();

    /**
     * @return the resource's uni-name
     */
    String getName();

    /**
     * @return the resource's local name
     */
    String getLocalizeName();
}
