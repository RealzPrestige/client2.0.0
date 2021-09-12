package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import client.manager.ModuleManager;
import client.modules.Module;
import client.modules.core.ClickGui;
import client.modules.visual.PopChams;
import client.util.ColorUtil;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import static client.manager.ModuleManager.moduleList;

public class BindButton
        extends Button {
    private final Setting setting;
    public boolean isListening;
    public static final Minecraft mc = Minecraft.getMinecraft();
    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515) : (!this.isHovering(mouseX, mouseY) ? Client.colorManager.getColorWithAlpha(Client.moduleManager.getModuleByClass(ClickGui.class).alpha.getCurrentState()) : Client.colorManager.getColorWithAlpha(Client.moduleManager.getModuleByClass(ClickGui.class).alpha.getCurrentState())));
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.9999999f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getCurrentState(), ClickGui.getInstance().d_green.getCurrentState(), ClickGui.getInstance().d_blue.getCurrentState(), ClickGui.getInstance().d_alpha.getCurrentState()));
            if (this.isListening) {
                Client.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
            } else {
                Client.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getCurrentState().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
            }
        } else if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW){
            int color = ColorUtil.toARGB(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), ClickGui.getInstance().newaalpha.getCurrentState());
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? color : ColorUtil.toRGBA(0,0,0,0));
            int ercolor = ColorUtil.toARGB(ClickGui.getInstance().newred.getCurrentState(), ClickGui.getInstance().newgreen.getCurrentState(), ClickGui.getInstance().newblue.getCurrentState(), ClickGui.getInstance().newtheAlpha.getCurrentState());
            RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, ercolor);
            if (this.isListening) {
                Client.textManager.drawStringWithShadow("Listening...", this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
            } else {
                Client.textManager.drawStringWithShadow("Keybind: " + ChatFormatting.GRAY + this.setting.getCurrentState().toString(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
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
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                bind = new Bind(-1);
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            if (bind.toString().equalsIgnoreCase("Backspace")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}

