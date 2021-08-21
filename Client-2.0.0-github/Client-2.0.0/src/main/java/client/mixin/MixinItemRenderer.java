package client.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderItem.class })
public abstract class MixinItemRenderer
{

    @Inject(method = { "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V" }, at = { @At("INVOKE") })
    public void renderItem(final ItemStack stack, final EntityLivingBase entitylivingbaseIn, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (Viewmodel.getINSTANCE().isEnabled() && (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)) {
            final Viewmodel changer = Viewmodel.getINSTANCE();
            GlStateManager.scale( Viewmodel.getINSTANCE().sizeX.getValue() , Viewmodel.getINSTANCE().sizeY.getValue() , Viewmodel.getINSTANCE().sizeZ.getValue() );
            if (transform.equals( ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND )) {
                GL11.glTranslated( changer.offhandX.getValue() / 4.0f , changer.offhandY.getValue() / 4.0f , changer.offhandZ.getValue() / 4.0f );
            }
            else {
                GL11.glTranslated( changer.offsetX.getValue() / 4.0f , changer.offsetY.getValue() / 4.0f , changer.offsetZ.getValue() / 4.0f );
            }
        }
    }
}


