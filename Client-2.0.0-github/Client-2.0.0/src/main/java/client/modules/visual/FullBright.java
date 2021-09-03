package client.modules.visual;

import client.modules.Module;

public class FullBright extends Module {

    public FullBright() {
        super("Fullbright", "Makes your brightness higher than possible", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        FullBright.mc.gameSettings.gammaSetting = 6969.0f;
    }

    @Override
    public void onUpdate() {
        if (FullBright.mc.gameSettings.gammaSetting != 6969.0f) {
            FullBright.mc.gameSettings.gammaSetting = 6969.0f;
        }
    }
}
