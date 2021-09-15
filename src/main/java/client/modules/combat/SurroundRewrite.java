package client.modules.combat;

import client.command.Command;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.modules.movement.Step;
import client.util.*;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SurroundRewrite extends Module {
    int maxBlocks;
    int itemSlot;
    HashMap<BlockPos, Integer> surroundFadeBlock = new HashMap<>();
    HashSet<BlockPos> staticBlocks = Sets.newHashSet();
    public Setting<SpeedFactor> speedFactor = register(new Setting<>("SpeedFactor", SpeedFactor.UPDATE));
    public enum SpeedFactor{UPDATE, TICK}
    public Setting<PlaceMode> placeMode = register(new Setting<>("PlaceMode", PlaceMode.VANILLA));
    public enum PlaceMode{VANILLA, PACKET}
    public Setting<SwingMode> swingMode = register(new Setting<>("SwingMode", SwingMode.MAINHAND));
    public enum SwingMode{MAINHAND, OFFHAND}
    public Setting<DisableMode> disableMode = register(new Setting<>("DisableMode", DisableMode.SMART));
    public enum DisableMode{ONCOMPLETE, MOTION, ONGROUND, SMART, STEPHEIGHT}
    public Setting<BlockSelection> blocks = register(new Setting<>("Blocks", BlockSelection.OBSIDIAN));
    public enum BlockSelection{OBSIDIAN, ECHEST, AUTO}
    public Setting<Boolean> bottomBlocks = register(new Setting<>("BottomBlocks", false));
    public Setting<Boolean> bottomBlocksExtend = register(new Setting<>("BottomBlocksExtend", false, v-> bottomBlocks.getCurrentState()));
    public Setting<Boolean> maxBlock = register(new Setting<>("MaxBlocks", false));
    public Setting<Integer> maxBlocksAmount = register(new Setting<>("MaxBlocksAmount", 10, 0, 20, v-> maxBlock.getCurrentState()));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> render = register(new Setting<>("Render", false));
    public Setting<RenderMode> renderMode = register(new Setting<>("RenderMode", RenderMode.STATIC, v-> render.getCurrentState()));
    public enum RenderMode{STATIC, FADE}
    public Setting<Boolean> box = register(new Setting<>("Box", false, v-> render.getCurrentState()));
    public Setting<Integer> red = register(new Setting<>("BoxRed", 255, 0, 255, v-> render.getCurrentState() && box.getCurrentState()));
    public Setting<Integer> green = register(new Setting<>("BoxGreen", 255, 0, 255, v-> render.getCurrentState() && box.getCurrentState()));
    public Setting<Integer> blue = register(new Setting<>("BoxBlue", 255, 0, 255, v-> render.getCurrentState() && box.getCurrentState()));
    public Setting<Integer> alpha = register(new Setting<>("BoxAlpha", 255, 0, 255, v-> render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC && box.getCurrentState()));
    public Setting<Boolean> outline = register(new Setting<>("Outline", false, v-> render.getCurrentState()));
    public Setting<Integer> outlineRed = register(new Setting<>("OutlineRed", 255, 0, 255, v-> render.getCurrentState() && outline.getCurrentState()));
    public Setting<Integer> outlineGreen = register(new Setting<>("OutlineGreen", 255, 0, 255, v-> render.getCurrentState() && outline.getCurrentState()));
    public Setting<Integer> outlineBlue = register(new Setting<>("OutlineBlue", 255, 0, 255, v-> render.getCurrentState() && outline.getCurrentState()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("OutlineAlpha", 255, 0, 255, v-> render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC && outline.getCurrentState()));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 1.0f, 0.0f, 5.0f, v-> render.getCurrentState() && outline.getCurrentState()));
    public Setting<Integer> startAlpha = register(new Setting<>("StartAlpha", 255, 0, 255, v-> render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE));
    public Setting<Integer> endAlpha = register(new Setting<>("EndAlpha", 0, 0, 255, v-> render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE));
    public Setting<Integer> fadeStep = register(new Setting<>("FadeStep", 20, 10, 100, v-> render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE));

    public SurroundRewrite(){
        super("SurroundRewrite", "Surrounds you with Obsidian.", Category.COMBAT);
    }
    public void onUpdate(){
        if(speedFactor.getCurrentState() == SpeedFactor.UPDATE){
            doSurround();
        }
    }
    public void onTick(){
        if(speedFactor.getCurrentState() == SpeedFactor.TICK){
            doSurround();
        }
        maxBlocks = 0;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(render.getCurrentState()) {
            if(renderMode.getCurrentState() == RenderMode.FADE) {
                for (Map.Entry<BlockPos, Integer> entry : surroundFadeBlock.entrySet()) {
                    surroundFadeBlock.put(entry.getKey(), entry.getValue() - (fadeStep.getCurrentState() / 10));
                    if (entry.getValue() <= endAlpha.getCurrentState()) {
                        surroundFadeBlock.remove(entry.getKey());
                        return;
                    }
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), entry.getValue()),true, new Color(outlineRed.getCurrentState(), outlineGreen.getCurrentState(), outlineBlue.getCurrentState(), entry.getValue()), lineWidth.getCurrentState(), outline.getCurrentState(), box.getCurrentState(), entry.getValue(), true);
                }
            } else {
                for(BlockPos pos : staticBlocks){
                    RenderUtil.drawBoxESP(pos, new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState()),true, new Color(outlineRed.getCurrentState(), outlineGreen.getCurrentState(), outlineBlue.getCurrentState(), outlineAlpha.getCurrentState()), lineWidth.getCurrentState(), outline.getCurrentState(), box.getCurrentState(), alpha.getCurrentState(), true);
                }
            }
        }
    }

    public void doSurround(){
        BlockPos pos = PlayerUtil.getPlayerPos();
        BlockPos center = PlayerUtil.getCenterPos(pos.getX(), pos.getY(), pos.getZ());
        int originalSlot = mc.player.inventory.currentItem;
        switch(blocks.getCurrentState()){
            case OBSIDIAN: {
                itemSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                break;
            }
            case ECHEST: {
                itemSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
                break;
            }
            case AUTO: {
                if(InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1){
                    itemSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                } else if(InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1) {
                    itemSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
                }
            }
        }
        if(itemSlot == -1){
            Command.sendMessage(ChatFormatting.WHITE + "<Client 2.0.0>" + ChatFormatting.BOLD +  " Surround: " + ChatFormatting.RESET + ChatFormatting.GRAY+ "No blocks found, Disabling.");
            disable();
        }
        switch(disableMode.getCurrentState()) {
            case SMART: {
                if (mc.player.motionY > 0.2 && !mc.player.onGround && mc.player.stepHeight > 0.6 || Step.getInstance().isEnabled()) {
                    disable();
                }
                break;
            }
            case MOTION: {
                if (mc.player.motionY > 0.2) {
                    disable();
                }
                break;
            }
            case ONGROUND: {
                if (!mc.player.onGround) {
                    disable();
                }
                break;
            }
            case ONCOMPLETE: {
                if (mc.world.getBlockState(center.north()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(center.east()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(center.south()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(center.west()).getBlock() == Blocks.OBSIDIAN) {
                    disable();
                }
                break;
            }
            case STEPHEIGHT: {
                if (mc.player.stepHeight > 1) {
                    disable();
                }
                break;
            }
        }
        if(bottomBlocksExtend.getCurrentState()) {
            if (mc.world.getBlockState(center.down().north()).getBlock() == Blocks.AIR) {
                if (maxBlock.getCurrentState()) {
                    if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                        mc.player.inventory.currentItem = itemSlot;
                        mc.playerController.updateController();
                        BlockUtil.placeBlock(center.down().north(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                        mc.player.inventory.currentItem = originalSlot;
                        mc.playerController.updateController();
                        ++maxBlocks;
                        if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                            surroundFadeBlock.put(center.down().north(), startAlpha.getCurrentState());
                        } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                            staticBlocks.add(center.down().north());
                        }
                    } else {
                        return;
                    }
                } else {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.down().north(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.down().north(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                        staticBlocks.add(center.down().north());
                    }
                }
            }

            if (mc.world.getBlockState(center.down().east()).getBlock() == Blocks.AIR) {
                if (maxBlock.getCurrentState()) {
                    if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                        mc.player.inventory.currentItem = itemSlot;
                        mc.playerController.updateController();
                        BlockUtil.placeBlock(center.down().east(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                        mc.player.inventory.currentItem = originalSlot;
                        mc.playerController.updateController();
                        ++maxBlocks;
                        if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                            surroundFadeBlock.put(center.down().east(), startAlpha.getCurrentState());
                        } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                            staticBlocks.add(center.down().east());
                        }
                    } else {
                        return;
                    }
                } else {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.down().east(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.down().east(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                        staticBlocks.add(center.down().east());
                    }
                }
            }

            if (mc.world.getBlockState(center.down().south()).getBlock() == Blocks.AIR) {
                if (maxBlock.getCurrentState()) {
                    if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                        mc.player.inventory.currentItem = itemSlot;
                        mc.playerController.updateController();
                        BlockUtil.placeBlock(center.down().south(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                        mc.player.inventory.currentItem = originalSlot;
                        mc.playerController.updateController();
                        ++maxBlocks;
                        if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                            surroundFadeBlock.put(center.down().south(), startAlpha.getCurrentState());
                        } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                            staticBlocks.add(center.down().south());
                        }
                    } else {
                        return;
                    }
                } else {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.down().south(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.down().south(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                        staticBlocks.add(center.down().south());
                    }
                }
            }
            if (mc.world.getBlockState(center.down().west()).getBlock() == Blocks.AIR) {
                if (maxBlock.getCurrentState()) {
                    if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                        mc.player.inventory.currentItem = itemSlot;
                        mc.playerController.updateController();
                        BlockUtil.placeBlock(center.down().west(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                        mc.player.inventory.currentItem = originalSlot;
                        mc.playerController.updateController();
                        ++maxBlocks;
                        if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                            surroundFadeBlock.put(center.down().west(), startAlpha.getCurrentState());
                        } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                            staticBlocks.add(center.down().west());
                        }
                    } else {
                        return;
                    }
                } else {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.down().west(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.down().west(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                        staticBlocks.add(center.down().west());
                    }
                }
            }
        }

        //Get Bottom
        if(mc.world.getBlockState(center.down()).getBlock() == Blocks.AIR) {
            if (maxBlock.getCurrentState()) {
                if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.down(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.down(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                        staticBlocks.add(center.down());
                    }
                } else {
                    return;
                }
            } else {
                mc.player.inventory.currentItem = itemSlot;
                mc.playerController.updateController();
                BlockUtil.placeBlock(center.down(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
                ++maxBlocks;
                if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                    surroundFadeBlock.put(center.down(), startAlpha.getCurrentState());
                } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC) {
                    staticBlocks.add(center.down());
                }
            }
        }
            //Get north:
        if(mc.world.getBlockState(center.north()).getBlock() == Blocks.AIR){
            if(maxBlock.getCurrentState()) {
                if(maxBlocks < maxBlocksAmount.getCurrentState()) {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.north(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.north(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                        staticBlocks.add(center.north());
                    }
                } else {
                    return;
                }
            } else {
                mc.player.inventory.currentItem = itemSlot;
                mc.playerController.updateController();
                BlockUtil.placeBlock(center.north(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
                ++maxBlocks;
                if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                    surroundFadeBlock.put(center.north(), startAlpha.getCurrentState());
                } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                    staticBlocks.add(center.north());
                }
            }
        }
        //Get east:
        if(mc.world.getBlockState(center.east()).getBlock() == Blocks.AIR){
            if(maxBlock.getCurrentState()) {
                if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.east(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.east(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                        staticBlocks.add(center.east());
                    }
                } else {
                    return;
                }
            } else {
                mc.player.inventory.currentItem = itemSlot;
                mc.playerController.updateController();
                BlockUtil.placeBlock(center.east(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
                ++maxBlocks;
                if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                    surroundFadeBlock.put(center.east(), startAlpha.getCurrentState());
                } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                    staticBlocks.add(center.east());
                }
            }
        }
        //Get south:
        if(mc.world.getBlockState(center.south()).getBlock() == Blocks.AIR){
            if(maxBlock.getCurrentState()) {
                if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.south(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.south(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                        staticBlocks.add(center.south());
                    }

                } else {
                    return;
                }
            }  else {
                mc.player.inventory.currentItem = itemSlot;
                mc.playerController.updateController();
                BlockUtil.placeBlock(center.south(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
                ++maxBlocks;
                if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                    surroundFadeBlock.put(center.south(), startAlpha.getCurrentState());
                } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                    staticBlocks.add(center.south());
                }
            }
        }
        //Get west:
        if(mc.world.getBlockState(center.west()).getBlock() == Blocks.AIR){
            if(maxBlock.getCurrentState()) {
                if (maxBlocks < maxBlocksAmount.getCurrentState()) {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.playerController.updateController();
                    BlockUtil.placeBlock(center.west(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                    mc.player.inventory.currentItem = originalSlot;
                    mc.playerController.updateController();
                    ++maxBlocks;
                    if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                        surroundFadeBlock.put(center.west(), startAlpha.getCurrentState());
                    } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                        staticBlocks.add(center.west());
                    }
                } else {
                    return;
                }
            } else {
                mc.player.inventory.currentItem = itemSlot;
                mc.playerController.updateController();
                BlockUtil.placeBlock(center.west(), EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
                ++maxBlocks;
                if(render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                    surroundFadeBlock.put(center.west(), startAlpha.getCurrentState());
                } else if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.STATIC){
                    staticBlocks.add(center.west());
                }
            }
        }
    }
    @Override
    public String hudInfoString() {
        if(maxBlocks > 0){
            return "Placing";
        }
        if(EntityUtil.isSafe(mc.player)){
            return "Safe";
        } else {
            return "Unsafe";
        }
    }
}
