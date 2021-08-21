package client.gui.impl.background;

import client.modules.Module;
import client.gui.impl.setting.Setting;

public class MenuToggler extends Module {
    private static MenuToggler INSTANCE = new MenuToggler();
    public Setting<Boolean> particles = this.register(new Setting("Particles", true));
    public MenuToggler(){
        super("MainMenuScreen", "Toggles MainMenuScreen", Category.CORE);
        this.setInstance();
    }

    public static MenuToggler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MenuToggler();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

}
