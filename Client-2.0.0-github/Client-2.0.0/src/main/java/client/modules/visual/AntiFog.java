package client.modules.visual;

import client.modules.Module;
import client.gui.impl.setting.Setting;
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
        super("AntiFog", "Removes / colors fog.", Category.VISUAL);
        this.red = this.register( new Setting <> ( "Red" , 1.0f , 0.0f , 1.0f ));
        this.green = this.register( new Setting <> ( "Green" , 1.0f , 0.0f , 1.0f ));
        this.blue = this.register( new Setting <> ( "Blue" , 1.0f , 0.0f , 1.0f ));
        this.red1 = this.register( new Setting <> ( "Nether Red" , 1.0f , 0.0f , 1.0f ));
        this.green1 = this.register( new Setting <> ( "Nether Green" , 1.0f , 0.0f , 1.0f ));
        this.blue1 = this.register( new Setting <> ( "Nether Blue" , 1.0f , 0.0f , 1.0f ));
        this.red2 = this.register( new Setting <> ( "End Red" , 1.0f , 0.0f , 1.0f ));
        this.green2 = this.register( new Setting <> ( "End Green" , 1.0f , 0.0f , 1.0f ));
        this.blue2 = this.register( new Setting <> ( "End Blue" , 1.0f , 0.0f , 1.0f ));
        this.clear = this.register( new Setting <> ( "Remove fog" , true ));
        this.color = this.register( new Setting <> ( "Color fog" , true ));
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @SubscribeEvent
    public void onFogDensity(final EntityViewRenderEvent.FogDensity event) {
        if (this.clear.getCurrentState()) {
            event.setDensity(0.0f);
        event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        if (this.color.getCurrentState()) {
           if (AntiFog.mc.player.dimension == 0) { event.setRed(this.red.getCurrentState());event.setGreen(this.green.getCurrentState());event.setBlue(this.blue.getCurrentState());
            }
            else if (AntiFog.mc.player.dimension == -1) { event.setRed(this.red1.getCurrentState());event.setGreen(this.green1.getCurrentState());event.setBlue(this.blue1.getCurrentState());
            }
            else if (AntiFog.mc.player.dimension == 1) { event.setRed(this.red2.getCurrentState());event.setGreen(this.green2.getCurrentState());event.setBlue(this.blue2.getCurrentState());
            }
        }
    }
}
