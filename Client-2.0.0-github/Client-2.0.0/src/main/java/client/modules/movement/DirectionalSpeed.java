package client.modules.movement;

import client.events.ClientEvent;
import client.events.MoveEvent;
import client.modules.Module;
import client.util.EntityUtil;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DirectionalSpeed extends Module {
    private static DirectionalSpeed INSTANCE = new DirectionalSpeed();
    public boolean antiShake = false;
    public double minY = 0.0;
    public boolean changeY = false;

    public DirectionalSpeed() {
        super("DirectionalSpeed", "Makes you faster", Category.MOVEMENT);
        this.setInstance();
    }

    public static DirectionalSpeed getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DirectionalSpeed();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (DirectionalSpeed.mc.player.isSneaking() || DirectionalSpeed.mc.player.isInWater() || DirectionalSpeed.mc.player.isInLava() || !mc.player.onGround) {
            return;
        }
    }

    @Override
    public void onDisable() {
        this.changeY = false;
        this.antiShake = false;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2) {
            DirectionalSpeed.mc.player.motionY = -0.1;
        }
    }

    public void onLogin(){
        if(this.isEnabled()){
            this.disable();
            this.enable();
        }
    }

    @SubscribeEvent
    public void onMode(MoveEvent event) {
        if (!(event.getStage() != 0 || DirectionalSpeed.nullCheck() || DirectionalSpeed.mc.player.isSneaking() || DirectionalSpeed.mc.player.isInWater() || DirectionalSpeed.mc.player.isInLava() || DirectionalSpeed.mc.player.movementInput.moveForward == 0.0f && DirectionalSpeed.mc.player.movementInput.moveStrafe == 0.0f) || !mc.player.onGround) {
            MovementInput movementInput = DirectionalSpeed.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = DirectionalSpeed.mc.player.rotationYaw;
            if ((double) moveForward == 0.0 && (double) moveStrafe == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else {
                if ((double) moveForward != 0.0) {
                    if ((double) moveStrafe > 0.0) {
                        rotationYaw += (float) ((double) moveForward > 0.0 ? -45 : 45);
                    } else if ((double) moveStrafe < 0.0) {
                        rotationYaw += (float) ((double) moveForward > 0.0 ? 45 : -45);
                    }
                    moveStrafe = 0.0f;
                    float f = moveForward == 0.0f ? moveForward : (moveForward = (double) moveForward > 0.0 ? 1.0f : -1.0f);
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double) moveStrafe > 0.0 ? 1.0f : -1.0f);
                event.setX((double) moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double) moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.setZ((double) moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double) moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }
}

