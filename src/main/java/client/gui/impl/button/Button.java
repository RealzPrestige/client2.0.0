package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.gui.impl.Component;
import client.gui.impl.Item;
import client.modules.core.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button
        extends Item {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            if (ClickGui.getInstance().disabled.getCurrentState()) {
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getCurrentState(), ClickGui.getInstance().d_green.getCurrentState(), ClickGui.getInstance().d_blue.getCurrentState(), ClickGui.getInstance().d_alpha.getCurrentState()));
            }
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? ClickGui.getInstance().rainbow.getCurrentState() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getCurrentState()).getRGB() : ColorUtil.toRGBA(ClickGui.getInstance().topRed.getCurrentState(), ClickGui.getInstance().topGreen.getCurrentState(), ClickGui.getInstance().topBlue.getCurrentState(), 255) : -5592406);
        } else if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW){
            int color = ColorUtil.toARGB(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), ClickGui.getInstance().newaalpha.getCurrentState());
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5f, this.getState() ? color : ColorUtil.toRGBA(10,10, 10,27));
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 1, this.y - 2.0f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : ClientGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}

