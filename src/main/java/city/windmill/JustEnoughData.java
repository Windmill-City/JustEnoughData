package city.windmill;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

@JEIPlugin
@Mod(modid = JustEnoughData.MODID, name = JustEnoughData.NAME, version = JustEnoughData.VERSION, dependencies = "required-after:jeresources@[0.9.2.60,);required-after:jei@[4.15.0,);required-after:forge@[14.23.5.2847,);")
public class JustEnoughData implements IModPlugin {

    public static final String MODID = "jeidata";
    public static final String NAME = "Just Enough Data";
    public static final String VERSION = "1.0";

    public static Logger logger;

    IJeiRuntime JEIRuntime = null;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIRuntime = jeiRuntime;
        HashSet<Class> clazzs = new HashSet();
        logger.info("Recipe Categories");
        for (IRecipeCategory category : JEIRuntime.getRecipeRegistry().getRecipeCategories()) {
            logger.info(String.format("Found %s by %s", category.getTitle(), category.getModName()));
            for (Object obj : JEIRuntime.getRecipeRegistry().getRecipeWrappers(category)){
                IRecipeWrapper wrapper = (IRecipeWrapper)obj;
                Ingredients ingredients = new Ingredients();
                //wrapper.getIngredients(ingredients);
            }
        }
        logger.info("Ingredients");
        for (Object object : JEIRuntime.getIngredientFilter().getFilteredIngredients()) {

            clazzs.add(object.getClass());
            if(object instanceof ItemStack)
                logger.info(String.format("ItemStack:%s",((ItemStack)object).getDisplayName()));
            else if(object instanceof FluidStack)
                logger.info(String.format("FluidStack:%s",((FluidStack)object).getLocalizedName()));
            else if(object instanceof EnchantmentData)
                logger.info(String.format("EnchantmentData:%s",((EnchantmentData)object).enchantment.getName()));
            else
                logger.info(String.format("Class:%s", object.getClass().getTypeName()));
        }
        logger.info("Ingredient Types");
        for (Class clazz : clazzs) {
            logger.info(String.format("Class:%s", clazz.getTypeName()));
        }
    }
}
