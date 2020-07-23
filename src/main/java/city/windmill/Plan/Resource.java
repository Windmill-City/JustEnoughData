package city.windmill.Plan;

import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Resource extends PlanObject {
    //region NeedToSave
    public Object resource = ItemStack.EMPTY;
    //endregion
    //region runtime
    private static final String MARKER_OTHER = "O:";
    private static final String MARKER_STACK = "T:";

    public IIngredientHelper helper;
    public IIngredientRenderer renderer;

    public final Set<Provider> providers = new HashSet<>();
    //endregion
    public Resource(Plan plan){
        super(plan);
        shape = PlanObjectShape.CIRCLE;
    }
    //region load
    //region SaveToFile
    @Override
    public void writeData(NBTTagCompound nbt) {
        super.writeData(nbt);
        if(resource != ItemStack.EMPTY)
            if(resource instanceof  ItemStack)
                nbt.setString("resource", MARKER_STACK + ((ItemStack)resource).writeToNBT(new NBTTagCompound()).toString());
            else
                nbt.setString("resource", MARKER_OTHER + helper.getUniqueId(resource));
    }

    @Override
    public void readData(NBTTagCompound nbt) {
        super.readData(nbt);
        if(nbt.hasKey("resource"))
            loadItemByJsonStr(nbt.getString("resource"));
        if(resource == ItemStack.EMPTY)
            deleteSelf();
    }
    //endregion
    //region ServerFile
    @Override
    public void writeNetData(DataOut data) {
        super.writeNetData(data);
        data.writeBoolean(resource != ItemStack.EMPTY);
        if(resource != ItemStack.EMPTY)
            if(resource instanceof ItemStack)
                data.writeString(MARKER_STACK + ((ItemStack)resource).writeToNBT(new NBTTagCompound()).toString());
            else
                data.writeString(MARKER_OTHER + helper.getUniqueId(resource));
    }

    @Override
    public void readNetData(DataIn data) {
        super.readNetData(data);
        if(data.readBoolean())
            loadItemByJsonStr(data.readString());
        else
            resource = ItemStack.EMPTY;
        if(resource == ItemStack.EMPTY)
            deleteSelf();
    }
    //endregion
    private void loadItemByJsonStr(String ingredientJsonString){
        Collection<IIngredientType> otherIngredientTypes = new ArrayList<>(Internal.getIngredientRegistry().getRegisteredIngredientTypes());
        otherIngredientTypes.remove(VanillaTypes.ITEM);

        if (ingredientJsonString.startsWith(MARKER_STACK)) {
            String itemStackAsJson = ingredientJsonString.substring(MARKER_STACK.length());
            try {
                NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(itemStackAsJson);
                ItemStack itemStack = new ItemStack(itemStackAsNbt);
                if (!itemStack.isEmpty()) {
                    resource = itemStack;
                    helper = Internal.getIngredientRegistry().getIngredientHelper(itemStack);
                    renderer = Internal.getIngredientRegistry().getIngredientRenderer(itemStack);
                } else {
                    JustEnoughData.logger.warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                }
            } catch (NBTException e) {
                JustEnoughData.logger.error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, e);
            }
        }else if(ingredientJsonString.startsWith(MARKER_OTHER)){
            String uid = ingredientJsonString.substring(MARKER_OTHER.length());
            Object ingredient = getUnknownIngredientByUid(otherIngredientTypes, uid);
            if (ingredient != null) {
                resource = ingredient;
                helper = Internal.getIngredientRegistry().getIngredientHelper(ingredient);
                renderer = Internal.getIngredientRegistry().getIngredientRenderer(ingredient);
            }
        }else{
            JustEnoughData.logger.error("Failed to load unknown bookmarked ingredient:\n{}", ingredientJsonString);
        }
    }

    @Nullable
    private Object getUnknownIngredientByUid(Collection<IIngredientType> ingredientTypes, String uid) {
        for (IIngredientType<?> ingredientType : ingredientTypes) {
            Object ingredient = Internal.getIngredientRegistry().getIngredientByUid(ingredientType, uid);
            if (ingredient != null) {
                return ingredient;
            }
        }
        return null;
    }
    //endregion
    //region BaseMethods
    @Override
    public void deleteSelf() {
        for (Provider provider : providers) {
            provider.outputs.remove(this);
            provider.inputs.remove(this);
        }
        super.deleteSelf();
    }

    @Override
    public PlanObjectType getObjectType() {
        return PlanObjectType.RESOURCE;
    }

    @Override
    public Path getFile() {
        return getPlanFile().saveFolder.resolve("Plans/" + getCodeString(getPlan().id) + "/" + getCodeString() + ".nbt");
    }
    //endregion
    //region IPanelItem
    @Override
    public void draw(Minecraft minecraft, int xOffset, int yOffset) {
        shape.draw(xOffset, yOffset, width, height);
        if(!icon.isEmpty())
            icon.draw(xOffset,yOffset,width,height);
        else if(!invalid){
            renderer.render(minecraft, xOffset, yOffset, resource);
        }
    }
    //endregion
}
