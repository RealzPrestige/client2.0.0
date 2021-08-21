package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.modules.client.ClickGui;
import client.gui.impl.setting.Setting;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

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
        if (ClickGui.getInstance().gui.getValue() == ClickGui.Gui.OLD) {
            if (this.getState()) {
                RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width, this.y + this.height - 3.0f, ColorUtil.toRGBA(102, 255, 51, 200));
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getValue(), ClickGui.getInstance().d_green.getValue(), ClickGui.getInstance().d_blue.getValue(), ClickGui.getInstance().d_alpha.getValue()));
            } else {
                RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width, this.y + this.height - 3.0f, ColorUtil.toRGBA(200, 200, 200, 200));
                RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getValue(), ClickGui.getInstance().d_green.getValue(), ClickGui.getInstance().d_blue.getValue(), ClickGui.getInstance().d_alpha.getValue()));
            }
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
        } else {
            int acolor = ColorUtil.toARGB(ClickGui.getInstance().newared.getValue(), ClickGui.getInstance().newagreen.getValue(), ClickGui.getInstance().newablue.getValue(), ClickGui.getInstance().newaalpha.getValue());
            int ercolor = ColorUtil.toARGB(ClickGui.getInstance().newred.getValue(), ClickGui.getInstance().newgreen.getValue(), ClickGui.getInstance().newblue.getValue(), ClickGui.getInstance().newtheAlpha.getValue());
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? acolor : ColorUtil.toRGBA(0, 0, 0, 0));
            RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, ercolor);
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);

        }
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
        this.setting.setValue( ! ( (Boolean) this.setting.getValue ( ) ) );
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getValue();
    }
}

