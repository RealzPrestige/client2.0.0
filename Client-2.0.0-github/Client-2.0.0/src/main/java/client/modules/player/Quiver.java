package client.modules.player;

import client.command.Command;
import client.modules.Module;
import client.util.InventoryUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;

public class Quiver extends Module {
    private int timer = 0;
    private int stage = 1;
    private int returnSlot = -1;

    public Quiver(){
        super("Quiver", "Shoots arrows at yourself", Category.PLAYER);
    }

    @Override
    public void onDisable() {
        timer = 0;
        this.stage = 0;
        mc.gameSettings.keyBindUseItem.pressed = false;
        if (returnSlot != -1) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, returnSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            returnSlot = -1;
        }
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;
        if (mc.currentScreen != null) return;
        InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
        if (InventoryUtil.findHotbarBlock(ItemBow.class) == -1) {
            this.disable();
            Command.sendMessage(ChatFormatting.RED + "No bow found!");
            return;
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, -90, mc.player.onGround));
        if (stage == 0) {
            if (!mapArrows()) {
                this.disable();
                Command.sendMessage(ChatFormatting.GREEN + "All effects applied!");
                return;
            }
            this.stage++;
        } else if (stage == 1) {
            this.stage++;
            timer++;
            return;
        } else if (stage == 2) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            timer = 0;
            this.stage++;
        } else if (stage == 3) {
            if (timer > 4) {
                this.stage++;
            }
        } else if (stage == 4) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.resetActiveHand();
            mc.gameSettings.keyBindUseItem.pressed = false;
            timer = 0;
            this.stage++;
        } else if (stage == 5) {
            if (timer < 10) {
                timer++;
                return;
            }
            this.stage = 0;
            timer = 0;
        }
        timer++;
    }

    private boolean mapArrows() {
        for (int a = 9; a < 45; a++) {
            if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemTippedArrow) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    if (!mc.player.isPotionActive(MobEffects.STRENGTH)) {
                        switchTo(a);
                        return true;
                    }
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    if (!mc.player.isPotionActive(MobEffects.SPEED)) {
                        switchTo(a);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getDisplayInfo() {
        return "Stage: " + stage;
    }

    private void switchTo(int from) {
        if (from == 9) return;
        returnSlot = from;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }
}