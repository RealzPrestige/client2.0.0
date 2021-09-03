package client.modules.movement;

import client.Client;
import client.events.ClientEvent;
import client.events.MoveEvent;
import client.events.WalkEvent;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.EntityUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Strafe extends Module {
    private static Strafe INSTANCE = new Strafe();
    private int delay;
    private boolean preMotion;
    private double motion;
    public boolean antiShake = false;
    public boolean changeY = false;
    public double minY = 0.0;
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.STRAFE));
    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1)));
    public enum Mode{STRAFE, INSTANT}
    public Strafe(){
        super("Strafe", "Increases and tweaks movement.", Category.MOVEMENT);
        this.setInstance();
    }

    public static Strafe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Strafe();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onLogin(){
        if(this.isEnabled()){
            this.disable();
            this.enable();
        }
    }

    @Override
    public void onEnable() {
        motion = 0.0;
    }
    public void onTick(){
        if(delay < 12) {
            ++delay;
        }
        if(delay > 10) {
            if (switchBind.getCurrentState().getKey() > -1) {
                if (Keyboard.isKeyDown(switchBind.getCurrentState().getKey())) {
                    if (mode.getCurrentState() == Mode.INSTANT) {
                        mode.setValue(Mode.STRAFE);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Client.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.GRAY + "Mode set to: " + ChatFormatting.RED + ChatFormatting.BOLD + "Strafe"), 1);
                        delay = 0;
                    } else if (mode.getCurrentState() == Mode.STRAFE) {
                        mode.setValue(Mode.INSTANT);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Client.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.GRAY + "Mode set to: " + ChatFormatting.RED + ChatFormatting.BOLD + "Instant"), 1);
                        delay = 0;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMove(WalkEvent event) {
        if (mode.getCurrentState() == Mode.STRAFE) {
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


    @Override
    public void onUpdate() {
        if (mc.player.isSneaking() || mc.player.isInWater() || mc.player.isInLava() || !mc.player.onGround && mode.getCurrentState() == Mode.INSTANT) {
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
            mc.player.motionY = -0.1;
        }
    }


    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if(mode.getCurrentState() == Mode.INSTANT){
        if (!(event.getStage() != 0 || nullCheck() || mc.player.isSneaking() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) || !mc.player.onGround) {
            MovementInput movementInput = mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;
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
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double) moveStrafe > 0.0 ? 1.0f : -1.0f);
                event.setX((double) moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double) moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.setZ((double) moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double) moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
        }
    }
}