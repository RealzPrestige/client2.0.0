package client.events;

import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends EventProcessor {
    
    private final Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

}