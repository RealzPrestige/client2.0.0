package client.manager;

import client.modules.Feature;
import client.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class RotationManager
        extends Feature {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private float yaw;
    private float pitch;

    public void updateRotations() {
        this.yaw = RotationManager.mc.player.rotationYaw;
        this.pitch = RotationManager.mc.player.rotationPitch;
    }

    public void restoreRotations() {
        RotationManager.mc.player.rotationYaw = this.yaw;
        RotationManager.mc.player.rotationYawHead = this.yaw;
        RotationManager.mc.player.rotationPitch = this.pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
        RotationManager.mc.player.rotationPitch = pitch;
    }


    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setPlayerRotations(angle[0], angle[1]);
    }


    public void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        this.setPlayerRotations(angle[0], angle[1]);
    }


    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

}

