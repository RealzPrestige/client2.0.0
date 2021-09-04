package client.mixin.mixins;

import client.modules.visual.Chams;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {LayerBipedArmor.class})
public abstract class MixinRenderArmor {
    @Shadow
    protected abstract void setModelVisible(ModelBiped var1);

    @Overwrite
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD: {
                p_188359_1_.bipedHead.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                p_188359_1_.bipedHeadwear.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                break;
            }
            case CHEST: {
                p_188359_1_.bipedBody.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                p_188359_1_.bipedRightArm.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                p_188359_1_.bipedLeftArm.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                break;
            }
            case LEGS: {
                p_188359_1_.bipedBody.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                p_188359_1_.bipedRightLeg.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                p_188359_1_.bipedLeftLeg.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                break;
            }
            case FEET: {
                p_188359_1_.bipedRightLeg.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
                p_188359_1_.bipedLeftLeg.showModel = Chams.getInstance().isEnabled() && !Chams.getInstance().removeArmor.getCurrentState();
            }
        }
    }
}

