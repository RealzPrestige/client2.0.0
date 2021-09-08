package client.modules.movement;

import client.Client;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.EntityUtil;
import client.util.Timer;

public class YPort extends Module {
    private final Setting<Double> speed;
    private final Timer timer;

    public YPort() {
        super("YPort", "Removing soon.", Category.MOVEMENT);
        this.speed = register(new Setting("Speed", 0.1, 0.0, 1.0D));
        this.timer = new Timer();
    }
    public void onDisable() {
        this.timer.reset();
        EntityUtil.resetTimer();
    }

    public void onUpdate() {
        if (mc.player.isSneaking() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || Client.moduleManager.isModuleEnabled("Strafe"))
            return;
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }
        handleYPortSpeed();
    }

    public void onToggle() {
        mc.player.motionY = -3.0D;
        mc.player.stepHeight = 0.6f;
    }

    private void handleYPortSpeed() {
        if (!EntityUtil.isMoving(mc.player) || (mc.player.isInWater() && mc.player.isInLava()) || mc.player.collidedHorizontally)
            return;
        if (mc.player.onGround) {
            mc.player.jump();
            EntityUtil.setSpeed(mc.player, EntityUtil.getBaseMoveSpeed() + this.speed.getCurrentState());
        } else {
            mc.player.motionY = -1.0D;
        }
    }
}