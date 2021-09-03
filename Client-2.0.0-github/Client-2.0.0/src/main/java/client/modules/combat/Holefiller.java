package client.modules.combat;

import client.events.Render3DEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.*;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.ToDoubleFunction;

public class Holefiller extends Module {
    public Setting<Integer> bpt;
    public Setting<Float> range;
    public Setting<Float> distance;
    public Setting<Boolean> rotate;
    public Setting<Boolean> packet;
    public Setting<Boolean> render;

    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    public Setting<Integer> boxAlpha;
    public Setting<Integer> lineWidth;
    public Setting<Boolean> fov;
    public Setting<Boolean> box;
    public Setting<Boolean> outline;
    public Setting<Integer> cRed;
    public Setting<Integer> cGreen;
    public Setting<Integer> cBlue;
    public Setting<Integer> cAlpha;
    private int placeAmount;
    private int blockSlot;
    public EntityPlayer target;
    private static final BlockPos[] surroundOffset;

    public Holefiller() {
        super("HoleFiller", "Fills holes near enemies.", Category.COMBAT);
        bpt = (Setting<Integer>)this.register(new Setting("Blocks Per Tick", 10, 1, 20));
        range = (Setting<Float>)this.register(new Setting("Range", 5.0f, 1.0f, 6.0f));
        distance = (Setting<Float>)this.register(new Setting("Smart range", 2.0f, 1.0f, 7.0f));
        rotate = (Setting<Boolean>)this.register(new Setting("Rotate", false));
        packet = (Setting<Boolean>)this.register(new Setting("Packet", true));
        render = (Setting<Boolean>)this.register(new Setting("Render", true));

        red = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255));
        green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255));
        blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255));
        alpha = (Setting<Integer>)this.register(new Setting("Alpha", 120, 0, 255));
        boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 120, 0, 255));
        lineWidth = (Setting<Integer>)this.register(new Setting("LineWidth", 1, 1, 5));
        fov = (Setting<Boolean>)this.register(new Setting("inFov", true));
        box = (Setting<Boolean>)this.register(new Setting("Box", true));
        outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        cRed = (Setting<Integer>)this.register(new Setting("OutlineRed", 255, 0, 255));
        cGreen = (Setting<Integer>)this.register(new Setting("OutlineGreen", 255, 0, 255));
        cBlue = (Setting<Integer>)this.register(new Setting("OutlineBlue", 255, 0, 255));
        cAlpha = (Setting<Integer>)this.register(new Setting("OutlineAlpha", 255, 0, 255));
        placeAmount = 0;
        blockSlot = -1;
    }


    @Subscribe
    public void onUpdate() {
        if (this.check()) {
            final EntityPlayer currentTarget = EntityUtil.getTarget(10.0f);
            target = currentTarget;
            if (currentTarget == null) {
                return;
            }
            if (EntityUtil.isInHole(currentTarget)) {
                return;
            }
            final List<BlockPos> holes = this.calcHoles();
            holes.sort(Comparator.comparingDouble((ToDoubleFunction<? super BlockPos>)currentTarget::getDistanceSq));
            if (holes.size() == 0) {
                return;
            }
            final int lastSlot = this.mc.player.inventory.currentItem;
            this.blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (this.blockSlot == -1) {
                return;
            }
            BlockPos hole = null;
            for (final BlockPos pos : holes) {
                if (currentTarget.getDistance(pos.getX(), pos.getY(), pos.getZ()) >= this.distance.getCurrentState()) {
                    continue;
                }
                hole = pos;
            }
            if (hole != null) {
                Objects.requireNonNull(this.mc.getConnection()).sendPacket(new CPacketHeldItemChange(this.blockSlot));
                this.placeBlock(hole);
                this.mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            }
        }
    }


    private void placeBlock(final BlockPos pos) {
        if (this.bpt.getCurrentState() > this.placeAmount) {
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getCurrentState(), this.packet.getCurrentState(), false);
            ++this.placeAmount;
        }
    }

    private boolean check() {
        if (this.mc.player == null) {
            return false;
        }
        this.placeAmount = 0;
        this.blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        return true;
    }

    public List<BlockPos> calcHoles() {
        final ArrayList<BlockPos> safeSpots = new ArrayList<>();
        final List<BlockPos> positions = BlockUtil.getCock(this.range.getCurrentState(), false);
        for (final BlockPos pos : positions) {
            if (BlockUtil.isPositionPlaceable(pos, true) != 1 && this.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && this.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                if (this.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                    boolean isSafe = true;
                    for (final BlockPos offset : Holefiller.surroundOffset) {
                        final Block block = this.mc.world.getBlockState(pos.add(offset)).getBlock();
                        if (block != Blocks.BEDROCK) {
                            if (block != Blocks.OBSIDIAN) {
                                isSafe = false;
                            }
                        }
                    }
                    if (isSafe) {
                        safeSpots.add(pos);
                    }
                }
            }
        }
        return safeSpots;
    }

    static {
        surroundOffset = BlockUtil.toBlockPos(BlockUtil.getOffsets(0, true));
    }
    @Override
    public void onRender3D(Render3DEvent event) {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() -  5; x < playerPos.getX() + this.range.getCurrentState(); ++x) {
            for (int z = playerPos.getZ() - 5; z < playerPos.getZ() + this.range.getCurrentState(); ++z) {
                for (int y = playerPos.getY() + 5; y > playerPos.getY() - 5; --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) || pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !isPosInFov(pos) && this.fov.getCurrentState())
                        continue;
                    if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                        RenderUtil.drawBoxESP(pos, new Color(this.red.getCurrentState(), this.green.getCurrentState(), this.blue.getCurrentState(), this.alpha.getCurrentState()), this.outline.getCurrentState(), new Color(this.cRed.getCurrentState(), this.cGreen.getCurrentState(), this.cBlue.getCurrentState(), this.cAlpha.getCurrentState()), this.lineWidth.getCurrentState().floatValue(), this.outline.getCurrentState(), this.box.getCurrentState(), this.boxAlpha.getCurrentState(), true);
                        continue;
                    }
                    if (isBlockUnSafe(mc.world.getBlockState(pos.down()).getBlock()) || isBlockUnSafe(mc.world.getBlockState(pos.east()).getBlock()) || isBlockUnSafe(mc.world.getBlockState(pos.west()).getBlock()) || isBlockUnSafe(mc.world.getBlockState(pos.south()).getBlock()) || isBlockUnSafe(mc.world.getBlockState(pos.north()).getBlock()))
                        continue;
                    RenderUtil.drawBoxESP(pos, new Color(this.red.getCurrentState(), this.green.getCurrentState(), this.blue.getCurrentState(), this.alpha.getCurrentState()), this.outline.getCurrentState(), new Color(this.cRed.getCurrentState(), this.cGreen.getCurrentState(), this.cBlue.getCurrentState(), this.cAlpha.getCurrentState()), this.lineWidth.getCurrentState().floatValue(), this.outline.getCurrentState(), this.box.getCurrentState(), this.boxAlpha.getCurrentState(), true);
                }
            }
        }
    }
    private static final List<Block> unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
    private static boolean isBlockUnSafe(Block block) { return !unSafeBlocks.contains(block); }
    private static Boolean isPosInFov(BlockPos pos) { int dirnumber = EntityUtil.getDirection4D();if (dirnumber == 0 && (double) pos.getZ() - BlockUtil.mc.player.getPositionVector().z < 0.0) { return false; }if (dirnumber == 1 && (double) pos.getX() - BlockUtil.mc.player.getPositionVector().x > 0.0) { return false; }if (dirnumber == 2 && (double) pos.getZ() - BlockUtil.mc.player.getPositionVector().z > 0.0) { return false; }return dirnumber != 3 || (double) pos.getX() - BlockUtil.mc.player.getPositionVector().x >= 0.0; }

    public String hudInfoString(){
        return "Auto | " + target.getName();
    }
}
