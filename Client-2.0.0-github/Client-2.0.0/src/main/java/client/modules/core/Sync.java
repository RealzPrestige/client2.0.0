package client.modules.core;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;

public class Sync extends Module {
    private static Sync INSTANCE = new Sync();

    public int color;

    public Setting<Boolean> rainbow = register(new Setting<>("Rainbow", false));
    public Setting<Integer> rainbowHue = register(new Setting<>("RainbowHue", 100, 0, 600, v-> rainbow.getCurrentState()));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", 150.0f, 1.0f, 255.0f, v-> rainbow.getCurrentState()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", 150.0f, 1.0f, 255.0f, v-> rainbow.getCurrentState()));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v-> !rainbow.getCurrentState()));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v-> !rainbow.getCurrentState()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v-> !rainbow.getCurrentState()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 50, 0, 255, v-> !rainbow.getCurrentState()));

    public Sync(){
        super("Sync", "Syncs your colors everywhere.", Category.CORE);
        color = rainbow.getCurrentState() ? ColorUtil.rainbowSync(rainbowHue.getCurrentState()).getRGB() : ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState());
        this.setInstance();
    }

    public static Sync getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sync();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }



}
