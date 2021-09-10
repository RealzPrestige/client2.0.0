package client.modules.player;

import client.Client;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.modules.combat.AutoArmor;
import client.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyEXP extends Module {
    private int armorCheck = 0;
    private int delay_count;
    int prvSlot;
    public Setting<Boolean> middleClick = this.register(new Setting<>("MiddleClick", false));
    public Setting<Bind> bind = this.register(new Setting<>("EXPBind:", new Bind(-1), v-> !middleClick.getCurrentState()));
    public Setting<Boolean> feet = this.register(new Setting<>("Feet", false));
    public Setting<Boolean> takeOff = this.register(new Setting<>("ArmorTakeOff", false));
    public Setting<Integer> threshold = this.register(new Setting<>("Threshold", 100, 0, 100, v-> this.takeOff.getCurrentState()));
    public Setting<Integer> enemyRange = this.register(new Setting<>("EnemyRange", 0, 0, 20, v-> this.takeOff.getCurrentState()));

    public KeyEXP() {
        super("KeyEXP", "SilentSwaps to throw EXP rapidly and mend your armor efficiently.", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (this.bind.getCurrentState().getKey() > -1) {
            if (Keyboard.isKeyDown(this.bind.getCurrentState().getKey()) && mc.currentScreen == null) {
                useXp();
                if (AutoArmor.getInstance().isEnabled()) {
                    this.armorCheck = 1;
                    return;
                } else {
                    return;
                }
            } else {
                if (this.armorCheck == 2) {
                    AutoArmor.getInstance().enable();
                    this.armorCheck = 0;

                }
            }
        } else if (middleClick.getCurrentState()) {
            if (Mouse.isButtonDown(2) && mc.currentScreen == null) {
                useXp();
            }
        }else if (this.bind.getCurrentState().getKey() < -1) {
                if (Mouse.isButtonDown(PlayerUtil.convertToMouse(this.bind.getCurrentState().getKey())) && mc.currentScreen == null) {
                    useXp();
            }
        }
    }

    @Override
    public void onEnable(){
        delay_count = 0;
    }
    private ItemStack getArmor(int first) {
        return mc.player.inventoryContainer.getInventory().get(first);
    }


    private void takeArmorOff() {
        int slot = 5;
        while (slot <= 8) {
            ItemStack item;
            item = getArmor(slot);
            double max_dam = item.getMaxDamage();
            double dam_left = item.getMaxDamage() - item.getItemDamage();
            double percent = (dam_left / max_dam) * 100;

            if (percent >= threshold.getCurrentState() && !item.equals(Items.AIR)) {
                if (!notInInv(Items.AIR)) {
                    return;
                }
                if (delay_count < 1) {
                    delay_count++;
                    return;
                }
                delay_count = 0;

                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);

            }
            slot++;
        }
    }

    private int HotbarEXP() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    public static EntityPlayer getClosestEnemy() {
        EntityPlayer closestPlayer = null;
        for (EntityPlayer player : KeyEXP.mc.world.playerEntities) {
            if (player == KeyEXP.mc.player || Client.friendManager.isFriend(player)) continue;
            if (closestPlayer == null) {
                closestPlayer = player;
                continue;
            }
            if (!(KeyEXP.mc.player.getDistanceSq(player) < KeyEXP.mc.player.getDistanceSq(closestPlayer)))
                continue;
            closestPlayer = player;
        }
        return closestPlayer;
    }

    private void useXp(){
        prvSlot = mc.player.inventory.currentItem;
        if (this.armorCheck == 1){
            AutoArmor.getInstance().disable();
            this.armorCheck = 2;
        }
        if (this.feet.getCurrentState()) {
            mc.player.connection.sendPacket(new Rotation(mc.player.rotationYaw, 90.0F, true));
        }
        mc.player.connection.sendPacket(new CPacketHeldItemChange(HotbarEXP()));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.inventory.currentItem = prvSlot;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(prvSlot));
        if (getClosestEnemy() == null){
            takeArmorOff();
        }
        if (getClosestEnemy() != null) {
            if (takeOff.getCurrentState() && (int) getClosestEnemy().getDistance(mc.player) > this.enemyRange.getCurrentState()) {
                takeArmorOff();
            }
        }
    }
    @Override
    public String hudInfoString() {
        if (this.bind.getCurrentState().getKey() > -1) {
            if (Keyboard.isKeyDown(this.bind.getCurrentState().getKey()) && mc.currentScreen == null) {
                return "Throwing";
            }
        }
        return null;
    }
    public Boolean notInInv(Item itemOfChoice) {
        int n;
        n = 0;
        if (itemOfChoice == mc.player.getHeldItemOffhand().getItem()) return true;

        for (int i = 35; i >= 0; i--) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == itemOfChoice) {
                return true;

            } else if (item != itemOfChoice) {
                n++;
            }
        }
        return n < 35;
    }
}
