package client.modules.miscellaneous;

import client.modules.Module;
import client.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Mouse;

public class MiddleClickPearl extends Module {

    private boolean clicked = false;

    public MiddleClickPearl() {
        super("MiddleClickPearl", "Throws a pearl when middle clicked", Category.MISC);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
            if(Mouse.isButtonDown(2)) {
                if(!clicked) {
                    throwPearl();
                }
                clicked = true;
            } else {
                clicked = false;
        }
    }

    private void throwPearl() {
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if(pearlSlot != -1 || offhand) {
            int oldslot = mc.player.inventory.currentItem;
            if(!offhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            mc.playerController.processRightClick(mc.player, mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if(!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }
}