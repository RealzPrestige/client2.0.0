package client.modules.player;

import client.events.PacketEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Interactions extends Module {
    private static Interactions INSTANCE = new Interactions();
    public Setting<Boolean> liquid = this.register(new Setting("LiquidPlace", false));
    public Setting<Boolean> heightLimit = this.register(new Setting("HeightLimit", false));
    public Setting<Boolean> reach = this.register(new Setting("Reach", false));
    public Setting<Float> reachAmount = this.register(new Setting<Object>("ReachAmount", 6.0f, 0.0f, 10.0f, v-> reach.getCurrentState()));

    public Interactions(){
        super("Interactions", "Changes the way you interact with things.", Category.PLAYER);
        this.setInstance();
    }

    public static Interactions getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Interactions();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayerTryUseItemOnBlock packet;
        if (heightLimit.getCurrentState() && event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && (packet = event.getPacket()).getPos().getY() >= 255 && packet.getDirection() == EnumFacing.UP) {
            packet.placedBlockDirection = EnumFacing.DOWN;
        }
    }
}
