package client.modules.visual;


import client.modules.Module;
import client.gui.impl.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;

public class SwingAnimations extends Module {
    private final Setting<Switch> switchSetting = this.register(new Setting<>("Switch", Switch.ONEDOTEIGHT));
    private enum Switch {ONEDOTNINE, ONEDOTEIGHT}
    private final Setting<Swing> swing = this.register(new Setting<>("Swing", Swing.MAINHAND));
    private enum Swing {MAINHAND, OFFHAND, CANCEL}

    private final Setting<Speed> speed = this.register(new Setting<>("Speed", Speed.NORMAL));
    private enum Speed {SLOW, NORMAL, FAST}

    public SwingAnimations() {
        super("SwingAnimations", "Tweaks the way your hands swing.", Module.Category.VISUAL);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (this.swing.getCurrentState() == Swing.OFFHAND) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        }
        if (this.swing.getCurrentState() == Swing.MAINHAND) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        }

        if (switchSetting.getCurrentState() == Switch.ONEDOTEIGHT && (double) mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
        if(speed.getCurrentState() == Speed.SLOW) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 10));
            mc.player.removePotionEffect(MobEffects.HASTE);
        } else if(speed.getCurrentState() == Speed.NORMAL){
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
            mc.player.removePotionEffect(MobEffects.HASTE);
        } else if(speed.getCurrentState() == Speed.FAST){
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
            mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 10));
        }
    }

    @Override
    public void onDisable() {
        mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
        mc.player.removePotionEffect(MobEffects.HASTE);
    }
    public String hudInfoString(){
        return swing.currentEnumName();
    }
}