package client.modules.movement;

import client.events.JesusEvent;
import client.events.PacketEvent;
import client.modules.Module;
import client.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus extends Module {

    public Jesus() {
        super("Jesus", "Allows you to walk on water.", Category.MOVEMENT);
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = event.getPacket();
            if (!EntityUtil.isInLiquid() && EntityUtil.isOnLiquid(0.05f) && EntityUtil.checkCollide() && mc.player.ticksExisted % 3 == 0) {
                packet.y -= 0.05f;
            }
        }
    }

    @SubscribeEvent
    public void onJesus(JesusEvent event) {
        if (event.getStage() == 0 && mc.world != null && mc.player != null && EntityUtil.checkCollide() && !(mc.player.motionY >= (double) 0.1f) && (double) event.getPos().getY() < mc.player.posY - (double) 0.05f) {
            event.setBoundingBox(Block.FULL_BLOCK_AABB);
            event.setCanceled(true);
        }
        if (nullCheck()) {
            return;
        }
    }
}
