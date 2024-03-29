package client.util;

import client.modules.core.ClickGui;
import client.modules.core.Hud;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtil {

    public static Color alphaStep(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index / (float) count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }


    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0);
        return Color.getHSBColor((float) ( rainbowState % 360.0 / 360.0), ClickGui.getInstance ( ).rainbowSaturation.getCurrentState( ) / 255.0f, ClickGui.getInstance ( ).rainbowBrightness.getCurrentState( ) / 255.0f);
    }

    public static Color rainbowHud(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0);
        return Color.getHSBColor((float) ( rainbowState % 360.0 / 360.0), Hud.getInstance ( ).rainbowSaturation.getCurrentState( ) / 255.0f, Hud.getInstance ( ).rainbowBrightness.getCurrentState( ) / 255.0f);
    }
    public static int toRGBA(Color color) {
        return ColorUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    public static void setColor(Color color) {
        GL11.glColor4d((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }
}

