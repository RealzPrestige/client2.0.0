package client.mixin;

import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ SPacketExplosion.class })
public interface ISPacketExplosion {
    @Accessor("motionX")
    float getMotionX();

    @Accessor("motionY")
    float getMotionY();

    @Accessor("motionZ")
    float getMotionZ();

    @Accessor("motionX")
    void setMotionX(final float p0);

    @Accessor("motionY")
    void setMotionY(final float p0);

    @Accessor("motionZ")
    void setMotionZ(final float p0);
}
