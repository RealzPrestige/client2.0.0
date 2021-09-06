package client.mixin.mixins;

import client.Client;
import client.modules.client.Hud;
import client.modules.client.Notify;
import client.util.ColorUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={GuiNewChat.class}, priority=0x7FFFFFFE)
public class MixinGuiNewChat
        extends Gui {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadowMaybe(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (text.contains(Client.commandManager.getClientMessage()) && Notify.getInstance().enabled.getCurrentState() && Notify.getInstance().rainbow.getCurrentState()) {
                Client.textManager.drawString(text,x,y,ColorUtil.rainbowHud(Hud.getInstance().rainbowDelay.getCurrentState()).getRGB(),true);
        } else {
            return fontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return color;
    }


}

