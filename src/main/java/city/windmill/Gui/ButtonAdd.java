package city.windmill.Gui;

import city.windmill.JustEnoughData;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.icon.ImageIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.util.ResourceLocation;

public abstract class ButtonAdd extends Button {
    public ButtonAdd(Panel panel) {
        super(panel);
        setTitle("Add");
        setIcon(new ImageIcon(new ResourceLocation(JustEnoughData.MODID, "textures/addbtn.png")));
    }

    @Override
    public abstract void onClicked(MouseButton mouseButton);
}
