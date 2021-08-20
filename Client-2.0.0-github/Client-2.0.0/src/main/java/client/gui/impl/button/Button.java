package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.gui.impl.Component;
import client.gui.impl.Item;
import client.modules.client.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button
        extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(ClickGui.getInstance().gui.getValue() == ClickGui.Gui.OLD) {
            if (ClickGui.getInstance().disabled.getValue()) {
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getValue(), ClickGui.getInstance().d_green.getValue(), ClickGui.getInstance().d_blue.getValue(), ClickGui.getInstance().d_alpha.getValue()));
            }
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.toRGBA(ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), 255) : -5592406);
        } else {
            int color = ColorUtil.toARGB(ClickGui.getInstance().newared.getValue(), ClickGui.getInstance().newagreen.getValue(), ClickGui.getInstance().newablue.getValue(), ClickGui.getInstance().newaalpha.getValue());
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

