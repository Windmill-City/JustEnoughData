package city.windmill.Gui;

import net.minecraft.client.Minecraft;

public interface IPanelItem {
    void setX(int x);
    void setY(int y);
    int getX();
    int getY();
    void setWidth(int width);
    int getWidth();
    void setHeight(int height);
    int getHeight();
    void draw(Minecraft minecraft, int xOffset, int yOffset);
}
