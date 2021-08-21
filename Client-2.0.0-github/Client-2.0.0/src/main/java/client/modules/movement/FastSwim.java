package client.modules.movement;

import client.modules.Module;
import client.util.Timer;

public class FastSwim extends Module {
    Timer timer = new Timer();
    public FastSwim() {
        super("FastSwim", "", Category.MOVEMENT);
    }
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        timer.setCurrentMS();
        if (mc.player.isInWater()) {
            if(!mc.player.isSneaking()){
                mc.player.motionY *= 0.01;
            }
            if(mc.gameSettings.keyBindJump.isKeyDown()){
                mc.player.motionY *= 1.8D;
            }
            mc.player.motionX *= 1.18D;
            mc.player.motionZ *= 1.18D;
        } else {
            timer.setLastMS();
        }

    }
}
