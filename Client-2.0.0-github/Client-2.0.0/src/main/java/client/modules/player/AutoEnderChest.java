package client.modules.player;

import client.modules.Module;
import client.util.EntityUtil;
import client.util.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class AutoEnderChest extends Module {

    public AutoEnderChest() {
        super("AutoEnderChest", "Farms enderschests automatically for you.", Category.PLAYER);
    }

    public void onEnable() {
        BlockPos pos = EntityUtil.getPlayerPosWithEntity().add(0, -1, 0);
        if (EntityUtil.isSafe(mc.player)) {
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString("Make sure to have echests in your hotbar and hold down Left Click."), 1);
            Burrow.getInstance().swapBlock = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            Burrow.getInstance().enable();
            mc.player.rotationPitch = 90;
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
            this.disable();
        }
    }
}
