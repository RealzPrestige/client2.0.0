package client.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateWalkingPlayerEvent
        extends EventProcessor {
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
}



