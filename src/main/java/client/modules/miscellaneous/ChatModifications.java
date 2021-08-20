package client.modules.miscellaneous;

import client.events.PacketEvent;
import client.gui.impl.background.GuiChat;
import client.modules.Module;
import client.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifications extends Module {
    private static ChatModifications getInstance = new ChatModifications();
    public Setting<Boolean> suffix = this.register(new Setting("Suffix", true));
    public Setting<Boolean> customChat = this.register(new Setting("CustomChat", false));
    public Setting<Boolean> nameHighLight = this.register(new Setting("NameHighLight", false, v->customChat.getValue()));
    public Setting<Boolean> smoothChat = this.register(new Setting("SmoothChat", false, v->customChat.getValue()));
    public Setting<Double> xOffset = this.register(new Setting("XOffset", 0.0, 0.0, 600, v->smoothChat.getValue() && customChat.getValue()));
    public Setting<Double> yOffset = this.register(new Setting("YOffset", 0.0, 0.0, 30.0, v->smoothChat.getValue() && customChat.getValue()));
    public Setting<Double> vSpeed = this.register(new Setting("VSpeed", 30.0, 1.0, 100.0, v->smoothChat.getValue() && customChat.getValue()));
    public Setting<Double> vLength = this.register(new Setting("VLength", 10.0, 5.0, 100.0, v->smoothChat.getValue() && customChat.getValue()));
    public Setting<Double> vIncrements = this.register(new Setting("VIncrements", 1.0, 1.0, 5.0, v->smoothChat.getValue() && customChat.getValue()));
    public Setting<Type> type = this.register(new Setting("Type", Type.HORIZONTAL, v->smoothChat.getValue() && customChat.getValue()));
    public enum Type{HORIZONTAL, VERTICAL}
    public static GuiChat guiChatSmooth;
    public static GuiNewChat guiChat;

    public ChatModifications(){
        super("ChatModifications", "Modifies your chat", Category.MISC);
        this.setInstance();
    }

    public static ChatModifications getInstance() {
        if (getInstance == null) {
            getInstance = new ChatModifications();
        }
        return getInstance;
    }

    private void setInstance() {
        getInstance = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage && suffix.getValue()) {
            CPacketChatMessage packet = event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/")) {
                return;
            }
                    s = s + " \u23E3";
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }
    @Override
    public void onEnable() {
        guiChatSmooth = new GuiChat(mc);
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (mc).ingameGUI, guiChatSmooth, "field_73840_e");
    }

    @Override
    public void onDisable() {
        guiChat = new GuiNewChat(mc);
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (mc).ingameGUI, guiChat, "field_73840_e");
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            if (((SPacketChat) event.getPacket()).getType() == ChatType.GAME_INFO) {
                return;
            }
            String originalMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            String message = originalMessage;
            if (nameHighLight.getValue()) {
                try {
                    message = message.replace(mc.player.getName(), ChatFormatting.RED + mc.player.getName() + ChatFormatting.RESET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
}
