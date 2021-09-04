package client.mixin.mixins;

import client.events.CloseInventoryEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerPlayer.class)
public class MixinContainerPlayer {
    @Inject(method = "onContainerClosed", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(EntityPlayer playerIn, CallbackInfo ci) {
        CloseInventoryEvent event = new CloseInventoryEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
