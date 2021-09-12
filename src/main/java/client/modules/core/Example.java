package client.modules.core;

import client.gui.impl.setting.Setting;
import client.modules.Module;

public class Example extends Module {

    public Setting<Boolean> friendMessages = register(new Setting("FriendMessages", false));

    public Example(){
        super("Example", "Example.", Category.CORE);
    }
}

