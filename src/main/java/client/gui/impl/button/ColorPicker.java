package client.gui.impl.button;


import client.Client;
import client.gui.ClientGui;
import client.gui.impl.setting.Setting;
import client.modules.core.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class ColorPicker extends Button { //TODO: finish this
    public static final Minecraft mc = Minecraft.getMinecraft();
    public Setting setting;
    public Boolean open = false;
    private final int booleanButtonOffset = 80;
    boolean pickingColor = false;
    boolean pickingHue = false;
    boolean pickingAlpha = false;
    private double y2;
    int offset = 5;

    public ColorPicker(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    /**
     * made by kambing
     * custom color settings
     * creds:wp3&trinity
     */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int sideColor = ColorUtil.toARGB(ClickGui.getInstance().newred.getCurrentState(), ClickGui.getInstance().newgreen.getCurrentState(), ClickGui.getInstance().newblue.getCurrentState(), ClickGui.getInstance().newtheAlpha.getCurrentState());
        if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW) {
            RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width, this.y + this.height - 3.0f, ColorUtil.toRGBA(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue()));
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getCurrentState(), ClickGui.getInstance().d_green.getCurrentState(), ClickGui.getInstance().d_blue.getCurrentState(), ClickGui.getInstance().d_alpha.getCurrentState()));
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
            if (this.open) {
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY) && !this.open) {
            this.open = true;
        }else if (mouseButton == 0 && this.isHovering(mouseX, mouseY) && this.open) {
            this.open = false;
        }
    }
}
