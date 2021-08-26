package client.modules.player;

import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class FastPlace extends Module {

    public Setting <Boolean> exp = this.register(new Setting("Exp", true));
    public Setting <Boolean> crazyFastExpExploit = this.register(new Setting("UltraSpeed", false));
    public Setting <Integer> packets = this.register(new Setting("Packets", 1, 0, 20, v-> crazyFastExpExploit.getCurrentState()));
    public Setting <Boolean> crystal = this.register(new Setting("Crystal", true));
    public FastPlace(){
        super("FastPlace", "Changes the delay of things", Category.PLAYER);
    }

    @Override
    public void onUpdate(){
        if(fullNullCheck()){
            return;
        }
        if (InventoryUtil.holdingItem(ItemExpBottle.class) && this.exp.getCurrentState()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(ItemEndCrystal.class) && this.crystal.getCurrentState()){
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.crazyFastExpExploit.getCurrentState()) {
            if ((InventoryUtil.holdingItem(ItemExpBottle.class))) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(HotbarEXP()));
                for (int i = 0; i < packets.getCurrentState(); i++) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            }
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
}
