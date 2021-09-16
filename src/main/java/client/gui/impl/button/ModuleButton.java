package client.gui.impl.button;

import client.Client;
import client.command.Command;
import client.gui.ClientGui;
import client.gui.impl.Component;
import client.gui.impl.Item;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.modules.core.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton
        extends Button {
    private final Module module;
    private List<Item> items = new ArrayList <> ( );
    private boolean subOpen;
    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }

    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if (setting.getCurrentState() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton(setting));
                }
                if (setting.getCurrentState() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton(setting));
                }
                if ((setting.getCurrentState() instanceof String || setting.getCurrentState() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringButton(setting));
                }
                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new Slider(setting));
                    continue;
                }
                if (setting.isEnumSetting()) {
                    newItems.add(new EnumButton(setting));
                    continue;
                }
                if (setting.isColorSetting()) {
                    newItems.add(new ColorPicker(setting));
                }
            }
            newItems.add(new BindButton(this.module.getSettingByName("Keybind")));
            this.items = newItems;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            if (!this.items.isEmpty()) {
                Client.textManager.drawString(ClickGui.getInstance().button.getCurrentState() ? (this.subOpen ? "-" : ClickGui.getInstance().buttonButton.getCurrentState() == ClickGui.Button.DOT ? "\u2022" : "+") : "", this.x - 1.5f + (float) this.width - 7.4f, this.y - 2.0f - (float) ClientGui.getClickGui().getTextOffset(), -1, false);
                if (this.subOpen) {
                    float height = 1.0f;
                    for (Item item : this.items) {
                        Component.counter1[0] = Component.counter1[0] + 1;
                        if (!item.isHidden()) {
                            item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                            item.setHeight(15);
                            item.setWidth(this.width - 9);
                            item.drawScreen(mouseX, mouseY, partialTicks);
                        }
                        item.update();
                    }
                }
            }
        } else if (ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW){
            super.drawScreen(mouseX, mouseY, partialTicks);
            if (!this.items.isEmpty()) {
                Client.textManager.drawString((this.subOpen ? "v" : ">"), this.x - 1.5f + (float) this.width - 7.4f, this.y - 2.0f - (float) ClientGui.getClickGui().getTextOffset(), -1, false);
                if (this.subOpen) {
                    float height = 1.0f;
                    for (Item item : this.items) {
                        Component.counter1[0] = Component.counter1[0] + 1;
                        if (!item.isHidden()) {
                            item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                            item.setHeight(15);
                            item.setWidth(this.width - 9);
                            item.drawScreen(mouseX, mouseY, partialTicks);
                        }
                        item.update();
                    }
                }
            }
        }
        if(isHovering(mouseX, mouseY)){
            RenderUtil.drawRect(mouseX + 10, mouseY, mouseX + 10 + renderer.getStringWidth(module.getDescription()), mouseY + 10, ColorUtil.toRGBA(0,0,0, 50));
            RenderUtil.drawBorder(mouseX + 10, mouseY, renderer.getStringWidth(module.getDescription()), 10, new Color(ColorUtil.toRGBA(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), 30 / 255)));
            renderer.drawStringWithShadow(module.getDescription(), mouseX + 10, mouseY, -1);
        }
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (mouseButton == 2 && this.isHovering(mouseX, mouseY)) {
                if(module.isDrawn()) {
                    module.setUndrawn();
                    Command.sendMessage(module.getName() + " is no longer Drawn.");
                } else {
                    module.setDrawn();
                    Command.sendMessage(module.getName() + " is now Drawn.");
                }
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (this.subOpen) {
                for (Item item : this.items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (Item item : this.items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}

