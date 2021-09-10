package client.modules.miscellaneous;

import client.gui.impl.setting.Setting;
import client.modules.Module;

public class NoEntityTrace extends Module {
    private static NoEntityTrace INSTANCE = new NoEntityTrace();
    public Setting<Boolean> pickaxe = register(new Setting<>("Pickaxe", true));
    public Setting<Boolean> gapple = register(new Setting<>("Gapple", true));

    public NoEntityTrace() {
        super("NoEntityTrace", "NoHitBox.", Module.Category.MISC);
        this.setInstance();
    }

    public static NoEntityTrace getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new NoEntityTrace();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}