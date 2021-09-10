package client.mixin.mixins;

import client.modules.visual.NoRender;
import client.modules.visual.ViewTweaks;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class}, priority=0x7FFFFFFE)
public abstract class MixinEntityRenderer {

    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        return ViewTweaks.getInstance().isEnabled() && ViewTweaks.getInstance().cameraClip.getCurrentState() && ViewTweaks.getInstance().extend.getCurrentState() ? ViewTweaks.getInstance().distance.getCurrentState() : range;
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        return ViewTweaks.getInstance().isEnabled() && ViewTweaks.getInstance().cameraClip.getCurrentState() && ViewTweaks.getInstance().extend.getCurrentState() ? ViewTweaks.getInstance().distance.getCurrentState() : (ViewTweaks.getInstance().isEnabled() && ViewTweaks.getInstance().cameraClip.getCurrentState() && !ViewTweaks.getInstance().extend.getCurrentState() ? 4.0 : range);
    }
    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().hurtcam.getCurrentState()) {
            info.cancel();
        }
    }

}