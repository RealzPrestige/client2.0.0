package client.modules.client;

import client.Client;
import client.events.ClientEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.TextUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notify extends Module {
    private static Notify INSTANCE = new Notify();

    public Setting<Boolean> friendMessages = register(new Setting("FriendMessages", false));
    public Setting<Boolean> chatMessages = register(new Setting("ToggleMessages", true));
    public Setting<Boolean> rainbow = this.register(new Setting("RainbowToggleMessages", false, v -> chatMessages.getCurrentState()));
    public Setting<String> command = register(new Setting("NotifyString", "Client 2.0.0"));
    public Setting<String> commandBracket = register(new Setting("Bracket", "<"));
    public Setting<String> commandBracket2 = register(new Setting("Bracket2", ">"));
    public Setting<TextUtil.Color> commandColor = register(new Setting("NameColor", TextUtil.Color.WHITE));
    public Setting<TextUtil.Color> bracketColor = register(new Setting("BracketColor", TextUtil.Color.WHITE));
    public Notify() {
        super("Notify", "Notifies modules in chat.", Module.Category.CORE);
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
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
    }
}
