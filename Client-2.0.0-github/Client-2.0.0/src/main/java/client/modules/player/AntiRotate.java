package client.modules.player;

import client.events.PacketEvent;
import client.mixin.AccessorSPacketPlayerPosLook;
import client.modules.Module;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiRotate extends Module {

    public AntiRotate(){
        super("AntiRotate", "Prevents rotate packets.", Category.PLAYER);
    }
    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            AccessorSPacketPlayerPosLook packet = event.getPacket();
            packet.setPitch(this.mc.player.rotationPitch);
            packet.setYaw(this.mc.player.rotationYaw);
        }
        if (fullNullCheck()) {
            return;
        }
    }
}

