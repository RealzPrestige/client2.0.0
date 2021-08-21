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
    public Setting<Float> horizontal = this.register(new Setting("Horizontal", 0.0F, 0.0F, 100.0F, v-> explosions.getValue()));
    public Setting<Float> vertical = this.register(new Setting("Vertical", 0.0F, 0.0F, 100.0F, v-> explosions.getValue()));
    public Setting<Boolean> noPush = this.register(new Setting("NoPush", false));
    public Setting<Boolean> blocks = this.register(new Setting("Blocks", false));

    public Velocity() {
        super("Velocity", "Allows you to control your velocity", Category.MOVEMENT);
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
                    if (this.horizontal.getValue() == 0.0f && this.vertical.getValue() == 0.0f) {
                        event.setCanceled(true);
                        return;
                    }
                    velocity.motionX *= this.horizontal.getValue() / 100;
                    velocity.motionY *= this.vertical.getValue() / 100;
                    velocity.motionZ *= this.horizontal.getValue() / 100;
                }
            }
            if (this.explosions.getValue() && event.getPacket() instanceof SPacketExplosion) {
                if (this.horizontal.getValue() == 0.0f && this.vertical.getValue() == 0.0f) {
                    event.setCanceled(true);
                    return;
                }
                final SPacketExplosion velocity2;
                final SPacketExplosion sPacketExplosion4 = (velocity2 = event.getPacket());
                sPacketExplosion4.motionX *= this.horizontal.getValue();
                final SPacketExplosion sPacketExplosion5 = velocity2;
                sPacketExplosion5.motionY *= this.vertical.getValue();
                final SPacketExplosion sPacketExplosion6 = velocity2;
                sPacketExplosion6.motionZ *= this.horizontal.getValue();
            }
        }
    }

    @SubscribeEvent
    public void onPush(final PushEvent event) {
        if (event.getStage() == 1 && this.blocks.getValue()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityCollision(final EntityCollisionEvent event) {
        if (noPush.getValue()) {
            event.setCanceled(true);
        }
    }
}