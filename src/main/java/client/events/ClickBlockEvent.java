package client.events;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClickBlockEvent extends EventProcessor{
    final BlockPos pos;
    final EnumFacing facing;
    final int stage;

    public ClickBlockEvent(final int stage, final BlockPos pos, final EnumFacing facing) {
        this.stage = stage;
        this.pos = pos;
        this.facing = facing;
    }


    public final BlockPos getPos() {
        return this.pos;
    }

    public final EnumFacing getFacing() {
        return this.facing;
    }

    public final int getStage() {
        return this.stage;
    }
}
