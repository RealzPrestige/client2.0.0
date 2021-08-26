package client.gui.impl.background;

import client.Client;
import client.gui.impl.background.particles.ParticleSystem;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MainMenuScreen extends GuiScreen {
    private final ResourceLocation resourceLocation = new ResourceLocation("textures/background.jpg");
    public ParticleSystem particleSystem;
    private int y;
    private int x;

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }

    public void initGui() {
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
        int yOffset = (int) (-1.0f * (this.height / 2.0f) / (this.height / 18.0f));
        this.buttonList.add(new TextButton(0, 3 + Client.textManager.getStringWidth("Welcome to Client 2.0.0") / 2, yOffset + 10 , "Welcome to Client 2.0.0"));
        this.buttonList.add(new TextButton(0, 3 + Client.textManager.getStringWidth("Singleplayer") / 2, this.y + 20, "Singleplayer"));
        this.buttonList.add(new TextButton(1, 3 + Client.textManager.getStringWidth("Multiplayer") / 2, this.y + 44, "Multiplayer"));
        this.buttonList.add(new TextButton(2, 3 + Client.textManager.getStringWidth("Options") / 2, this.y + 66, "Options"));
        this.buttonList.add(new TextButton(2, 3 + Client.textManager.getStringWidth("Quit game") / 2, this.y + 88, "Quit game"));
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    @Override
    public void updateScreen() {
            if (particleSystem != null) {
                particleSystem.update();
            }
        super.updateScreen();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MainMenuScreen.isHovered(Client.textManager.getStringWidth("Singleplayer") / 2, this.y + 20, Client.textManager.getStringWidth("Singleplayer"), Client.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (MainMenuScreen.isHovered(Client.textManager.getStringWidth("Multiplayer") / 2, this.y + 44, Client.textManager.getStringWidth("Multiplayer"), Client.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (MainMenuScreen.isHovered(Client.textManager.getStringWidth("Options") / 2, this.y + 66, Client.textManager.getStringWidth("Options"), Client.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        } else if (MainMenuScreen.isHovered( Client.textManager.getStringWidth("Quit game") / 2, this.y + 88, Client.textManager.getStringWidth("Quit game"), Client.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            this.mc.shutdown();
        }
    }

    public void drawLogo() {
        ResourceLocation logo = new ResourceLocation("textures/logo.png");
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo);
        drawCompleteImage(0, 464, 250, 48);
        mc.getTextureManager().deleteTexture(logo);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }

    public void drawRoundedLogo() {
        ResourceLocation logo = new ResourceLocation("textures/roundedlogo.png");
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo);
        drawCompleteImage(this.x - 144 / 2, this.y - 144 / 2, 144, 144);
        mc.getTextureManager().deleteTexture(logo);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float xOffset = -1.0f * ((this.width / 2.0f) / ((float) this.width / 32.0f));
        float yOffset = -1.0f * ((this.height / 2.0f) / ((float) this.height / 18.0f));
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        this.mc.getTextureManager().bindTexture(this.resourceLocation);
        MainMenuScreen.drawCompleteImage(-16.0f + xOffset, -9.0f + yOffset, this.width + 32, this.height + 18);
        RenderUtil.drawRect(xOffset, yOffset, 70, 1000 , ColorUtil.toRGBA(20,20,20,70));
        super.drawScreen(970, 540, partialTicks);
        this.drawLogo();
        this.drawRoundedLogo();
        if (particleSystem != null && MenuToggler.getInstance().particles.getCurrentState()) {
            particleSystem.render(mouseX, mouseY);
        } else {
            this.particleSystem = new ParticleSystem(new ScaledResolution(mc));
        }
    }


    private static class TextButton
            extends GuiButton {
        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, Client.textManager.getStringWidth(buttonText), Client.textManager.getFontHeight(), buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.enabled = true;
                this.hovered = (float) mouseX >= (float) this.x - (float) Client.textManager.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                Client.textManager.drawStringWithShadow(this.displayString, (float) this.x - (float) Client.textManager.getStringWidth(this.displayString) / 2.0f, this.y, Color.WHITE.getRGB());
                if (this.hovered) {
                    RenderUtil.drawLine((float) (this.x - 1) - (float) Client.textManager.getStringWidth(this.displayString) / 2.0f, this.y + 2 + Client.textManager.getFontHeight(), (float) this.x + (float) Client.textManager.getStringWidth(this.displayString) / 2.0f + 1.0f, this.y + 2 + Client.textManager.getFontHeight(), 1.0f, Color.WHITE.getRGB());
                }
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return this.enabled && this.visible && (float) mouseX >= (float) this.x - (float) Client.textManager.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }

}

