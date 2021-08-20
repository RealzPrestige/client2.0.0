package client.modules.visual;


import client.modules.Module;
import client.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;

public class SwingAnimations extends Module {
    private final Setting<Switch> switchSetting = this.register(new Setting<>("Switch", Switch.ONEDOTEIGHT));
    private enum Switch {ONEDOTNINE, ONEDOTEIGHT}
    private final Setting<Swing> swing = this.register(new Setting<>("Swing", Swing.MAINHAND));
    private enum Swing {MAINHAND, OFFHAND}

    private final Setting<Speed> speed = this.register(new Setting<>("Speed", Speed.NORMAL));
    private enum Speed {SLOW, NORMAL, FAST}

    public SwingAnimations() {
        super("SwingAnimations", "Changes animations.", Module.Category.VISUAL);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (this.swing.getValue() == Swing.OFFHAND) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        }
        if (this.swing.getValue() == Swing.MAINHAND) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        }

        if (switchSetting.getValue() == Switch.ONEDOTEIGHT && (double) mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
        if(speed.getValue() == Speed.SLOW) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 10));
            mc.player.removePotionEffect(MobEffects.HASTE);
        } else if(speed.getValue() == Speed.NORMAL){
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
            mc.player.removePotionEffect(MobEffects.HASTE);
        } else if(speed.getValue() == Speed.FAST){
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
            mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 10));
        }
    }

    @Override
    public void onDisable() {
        mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
        mc.player.removePotionEffect(MobEffects.HASTE);
    }
}