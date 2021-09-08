package client.mixin.mixins;

import client.modules.visual.PrestigeChams;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value={AbstractClientPlayer.class}, priority=0x7FFFFFFE)
public abstract class MixinClientPlayer {
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method={"getLocationSkin()Lnet/minecraft/util/ResourceLocation;"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (PrestigeChams.getInstance().isEnabled() && PrestigeChams.getInstance().mode.getCurrentState() == PrestigeChams.Mode.ZPRESTIGE) {
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/prestigeskin.png"));
        } else if(PrestigeChams.getInstance().isEnabled() && PrestigeChams.getInstance().mode.getCurrentState() == PrestigeChams.Mode.IIV){
            callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/iiv.png"));
        }
    }

}

