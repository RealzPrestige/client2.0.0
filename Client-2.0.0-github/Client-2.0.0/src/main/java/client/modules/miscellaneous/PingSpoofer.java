package client.modules.miscellaneous;

import client.events.PacketEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.MathUtil;
import client.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PingSpoofer extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Object>("DelayMS", 20, 0, 1000));
    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue();
    private final Timer timer = new Timer();
    private boolean receive = true;

    public PingSpoofer() {
        super("PingSpoofer", "Makes it look like you have higher ping than you really do.", Category.MISC);
    }

    @Override
    public void onUpdate() {
        this.clearQueue();
    }

    @Override
    public void onDisable() {
        this.clearQueue();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (this.receive && mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && event.getStage() == 0 && event.getPacket() instanceof CPacketKeepAlive) {
            this.packets.add(event.getPacket());
            event.setCanceled(true);
        }
    }

    public void clearQueue() {
        if (mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && (this.timer.passedMs(this.delay.getValue()))) {
            double limit = MathUtil.getIncremental(Math.random() * 10.0, 1.0);
            this.receive = false;
            int i = 0;
            while ((double) i < limit) {
                Packet<?> packet = this.packets.poll();
                if (packet != null) {
                    mc.player.connection.sendPacket(packet);
                }
                ++i;
            }
            this.timer.reset();
            this.receive = true;
        }
    }
}

