package client.modules.visual;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
public class CrossHairModifier extends Module {

    public Setting<Boolean> dynamic = register(new Setting<>("Dynamic", false));
    public Setting<Integer> thickness = register(new Setting<>("Thickness", 250, 200, 500));
    public Setting<Integer> distance = register(new Setting<>("Distance", 300, 0, 1000));
    public Setting<Integer> width = register(new Setting<>("Width", 400, 100, 1000));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 150, 0, 255));

    public CrossHairModifier() {
        super("CrossHairModifier", "Changes ur crosshair.", Category.VISUAL);
    }

    public void onLogin(){
        if(isEnabled()){
            disable();
            enable();
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == ElementType.CROSSHAIRS) {
            event.setCanceled(true);
            RenderUtil.drawCrosshairs(distance.getCurrentState() / 100, width.getCurrentState() / 100, thickness.getCurrentState() / 100, dynamic.getCurrentState(), ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState()));
        }
    }
}