package city.windmill.Plan;

import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Provider extends PlanObject {
    //region NeedToSave
    public ItemStack provider = ItemStack.EMPTY;
    public Ticks processTime = Ticks.NO_TICKS;

    public Map<Resource,Amount> inputs = new HashMap<>();
    public Map<Resource,Amount> outputs = new HashMap<>();
    //endregion
    //region runtime
    private static final String MARKER_STACK = "T:";
    public IIngredientHelper helper = null;
    public IIngredientRenderer renderer = null;
    //endregion
    //region Util Func
    public Function<Map<Resource,Amount>, NBTTagList> writeToNBT = data -> {
        NBTTagList list = new NBTTagList();
        for (Resource res :
                data.keySet()) {
            NBTTagCompound resData = new NBTTagCompound();
            resData.setInteger("id", res.id);
            resData.setInteger("amount", data.get(res).amount);
            if(data.get(res).chance != 1)
                resData.setFloat("chance", data.get(res).chance);
            list.appendTag(resData);
        }
        return list;
    };

    public Function<NBTTagList, Map<Resource,Amount>> readFromNBT = list -> {
        HashMap<Resource,Amount> data = new HashMap<>();
        for (NBTBase nbt :
                list) {
            NBTTagCompound resData = (NBTTagCompound) nbt;
            int id = resData.getInteger("id");
            PlanObjectBase base = getPlanFile().get(id);
            if(base instanceof Resource) {
                int amount = resData.getInteger("amount");
                Amount amount_;
                if (resData.hasKey("chance")) {
                    amount_ = new Amount(amount, resData.getFloat("chance"));
                } else
                    amount_ = new Amount(amount);
                data.put((Resource) base, amount_);
                ((Resource)base).providers.add(this);
            }else{
                JustEnoughData.logger.error("Resource Not Found/Incorrect Type:" + id);
            }
        }
        return data;
    };

    public BiFunction<DataOut, Map<Resource,Amount>, DataOut> writeToNet = (dataOut, data) ->{
        dataOut.writeVarInt(data.size());
        for (Resource res :
                data.keySet()) {
            dataOut.writeVarInt(res.id);
            dataOut.writeVarInt(data.get(res).amount);
            if(data.get(res).chance != 1){
                dataOut.writeBoolean(true);
                dataOut.writeFloat(data.get(res).chance);
            }else
                dataOut.writeBoolean(false);
        }
        return dataOut;
    };

    public Function<DataIn, Map<Resource,Amount>> readFromNet = dataIn ->{
        HashMap<Resource,Amount> data = new HashMap<>();
        int size = dataIn.readVarInt();
        for (int i = 0; i < size; i++) {
            int id = dataIn.readVarInt();
            PlanObjectBase base = getPlanFile().get(id);
            if(base instanceof Resource) {
                int amount = dataIn.readVarInt();
                Amount amount_;
                if (dataIn.readBoolean()) {
                    amount_ = new Amount(amount, dataIn.readFloat());
                } else
                    amount_ = new Amount(amount);
                data.put((Resource) base, amount_);
                ((Resource)base).providers.add(this);
            }else{
                JustEnoughData.logger.error("Resource Not Found/Incorrect Type:" + id);
            }
        }
        return data;
    };

    public Supplier<Set<Resource>> clearOldData = () ->{
        Set<Resource> old = new HashSet<>();
        old.addAll(inputs.keySet());
        old.addAll(outputs.keySet());
        for (Resource res :
                old) {
            res.providers.remove(this);
        }
        return old;
    };
    //endregion
    //region constructors
    public Provider(Plan plan){
        super(plan);
        shape = PlanObjectShape.HORNSQUARE;
    }
    //endregion
    //region load
    //region SaveToFile
    @Override
    public void writeData(NBTTagCompound nbt) {
        super.writeData(nbt);
        if(provider != ItemStack.EMPTY)
            nbt.setString("provider", MARKER_STACK + provider.writeToNBT(new NBTTagCompound()).toString());
        if(processTime != Ticks.NO_TICKS)
            nbt.setString("processTime", processTime.toString());
        if(!inputs.isEmpty()) {
            nbt.setTag("inputs", writeToNBT.apply(inputs));
        }
        if(!outputs.isEmpty()) {
            nbt.setTag("outputs", writeToNBT.apply(outputs));
        }
    }

    @Override
    public void readData(NBTTagCompound nbt) {
        super.readData(nbt);
        if(nbt.hasKey("provider"))
            loadItemByJsonStr(nbt.getString("provider"));
        else
            provider = ItemStack.EMPTY;
        if(nbt.hasKey("processTime"))
            processTime = Ticks.get(nbt.getString("processTime"));
        //this is initial load, no old data
        if(nbt.hasKey("inputs"))
            inputs = readFromNBT.apply(nbt.getTagList("inputs", 10));
        if(nbt.hasKey("outputs"))
            outputs = readFromNBT.apply(nbt.getTagList("outputs", 10));
    }
    //endregion
    //region ServerFile
    @Override
    public void writeNetData(DataOut data) {
        super.writeNetData(data);
        data.writeBoolean(provider != ItemStack.EMPTY);
        if(provider != ItemStack.EMPTY)
            data.writeString(MARKER_STACK + provider.writeToNBT(new NBTTagCompound()).toString());
        data.writeString(processTime.toString());
        writeToNet.apply(data, inputs);
        writeToNet.apply(data, outputs);
    }

    @Override
    public void readNetData(DataIn data) {
        super.readNetData(data);
        if(data.readBoolean())
            loadItemByJsonStr(data.readString());
        else
            provider = ItemStack.EMPTY;
        processTime = Ticks.get(data.readString());
        inputs = readFromNet.apply(data);
        outputs = readFromNet.apply(data);
    }
    //endregion
    private void loadItemByJsonStr(String ingredientJsonString){
        if (ingredientJsonString.startsWith(MARKER_STACK)) {
            String itemStackAsJson = ingredientJsonString.substring(MARKER_STACK.length());
            try {
                NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(itemStackAsJson);
                ItemStack itemStack = new ItemStack(itemStackAsNbt);
                if (!itemStack.isEmpty()) {
                    provider = itemStack;
                    helper = Internal.getIngredientRegistry().getIngredientHelper(itemStack);
                    renderer = Internal.getIngredientRegistry().getIngredientRenderer(itemStack);
                } else {
                    JustEnoughData.logger.warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                }
            } catch (NBTException e) {
                JustEnoughData.logger.error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, e);
            }
        } else{
            JustEnoughData.logger.error("Failed to load unknown bookmarked ingredient:\n{}", ingredientJsonString);
        }
    }
    //endregion
    //region BaseMethod
    @Override
    public void deleteSelf() {
        clearOldData.get();
        super.deleteSelf();
    }

    @Override
    public PlanObjectType getObjectType() {
        return PlanObjectType.PROVIDER;
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
        icon.draw(xOffset, yOffset, width, height);
    }
    //endregion
}
