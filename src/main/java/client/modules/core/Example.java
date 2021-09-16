package client.modules.core;

import client.gui.impl.setting.Setting;
import client.modules.Module;

import java.awt.*;

public class Example extends Module {

    public Setting friendMessages = register(new Setting("FriendMessages",255,255,255,255 ));

    public Example(){
        super("Example", "Example.", Category.CORE);
    }
}

