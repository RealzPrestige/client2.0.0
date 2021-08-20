package client.gui.impl;

import client.Client;
import client.gui.ClientGui;
import client.gui.impl.button.Button;
import client.modules.Feature;
import client.modules.client.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

public class Component
        extends Feature {
    public static int[] counter1 = new int[]{1};
    private final ArrayList items = new ArrayList();
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }

    public void setupItems() {
    }

    private void drag(int mouseX, int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().gui.getValue() == ClickGui.Gui.OLD) {
            this.drag(mouseX, mouseY);
            counter1 = new int[]{1};
            float var4 = this.open ? this.getTotalItemHeight() - 2.0F : 0.0F;
            int color = ColorUtil.toARGB(ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), ClickGui.getInstance().secondAlpha.getValue());
            if (this.open) {
                RenderUtil.drawRect((float) this.x + 1, (float) this.y + 15, (float) (this.x + this.width), (float) (this.y + this.height + 2) + var4, ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), ClickGui.getInstance().b_alpha.getValue()));
                if (ClickGui.getInstance().outline.getValue() && ClickGui.getInstance().rainbow.getValue()) {
                    RenderUtil.drawRect((float) this.x, (float) this.y + 12.2F, (float) (this.x + this.width - 99), (float) (this.y + this.height) + var4 + 1, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB());
                    RenderUtil.drawRect((float) this.x + 99, (float) this.y + 12.2F, (float) (this.x + this.width), (float) (this.y + this.height) + var4 + 1, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB());
                    RenderUtil.drawRect((float) this.x, (float) (this.y + this.height + 2) + var4 - 1, (float) (this.x + this.width), (float) (this.y + this.height + 2) + var4, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB());
                } else if (ClickGui.getInstance().outline.getValue()) {
                    RenderUtil.drawRect((float) this.x, (float) this.y + 12.2F, (float) (this.x + this.width - 99), (float) (this.y + this.height) + var4 + 1, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
                    RenderUtil.drawRect((float) this.x + 99, (float) this.y + 12.2F, (float) (this.x + this.width), (float) (this.y + this.height) + var4 + 1, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
                    RenderUtil.drawRect((float) this.x, (float) (this.y + this.height + 2) + var4 - 1, (float) (this.x + this.width), (float) (this.y + this.height + 2) + var4, ColorUtil.toRGBA(ClickGui.getInstance().o_red.getValue(), ClickGui.getInstance().o_green.getValue(), ClickGui.getInstance().o_blue.getValue(), ClickGui.getInstance().o_alpha.getValue()));
                }
            }
            if (ClickGui.getInstance().topRect.getValue() == ClickGui.Rect.SQUARE) {
                Gui.drawRect(this.x, this.y + 3, this.x + this.width, this.y + this.height - 4, ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : color);
            } else if (ClickGui.getInstance().topRect.getValue() == ClickGui.Rect.ROUNDED) {
                RenderUtil.drawTopRoundedRect(this.x, this.y + 2, this.width, this.height - 5, ClickGui.getInstance().roundedness.getValue() == ClickGui.Roundedness.FULL ? 25 : ClickGui.getInstance().roundedness.getValue() == ClickGui.Roundedness.LARGE ? 20 : ClickGui.getInstance().roundedness.getValue() == ClickGui.Roundedness.MEDIUM ? 15 : ClickGui.getInstance().roundedness.getValue() == ClickGui.Roundedness.LITTLE ? 10 : ClickGui.getInstance().roundedness.getValue() == ClickGui.Roundedness.TINY ? 5 : 0, ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(ColorUtil.toRGBA(ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), ClickGui.getInstance().secondAlpha.getValue())));
            }
            if (ClickGui.getInstance().bottomRect.getValue() == ClickGui.Bottom.ROUNDED) {
                RenderUtil.drawBottomRoundedRect(this.x, (float) (this.y + this.height + 2) + var4 - 8 + (!open ? 3 : 0), this.width, this.height - 5, 15, ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(ColorUtil.toRGBA(ClickGui.getInstance().topRed.getValue(), ClickGui.getInstance().topGreen.getValue(), ClickGui.getInstance().topBlue.getValue(), ClickGui.getInstance().secondAlpha.getValue())));
            }
            if (ClickGui.getInstance().componentAlign.getValue() == ClickGui.Align.MIDDLE) {
                Client.textManager.drawStringWithShadow(ClickGui.getInstance().topRectTextBold.getValue() ? ChatFormatting.BOLD + this.getName() : this.getName(), (float) this.x + this.width / 2 - this.renderer.getStringWidth(this.getName()) / 2, (float) this.y - 4.0f - (float) ClientGui.getClickGui().getTextOffset() + 3, -1);
            } else {
                Client.textManager.drawStringWithShadow(ClickGui.getInstance().topRectTextBold.getValue() ? ChatFormatting.BOLD + this.getName() : this.getName(), (float) this.x + 3.0f, (float) this.y - 4.0f - (float) ClientGui.getClickGui().getTextOffset() + 3, -1);
            }
            if (this.open) {
                float y = (float) (this.getY() + this.getHeight()) - 3.0f;
                for (Item item : this.getItems()) {
                    Component.counter1[0] = counter1[0] + 1;
                    if (item.isHidden()) continue;
                    item.setLocation((float) this.x + 2.0f, y);
                    item.setWidth(this.getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += (float) item.getHeight() + 1.5f;
                }
            }
        } else if (ClickGui.getInstance().gui.getValue() == ClickGui.Gui.NEW){
            this.drag(mouseX, mouseY);
            counter1 = new int[]{1};
            float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
            int color = ColorUtil.toARGB(ClickGui.getInstance().newtopred.getValue(), ClickGui.getInstance().newtopgreen.getValue(), ClickGui.getInstance().newtopblue.getValue(), ClickGui.getInstance().newtopalpha.getValue());
            int thirdcolor = ColorUtil.toARGB(ClickGui.getInstance().newthirdRed.getValue(), ClickGui.getInstance().newthirdGreen.getValue(), ClickGui.getInstance().newthirdBlue.getValue(), ClickGui.getInstance().newthirdAlpha.getValue());
            Gui.drawRect(this.x, this.y - 1, this.x + this.width, this.y + this.height - 6,  color);
            Gui.drawRect(this.x, this.y + 11, this.x + this.width, this.y + this.height - 6, thirdcolor);
            if (this.open) {
                RenderUtil.drawRect(this.x, (float) this.y + 12.5f, this.x + this.width, (float) (this.y + this.height) + totalItemHeight, ColorUtil.toRGBA(10, 10, 10, ClickGui.getInstance().newbgAlpha.getValue()));
            }
            Client.textManager.drawStringWithShadow(this.getName(), (float) this.x + this.width / 2 - this.renderer.getStringWidth(this.getName()) / 2, (float) this.y - 4.0f - (float) ClientGui.getClickGui().getTextOffset(), -1);
            if (this.open) {
                float y = (float) (this.getY() + this.getHeight()) - 3.0f;
                for (Item item : this.getItems()) {
                    Component.counter1[0] = counter1[0] + 1;
                    if (item.isHidden()) continue;
                    item.setLocation((float) this.x + 2.0f, y);
                    item.setWidth(this.getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += (float) item.getHeight() + 1.5f;
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            ClientGui.getClickGui().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            return;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public final ArrayList<Item> getItems() {
        return this.items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0f;
        for (Item item : this.getItems()) {
            height += (float) item.getHeight() + 1.5f;
        }
        return height;
    }
}

