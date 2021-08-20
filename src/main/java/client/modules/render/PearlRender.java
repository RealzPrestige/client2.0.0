package client.modules.render;

import client.events.Render3DEvent;
import client.modules.Module;
import client.util.EntityUtil;
import client.util.MathUtil;
import client.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class PearlRender extends Module {

    public PearlRender(){
        super("PearlRender", "Renders where pearls will go", Category.RENDER);
    }

    public double interpolate(double now, double then) {
        return then + (now - then) * (double) mc.getRenderPartialTicks();
    }

    public double[] interpolate(Entity entity) {
        double posX = this.interpolate(entity.posX, entity.lastTickPosX) -mc.getRenderManager().renderPosX;
        double posY = this.interpolate(entity.posY, entity.lastTickPosY) -mc.getRenderManager().renderPosY;
        double posZ = this.interpolate(entity.posZ, entity.lastTickPosZ) -mc.getRenderManager().renderPosZ;
        return new double[]{posX, posY, posZ};
    }

    public void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
        double[] xyz = this.interpolate(e);
        this.drawLine(xyz[0], xyz[1], xyz[2], red, green, blue, opacity);
    }

    public void drawLine(double posx, double posy, double posz, float red, float green, float blue, float opacity) {
        Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float) Math.toRadians(mc.player.rotationPitch))).rotateYaw(-((float) Math.toRadians(mc.player.rotationYaw)));
        this.drawLineFromPosToPos(eyes.x, eyes.y + (double)mc.player.getEyeHeight(), eyes.z, posx, posy, posz, red, green, blue, opacity);
    }

    public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc(770, 771);
        glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLoadIdentity();
        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
        GL11.glBegin(GL11.GL_LINES); {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
        }
        GL11.glEnd();
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        GL11.glColor3d(1d, 1d, 1d);
        mc.gameSettings.viewBobbing = bobbing;
    }
    @Override
    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        i = 0;
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderPearl) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
            interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
            bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            glEnable(2848);
            glHint(3154, 4354);
            GL11.glLineWidth(1.0f);
            RenderGlobal.renderFilledBox(bb, 255, 255, 255, 255);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            RenderUtil.drawBlockOutline(bb, new Color(255, 255, 255), 1.0f);
            drawLineToEntity(entity, 255, 255, 255, 255);
            BlockPos posEntity = entity.getPosition();
            RenderUtil.drawText(posEntity, "X: " + MathUtil.round(entity.posX, 0) + " Y: " + MathUtil.round(entity.posY, 0) + " Z:" + MathUtil.round(entity.posZ, 2));
            if (++i < 50) continue;
            break;
        }
    }
}
