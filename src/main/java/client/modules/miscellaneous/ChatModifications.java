package client.modules.miscellaneous;

import client.events.PacketEvent;
import client.gui.impl.background.GuiChat;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.TextUtil;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatModifications extends Module {
    private static ChatModifications getInstance = new ChatModifications();
    public Setting<Boolean> suffix = this.register(new Setting("Suffix", true));
    public Setting<Boolean> space = this.register(new Setting("TimeStamps", true));
    public Setting<Boolean> customChat = this.register(new Setting("CustomChat", false));
    public Setting<Boolean> alphaStep = this.register(new Setting("TimeAlphastep", false));
    public Setting<Integer> R = this.register(new Setting<>("AlphastepR", 255, 0, 255, v -> this.alphaStep.getValue()));
    public Setting<Integer> G = this.register(new Setting<>("AlphastepG", 255, 0, 255, v -> this.alphaStep.getValue()));
    public Setting<Integer> B = this.register(new Setting<>("AlphastepB", 255, 0, 255, v -> this.alphaStep.getValue()));
    public Setting<TextUtil.Color> bracket = this.register(new Setting<Object>("Bracket", TextUtil.Color.WHITE, v -> this.timeStamps.getCurrentState() != TextUtil.Color.NONE));
    public Setting<TextUtil.Color> timeStamps = this.register(new Setting<>("Time", TextUtil.Color.NONE));
    public Setting<Boolean> rainbowTimeStamps = this.register(new Setting<Object>("RainbowTimeStamps", Boolean.FALSE, v -> this.timeStamps.getCurrentState() != TextUtil.Color.NONE));
    public Setting<Boolean> nameHighLight = this.register(new Setting("NameHighLight", false, v -> customChat.getCurrentState()));
    public Setting<Boolean> smoothChat = this.register(new Setting("SmoothChat", false, v -> customChat.getCurrentState()));
    public Setting<Double> xOffset = this.register(new Setting("XOffset", 0.0, 0.0, 600, v -> smoothChat.getCurrentState() && customChat.getCurrentState()));
    public Setting<Double> yOffset = this.register(new Setting("YOffset", 0.0, 0.0, 30.0, v -> smoothChat.getCurrentState() && customChat.getCurrentState()));
    public Setting<Double> vSpeed = this.register(new Setting("VSpeed", 30.0, 1.0, 100.0, v -> smoothChat.getCurrentState() && customChat.getCurrentState()));
    public Setting<Double> vLength = this.register(new Setting("VLength", 10.0, 5.0, 100.0, v -> smoothChat.getCurrentState() && customChat.getCurrentState()));
    public Setting<Double> vIncrements = this.register(new Setting("VIncrements", 1.0, 1.0, 5.0, v -> smoothChat.getCurrentState() && customChat.getCurrentState()));
    public Setting<Type> type = this.register(new Setting("Type", Type.HORIZONTAL, v -> smoothChat.getCurrentState() && customChat.getCurrentState()));

    public enum Type {HORIZONTAL, VERTICAL}

    public static GuiChat guiChatSmooth;
    public static GuiNewChat guiChat;
    public String message;

    public ChatModifications() {
        super("ChatModifications", "Modifies your chat to look cleaner.", Category.MISC);
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
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage && suffix.getCurrentState()) {
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
        if (event.getStage() == 0 && this.timeStamps.getCurrentState() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat) event.getPacket()).isSystem()) {
                return;
            }
            String originalMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    public String getTimeString(String message) {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        if (this.rainbowTimeStamps.getCurrentState()) {
            String timeString = "<" + date + ">" + (this.space.getCurrentState() ? " " : "");
            StringBuilder builder = new StringBuilder(timeString);
            builder.insert(0, "\u00a7+");
        }
        return (this.bracket.getCurrentState() == TextUtil.Color.NONE ? "" : TextUtil.coloredString("<", this.bracket.getCurrentState())) + TextUtil.coloredString(date, this.timeStamps.getCurrentState()) + (this.bracket.getCurrentState() == TextUtil.Color.NONE ? "" : TextUtil.coloredString(">", this.bracket.getCurrentState())) + (this.space.getCurrentState() ? " " : "") + "\u00a7r";
    }

    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
}
