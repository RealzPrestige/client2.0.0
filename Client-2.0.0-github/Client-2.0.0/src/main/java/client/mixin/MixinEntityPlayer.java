package client.mixin;

import client.events.EntityCollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ EntityPlayer.class })
public abstract class MixinEntityPlayer extends EntityLivingBase
{
    public MixinEntityPlayer(final World worldIn) {
        super(worldIn);
    }

    @Inject(method = { "applyEntityCollision" }, at = { @At("HEAD") }, cancellable = true)
    public void applyEntityCollision(final Entity entity, final CallbackInfo info) {
        final EntityCollisionEvent entityCollisionEvent = new EntityCollisionEvent();
        MinecraftForge.EVENT_BUS.post((Event)entityCollisionEvent);
        if (entityCollisionEvent.isCanceled()) {
            info.cancel();
        }
    }
}
