package client.modules.movement;

import client.events.EntityCollisionEvent;
import client.events.PacketEvent;
import client.events.PushEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {
    public Setting<Boolean> explosions = this.register(new Setting("Explosions", false));
    public Setting<Float> horizontal = this.register(new Setting("Horizontal", 0.0F, 0.0F, 100.0F, v-> explosions.getCurrentState()));
    public Setting<Float> vertical = this.register(new Setting("Vertical", 0.0F, 0.0F, 100.0F, v-> explosions.getCurrentState()));
    public Setting<Boolean> noPush = this.register(new Setting("NoPush", false));
    public Setting<Boolean> blocks = this.register(new Setting("Blocks", false));

    public Velocity() {
        super("Velocity", "Allows you to control your velocity.", Category.MOVEMENT);
    }

    public void onLogin(){
        if(this.isEnabled()){
            this.disable();
            this.enable();
        }
    }
    @SubscribeEvent
    public void onPacketReceived(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && Velocity.mc.player != null) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                final SPacketEntityVelocity velocity;
                velocity = event.getPacket();
                if (velocity.getEntityID() == Velocity.mc.player.entityId) {
                    if (this.horizontal.getCurrentState() == 0.0f && this.vertical.getCurrentState() == 0.0f) {
                        event.setCanceled(true);
                        return;
                    }
                    velocity.motionX *= this.horizontal.getCurrentState() / 100;
                    velocity.motionY *= this.vertical.getCurrentState() / 100;
                    velocity.motionZ *= this.horizontal.getCurrentState() / 100;
                }
            }
            if (this.explosions.getCurrentState() && event.getPacket() instanceof SPacketExplosion) {
                if (this.horizontal.getCurrentState() == 0.0f && this.vertical.getCurrentState() == 0.0f) {
                    event.setCanceled(true);
                    return;
                }
                final SPacketExplosion velocity2;
                final SPacketExplosion sPacketExplosion4 = (velocity2 = event.getPacket());
                sPacketExplosion4.motionX *= this.horizontal.getCurrentState();
                final SPacketExplosion sPacketExplosion5 = velocity2;
                sPacketExplosion5.motionY *= this.vertical.getCurrentState();
                final SPacketExplosion sPacketExplosion6 = velocity2;
                sPacketExplosion6.motionZ *= this.horizontal.getCurrentState();
            }
        }
    }

    @SubscribeEvent
    public void onPush(final PushEvent event) {
        if (event.getStage() == 1 && this.blocks.getCurrentState()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityCollision(final EntityCollisionEvent event) {
        if (noPush.getCurrentState()) {
            event.setCanceled(true);
        }
    }
    public String hudInfoString(){
        return "H: " + horizontal + " | V:" + vertical;
    }
}