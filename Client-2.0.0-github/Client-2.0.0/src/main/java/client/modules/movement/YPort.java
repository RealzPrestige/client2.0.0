package client.modules.movement;

import client.Client;
import client.modules.Module;
import client.setting.Setting;
import client.util.EntityUtil;
import client.util.Timer;

public class YPort extends Module {
    private final Setting<Double> speed;
    public Setting<String> futurePrefix;
    private Timer timer;

    public YPort() {
        super("Longjump", "Terrible rip off longjump", Module.Category.MOVEMENT);
        this.speed = register(new Setting("Speed", 0.1, 0.0, 1.0D));
        this.futurePrefix = register(new Setting("FuturePrefix", "."));
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

        if(Step.mc.player.collidedHorizontally && Step.mc.player.onGround){
            disable();
            mc.player.sendChatMessage(futurePrefix.getValue() + "toggle Speed");
            Step.getInstance().enable();
        }
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
            EntityUtil.setSpeed(mc.player, EntityUtil.getBaseMoveSpeed() + this.speed.getValue());
        } else {
            mc.player.motionY = -1.0D;
        }
    }
}