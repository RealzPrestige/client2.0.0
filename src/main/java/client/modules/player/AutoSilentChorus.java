package client.modules.player;

import client.modules.Module;
import client.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class AutoSilentChorus extends Module {

    int delay;

    public AutoSilentChorus(){
        super("AutoSilentChorus", "Choruses using SilentSwitch and packets.", Category.PLAYER);
    }

    public void onTick(){
            ++delay;
    }

    public void onEnable() {
        int chorusSlot = InventoryUtil.findHotbarBlock(ItemChorusFruit.class);
        if(chorusSlot != -1){
            delay = 0;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(this.getChorusSlot()));
        }
    }
    public void onUpdate(){
        if(delay > 68){
            this.disable();
        }
        if(delay < 68) {    
            mc.gameSettings.keyBindUseItem.pressed = true;
        } else {
            mc.gameSettings.keyBindUseItem.pressed = false;
        }
    }

    public void onDisable(){
        mc.gameSettings.keyBindUseItem.pressed = false;
        int postChorusSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(postChorusSlot));
    }

    private int getChorusSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != Items.CHORUS_FRUIT) continue;
            return i;
        }
        return -1;
    }
}
