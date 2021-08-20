package client.events;

import net.minecraft.entity.player.EntityPlayer;

public class TotemPopEvent extends EventProcessor {
    private final EntityPlayer entity;

    public TotemPopEvent(EntityPlayer entity) {
        this.entity = entity;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }
}

