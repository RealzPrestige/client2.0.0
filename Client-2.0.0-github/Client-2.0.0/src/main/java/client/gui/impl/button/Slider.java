package client.gui.impl.button;

import client.Client;
import client.gui.ClientGui;
import client.gui.impl.Component;
import client.gui.impl.setting.Setting;
import client.modules.core.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.lwjgl.input.Mouse;

public class Slider
        extends Button {
    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            this.dragSetting(mouseX, mouseY);
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.9999999f, ColorUtil.toRGBA(ClickGui.getInstance().d_red.getCurrentState(), ClickGui.getInstance().d_green.getCurrentState(), ClickGui.getInstance().d_blue.getCurrentState(), ClickGui.getInstance().d_alpha.getCurrentState()));
            RenderUtil.drawRect(this.x, this.y, ((Number) this.setting.getCurrentState()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float) this.width + 7.4f) * this.partialMultiplier(), this.y + (float) this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? Client.colorManager.getColorWithAlpha(Client.moduleManager.getModuleByClass(ClickGui.class).alpha.getCurrentState()) : Client.colorManager.getColorWithAlpha(Client.moduleManager.getModuleByClass(ClickGui.class).alpha.getCurrentState()));
            Client.textManager.drawStringWithShadow(this.getName() + " " + ChatFormatting.GRAY + (this.setting.getCurrentState() instanceof Float ? this.setting.getCurrentState() : Double.valueOf(((Number) this.setting.getCurrentState()).doubleValue())), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
        } else if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW) {
            this.dragSetting(mouseX, mouseY);
            int ercolor = ColorUtil.toARGB(ClickGui.getInstance().newred.getCurrentState(), ClickGui.getInstance().newgreen.getCurrentState(), ClickGui.getInstance().newblue.getCurrentState(), ClickGui.getInstance().newtheAlpha.getCurrentState());
            int color = ColorUtil.toARGB(ClickGui.getInstance().integerRed.getCurrentState(), ClickGui.getInstance().integerGreen.getCurrentState(), ClickGui.getInstance().integerBlue.getCurrentState(), ClickGui.getInstance().integerAlpha.getCurrentState());
            RenderUtil.drawRect(this.x, this.y, ((Number) this.setting.getCurrentState()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float) this.width + 7.4f) * this.partialMultiplier(), this.y + (float) this.height - 0.5f, color);
            RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, ercolor);
            Client.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);
            Client.textManager.drawStringWithShadow("" + ChatFormatting.GRAY + (this.setting.getCurrentState() instanceof Float ? this.setting.getCurrentState() : Double.valueOf(((Number) this.setting.getCurrentState()).doubleValue())), this.x + 3 + this.width - this.renderer.getStringWidth("" + (this.setting.getCurrentState() instanceof Float ? this.setting.getCurrentState() : Double.valueOf(((Number) this.setting.getCurrentState()).doubleValue()))), this.y - 1.7f - (float) ClientGui.getClickGui().getTextOffset(), -1);

        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : ClientGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() + 8.0f && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float) mouseX - this.x) / ((float) this.width + 7.4f);
        if (this.setting.getCurrentState() instanceof Double) {
            double result = (Double) this.setting.getMin() + (double) ((float) this.difference * percent);
            this.setting.setValue((double) Math.round(10.0 * result) / 10.0);
        } else if (this.setting.getCurrentState() instanceof Float) {
            float result = (Float) this.setting.getMin ( ) + (float) this.difference * percent;
            this.setting.setValue( (float) Math.round ( 10.0f * result ) / 10.0f );
        } else if (this.setting.getCurrentState() instanceof Integer) {
            this.setting.setValue((Integer) this.setting.getMin() + (int) ((float) this.difference * percent));
        }
    }

    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }

    private float part() {
        return ((Number) this.setting.getCurrentState()).floatValue() - this.min.floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}

