package client.util;

import client.Client;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil{
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Tessellator tessellator;
    public static RenderItem itemRender;
    public static ICamera camera;
    static {
        RenderUtil.itemRender = mc.getRenderItem();
        RenderUtil.camera = new Frustum();
    }

    public static void drawCrosshairs(double separation, double width, double thickness, boolean dynamic, int color) {
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        int screenHeight = new ScaledResolution(mc).getScaledHeight();
        separation += (EntityUtil.isMoving() && dynamic) ? 1 : 0;
        RenderUtil.drawLine((float) ((screenWidth / 2) - separation), (float) ((screenHeight / 2)), (float) ((screenWidth / 2) - separation - width), (float) ((screenHeight / 2)), (int) thickness, color);
        RenderUtil.drawLine((float) ((screenWidth / 2) + separation), (float) ((screenHeight / 2)), (float) ((screenWidth / 2) + separation + width), (float) ((screenHeight / 2)), (int) thickness, color);
        RenderUtil.drawLine((float) ((screenWidth / 2)), (float) ((screenHeight / 2) - separation), (float) ((screenWidth / 2)), (float) ((screenHeight / 2) - separation - width), (int) thickness, color);
        RenderUtil.drawLine((float) ((screenWidth / 2)), (float) ((screenHeight / 2) + separation), (float) ((screenWidth / 2)), (float) ((screenHeight / 2) + separation + width), (int) thickness, color);
    }
    public static void drawRectCol(final float x, final float y, final float width, final float height, final Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    public static void drawBorder(float x, float y, float width, float height, Color color) {
        RenderUtil.drawRectCol(x - 1, y - 1, 1, height + 2, color);
        RenderUtil.drawRectCol(x + width, y - 1, 1, height + 2, color);
        RenderUtil.drawRectCol(x, y - 1, width, 1, color);
        RenderUtil.drawRectCol(x, y + height, width, 1, color);
    }
    public static void drawFilledBox ( AxisAlignedBB bb , int color ) {
        GlStateManager.pushMatrix ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableDepth ( );
        GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 0 , 1 );
        GlStateManager.disableTexture2D ( );
        GlStateManager.depthMask ( false );
        float alpha = (float) ( color >> 24 & 0xFF ) / 255.0f;
        float red = (float) ( color >> 16 & 0xFF ) / 255.0f;
        float green = (float) ( color >> 8 & 0xFF ) / 255.0f;
        float blue = (float) ( color & 0xFF ) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance ( );
        BufferBuilder bufferbuilder = tessellator.getBuffer ( );
        bufferbuilder.begin ( 7 , DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( red , green , blue , alpha ).endVertex ( );
        tessellator.draw ( );
        GlStateManager.depthMask ( true );
        GlStateManager.enableDepth ( );
        GlStateManager.enableTexture2D ( );
        GlStateManager.disableBlend ( );
        GlStateManager.popMatrix ( );
    }

    public static AxisAlignedBB interpolateAxis ( AxisAlignedBB bb ) {
        return new AxisAlignedBB ( bb.minX - RenderUtil.mc.getRenderManager ( ).viewerPosX , bb.minY - RenderUtil.mc.getRenderManager ( ).viewerPosY , bb.minZ - RenderUtil.mc.getRenderManager ( ).viewerPosZ , bb.maxX - RenderUtil.mc.getRenderManager ( ).viewerPosX , bb.maxY - RenderUtil.mc.getRenderManager ( ).viewerPosY , bb.maxZ - RenderUtil.mc.getRenderManager ( ).viewerPosZ );
    }
    public static double interpolate(double previous, double current, float partialTicks) {
        return previous + (current - previous) * (double)partialTicks;
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder2 = tessellator.getBuffer();
        BufferBuilder2.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder2.pos(x, y + height, zLevel).tex((float) (textureX) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x, y, zLevel).tex((float) (textureX) * 0.00390625f, (float) (textureY) * 0.00390625f).endVertex();
        tessellator.draw();
    }
    public static void drawRoundedRect(double x, double y, double width, double height, final double radius, final Color color) {
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= 2.0;
        y *= 2.0;
        width *= 2.0;
        height *= 2.0;
        width += x;
        height += y;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        // draws top left
      for (int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(x + radius + Math.sin(i * 3.141592653589793 / 180.0) * radius * -1.0, y + radius + Math.cos(i * 3.141592653589793 / 180.0) * radius * -1.0);
        }
        //draws bottom left
        for (int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(x + radius + Math.sin(i * 3.141592653589793 / 180.0) * radius * -1.0, height - radius + Math.cos(i * 3.141592653589793 / 180.0) * radius * -1.0);
        }
        // draws bottom right
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(width - radius + Math.sin(i * 3.141592653589793 / 180.0) * radius, height - radius + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }
        //draws top right
        for (int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(width - radius + Math.sin(i * 3.141592653589793 / 180.0) * radius, y + radius + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
    }
    public static void drawBottomRoundedRect(double x, double y, double width, double height, final double radius, final Color color) {
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= 2.0;
        y *= 2.0;
        width *= 2.0;
        height *= 2.0;
        width += x;
        height += y;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        // draws top left
     /*   for (int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(x + radius + Math.sin(i * 3.141592653589793 / 180.0) * radius * -1.0, y + radius + Math.cos(i * 3.141592653589793 / 180.0) * radius * -1.0);
        } */
        //draws bottom left
        for (int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(x + radius + Math.sin(i * 3.141592653589793 / 180.0) * radius * -1.0, height - radius + Math.cos(i * 3.141592653589793 / 180.0) * radius * -1.0);
        }
        // draws bottom right
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(width - radius + Math.sin(i * 3.141592653589793 / 180.0) * radius, height - radius + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }
        //draws top right
      /*  for (int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(width - radius + Math.sin(i * 3.141592653589793 / 180.0) * radius, y + radius + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }*/
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
    }
    public static void drawTopRoundedRect(double x, double y, double width, double height, final double radius, final Color color) {
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= 2.0;
        y *= 2.0;
        width *= 2.0;
        height *= 2.0;
        width += x;
        height += y;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(x + radius + Math.sin(i * 3.141592653589793 / 180.0) * radius * -1.0, y + radius + Math.cos(i * 3.141592653589793 / 180.0) * radius * -1.0);
        }
        for (int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(x + 1.0 + Math.sin(i * 3.141592653589793 / 180.0) * -1.0, height - 1.0 + Math.cos(i * 3.141592653589793 / 180.0) * -1.0);
        }
        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex2d(width - 1.0 + Math.sin(i * 3.141592653589793 / 180.0), height - 1.0 + Math.cos(i * 3.141592653589793 / 180.0));
        }
        for (int i = 90; i <= 180; ++i) {
            GL11.glVertex2d(width - radius + Math.sin(i * 3.141592653589793 / 180.0) * radius, y + radius + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
    }

    public static void drawClawBox(BlockPos blockPos, double height, double length, double width, Color color) {
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addChainedClawBoxVertices(buffer, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + length, blockPos.getY() + height, blockPos.getZ() + width, color);
        tessellator.draw();
    }
    public static void addChainedClawBoxVertices(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY, maxZ - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY, minZ + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY, maxZ - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX - 0.8, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX - 0.8, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX + 0.8, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX + 0.8, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY + 0.2, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, minY + 0.2, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY + 0.2, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, minY + 0.2, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, maxY, maxZ - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, maxY, minZ + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, maxY, maxZ - 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, maxY, minZ + 0.8).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX - 0.8, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX - 0.8, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX + 0.8, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX + 0.8, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, maxY - 0.2, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(minX, maxY - 0.2, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, maxY - 0.2, minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0.0F).endVertex();
        buffer.pos(maxX, maxY - 0.2, maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawRoundedRect(int x, int y, int width, int height, int color, int radius) {
        drawRect(x, y + radius, x + width, y + height -radius, color);
        drawRect(x + radius, y, x + width - radius, y + radius, color);
        drawRect(x + radius, y + height - radius, x + width - radius, y + height, color);
        drawFilledCircle(x + radius, y + radius, radius, color);
        drawFilledCircle(x + width - radius, y + radius, radius, color);
        drawFilledCircle(x + radius, y + height - radius, radius, color);
        drawFilledCircle(x + width - radius, y + height - radius, radius, color);
    }

    public static void drawFilledCircle(int x, int y, double radius, int color) {
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glColor4f(((color >> 16) & 0xff) / 255F, ((color >> 8) & 0xff) / 255F, (color & 0xff) / 255F, ((color >> 24) & 0xff) / 255F);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++)
            glVertex2d( x + Math.sin(((i * Math.PI) / 180)) * radius, y + Math.cos(((i * Math.PI) / 180)) * radius);

        glEnd();
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float) (hex >> 16 & 0xFF) / 255.0f;
        float green = (float) (hex >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hex & 0xFF) / 255.0f;
        float alpha = (float) (hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x1, y1, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawBlockOutline(final AxisAlignedBB bb, final Color color, final float linewidth) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void setColor(final Color color) {
        GL11.glColor4d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0);
    }

    public static void drawText(BlockPos pos, String text) {
        GlStateManager.pushMatrix();
        RenderUtil.glBillboardDistanceScaled((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate(-((double) Client.textManager.getStringWidth(text) / 2.0), 0.0, 0.0);
        Client.textManager.drawStringWithShadow(text, 0.0f, 0.0f, -5592406);
        GlStateManager.popMatrix();
    }
    public static void drawTextWhite(BlockPos pos, String text) {
        GlStateManager.pushMatrix();
        RenderUtil.glBillboardDistanceScaled((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate(-((double) Client.textManager.getStringWidth(text) / 2.0), 0.0, 0.0);
        Client.textManager.drawStringWithShadow(text, 0.0f, 0.0f, -1);
        GlStateManager.popMatrix();
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void glEnd() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    public static void drawBox(final BlockPos pos, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() + 1 - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
    public static void drawBlockOutline(final BlockPos pos, final Color color, final float linewidth, final boolean air) {
        final IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            final Vec3d interp = EntityUtil.interpolateEntity( RenderUtil.mc.player , RenderUtil.mc.getRenderPartialTicks());
            drawBlockOutline(iblockstate.getSelectedBoundingBox( RenderUtil.mc.world , pos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), color, linewidth);
        }
    }
    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }
    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, float boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }
    public static void drawPerryESP ( AxisAlignedBB a , Color boxColor , Color outlineColor , float lineWidth , boolean outline , boolean box , float alpha , float scale , float slab ) {
        double f = 0.5 * ( 1 - scale );
        AxisAlignedBB bb = RenderUtil.interpolateAxis ( new AxisAlignedBB (
                a.minX + f ,
                a.minY + f + ( 1 - slab ) ,
                a.minZ + f ,
                a.maxX - f ,
                a.maxY - f ,
                a.maxZ - f
        ) );
        float rB = (float) boxColor.getRed ( ) / 255.0f;
        float gB = (float) boxColor.getGreen ( ) / 255.0f;
        float bB = (float) boxColor.getBlue ( ) / 255.0f;
        float aB = (float) boxColor.getAlpha ( ) / 255.0f;
        float rO = (float) outlineColor.getRed ( ) / 255.0f;
        float gO = (float) outlineColor.getGreen ( ) / 255.0f;
        float bO = (float) outlineColor.getBlue ( ) / 255.0f;
        float aO = (float) outlineColor.getAlpha ( ) / 255.0f;
        if ( alpha > 1 ) alpha = 1;
        aB *= alpha;
        aO *= alpha;
        if ( box ) {
            GlStateManager.pushMatrix ( );
            GlStateManager.enableBlend ( );
            GlStateManager.disableDepth ( );
            GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 0 , 1 );
            GlStateManager.disableTexture2D ( );
            GlStateManager.depthMask ( false );
            GL11.glEnable ( 2848 );
            GL11.glHint ( 3154 , 4354 );
            RenderGlobal.renderFilledBox ( bb , rB , gB , bB , aB );
            GL11.glDisable ( 2848 );
            GlStateManager.depthMask ( true );
            GlStateManager.enableDepth ( );
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableBlend ( );
            GlStateManager.popMatrix ( );
        }
        if ( outline ) {
            GlStateManager.pushMatrix ( );
            GlStateManager.enableBlend ( );
            GlStateManager.disableDepth ( );
            GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 0 , 1 );
            GlStateManager.disableTexture2D ( );
            GlStateManager.depthMask ( false );
            GL11.glEnable ( 2848 );
            GL11.glHint ( 3154 , 4354 );
            GL11.glLineWidth ( lineWidth );
            Tessellator tessellator = Tessellator.getInstance ( );
            BufferBuilder bufferbuilder = tessellator.getBuffer ( );
            bufferbuilder.begin ( 3 , DefaultVertexFormats.POSITION_COLOR );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            tessellator.draw ( );
            GL11.glDisable ( 2848 );
            GlStateManager.depthMask ( true );
            GlStateManager.enableDepth ( );
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableBlend ( );
            GlStateManager.popMatrix ( );
        }
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.translate((double) x - mc.getRenderManager().renderPosX, (double) y - mc.getRenderManager().renderPosY, (double) z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        RenderUtil.glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void drawBoxESPFlat(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBoxFlat(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutlineFlat(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }

    public static void drawBlockOutlineFlat(final BlockPos pos, final Color color, final float linewidth, final boolean air) {
        final IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            final Vec3d interp = EntityUtil.interpolateEntity( RenderUtil.mc.player , RenderUtil.mc.getRenderPartialTicks());
            drawBlockOutlineFlat(iblockstate.getSelectedBoundingBox(RenderUtil.mc.world , pos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), color, linewidth);
        }
    }

    public static void drawBoxFlat(final BlockPos pos, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBlockOutlineFlat(final AxisAlignedBB bb, final Color color, final float linewidth) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    //Kami5
    public static void renderEntity(EntityLivingBase entity, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (mc.getRenderManager() == null) return;

        if (modelBase instanceof ModelPlayer) {
            ModelPlayer modelPlayer = ((ModelPlayer) modelBase);
            modelPlayer.bipedHeadwear.showModel = false;
            modelPlayer.bipedBodyWear.showModel = false;
            modelPlayer.bipedLeftLegwear.showModel = false;
            modelPlayer.bipedRightLegwear.showModel = false;
            modelPlayer.bipedLeftArmwear.showModel = false;
            modelPlayer.bipedRightArmwear.showModel = false;
        }

        float partialTicks = mc.getRenderPartialTicks();
        double x = entity.posX - mc.getRenderManager().viewerPosX;
        double y = entity.posY - mc.getRenderManager().viewerPosY;
        double z = entity.posZ - mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();

        if (entity.isSneaking()) {
            y -= 0.125D;
        }
        renderLivingAt(x, y, z);
        prepareRotations(entity);
        float f4 = prepareScale(entity, scale);
        float yaw = handleRotationFloat(entity, partialTicks);

        GlStateManager.enableAlpha();
        modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        modelBase.setRotationAngles(limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4, entity);
        modelBase.render(entity, limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4);

        //  GlStateManager.depthMask(true);

        //   GlStateManager.disableRescaleNormal();
        //   GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        GlStateManager.popMatrix();
    }

    public static void renderLivingAt(double x, double y, double z) {
        GlStateManager.translate((float) x, (float) y, (float) z);

    }

    public static float prepareScale(EntityLivingBase entity, float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
        double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;

        GlStateManager.scale(scale + widthX, scale * entity.height, scale + widthZ);
        //  preRenderCallback();
        float f = 0.0625F;

        GlStateManager.translate(0.0F, -1.501F, 0.0F);
        //  GlStateManager.translate(0.0F, -f * 4, 0.0F);
        return f;
    }

    public static void prepareRotations(EntityLivingBase entityLivingBase) {
        GlStateManager.rotate(180 - entityLivingBase.rotationYaw, 0, 1, 0);
    }

    public static float handleRotationFloat(EntityLivingBase livingBase, float partialTicks) {
        return livingBase.rotationYawHead;
    }

    public static class RenderTesselator extends Tessellator {

        public static RenderTesselator INSTANCE = new RenderTesselator();

        public RenderTesselator() {
            super(0x200000);
        }

        public static void prepare(int mode) {
            prepareGL();
            begin(mode);
        }

        public static void prepareGL() {
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(1.5F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1);
        }

        public static void begin(int mode) {
            INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
        }

        public static void release() {
            render();
            releaseGL();
        }

        public static void render() {
            INSTANCE.draw();
        }

        public static void releaseGL() {
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }
    }
}

