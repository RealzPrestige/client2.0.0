package client.modules.combat;

import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.*;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HolefillerRewrite extends Module {
    public static final List<net.minecraft.block.Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static final List<net.minecraft.block.Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    int blockSlot;
    HashMap<BlockPos, Integer> filledFadeHoles = new HashMap();
    HashSet<BlockPos> fillableHoles = Sets.newHashSet();
    public Setting<Mode> mode = register(new Setting<>("FillMode", Mode.NORMAL));
    public enum Mode{NORMAL, SMART}
    public Setting<PlaceMode> placeMode = register(new Setting<>("PlaceMode", PlaceMode.VANILLA));
    public enum PlaceMode{VANILLA, PACKET}
    public Setting<SwingMode> swingMode = register(new Setting<>("SwingMode", SwingMode.MAINHAND));
    public enum SwingMode{MAINHAND, OFFHAND, NONE}
    public Setting<Block> block = register(new Setting<>("Block", Block.OBSIDIAN));
    public enum Block{OBSIDIAN, ECHEST, WEB}
    public Setting<OnGroundChecks> onGroundChecks = register(new Setting<>("OnGroundChecks", OnGroundChecks.NONE));
    public enum OnGroundChecks{SELF, TARGET, BOTH, NONE}
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> autoDisable = register(new Setting<>("AutoDisable", false));
    public Setting<Boolean> autoSwitch = register(new Setting<>("AutoSwitch", false));
    public Setting<Boolean> silentSwitch = register(new Setting<>("SilentSwitch", false, v-> autoSwitch.getCurrentState()));
    public Setting<Boolean> doubles = register(new Setting<>("DoubleHoles", false));
    public Setting<Boolean> throughWalls = register(new Setting<>("ThroughWalls", false));;
    public Setting<Boolean> swordCheck = register(new Setting<>("SwordCheck", false));
    public Setting<Boolean> targetUnSafe = register(new Setting<>("TargetUnSafe", false, v-> mode.getCurrentState() == Mode.SMART));
    public Setting<Integer> smartRange = register(new Setting<>("Smart-Range", 5, 0, 6, v-> mode.getCurrentState() == Mode.SMART));
    public Setting<Integer> targetRange = register(new Setting<>("TargetRange", 9, 1, 15, v-> mode.getCurrentState() == Mode.SMART));
    public Setting<Integer> rangeX = register(new Setting<>("X-Range", 5, 1, 6));
    public Setting<Integer> rangeY = register(new Setting<>("Y-Range", 5, 1, 6));
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
    public HolefillerRewrite(){
        super("HoleFillerRewrite", "Fills safe spots (near enemies).", Category.COMBAT);
    }

    public void onTick() {
            fillableHoles.clear();
            findFillableHoles();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(render.getCurrentState()) {
            if(renderMode.getCurrentState() == RenderMode.FADE) {
                for (Map.Entry<BlockPos, Integer> entry : filledFadeHoles.entrySet()) {
                    filledFadeHoles.put(entry.getKey(), entry.getValue() - (fadeStep.getCurrentState() / 10));
                    if (entry.getValue() <= endAlpha.getCurrentState()) {
                        filledFadeHoles.remove(entry.getKey());
                        return;
                    }
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), entry.getValue()),true, new Color(outlineRed.getCurrentState(), outlineGreen.getCurrentState(), outlineBlue.getCurrentState(), entry.getValue()), lineWidth.getCurrentState(), outline.getCurrentState(), box.getCurrentState(), entry.getValue(), true);
               }
            } else {
                for(BlockPos pos : fillableHoles){
                    RenderUtil.drawBoxESP(pos, new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState()),true, new Color(outlineRed.getCurrentState(), outlineGreen.getCurrentState(), outlineBlue.getCurrentState(), outlineAlpha.getCurrentState()), lineWidth.getCurrentState(), outline.getCurrentState(), box.getCurrentState(), alpha.getCurrentState(), true);
                }
            }
        }
    }

    public void onUpdate(){
        for(BlockPos pos : fillableHoles){
            switch(block.getCurrentState()){
                case OBSIDIAN:
                    blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                    break;
                case ECHEST:
                    blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                    break;
                case WEB:
                    blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WEB));
                    break;
            }
            switch (onGroundChecks.getCurrentState()){
                case SELF:
                    if(!mc.player.onGround){
                        return;
                    }
                    break;
                case TARGET:
                    if(!getPlayerTarget(targetRange.getCurrentState()).onGround){
                        return;
                    }
                    break;
                case BOTH:
                    if(!getPlayerTarget(targetRange.getCurrentState()).onGround){
                        return;
                    }
                    if(!mc.player.onGround){
                        return;
                    }
                    break;
                case NONE:
                    break;
            }
        if(mode.getCurrentState() == Mode.NORMAL){
            if(swordCheck.getCurrentState() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD){
                return;
            }
            if (this.blockSlot == -1) {
                return;
            }
            int lastSlot = mc.player.inventory.currentItem;
            if(autoSwitch.getCurrentState()) {
                if (silentSwitch.getCurrentState()) {
                    mc.player.inventory.currentItem = blockSlot;
                    mc.playerController.updateController();

                } else {
                    mc.player.inventory.currentItem = blockSlot;
                }
            }
            if(throughWalls.getCurrentState()) {
                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                        placeBlock(pos, EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false, swingMode.getCurrentState() != SwingMode.NONE, swingMode.getCurrentState() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                            if (!filledFadeHoles.containsKey(pos)) {
                                filledFadeHoles.put(pos, startAlpha.getCurrentState());
                        }
                    }
                }
            } else if(canBlockBeSeen(pos)) {
                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                        placeBlock(pos, EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false, swingMode.getCurrentState() != SwingMode.NONE, swingMode.getCurrentState() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                            if (!filledFadeHoles.containsKey(pos)) {
                                filledFadeHoles.put(pos, startAlpha.getCurrentState());
                            }
                    }
                }
            }
                if (autoSwitch.getCurrentState() && silentSwitch.getCurrentState()) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
            }
                if(autoDisable.getCurrentState()){
                    disable();
                }
          }

            if(mode.getCurrentState() == Mode.SMART){
                if(swordCheck.getCurrentState() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD){
                    return;
                }
                if (this.blockSlot == -1) {
                    return;
                }
                int lastSlot = mc.player.inventory.currentItem;
                blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if(autoSwitch.getCurrentState()) {
                    if (silentSwitch.getCurrentState()) {
                        mc.player.inventory.currentItem = blockSlot;
                        mc.playerController.updateController();
                    } else {
                        mc.player.inventory.currentItem = blockSlot;
                    }
                }
                if(getPlayerTarget(targetRange.getCurrentState()) != null && Objects.requireNonNull(getPlayerTarget(targetRange.getCurrentState())).getDistanceSq(pos) < smartRange.getCurrentState()) {
                    if(targetUnSafe.getCurrentState()) {
                        if(getPlayerTarget(targetRange.getCurrentState()) != null && !EntityUtil.isSafe(getPlayerTarget(targetRange.getCurrentState()))) {
                            if(throughWalls.getCurrentState()) {
                                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                    placeBlock(pos, EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false, swingMode.getCurrentState() != SwingMode.NONE, swingMode.getCurrentState() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getCurrentState());
                                       }
                                    }
                                }
                            } else if (canBlockBeSeen(pos)){
                                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                    placeBlock(pos, EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false, swingMode.getCurrentState() != SwingMode.NONE, swingMode.getCurrentState() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getCurrentState());
                                       }
                                    }
                                }
                            }
                        }
                    } else {
                        if(throughWalls.getCurrentState()) {
                                if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                placeBlock(pos, EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false, swingMode.getCurrentState() != SwingMode.NONE, swingMode.getCurrentState() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getCurrentState());
                                   }
                                }
                            }
                        } else if (canBlockBeSeen(pos)){
                                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                    placeBlock(pos, EnumHand.MAIN_HAND, rotate.getCurrentState(), placeMode.getCurrentState() == PlaceMode.PACKET, false, swingMode.getCurrentState() != SwingMode.NONE, swingMode.getCurrentState() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getCurrentState());
                                    }
                                }
                            } else {
                                if (render.getCurrentState() && renderMode.getCurrentState() == RenderMode.FADE) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getCurrentState());
                                    }
                                }
                            }
                        }
                    }
                }
                if(autoSwitch.getCurrentState() && silentSwitch.getCurrentState()) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
                if(autoDisable.getCurrentState()){
                    disable();
                }
            }
        }
    }


    public void findFillableHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - rangeX.getCurrentState(); x < playerPos.getX() + rangeX.getCurrentState(); ++x) {
            for (int z = playerPos.getZ() - rangeX.getCurrentState(); z < playerPos.getZ() + rangeX.getCurrentState(); ++z) {
                for (int y = playerPos.getY() + rangeY.getCurrentState(); y > playerPos.getY() - rangeY.getCurrentState(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            fillableHoles.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                            fillableHoles.add(pos);
                        }
                        if(doubles.getCurrentState()) {
                            if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.north());
                            } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.north());
                            } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.west());
                            } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.west());
                        }
                    }
                }
            }
        }
    }

    public static EntityPlayer getPlayerTarget(int targetRange){
        EntityPlayer target = EntityUtil.getTarget(targetRange);
        if(target != null){
            return target;
        }
        return null;
    }

    public static boolean canBlockBeSeen(final BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) == null;
    }
    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking, boolean swing, EnumHand renderHand) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        net.minecraft.block.Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            PlayerUtil.faceVector(hitVec, true);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        if(swing) {
            mc.player.swingArm(renderHand);
        }
        mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }
    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }
    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = BlockUtil.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    @Override
    public String hudInfoString() {
        return "Auto";
    }

}
