package client.modules.movement;

import client.events.WalkEvent;
import client.modules.Module;
import client.util.EntityUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Module {
    private boolean preMotion;
    private double motion;

    public Strafe(){
        super("Strafe", "", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        motion = 0.0;
    }

    @SubscribeEvent
    public void onMove(WalkEvent event) {
        if (!EntityUtil.onMovementInput()) {
            motion = EntityUtil.getMovementSpeed();
            event.setMotionX(0.0);
            event.setMotionZ(0.0);
            return;
        }
        if (mc.player.onGround) {
            motion *= 1.57;
            mc.player.motionY = 0.412;
            event.setMotionY(0.412);
        } else {
            motion = preMotion ? (motion -= 0.66 * (motion - EntityUtil.getMovementSpeed())) : (motion -= motion / 159.0);
        }
        preMotion = mc.player.onGround;
        motion = Math.max(motion, EntityUtil.getMovementSpeed());
        motion = Math.min(motion, 0.547);
        mc.player.motionX = -(Math.sin(EntityUtil.getDirection()) * motion);
        event.setMotionX(mc.player.motionX);
        mc.player.motionZ = Math.cos(EntityUtil.getDirection()) * motion;
        event.setMotionZ(mc.player.motionZ);
    }
}