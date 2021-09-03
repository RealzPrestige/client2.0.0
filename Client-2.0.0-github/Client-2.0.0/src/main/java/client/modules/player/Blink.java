package client.modules.player;

import client.events.PacketEvent;
import client.modules.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class Blink extends Module {
    private final int entityId = -420;

    public Blink(){
        super("Blink", "Stops all packets from sending to the server.", Category.PLAYER);
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        event.setCanceled(true);
    }

    public void onLogout(){
        disable();
    }
    public void onEnable(){
            if (fullNullCheck()) return;
            GameProfile profile = new GameProfile(UUID.fromString("12cbdfad-33b7-4c07-aeac-01766e609482"), "FakePlayer");
            EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, profile);
            player.copyLocationAndAnglesFrom(mc.player);
            player.rotationYawHead = mc.player.rotationYawHead;
            player.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(entityId, player);
            return;
    }
    public void onDisable() {
        if (fullNullCheck()) return;
        mc.world.removeEntityFromWorld(entityId);
    }
}
