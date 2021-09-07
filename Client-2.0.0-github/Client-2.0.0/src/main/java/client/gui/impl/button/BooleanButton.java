package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.gui.impl.setting.Setting;
import client.modules.client.ClickGui;
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
            int booleancolor = ColorUtil.toARGB(0,0,0, 50);
            int sidecolor = ColorUtil.toARGB(ClickGui.getInstance().newred.getCurrentState(), ClickGui.getInstance().newgreen.getCurrentState(), ClickGui.getInstance().newblue.getCurrentState(), ClickGui.getInstance().newtheAlpha.getCurrentState());
            int color = ColorUtil.toARGB(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), ClickGui.getInstance().newaalpha.getCurrentState());

            RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, sidecolor);
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
           RenderUtil.drawRect(this.x + 74, this.y + 5, this.x + 95, this.y + 13, booleancolor);
            if(getState()) {
                RenderUtil.drawRect(this.x + 85, this.y + 6, this.x + 94, this.y + 12, color);
            } else {
                RenderUtil.drawRect(this.x + 75, this.y + 6, this.x + 84, this.y + 12, -1);
            }
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
        this.setting.setValue( ! ( (Boolean) this.setting.getCurrentState( ) ) );
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getCurrentState();
    }
}

