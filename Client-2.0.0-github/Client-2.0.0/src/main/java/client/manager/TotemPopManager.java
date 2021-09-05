package client.manager;

import client.events.NewPopEvent;
import client.events.PacketEvent;
import client.util.Util;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import java.util.HashMap;
import java.util.Map;

public class TotemPopManager {
    public final Map<String, Integer> popMap;

    public TotemPopManager() {
        this.popMap = new HashMap<>();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35) {
                final Entity entity = packet.getEntity(Util.mc.world);
                if (entity instanceof EntityPlayer) {
                    if (entity.equals(Util.mc.player)) {
                        return;
                    }
                    final EntityPlayer player = (EntityPlayer)entity;
                    this.handlePop(player);
                }
            }
        }
    }

    public void handlePop(final EntityPlayer player) {
        if (!this.popMap.containsKey(player.getName())) {
            MinecraftForge.EVENT_BUS.post(new NewPopEvent(player.getName(), 1, player.getEntityId()));
            this.popMap.put(player.getName(), 1);
        }
        else {
            this.popMap.put(player.getName(), this.popMap.get(player.getName()) + 1);
            MinecraftForge.EVENT_BUS.post(new NewPopEvent(player.getName(), this.popMap.get(player.getName()), player.getEntityId()));
        }
    }

    public void onTick() {
        for (final EntityPlayer player : Util.mc.world.playerEntities) {
            if (player == Util.mc.player || !this.popMap.containsKey(player.getName()) || (!player.isDead && player.isEntityAlive() && player.getHealth() > 0.0f)) {
                continue;
            }
            this.popMap.remove(player.getName());
        }
    }
}
