package client.modules.combat;

import client.Client;
import client.command.Command;
import client.modules.Module;
import client.setting.Setting;
import client.util.BlockUtil;
import client.util.EntityUtil;
import client.util.InventoryUtil;
import client.util.PlayerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class Surround extends Module {
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> noGhost;
    private final boolean floor;
    private final Setting<Boolean> centerp;
    private final Setting<Center> centerPlayer;
    private final Setting<Boolean> rotate;
    private final client.util.Timer timer;
    private final client.util.Timer retryTimer;
    private final Set<Vec3d> extendingBlocks;
    private final Map<BlockPos, Integer> retries;
    private int isSafe;
    public static boolean isPlacing;
    private BlockPos startPos;
    private boolean didPlace;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements;
    private int extenders;
    private int obbySlot;
    private boolean offHand;
    Vec3d center;

    public Surround() {
        super("Surround", "Surrounds you with Obsidian", Category.COMBAT);
        this.delay = (Setting<Integer>)this.register(new Setting<>("Delay", 0, 0, 250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting<>("BlocksPerTick", 20, 1, 20));
        this.noGhost = (Setting<Boolean>)this.register(new Setting<>("PacketPlace", false));
        this.centerp = (Setting<Boolean>)this.register(new Setting<>("Center", false));
        this.centerPlayer = (Setting<Center>)this.register(new Setting("Center", Center.SMOOTH, v-> this.centerp.getValue()));
        this.rotate = (Setting<Boolean>)this.register(new Setting<>("Rotate", true));
        this.floor = true;
        this.timer = new client.util.Timer();
        this.retryTimer = new client.util.Timer();
        this.extendingBlocks = new HashSet<Vec3d>();
        this.retries = new HashMap<BlockPos, Integer>();
        this.didPlace = false;
        this.placements = 0;
        this.extenders = 1;
        this.obbySlot = -1;
        this.offHand = false;
        this.center = Vec3d.ZERO;
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }
        this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)Surround.mc.player);
        this.center = PlayerUtil.getCenter(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posZ);
        if(this.centerp.getValue()) {
            switch (this.centerPlayer.getValue()) {
                case INSTANT: {
                    Surround.mc.player.motionX = 0.0;
                    Surround.mc.player.motionZ = 0.0;
                    Surround.mc.player.connection.sendPacket((Packet<net.minecraft.network.play.INetHandlerPlayServer>) new CPacketPlayer.Position(this.center.x, this.center.y, this.center.z, true));
                    Surround.mc.player.setPosition(this.center.x, this.center.y, this.center.z);
                    break;
                }
                case SMOOTH: {
                    Surround.mc.player.motionX = (this.center.x - Surround.mc.player.posX) / 2.0;
                    Surround.mc.player.motionZ = (this.center.z - Surround.mc.player.posZ) / 2.0;
                    break;
                }
            }
        }
        this.retries.clear();
        this.retryTimer.reset();
    }

    @Override
    public void onTick() {
        this.doSurround();
    }

    @Override
    public void onUpdate() {
        if (this.check()) {
            return;
        }
        final boolean onWeb = Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.WEB;
        if (!BlockUtil.isSafe((Entity)Surround.mc.player, onWeb ? 1 : 0, this.floor)) {
            this.placeBlocks(Surround.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(Surround.mc.player.getPositionVector(), (int)(onWeb ? 1 : 0), true), false, false, false);
        }
        boolean inEChest = Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (Surround.mc.player.posY - (int)Surround.mc.player.posY < 0.7) {
            inEChest = false;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {
        if (nullCheck()) {
            return;
        }
        Surround.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }


    @Override
    public String getDisplayInfo() {
        switch (this.isSafe) {
            case 0: {
                return ChatFormatting.RED + "Unsafe";
            }
            case 1: {
                return ChatFormatting.YELLOW + "Safe";
            }
            default: {
                return ChatFormatting.GREEN + "Safe";
            }
        }
    }

    private void doSurround() {
        if (this.check()) {
            return;
        }
        if (!EntityUtil.isSafe((Entity)Surround.mc.player, 0, true)) {
            this.isSafe = 0;
            this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player, 0, true), true, false, false);
        }
        else if (!EntityUtil.isSafe((Entity)Surround.mc.player, -1, false)) {
            this.isSafe = 1;
            this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player, -1, false), false, false, true);
        }
        else {
            this.isSafe = 3;
            if (mc.world.getBlockState(EntityUtil.getRoundedBlockPos(mc.player)).getBlock().equals(Blocks.ENDER_CHEST) && mc.player.posY != EntityUtil.getRoundedBlockPos(mc.player).getY()) {
                this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(Surround.mc.player, 1, false), false, false, true);
            } else {
                this.isSafe = 4;
            }
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    private boolean check() {
        if (nullCheck()) {
            return true;
        }
        final int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        final int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
        }
        this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        Surround.isPlacing = false;
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        final int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.isOff()) {
            return true;
        }
        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
            Command.sendMessage("No obsidian, toggling");
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (Surround.mc.player.inventory.currentItem != this.lastHotbarSlot && Surround.mc.player.inventory.currentItem != this.obbySlot && Surround.mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        }
        if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(Surround.mc.player))) {
            this.disable();
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue());
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
            final Vec3d[] array = new Vec3d[2];
            int i = 0;
            final Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
            while (iterator.hasNext()) {
                final Vec3d vec3d = array[i] = iterator.next();
                ++i;
            }
            final int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), BlockUtil.getUnsafeBlockArray(this.areClose(array), 0, this.floor), false, false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        }
        else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
            this.extendingBlocks.clear();
        }
    }

    private Vec3d areClose(final Vec3d[] vec3ds) {
        int matches = 0;
        for (final Vec3d vec3d : vec3ds) {
            for (final Vec3d pos : BlockUtil.getUnsafeBlockArray(Surround.mc.player.getPositionVector(), 0, this.floor)) {
                if (vec3d.equals((Vec3d)pos)) {
                    ++matches;
                }
            }
        }
        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(final Vec3d pos, final Vec3d[] vec3ds, final boolean hasHelpingBlocks, final boolean isHelping, final boolean isExtending) {
        boolean gotHelp = true;
        for (final Vec3d vec3d : vec3ds) {
            gotHelp = true;
            final BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, false)) {
                case 1: {
                    if (this.retries.get(position) == null || this.retries.get(position) < 4) {
                        this.placeBlock(position);
                        this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                        this.retryTimer.reset();
                        break;
                    }
                    if (Client.speedManager.getSpeedKpH() != 0.0 || isExtending) {
                        break;
                    }
                    if (this.extenders >= 1) {
                        break;
                    }
                    this.placeBlocks(Surround.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(Surround.mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    break;
                }
                case 2: {
                    if (!hasHelpingBlocks) {
                        break;
                    }
                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (!isHelping) {
                        break;
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue()) {
            final int originalSlot = Surround.mc.player.inventory.currentItem;
            final int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            final int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }
            Surround.isPlacing = true;
            Surround.mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
            Surround.mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking);
            Surround.mc.player.inventory.currentItem = originalSlot;
            Surround.mc.playerController.updateController();
            this.didPlace = true;
            ++this.placements;
        }
    }

    private List<BlockPos> position() {
        if (this.floor) {
            return Arrays.asList(new BlockPos(Surround.mc.player.getPositionVector()).add(0, -1, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(-1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, -1), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, 1));
        }
        return Arrays.asList(new BlockPos(Surround.mc.player.getPositionVector()).add(1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(-1, 0, 0), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, -1), new BlockPos(Surround.mc.player.getPositionVector()).add(0, 0, 1));
    }
    static {
        Surround.isPlacing = false;
    }
    public enum Center {
        INSTANT,
        SMOOTH
    }
}