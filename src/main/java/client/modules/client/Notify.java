package client.modules.client;

import client.Client;
import client.events.ClientEvent;
import client.modules.Module;
import client.setting.Setting;
import client.util.TextUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notify extends Module {
    private static Notify INSTANCE = new Notify();
    public Setting<Boolean> chatMessages = register(new Setting("ToggleMessages", true));
    public Setting<Boolean> rainbowPrefix = register(new Setting("RainbowPrefix", true));
    public Setting<String> command = register(new Setting("NotifyString", "Client 2.0.0"));
    public Setting<String> commandBracket = register(new Setting("Bracket", "<"));
    public Setting<String> commandBracket2 = register(new Setting("Bracket2", ">"));
    public Setting<TextUtil.Color> commandColor = register(new Setting("NameColor", TextUtil.Color.WHITE));
    public Setting<TextUtil.Color> bracketColor = register(new Setting("BracketColor", TextUtil.Color.WHITE));
    public Notify() {
        super("Notify", "Notifies things in chat.", Module.Category.CORE);
        setInstance();
    }

    public static Notify getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Notify();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    public void onLoad() {
        Client.commandManager.setClientMessage(getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 &&
                equals(event.getSetting().getFeature()))
            Client.commandManager.setClientMessage(getCommandMessage());
    }
    public String getCommandMessage() {
        if ( this.rainbowPrefix.getPlannedValue ( ) ) {
            StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
            stringBuilder.insert(0, "\u00a7+");
            stringBuilder.append("\u00a7r");
            return stringBuilder.toString();
        }
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue()) + "\u00a7r";
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return this.commandBracket.getValue() + this.command.getValue() + this.commandBracket2.getValue() + "\u00a7r";
    }
    public enum Preset {
        CUSTOM,
        PRESET
    }
    public enum Presets {
        LINE,
        DOUBLELINE,
        TRIPLELINE,
        DOUBLEARROW
    }
}
