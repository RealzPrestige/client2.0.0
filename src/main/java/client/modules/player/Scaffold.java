package client.modules.player;

import client.events.UpdateWalkingPlayerEvent;
import client.modules.Module;
import client.util.BlockUtil;
import client.util.EntityUtil;
import client.util.InventoryUtil;
import client.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold extends Module {
    private final Timer timer = new Timer();

    public Scaffold() {
        super("Scaffold", "Places Blocks underneath you.", Module.Category.PLAYER);
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @SubscribeEvent
    public void onPlayerJump(UpdateWalkingPlayerEvent event) {
        BlockPos playerBlock;
        if (this.isOff() || Scaffold.fullNullCheck() || event.getStage() == 0) {
            return;
        }
        if (!Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.timer.reset();
        }
        if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).add(0, -1, 0))) {
            if (BlockUtil.isValidBlock(playerBlock.add(0, -2, 0))) {
                this.placeBlockBelowFeet(playerBlock.add(0, -1, 0), EnumFacing.UP);
            } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
                this.placeBlockBelowFeet(playerBlock.add(0, -1, 0), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 0))) {
                this.placeBlockBelowFeet(playerBlock.add(0, -1, 0), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, -1))) {
                this.placeBlockBelowFeet(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                this.placeBlockBelowFeet(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
            } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                    this.placeBlockBelowFeet(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
                }
                this.placeBlockBelowFeet(playerBlock.add(1, -1, 1), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
                    this.placeBlockBelowFeet(playerBlock.add(0, -1, 1), EnumFacing.WEST);
                }
                this.placeBlockBelowFeet(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                    this.placeBlockBelowFeet(playerBlock.add(0, -1, 1), EnumFacing.SOUTH);
                }
                this.placeBlockBelowFeet(playerBlock.add(1, -1, 1), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                    this.placeBlockBelowFeet(playerBlock.add(0, -1, 1), EnumFacing.EAST);
                }
                this.placeBlockBelowFeet(playerBlock.add(1, -1, 1), EnumFacing.NORTH);
            }
        }
    }

    public void placeBlockBelowFeet(BlockPos posI, EnumFacing face) {
        BlockPos pos = posI;
        if (face == EnumFacing.UP) {
            pos = pos.add(0, -1, 0);
        } else if (face == EnumFacing.NORTH) {
            pos = pos.add(0, 0, 1);
        } else if (face == EnumFacing.SOUTH) {
            pos = pos.add(0, 0, -1);
        } else if (face == EnumFacing.EAST) {
            pos = pos.add(-1, 0, 0);
        } else if (face == EnumFacing.WEST) {
            pos = pos.add(1, 0, 0);
        }
        int oldSlot = Scaffold.mc.player.inventory.currentItem;
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Scaffold.mc.player.inventory.getStackInSlot(i);
            if (InventoryUtil.isNull(stack) || !(stack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock())
                continue;
            newSlot = i;
            break;
        }
        if (newSlot == -1) {
            return;
        }
        boolean crouched = false;
        if (!Scaffold.mc.player.isSneaking() && BlockUtil.blackList.contains(Scaffold.mc.world.getBlockState(pos).getBlock())) {
            Scaffold.mc.player.connection.sendPacket(new CPacketEntityAction(Scaffold.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            crouched = true;
        }
        if (!(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
            Scaffold.mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
            Scaffold.mc.player.inventory.currentItem = newSlot;
            Scaffold.mc.playerController.updateController();
        }
        if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            Scaffold.mc.player.motionX *= 0.3;
            Scaffold.mc.player.motionZ *= 0.3;
            Scaffold.mc.player.jump();
            if (this.timer.passedMs(1500L)) {
                Scaffold.mc.player.motionY = -0.28;
                this.timer.reset();
            }
        }
        Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, pos, face, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
        Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
        Scaffold.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        Scaffold.mc.player.inventory.currentItem = oldSlot;
        Scaffold.mc.playerController.updateController();
        if (crouched) {
            Scaffold.mc.player.connection.sendPacket(new CPacketEntityAction(Scaffold.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
}

