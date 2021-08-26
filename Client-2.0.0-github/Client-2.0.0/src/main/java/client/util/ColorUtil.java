package client.util;

import client.modules.client.ClickGui;
import client.modules.client.Hud;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtil {
    private float hue;
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

    public static Color rainbowHighDelay(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay * 100) / 20.0);
        return Color.getHSBColor((float) ( rainbowState % 360.0 / 360.0), ClickGui.getInstance ( ).rainbowSaturation.getCurrentState( ) / 255.0f, ClickGui.getInstance ( ).rainbowBrightness.getCurrentState( ) / 255.0f);
    }

    public static Color rainbowHud(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0);
        return Color.getHSBColor((float) ( rainbowState % 360.0 / 360.0), Hud.getInstance ( ).rainbowSaturation.getCurrentState( ) / 255.0f, Hud.getInstance ( ).rainbowBrightness.getCurrentState( ) / 255.0f);
    }
    public static int toRGBA(Color color) {
        return ColorUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getRainbow(int speed, int offset, float s, float b) {
        float hue = (System.currentTimeMillis() + (long)offset) % (long)speed;
        return Color.getHSBColor(hue /= (float)speed, s, b).getRGB();
    }
    public static void setColor(Color color) {
        GL11.glColor4d((double)((float)color.getRed() / 255.0f), (double)((float)color.getGreen() / 255.0f), (double)((float)color.getBlue() / 255.0f), (double)((float)color.getAlpha() / 255.0f));
    }
}

