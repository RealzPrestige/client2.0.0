package client.modules.core;


import client.DiscordPresence;
import client.modules.Module;

public class RPC extends Module {
    public static RPC INSTANCE;

    public RPC(){
        super("RPC", "Displays client 2.0.0 as your Discord Rich Presence.", Category.CORE);
        INSTANCE = this;
    }

    @Override
    public void onLogin() {
        if (RPC.INSTANCE.isOn()){
            RPC.INSTANCE.disable();
            RPC.INSTANCE.enable();
        }
    }

    @Override
    public void onEnable() {
        DiscordPresence.start();
    }

    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }
}

