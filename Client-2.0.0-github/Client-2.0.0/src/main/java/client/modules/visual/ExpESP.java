package client.modules.visual;

import client.events.Render3DEvent;
import client.modules.Module;
import client.setting.Setting;
import client.util.EntityUtil;
import client.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ExpESP extends Module {

    public ExpESP(){
        super("ExpESP", "Bottle of enchant rendaaa", Category.VISUAL);
    }
    public Setting<Boolean> bottles = this.register(new Setting<Boolean>("Bottles", true));
    public Setting<Boolean> orbs = this.register(new Setting<Boolean>("Orbs", true));
    public Setting<Integer> red = this.register(new Setting<Integer>("BottlesRed", 255, 0, 255,v-> this.bottles.getValue()));
    public Setting<Integer> green = this.register(new Setting<Integer>("BottlesGreen", 255, 0, 255,v-> this.bottles.getValue()));
    public Setting<Integer> blue = this.register(new Setting<Integer>("BottlesBlue", 255, 0, 255,v-> this.bottles.getValue()));
    public Setting<Integer> alpha = this.register(new Setting<Integer>("BottlesAlpha", 150, 0, 255,v-> this.bottles.getValue()));
    public Setting<Integer> o_red = this.register(new Setting<Integer>("OrbsRed", 255, 0, 255,v-> this.orbs.getValue()));
    public Setting<Integer> o_green = this.register(new Setting<Integer>("OrbsGreen", 255, 0, 255,v-> this.orbs.getValue()));
    public Setting<Integer> o_blue = this.register(new Setting<Integer>("OrbsBlue", 255, 0, 255,v-> this.orbs.getValue()));
    public Setting<Integer> o_alpha = this.register(new Setting<Integer>("OrbsAlpha", 150, 0, 255,v-> this.orbs.getValue()));
    @Override
    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
            i = 0;
            if(this.orbs.getValue()) {
                for (Entity entity : mc.world.loadedEntityList) {
                    if (!(entity instanceof EntityXPOrb) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                    interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                    bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0f);
                    RenderGlobal.renderFilledBox(bb, (float) red.getValue() / 255.0f, (float) green.getValue() / 255.0f, (float) blue.getValue() / 255.0f, (float) alpha.getValue() / 255.0f);
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()), 1.0f);
                    if (++i < 50) continue;
                    break;
                }
            }

        if (this.bottles.getValue()) {
            i = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityExpBottle) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) o_red.getValue() / 255.0f, (float) o_green.getValue() / 255.0f, (float) o_blue.getValue() / 255.0f, (float) o_alpha.getValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(o_red.getValue(), o_green.getValue(), o_blue.getValue(), o_alpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
    }
}
