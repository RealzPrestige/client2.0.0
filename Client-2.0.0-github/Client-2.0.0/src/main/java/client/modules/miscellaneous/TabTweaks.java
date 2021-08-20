package client.modules.miscellaneous;

import client.modules.Module;
import client.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;


public class TabTweaks extends Module {
    private static TabTweaks INSTANCE = new TabTweaks();
    public Setting<Boolean> pingDisplay = this.register(new Setting("Ping", true));
    public Setting<Boolean> coloredPing = this.register(new Setting("Colored", true));
    public Setting<Integer> size = this.register(new Setting("Size", 250, 1, 1000));

    public TabTweaks() {
        super("TabTweaks", "Lets you do stuff with tab", Module.Category.MISC);
        this.setInstance();
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if(getINSTANCE().pingDisplay.getValue()) {
            if(getINSTANCE().coloredPing.getValue()) {
                if (networkPlayerInfoIn.getResponseTime() <= 50) {
                        return name + ChatFormatting.GREEN + " " + (getINSTANCE().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                    } else if (networkPlayerInfoIn.getResponseTime() <= 100) {
                        return name + ChatFormatting.GOLD + " " + (getINSTANCE().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                } else if (networkPlayerInfoIn.getResponseTime() <= 150) {
                    return name + ChatFormatting.RED + " " + (getINSTANCE().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                } else if (networkPlayerInfoIn.getResponseTime() <= 1000) {
                    return name + ChatFormatting.DARK_RED + " " + (getINSTANCE().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                }
            } else {
                return name + ChatFormatting.GRAY + " " + (getINSTANCE().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");

            }
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

