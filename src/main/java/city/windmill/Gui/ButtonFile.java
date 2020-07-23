package city.windmill.Gui;

import city.windmill.ClientProxy;
import city.windmill.CommonProxy;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ButtonFile extends SimpleTextButton {
    private PanelPlan panelPlan;
    public ButtonFile(PanelPlan panel) {
        super(panel, "", Icon.EMPTY);
        panelPlan = panel;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean renderTitleInCenter() {
        return true;
    }

    @Override
    public void onClicked(MouseButton button) {
        boolean remote = panelPlan.guiPlan.getPlanFile().target.isRemote();
        if(panelPlan.guiPlan.getPlanFile().target.isRemote() || ClientProxy.remoteFile.invalid || FMLClientHandler.instance().getClient().isIntegratedServerRunning()){
            panelPlan.guiPlan.setPlanFile(CommonProxy.localFile);
        }else{
            panelPlan.guiPlan.setPlanFile(ClientProxy.remoteFile);
        }
    }

    @SubscribeEvent
    public void onFileChange(GuiPlan.ContentChangeEvent.CurFile event){
        setTitle(event.to.target.isRemote() ? "Server" : "Client");
    }
}
