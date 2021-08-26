package client.mixin;

import client.Client;
import client.modules.client.ClickGui;
import client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value={GuiNewChat.class}, priority = 999999999)
public class MixinGuiNewChat
        extends Gui {
    @Shadow
    @Final
    public List<ChatLine> drawnChatLines;
    private ChatLine chatLine;

    @Redirect(method={"drawChat"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (text.contains("\u00a7+")) {
            int[] arrayOfInt = {1};
            char[] stringToCharArray = text.toCharArray();
            float f = 0.0F;
            for (char c : stringToCharArray) {
                Client.textManager.drawString(String.valueOf(c), x + f,y, ColorUtil.rainbowHud(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getCurrentState()).getRGB(), true);
                f += Client.textManager.getStringWidth(String.valueOf(c));
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
            } else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return 0;
    }
}

