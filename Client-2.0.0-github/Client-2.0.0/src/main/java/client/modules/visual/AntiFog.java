package client.modules.visual;

import client.modules.Module;
import client.setting.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiFog extends Module {
    private final Setting<Float> red;
    private final Setting<Float> green;
    private final Setting<Float> blue;
    private final Setting<Float> red1;
    private final Setting<Float> green1;
    private final Setting<Float> blue1;
    private final Setting<Float> red2;
    private final Setting<Float> green2;
    private final Setting<Float> blue2;
    private final Setting<Boolean> clear;
    private final Setting<Boolean> color;

    public AntiFog() {
        super("AntiFog", "Removes fog and makes it colored", Category.VISUAL);
        this.red = this.register(new Setting<Float>("Red", 1.0f, 0.0f, 1.0f));
        this.green = this.register(new Setting<Float>("Green", 1.0f, 0.0f, 1.0f));
        this.blue = this.register(new Setting<Float>("Blue", 1.0f, 0.0f, 1.0f));
        this.red1 = this.register(new Setting<Float>("Nether Red", 1.0f, 0.0f, 1.0f));
        this.green1 = this.register(new Setting<Float>("Nether Green", 1.0f, 0.0f, 1.0f));
        this.blue1 = this.register(new Setting<Float>("Nether Blue", 1.0f, 0.0f, 1.0f));
        this.red2 = this.register(new Setting<Float>("End Red", 1.0f, 0.0f, 1.0f));
        this.green2 = this.register(new Setting<Float>("End Green", 1.0f, 0.0f, 1.0f));
        this.blue2 = this.register(new Setting<Float>("End Blue", 1.0f, 0.0f, 1.0f));
        this.clear = this.register(new Setting<Boolean>("Remove fog", true));
        this.color = this.register(new Setting<Boolean>("Color fog", true));
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @SubscribeEvent
    public void onFogDensity(final EntityViewRenderEvent.FogDensity event) {
        if (this.clear.getValue()) {
            event.setDensity(0.0f);
        event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        if (this.color.getValue()) {
           if (AntiFog.mc.player.dimension == 0) { event.setRed(this.red.getValue());event.setGreen(this.green.getValue());event.setBlue(this.blue.getValue());
            }
            else if (AntiFog.mc.player.dimension == -1) { event.setRed(this.red1.getValue());event.setGreen(this.green1.getValue());event.setBlue(this.blue1.getValue());
            }
            else if (AntiFog.mc.player.dimension == 1) { event.setRed(this.red2.getValue());event.setGreen(this.green2.getValue());event.setBlue(this.blue2.getValue());
            }
        }
    }
}
