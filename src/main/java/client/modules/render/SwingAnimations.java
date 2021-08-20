package client.modules.render;

import client.events.PacketEvent;
import client.modules.Module;
import client.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwingAnimations extends Module {
    private final Setting<Mode> mode = this.register(new Setting<>("OldAnimations", Mode.OneDotEight));
    private final Setting<Swing> swing = this.register(new Setting<>("Swing", Swing.Mainhand));
    private final Setting<Boolean> slow = this.register(new Setting<>("Slow", false));

    public SwingAnimations() {
        super("SwingAnimations", "Changes animations.", Module.Category.RENDER);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (this.swing.getValue() == Swing.Offhand) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        }
        if (this.mode.getValue() == Mode.OneDotEight && (double) mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
        if(this.slow.getValue()) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 255000));
        }
    }

    @Override
    public void onDisable() {
        if(this.slow.getValue()) {
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send send) {
        Object t = send.getPacket();
        if (t instanceof CPacketAnimation) {
            if (this.swing.getValue() == Swing.Disable) {
                send.setCanceled(true);
            }
        }
    }
    private enum Mode {Normal, OneDotEight}
    private enum Swing {Mainhand, Offhand, Disable}
}