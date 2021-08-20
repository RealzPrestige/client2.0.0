package client.modules.render;

import client.modules.Module;

public class FullBright extends Module {

    public FullBright() {
        super("Fullbright", "Permanent brightness", Category.RENDER);
    }

    @Override
    public void onEnable() {
        FullBright.mc.gameSettings.gammaSetting = 1000.0f;
    }

    @Override
    public void onUpdate() {
        if (FullBright.mc.gameSettings.gammaSetting != 1000.0f) {
            FullBright.mc.gameSettings.gammaSetting = 1000.0f;
        }
    }
}
