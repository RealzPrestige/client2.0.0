package client.modules.visual;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
public class ViewTweaks extends Module {
    private static ViewTweaks INSTANCE = new ViewTweaks();

    public Setting<Boolean> fullBright = register(new Setting("FullBright", false));
    public Setting<Boolean> fovSetting = register(new Setting("Fov", false));
    public Setting<Float> fov = register(new Setting("FovValue", 140.0f, 0.0f, 180.0f, v-> fovSetting.getCurrentState()));
    public Setting<Boolean> antiFog = register(new Setting("AntiFog", false));
    public Setting<Boolean> noWeather = register(new Setting("NoWeather", false));
    public Setting<Boolean> timeChanger  = register(new Setting("TimeChanger", false));
    public Setting<Integer> time = register(new Setting<>("Time", 0, 0, 23000, v-> timeChanger.getCurrentState()));
    public Setting<Boolean> skyColor = register(new Setting("SkyColor", false));
    public Setting<Float> red = register(new Setting<>("SkyRed", 255.0f, 0.0f, 255.0f, v-> skyColor.getCurrentState()));
    public Setting<Float> green = register(new Setting<>("SkyGreen", 255.0f, 0.0f, 255.0f, v-> skyColor.getCurrentState()));
    public Setting<Float> blue = register(new Setting<>("SkyBlue", 255.0f, 0.0f, 255.0f, v-> skyColor.getCurrentState()));
    public Setting<Boolean> cameraClip = register(new Setting("CameraClip", false));
    public Setting<Boolean> extend = register(new Setting<>("Extend",false, v-> cameraClip.getCurrentState()));
    public Setting<Double> distance = register(new Setting<>("Distance",10.0, 0.0, 50.0, v-> cameraClip.getCurrentState() && extend.getCurrentState()) );

    public ViewTweaks(){
        super("ViewTweaks", "Tweaks the way stuff looks.", Category.VISUAL);
        this.setInstance();
    }

    public static ViewTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewTweaks();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
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
        if(noWeather.getCurrentState()){
            mc.world.setRainStrength(0);
        }
        if(timeChanger.getCurrentState()){
            mc.world.setWorldTime((long) time.getCurrentState());
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

}
