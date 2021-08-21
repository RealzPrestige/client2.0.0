package client.mixin;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ SPacketEntityVelocity.class })
public interface ISPacketEntityVelocity
{
    @Accessor("motionX")
    int getMotionX();

    @Accessor("motionY")
    int getMotionY();

    @Accessor("motionZ")
    int getMotionZ();

    @Accessor("motionX")
    void setMotionX(final int p0);

    @Accessor("motionY")
    void setMotionY(final int p0);

    @Accessor("motionZ")
    void setMotionZ(final int p0);
}
