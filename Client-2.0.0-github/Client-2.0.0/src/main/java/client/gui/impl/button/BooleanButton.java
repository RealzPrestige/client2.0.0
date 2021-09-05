package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.modules.client.ClickGui;
import client.gui.impl.setting.Setting;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BooleanButton
        extends Button {
    private final Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            if (this.getState()) {
                RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width, this.y + this.height - 3.0f, ColorUtil.toRGBA(102, 255, 51, 200));
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getCurrentState(), ClickGui.getInstance().d_green.getCurrentState(), ClickGui.getInstance().d_blue.getCurrentState(), ClickGui.getInstance().d_alpha.getCurrentState()));
            } else {
                RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width, this.y + this.height - 3.0f, ColorUtil.toRGBA(200, 200, 200, 200));
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getCurrentState(), ClickGui.getInstance().d_green.getCurrentState(), ClickGui.getInstance().d_blue.getCurrentState(), ClickGui.getInstance().d_alpha.getCurrentState()));
            }
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
        } else if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW){
            int acolor = ColorUtil.toARGB(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), ClickGui.getInstance().newaalpha.getCurrentState());
            int ercolor = ColorUtil.toARGB(ClickGui.getInstance().newred.getCurrentState(), ClickGui.getInstance().newgreen.getCurrentState(), ClickGui.getInstance().newblue.getCurrentState(), ClickGui.getInstance().newtheAlpha.getCurrentState());
            RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, ercolor);
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
            if(this.getState()) {
                this.drawImageLogo();
            }
        }
    }

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

    public void drawImageLogo() {
        ResourceLocation logo = new ResourceLocation("textures/checkmark.png");
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo);
        drawCompleteImage(this.x + width - 7, this.y - 1, 20, 20);
        mc.getTextureManager().deleteTexture(logo);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.setValue( ! ( (Boolean) this.setting.getCurrentState( ) ) );
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getCurrentState();
    }
}

