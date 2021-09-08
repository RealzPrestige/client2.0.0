package client.mixin.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ PlayerControllerMP.class })
public interface IPlayerControllerMP
{
    @Accessor("curBlockDamageMP")
    void setCurrentBlockDamage(final float p0);

    @Accessor("blockHitDelay")
    void setBlockHitDelay(final int p0);

    @Invoker("syncCurrentPlayItem")
    void syncCurrentPlayItem();
}
