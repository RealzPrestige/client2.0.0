package client.events;

import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent
        extends EventProcessor {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}

