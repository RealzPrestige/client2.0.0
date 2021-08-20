package client.modules.miscellaneous;

import client.modules.Module;
import net.minecraft.client.gui.GuiGameOver;

public class AutoRespawn
        extends Module {

    public AutoRespawn() {
        super("AutoRespawn", "Respawns you if you die.", Category.MISC);
    }

    public void onUpdate() {
        if(mc.currentScreen instanceof GuiGameOver) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }
}