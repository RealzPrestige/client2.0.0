package client.util;

import client.Client;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil
        implements Util{
    public static Tessellator tessellator;
    public static RenderItem itemRender;
    public static ICamera camera;
    private static boolean depth = GL11.glIsEnabled(2896);
    private static boolean texture = GL11.glIsEnabled(3042);
    private static boolean clean = GL11.glIsEnabled(3553);
    private static boolean bind = GL11.glIsEnabled(2929);
    private static boolean override = GL11.glIsEnabled(2848);
    static {
        RenderUtil.itemRender = mc.getRenderItem();
        RenderUtil.camera = new Frustum();
    }

    public static double interpolate(double previous, double current, float partialTicks) {
        return previous + (current - previous) * (double)partialTicks;
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder2 = tessellator.getBuffer();
        BufferBuilder2.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder2.pos(x + 0, y + height, zLevel).tex((float) (textureX + 0) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + 0, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY + 0) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + 0, y + 0, zLevel).tex((float) (textureX + 0) * 0.00390625f, (float) (textureY + 0) * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public static void GLPre(float lineWidth) {
        depth = GL11.glIsEnabled(2896);
        texture = GL11.glIsEnabled(3042);
        clean = GL11.glIsEnabled(3553);
        bind = GL11.glIsEnabled(2929);
        override = GL11.glIsEnabled(2848);
        RenderUtil.GLPre(depth, texture, clean, bind, override, lineWidth);
    }
    private static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
        if (depth) {
            GL11.glDisable(2896);
        }
        if (!texture) {
            GL11.glEnable(3042);
        }
        GL11.glLineWidth(lineWidth);
        if (clean) {
            GL11.glDisable(3553);
        }
        if (bind) {
            GL11.glDisable(2929);
        }
        if (!override) {
            GL11.glEnable(2848);
        }
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint(3154, 4354);
        GlStateManager.depthMask(false);
    }

    public static void GlPost() {
        RenderUtil.GLPost(depth, texture, clean, bind, override);
    }

    private static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
        GlStateManager.depthMask(true);
        if (!override) {
            GL11.glDisable(2848);
        }
        if (bind) {
            GL11.glEnable(2929);
        }
        if (clean) {
            GL11.glEnable(3553);
        }
        if (!texture) {
            GL11.glDisable(3042);
        }
        if (depth) {
            GL11.glEnable(2896);
        }
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

    public static void drawGradientRect(float x, float y, float w, float h, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float) (startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (startColor & 0xFF) / 255.0f;
        float f4 = (float) (endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float) (endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float) (endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float) (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double) x + (double) w, y, 0.0).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos(x, y, 0.0).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos(x, (double) y + (double) h, 0.0).color(f5, f6, f7, f4).endVertex();
        vertexbuffer.pos((double) x + (double) w, (double) y + (double) h, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawClawBox(BlockPos blockPos, double height, double length, double width, Color color) {
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addChainedClawBoxVertices(buffer, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + length, blockPos.getY() + height, blockPos.getZ() + width, color);
        tessellator.draw();
    }
    public static void drawBoxESPWithGradientOption(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height, gradientBox, invertGradientBox, gradientAlpha);
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height, gradientOutline, invertGradientOutline, gradientAlpha);
        }
    }
    public static void drawBox(BlockPos pos, Color color, double height, boolean gradient, boolean invert, int alpha) {
        if (gradient) {
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            RenderUtil.drawOpenGradientBox(pos, invert ? endColor : color, invert ? color : endColor, height);
            return;
        }
        AxisAlignedBB bb = new AxisAlignedBB((double) pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double) pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double) pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double) (pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double) (pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY + height, (double) (pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawOpenGradientBox(BlockPos pos, Color startColor, Color endColor, double height) {
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP) continue;
            RenderUtil.drawGradientPlane(pos, face, startColor, endColor, height);
        }
    }

    public static void drawGradientPlane(BlockPos pos, EnumFacing face, Color startColor, Color endColor, boolean half, boolean top) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z);
        float red = (float) startColor.getRed() / 255.0f;
        float green = (float) startColor.getGreen() / 255.0f;
        float blue = (float) startColor.getBlue() / 255.0f;
        float alpha = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) endColor.getRed() / 255.0f;
        float green1 = (float) endColor.getGreen() / 255.0f;
        float blue1 = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) endColor.getAlpha() / 255.0f;
        double x1 = 0.0;
        double y1 = 0.0;
        double z1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double z2 = 0.0;
        if (face == EnumFacing.DOWN) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.minY + (top ? 0.5 : 0.0);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.UP) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.maxY / (double) (half ? 2 : 1);
            y2 = bb.maxY / (double) (half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.EAST) {
            x1 = bb.maxX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double) (half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.WEST) {
            x1 = bb.minX;
            x2 = bb.minX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double) (half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.SOUTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double) (half ? 2 : 1);
            z1 = bb.maxZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.NORTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY + (top ? 0.5 : 0.0);
            y2 = bb.maxY / (double) (half ? 2 : 1);
            z1 = bb.minZ;
            z2 = bb.minZ;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
        } else if (face == EnumFacing.UP) {
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
        } else if (face == EnumFacing.DOWN) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawGradientPlane(BlockPos pos, EnumFacing face, Color startColor, Color endColor, double height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        AxisAlignedBB bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, height, 0.0);
        float red = (float) startColor.getRed() / 255.0f;
        float green = (float) startColor.getGreen() / 255.0f;
        float blue = (float) startColor.getBlue() / 255.0f;
        float alpha = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) endColor.getRed() / 255.0f;
        float green1 = (float) endColor.getGreen() / 255.0f;
        float blue1 = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) endColor.getAlpha() / 255.0f;
        double x1 = 0.0;
        double y1 = 0.0;
        double z1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double z2 = 0.0;
        if (face == EnumFacing.DOWN) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.minY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.UP) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.maxY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.EAST) {
            x1 = bb.maxX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.WEST) {
            x1 = bb.minX;
            x2 = bb.minX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.SOUTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.maxZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.NORTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY;
            z1 = bb.minZ;
            z2 = bb.minZ;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
        } else if (face == EnumFacing.UP) {
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
            builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
        } else if (face == EnumFacing.DOWN) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }


    public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air, double height, boolean gradient, boolean invert, int alpha) {
        if (gradient) {
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            RenderUtil.drawGradientBlockOutline(pos, invert ? endColor : color, invert ? color : endColor, linewidth, height);
            return;
        }
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            AxisAlignedBB blockAxis = new AxisAlignedBB((double) pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double) pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double) pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double) (pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double) (pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY + height, (double) (pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
            RenderUtil.drawBlockOutline(blockAxis.grow(0.002f), color, linewidth);
        }
    }
    public static void drawGradientBlockOutline(BlockPos pos, Color startColor, Color endColor, float linewidth, double height) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        RenderUtil.drawGradientBlockOutline(iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, height, 0.0), startColor, endColor, linewidth);
    }

    public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
        float red = (float) startColor.getRed() / 255.0f;
        float green = (float) startColor.getGreen() / 255.0f;
        float blue = (float) startColor.getBlue() / 255.0f;
        float alpha = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) endColor.getRed() / 255.0f;
        float green1 = (float) endColor.getGreen() / 255.0f;
        float blue1 = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) endColor.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
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

    public static void drawRoundedBottemRect(int x, int y, int width, int height, int color, int radius) {
        drawRect(x, y, x + width, y + height -radius, color);
        drawRect(x + radius, y + height - radius, x + width - radius, y + height, color);
        drawFilledCircle(x + radius, y + height - radius, radius, color);
        drawFilledCircle(x + width - radius, y + height - radius, radius, color);
    }

    public static void drawRoundedTopRect(int x, int y, int width, int height, int color, int radius) {
        drawRect(x, y + radius, x + width, y + height, color);
        drawRect(x + radius, y, x + width - radius, y + radius, color);
        drawFilledCircle(x + radius, y + radius, radius, color);
        drawFilledCircle(x + width - radius, y + radius, radius, color);
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

    public static void renderCrosses(final BlockPos pos, final Color color, final float lineWidth) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() + 1 - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            renderCrosses(bb, color);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
    public static void renderCrosses(final AxisAlignedBB bb, final Color color) {
        final int hex = color.getRGB();
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        final BufferBuilder bufferbuilder = RenderUtil.tessellator.getBuffer();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 1.0f).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, 1.0f).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, 1.0f).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, 1.0f).endVertex();
        RenderUtil.tessellator.draw();
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

    public static void drawSecondBoxESP(final BlockPos pos, final Color color, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha, final float height) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() + height - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            if (box) {
                RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f);
            }
            if (outline) {
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            }
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void boxESP(final BlockPos pos, final Color color, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha) {
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
            GL11.glLineWidth(lineWidth);
            final double dist = RenderUtil.mc.player.getDistance( pos.getX() + 0.5f , pos.getY() + 0.5f , pos.getZ() + 0.5f ) * 0.75;
            final double percentage = RenderUtil.mc.playerController.curBlockDamageMP;
            double minX;
            double minY;
            double minZ;
            double maxX;
            double maxY;
            double maxZ;
            minX = bb.minX + 0.5 - percentage / 2.0;
            minY = bb.minY + 0.5 - percentage / 2.0;
            minZ = bb.minZ + 0.5 - percentage / 2.0;
            maxX = bb.maxX - 0.5 + percentage / 2.0;
            maxY = bb.maxY - 0.5 + percentage / 2.0;
            maxZ = bb.maxZ - 0.5 + percentage / 2.0;
            final AxisAlignedBB a = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
            if (box) {
                drawFilledBox(a, new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f).getRGB());
            }
            if (outline) {
                drawBlockOutline(a, new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f), 1);
            }
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
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

    public static void drawFilledBox(final AxisAlignedBB bb, final int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void setColor(final Color color) {
        GL11.glColor4d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0);
    }

    public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 2) != 0) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 4) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 8) != 0) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }


    public static void drawGradientSideways(final double leftpos, final double top, final double right, final double bottom, final int col1, final int col2) {
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        final float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        final float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        final float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        final float f8 = (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(leftpos, top);
        GL11.glVertex2d(leftpos, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawText(BlockPos pos, String text) {
        GlStateManager.pushMatrix();
        RenderUtil.glBillboardDistanceScaled((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate(-((double) Client.textManager.getStringWidth(text) / 2.0), 0.0, 0.0);
        Client.textManager.drawStringWithShadow(text, 0.0f, 0.0f, -5592406);
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
    public static void drawBoxWithGradientOption(final BlockPos pos, final Color color, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() + 1 - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            final double dist = mc.player.getDistance( pos.getX() + 0.5f , pos.getY() + 0.5f , pos.getZ() + 0.5f ) * 0.75;
            if (box) {
                RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f);
            }
            if (outline) {
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            }
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}

