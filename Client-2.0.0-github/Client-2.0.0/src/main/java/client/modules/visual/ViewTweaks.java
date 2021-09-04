package client.modules.visual;

import client.events.PerspectiveEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ViewTweaks extends Module {

    public Setting<Boolean> fullBright = register(new Setting("FullBright", false));
    public Setting<Boolean> fovSetting = register(new Setting("Fov", false));
    public Setting<Float> fov = register(new Setting("FovValue", 140.0f, 0.0f, 180.0f, v-> fovSetting.getCurrentState()));
    public Setting<Boolean> aspectSetting = register(new Setting("Aspect", false));
    public final Setting<Double> aspect = this.register(new Setting<>("Aspect", (double)mc.displayWidth / (double)mc.displayHeight, 0.0, 3.0, v-> aspectSetting.getCurrentState()));
    public Setting<Boolean> antiFog = register(new Setting("AntiFog", false));
    public Setting<Boolean> skyColor = register(new Setting("SkyColor", false));
    private final Setting<Float> red = this.register(new Setting<>("SkyRed", 255.0f, 0.0f, 255.0f, v-> skyColor.getCurrentState()));
    private final Setting<Float> green = this.register(new Setting<>("SkyGreen", 255.0f, 0.0f, 255.0f, v-> skyColor.getCurrentState()));
    private final Setting<Float> blue = this.register(new Setting<>("SkyBlue", 255.0f, 0.0f, 255.0f, v-> skyColor.getCurrentState()));


    public ViewTweaks(){
        super("ViewTweaks", "Tweaks the way stuff looks.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        if(fullBright.getCurrentState()) {
            mc.gameSettings.gammaSetting = 6969.0f;
        }
    }
    @Override
    public void onUpdate() {
        if (fovSetting.getCurrentState()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getCurrentState( ) );
        }
        if (mc.gameSettings.gammaSetting != 6969.0f && fullBright.getCurrentState()) {
            mc.gameSettings.gammaSetting = 6969.0f;
        }
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if(antiFog.getCurrentState()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        if(skyColor.getCurrentState()) {
            event.setRed(red.getCurrentState() / 255.0f);
            event.setGreen(green.getCurrentState() / 255.0f);
            event.setBlue(blue.getCurrentState() / 255.0f);
        }
    }
    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent event) {
        event.setAspect(aspect.getCurrentState().floatValue());
    }
}
