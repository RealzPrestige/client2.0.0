package client.modules.movement;

import client.modules.Module;

public class AutoWalk extends Module {

    public AutoWalk(){
        super("AutoWalk", "OP MODULE!!!", Category.MOVEMENT);
    }
    public void onUpdate(){
        mc.gameSettings.keyBindForward.pressed = true;
    }
}
