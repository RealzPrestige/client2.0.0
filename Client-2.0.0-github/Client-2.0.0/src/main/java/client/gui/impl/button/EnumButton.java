package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.modules.client.ClickGui;
import client.setting.Setting;
import client.util.ColorUtil;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton
        extends Button {
    public Setting setting;

    public EnumButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().gui.getValue() == ClickGui.Gui.OLD){
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Client.colorManager.getColorWithAlpha(Client.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : Client.colorManager.getColorWithAlpha(Client.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        Client.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    } else {
            int color = ColorUtil.toARGB(ClickGui.getInstance().newared.getValue(), ClickGui.getInstance().newagreen.getValue(), ClickGui.getInstance().newablue.getValue(), ClickGui.getInstance().newaalpha.getValue());
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? color : ColorUtil.toRGBA(0,0,0, 0));
            Client.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
            int ercolor = ColorUtil.toARGB(ClickGui.getInstance().newred.getValue(), ClickGui.getInstance().newgreen.getValue(), ClickGui.getInstance().newblue.getValue(), ClickGui.getInstance().newtheAlpha.getValue());
            RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, ercolor);

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
        this.setting.increaseEnum();
    }

    @Override
    public boolean getState() {
        return true;
    }
}

