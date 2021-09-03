package client.modules.visual;

import client.gui.impl.setting.Setting;
import client.modules.Module;

public class PrestigeChams extends Module {

    private static PrestigeChams INSTANCE = new PrestigeChams();
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.ZPRESTIGE));
    public enum Mode{ZPRESTIGE, IIV}

    public PrestigeChams(){
        super("PrestigeChams", "Makes everyone look like the sexy zPrestige.", Category.VISUAL);
        this.setInstance();
    }

    public static PrestigeChams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestigeChams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

}
