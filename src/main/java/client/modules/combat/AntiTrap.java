package client.modules.combat;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.EntityUtil;
import client.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AntiTrap extends Module {
    boolean canPlaceNorth;
    boolean canPlaceEast;
    boolean canPlaceSouth;
    boolean canPlaceWest;
    public Setting<SpeedFactor> speedFactor = register(new Setting("SpeedFactor", SpeedFactor.UPDATE));
    public enum SpeedFactor{UPDATE, TICK}

    public AntiTrap(){
        super("AntiTrap", "Prevents you from being trapped.", Category.COMBAT);
    }

    public void onUpdate(){
        if(speedFactor.getCurrentState() == SpeedFactor.UPDATE){
            placeCrystal();
        }
    }

    public void onTick(){
        if(speedFactor.getCurrentState() == SpeedFactor.TICK){
            placeCrystal();
        }
    }

    public void placeCrystal(){
        BlockPos pos = PlayerUtil.getPlayerPos();
        if(mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL){
            return;
        }
        if(EntityUtil.isSafe(mc.player) &&
                (mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.up().north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.up().north()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.up().east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.up().east()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.up().south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.up().south()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(pos.up().west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.up().west()).getBlock() == Blocks.BEDROCK)){
            if(mc.world.getBlockState(pos.north().up().up()).getBlock() == Blocks.AIR &&
                    mc.world.getBlockState(pos.north().up().up().up()).getBlock() == Blocks.AIR){
                canPlaceNorth = true;
             } else {
                canPlaceNorth = false;
            }
            if(mc.world.getBlockState(pos.east().up().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east().up().up().up()).getBlock() == Blocks.AIR){
                canPlaceEast = true;
             } else {
                canPlaceEast = false;
            }
            if(mc.world.getBlockState(pos.south().up().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.south().up().up().up()).getBlock() == Blocks.AIR){
                canPlaceSouth = true;
            } else {
                canPlaceSouth = false;
            }
            if(mc.world.getBlockState(pos.west().up().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up().up().up()).getBlock() == Blocks.AIR){
                canPlaceWest = true;
            } else {
                canPlaceWest = false;
            }
        }
        if(canPlaceNorth) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.north().up(), EnumFacing.UP, EnumHand.OFF_HAND, 0.5f, 0.5f, 0.5f));
        } else if (canPlaceEast) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.east().up(), EnumFacing.UP,EnumHand.OFF_HAND, 0.5f, 0.5f, 0.5f));
        }else if(canPlaceSouth){
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.south().up(), EnumFacing.UP, EnumHand.OFF_HAND, 0.5f, 0.5f, 0.5f));
        } else if(canPlaceWest){
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.west().up(), EnumFacing.UP, EnumHand.OFF_HAND, 0.5f, 0.5f, 0.5f));
        }
    }
}
