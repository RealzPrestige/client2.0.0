package client.modules.player;

import client.events.CloseInventoryEvent;
import client.events.PacketEvent;
import client.modules.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XCarry extends Module {

    public XCarry() {
        super("XCarry", "", Category.PLAYER);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = event.getPacket();
            event.setCanceled(packet.windowId == 0);
        }
    }

    @SubscribeEvent
    public void onInventoryClose(CloseInventoryEvent event) {
        event.setCanceled(true);
    }
}