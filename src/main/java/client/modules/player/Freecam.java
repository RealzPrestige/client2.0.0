package client.modules.player;

import client.events.PacketEvent;
import client.events.PushEvent;
import client.gui.impl.setting.Setting;
import client.modules.Feature;
import client.modules.Module;
import client.util.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam extends Module {
    public Setting<Boolean> packet;
    private AxisAlignedBB oldBoundingBox;
    private EntityOtherPlayerMP entity;
    private Vec3d position;
    private Entity riding;
    private float yaw;
    private float pitch;

    public Freecam() {
        super("Freecam", "Cancels packets to allow you to do stuff from other angles.", Category.PLAYER);
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", true));
    }


    @Override
    public void onEnable() {
        if (!Feature.fullNullCheck()) {
            this.oldBoundingBox = mc.player.getEntityBoundingBox();
            mc.player.setEntityBoundingBox(new AxisAlignedBB(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.posX, mc.player.posY, mc.player.posZ));
            if (mc.player.getRidingEntity() != null) {
                this.riding = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }
            (this.entity = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile())).copyLocationAndAnglesFrom(mc.player);
            this.entity.rotationYaw = mc.player.rotationYaw;
            this.entity.rotationYawHead = mc.player.rotationYawHead;
            this.entity.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(69420, this.entity);
            this.position = mc.player.getPositionVector();
            this.yaw = mc.player.rotationYaw;
            this.pitch = mc.player.rotationPitch;
            mc.player.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (!Feature.fullNullCheck()) {
            mc.player.setEntityBoundingBox(this.oldBoundingBox);
            if (this.riding != null) {
                mc.player.startRiding(this.riding, true);
            }
            if (this.entity != null) {
                mc.world.removeEntity(this.entity);
            }
            if (this.position != null) {
                mc.player.setPosition(this.position.x, this.position.y, this.position.z);
            }
            mc.player.rotationYaw = this.yaw;
            mc.player.rotationPitch = this.pitch;
            mc.player.noClip = false;
        }
    }

    @Override
    public void onUpdate() {
        mc.player.noClip = true;
        mc.player.setVelocity(0.0, 0.0, 0.0);
        mc.player.jumpMovementFactor = 1.0f;
        final double[] dir = MathUtil.directionSpeed(1.0f);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        }
        else {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }
        mc.player.setSprinting(false);
        mc.player.motionY = 1.0f * -MathUtil.degToRad(mc.player.rotationPitch) * mc.player.movementInput.moveForward;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            final EntityPlayerSP player = mc.player;
            player.motionY += 1.0f;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            final EntityPlayerSP player2 = mc.player;
            player2.motionY -= 1.0f;
        }
    }

    @Override
    public void onLogout() {
        disable();
    }
    public void onLogin(){
        disable();
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPush(final PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }
}
