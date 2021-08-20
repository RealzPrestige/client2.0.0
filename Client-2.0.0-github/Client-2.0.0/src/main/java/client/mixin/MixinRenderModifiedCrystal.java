package client.mixin;

import client.events.RenderEntityModelEvent;
import client.modules.visual.CrystalChanger;
import client.util.ColorUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
@Mixin(value={RenderEnderCrystal.class}, priority = 9999999)
public class MixinRenderModifiedCrystal {
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    @SubscribeEvent
    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBasezPrestigesSexyBelly(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CrystalChanger.getInstance().isEnabled()) {
            GlStateManager.scale(CrystalChanger.getInstance().scale.getValue().floatValue(), CrystalChanger.getInstance().scale.getValue().floatValue(), CrystalChanger.getInstance().scale.getValue().floatValue());
        }
        if (CrystalChanger.getInstance().isEnabled() && CrystalChanger.getInstance().wireframe.getValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            CrystalChanger.getInstance().onRenderModel(event);
        }
        if (CrystalChanger.getInstance().isEnabled() && CrystalChanger.getInstance().chams.getValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            if (CrystalChanger.getInstance().XQZ.getValue() && CrystalChanger.getInstance().throughwalls.getValue()) {
                int hiddencolor = ColorUtil.toRGBA(CrystalChanger.getInstance().h_red.getValue(), CrystalChanger.getInstance().h_green.getValue(), CrystalChanger.getInstance().h_blue.getValue(), CrystalChanger.getInstance().h_alpha.getValue());
                int visibleColor = ColorUtil.toRGBA(CrystalChanger.getInstance().red.getValue(), CrystalChanger.getInstance().green.getValue(), CrystalChanger.getInstance().blue.getValue(), CrystalChanger.getInstance().alpha.getValue());
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(((float)CrystalChanger.getInstance().h_red.getValue() / 255.0f), ((float)CrystalChanger.getInstance().h_green.getValue() / 255.0f), (float)CrystalChanger.getInstance().h_blue.getValue() / 255.0f, (float)CrystalChanger.getInstance().alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glColor4f((float)CrystalChanger.getInstance().red.getValue() / 255.0f, (float)CrystalChanger.getInstance().green.getValue() / 255.0f, (float)CrystalChanger.getInstance().blue.getValue() / 255.0f, (float)CrystalChanger.getInstance().alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                 int visibleColor = ColorUtil.toRGBA(CrystalChanger.getInstance().red.getValue(), CrystalChanger.getInstance().green.getValue(), CrystalChanger.getInstance().blue.getValue(), CrystalChanger.getInstance().alpha.getValue());
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(((float)CrystalChanger.getInstance().red.getValue() / 255.0f), ((float)CrystalChanger.getInstance().green.getValue() / 255.0f), ((float)CrystalChanger.getInstance().blue.getValue() / 255.0f), (float)CrystalChanger.getInstance().alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (CrystalChanger.getInstance().glint.getValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 0.0f, 0.0f, 0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (CrystalChanger.getInstance().isEnabled()) {
            GlStateManager.scale((1.0f / CrystalChanger.getInstance().scale.getValue().floatValue()), (1.0f / CrystalChanger.getInstance().scale.getValue().floatValue()), 1.0f / CrystalChanger.getInstance().scale.getValue().floatValue());
        }
    }
    static {
        if (CrystalChanger.getInstance().glint.getValue()) {
            ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/rainbower.png");
        }
    }
}

