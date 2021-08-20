package client.modules.misc;

import client.Client;
import client.modules.Module;
import client.setting.Setting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;


public class TabTweaks extends Module {
    private static TabTweaks INSTANCE = new TabTweaks();

    public Setting<Integer> size = this.register(new Setting("Size", 250, 1, 1000));

    public TabTweaks() {
        super("TabTweaks", "Makes Tab larger", Module.Category.MISC);
        this.setInstance();
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String name;
        String string = name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (Client.friendManager.isFriend(name)) {
            return "\u00a7b" + name;
        }
        return name;
    }


    public static TabTweaks getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new TabTweaks();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

