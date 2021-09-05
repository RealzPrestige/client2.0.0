package client.modules.player;

import client.gui.impl.setting.Setting;
import client.modules.Module;

public class YawLock extends Module {

    public Setting<Integer> yaw = register(new Setting("Yaw", 0, -180, 180));

    public YawLock(){
        super("YawLock", "Locks your player yaw.", Category.PLAYER);
    }

    public void onUpdate(){
        mc.player.rotationYaw = yaw.getCurrentState();
    }

}
