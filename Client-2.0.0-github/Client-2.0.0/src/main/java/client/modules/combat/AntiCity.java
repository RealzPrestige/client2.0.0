package client.modules.combat;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.*;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;


public class AntiCity extends Module {
    public Setting<Boolean> packet = register(new Setting( "Packet" , true));
    public Setting<Boolean> rotate = register(new Setting( "Rotate" , false));
    public AntiCity(){
        super("AntiCity", "", Category.COMBAT);
    }

    public void onUpdate(){
        BlockPos pos = EntityUtil.getPlayerPos(mc.player);
        int preSlot = AutoTrap.mc.player.inventory.currentItem;
        int whileSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if(EntityUtil.isSafe(mc.player)) {
            if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.north().north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().north().down()).getBlock() == Blocks.BEDROCK)) {
                if(whileSlot > -1) {
                AutoTrap.mc.player.inventory.currentItem = whileSlot;
                AutoTrap.mc.playerController.updateController();
                BlockUtil.placeBlock(pos.north().north(), EnumHand.MAIN_HAND, this.rotate.getValue(), packet.getValue(), false);
                AutoTrap.mc.player.inventory.currentItem = preSlot;
                AutoTrap.mc.playerController.updateController();
                }
            }
            if (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.east().east()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east().east().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east().east().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east().east().down()).getBlock() == Blocks.BEDROCK)) {
                if(whileSlot > -1) {
                    AutoTrap.mc.player.inventory.currentItem = whileSlot;
                    AutoTrap.mc.playerController.updateController();
                    BlockUtil.placeBlock(pos.east().east(), EnumHand.MAIN_HAND, this.rotate.getValue(), packet.getValue(), false);
                    AutoTrap.mc.player.inventory.currentItem = preSlot;
                    AutoTrap.mc.playerController.updateController();
                }
            }
            if (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.south().south()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.south().south().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south().south().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south().south().down()).getBlock() == Blocks.BEDROCK)) {
                if (whileSlot > -1) {
                    AutoTrap.mc.player.inventory.currentItem = whileSlot;
                    AutoTrap.mc.playerController.updateController();
                    BlockUtil.placeBlock(pos.south().south(), EnumHand.MAIN_HAND, this.rotate.getValue(), packet.getValue(), false);
                    AutoTrap.mc.player.inventory.currentItem = preSlot;
                    AutoTrap.mc.playerController.updateController();
                }
            }
            if (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().west().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.west().west().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west().west().down()).getBlock() == Blocks.BEDROCK)) {
                if(whileSlot > -1) {
                    AutoTrap.mc.player.inventory.currentItem = whileSlot;
                    AutoTrap.mc.playerController.updateController();
                    BlockUtil.placeBlock(pos.west().west(), EnumHand.MAIN_HAND, this.rotate.getValue(), packet.getValue(), false);
                    AutoTrap.mc.player.inventory.currentItem = preSlot;
                    AutoTrap.mc.playerController.updateController();
                }
            }
        }
    }
}
