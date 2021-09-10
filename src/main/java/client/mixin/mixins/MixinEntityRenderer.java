package client.mixin.mixins;

import client.modules.miscellaneous.NoEntityTrace;
import client.modules.visual.NoRender;
import client.modules.visual.ViewTweaks;
import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

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

    @Redirect(method = {"getMouseOver"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        if (NoEntityTrace.getINSTANCE().isEnabled() && (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && NoEntityTrace.getINSTANCE().pickaxe.getCurrentState()  || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && NoEntityTrace.getINSTANCE().gapple.getCurrentState() || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.TNT_MINECART)) {
            return new ArrayList<Entity>();
        }
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

}