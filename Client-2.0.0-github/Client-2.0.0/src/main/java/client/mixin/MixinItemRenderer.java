package client.mixin;

import client.modules.visual.Chams;
import client.modules.visual.Viewmodel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderItem.class })
public abstract class MixinItemRenderer {
    @ModifyArg(method={"renderEffect"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"))
    private int renderEffect(int glintVal) {
        return Chams.getInstance().isEnabled() ? Chams.getInstance().enchantColor() : glintVal;
    }
    @Inject(method = { "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V" }, at = { @At("INVOKE") })
    public void renderItem(final ItemStack stack, final EntityLivingBase entitylivingbaseIn, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (Viewmodel.getINSTANCE().isEnabled() && (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)) {
            final Viewmodel viewmodel = Viewmodel.getINSTANCE();
            GlStateManager.scale( Viewmodel.getINSTANCE().sizeX.getCurrentState() , Viewmodel.getINSTANCE().sizeY.getCurrentState() , Viewmodel.getINSTANCE().sizeZ.getCurrentState() );
            if (transform.equals( ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND )) {
                GL11.glTranslated( viewmodel.offhandX.getCurrentState() / 4.0f , viewmodel.offhandY.getCurrentState() / 4.0f , viewmodel.offhandZ.getCurrentState() / 4.0f );
            }
            else {
                GL11.glTranslated( viewmodel.offsetX.getCurrentState() / 4.0f , viewmodel.offsetY.getCurrentState() / 4.0f , viewmodel.offsetZ.getCurrentState() / 4.0f );
            }
        }
    }
}


