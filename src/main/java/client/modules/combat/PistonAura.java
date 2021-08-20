package client.modules.combat;

import client.events.PacketEvent;
import client.manager.ModuleManager;
import client.modules.Module;
import client.setting.Setting;
import client.util.BlockUtil;
import client.util.EntityUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
public class PistonAura extends Module {
    public Setting<Boolean> rotate;
    public Setting<Boolean> blockPlayer;
    public Setting<Boolean> antiWeakness;
    public Setting<Double> enemyRange;
    public Setting<Integer> blocksPerTick;
    public Setting<Integer> startDelay;
    public Setting<Integer> trapDelay;
    public Setting<Integer> pistonDelay;
    public Setting<Integer> crystalDelay;
    public Setting<Integer> hitDelay;
    public Setting<BreakModes> breakMode;
    private boolean isSneaking;
    private boolean firstRun;
    private boolean noMaterials;
    private boolean hasMoved;
    private boolean isHole;
    private boolean enoughSpace;
    private int oldSlot;
    private int[] slot_mat;
    private int[] delayTable;
    private int stage;
    private int delayTimeTicks;
    private structureTemp toPlace;
    int[][] disp_surblock;
    Double[][] sur_block;
    private int stuck;
    boolean broken;
    boolean brokenCrystalBug;
    boolean brokenRedstoneTorch;
    public static ModuleManager moduleManager;
    private static PistonAura instance;
    private EntityPlayer closestTarget;
    double[] coordsD;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    public PistonAura() {
        super("PistonAura", "Use Pistons and Crystals to pvp.", Category.COMBAT);
        this.rotate = (Setting<Boolean>)this.register(new Setting<>("Rotate", false));
        this.blockPlayer = (Setting<Boolean>)this.register(new Setting<>("TrapPlayer", true));
        this.antiWeakness = (Setting<Boolean>)this.register(new Setting<>("AntiWeakness", false));
        this.enemyRange = (Setting<Double>)this.register(new Setting<>("Range", 5.9, 0.0, 6.0));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting<>("BlocksPerTick", 4, 0, 20));
        this.startDelay = (Setting<Integer>)this.register(new Setting<>("StartDelay", 4, 0, 20));
        this.trapDelay = (Setting<Integer>)this.register(new Setting<>("TrapDelay", 4, 0, 20));
        this.pistonDelay = (Setting<Integer>)this.register(new Setting<>("PistonDelay", 2, 0, 20));
        this.crystalDelay = (Setting<Integer>)this.register(new Setting<>("CrystalDelay", 2, 0, 20));
        this.hitDelay = (Setting<Integer>)this.register(new Setting<>("HitDelay", 2, 0, 20));
        this.breakMode = (Setting<BreakModes>)this.register(new Setting<>("Break Mode", BreakModes.swing));
        this.isSneaking = false;
        this.firstRun = false;
        this.noMaterials = false;
        this.hasMoved = false;
        this.isHole = true;
        this.enoughSpace = true;
        this.oldSlot = -1;
        this.disp_surblock = new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
        this.stuck = 0;
        PistonAura.instance = this;
    }
    public static PistonAura getInstance() {
        if (PistonAura.instance == null) {
            PistonAura.instance = new PistonAura();
        }
        return PistonAura.instance;
    }
    @Override
    public void onEnable() {
        this.coordsD = new double[3];
        this.delayTable = new int[] { this.startDelay.getValue(), this.trapDelay.getValue(), this.pistonDelay.getValue(), this.crystalDelay.getValue(), this.hitDelay.getValue() };
        this.toPlace = new structureTemp(0.0, 0, null);
        final boolean b = true;
        this.firstRun = b;
        this.isHole = b;
        final boolean b2 = false;
        this.brokenRedstoneTorch = b2;
        this.brokenCrystalBug = b2;
        this.broken = b2;
        this.hasMoved = b2;
        this.slot_mat = new int[] { -1, -1, -1, -1, -1 };
        final int stage = 0;
        this.stuck = stage;
        this.delayTimeTicks = stage;
        this.stage = stage;
        if (PistonAura.mc.player == null) {
            this.disable();
            return;
        }
        this.oldSlot = PistonAura.mc.player.inventory.currentItem;
    }
    @Override
    public void onDisable() {
        if (PistonAura.mc.player == null) {
            return;
        }
        if (this.isSneaking) {
            PistonAura.mc.player.connection.sendPacket(new CPacketEntityAction(PistonAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != PistonAura.mc.player.inventory.currentItem && this.oldSlot != -1) {
            PistonAura.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = false;
        this.firstRun = true;
    }
    @Override
    public void onUpdate() {
        if (PistonAura.mc.player == null) {
            this.disable();
            return;
        }
        if (this.firstRun) {
            this.closestTarget = EntityUtil.getTargetDouble(enemyRange.getValue());
            if (this.closestTarget == null) {
                return;
            }
            this.firstRun = false;
            if (this.getMaterialsSlot()) {
                if (this.is_in_hole()) {
                    this.enoughSpace = this.createStructure();
                }
                else {
                    this.isHole = false;
                }
            }
            else {
                this.noMaterials = true;
            }
        }
        else {
            if (this.delayTable == null) {
                return;
            }
            if (this.delayTimeTicks < this.delayTable[this.stage]) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
        }
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved) {
            this.disable();
            return;
        }
        if (this.trapPlayer()) {
            if (this.stage == 1) {
                final BlockPos targetPos = this.compactBlockPos(this.stage);
                this.placeBlock(targetPos, this.stage, this.toPlace.offsetX, this.toPlace.offsetZ);
                ++this.stage;
            }
            else if (this.stage == 2) {
                final BlockPos targetPosPiston = this.compactBlockPos(this.stage - 1);
                if (!(this.get_block(targetPosPiston.getX(), targetPosPiston.getY(), targetPosPiston.getZ()) instanceof BlockPistonBase)) {
                    --this.stage;
                }
                else {
                    final BlockPos targetPos2 = this.compactBlockPos(this.stage);
                    if (this.placeBlock(targetPos2, this.stage, this.toPlace.offsetX, this.toPlace.offsetZ)) {
                        ++this.stage;
                    }
                }
            }
            else if (this.stage == 3) {
                for (final Entity t : PistonAura.mc.world.loadedEntityList) {
                    if (t instanceof EntityEnderCrystal && (int)t.posX == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).x && (int)t.posZ == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).z) {
                        --this.stage;
                        break;
                    }
                }
                if (this.stage == 3) {
                    final BlockPos targetPos = this.compactBlockPos(this.stage);
                    this.placeBlock(targetPos, this.stage, this.toPlace.offsetX, this.toPlace.offsetZ);
                    ++this.stage;
                }
            }
            else if (this.stage == 4) {
                this.destroyLeCrystal();
            }
        }
    }

    public void destroyLeCrystal() {
        Entity crystal = null;
        for (final Entity t : PistonAura.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && (((t.posX == (int)t.posX || (int)t.posX == (int)this.closestTarget.posX) && ((int)((int)t.posX - 0.1) == (int)this.closestTarget.posX || (int)((int)t.posX + 0.1) == (int)this.closestTarget.posX) && (int)t.posZ == (int)this.closestTarget.posZ) || ((t.posZ == (int)t.posZ || (int)t.posZ == (int)this.closestTarget.posZ) && ((int)((int)t.posZ - 0.1) == (int)this.closestTarget.posZ || (int)((int)t.posZ + 0.1) == (int)this.closestTarget.posZ) && (int)t.posX == (int)this.closestTarget.posX))) {
                crystal = t;
            }
        }
        if (this.broken && crystal == null) {
            final int n = 0;
            this.stuck = n;
            this.stage = n;
            this.broken = false;
        }
        if (crystal != null) {
            this.breakCrystalPiston(crystal);
                this.broken = true;
        }
        else if (++this.stuck >= 35) {
            boolean found = false;
            for (final Entity t2 : PistonAura.mc.world.loadedEntityList) {
                if (t2 instanceof EntityEnderCrystal && (int)t2.posX == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).x && (int)t2.posZ == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).z) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                final BlockPos offsetPosPist = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
                final BlockPos pos = new BlockPos(this.closestTarget.getPositionVector()).add(offsetPosPist.getX(), offsetPosPist.getY(), offsetPosPist.getZ());
                if (this.brokenRedstoneTorch && this.get_block(pos.getX(), pos.getY(), pos.getZ()) instanceof BlockAir) {
                    this.stage = 1;
                    this.brokenRedstoneTorch = false;
                }
                else {
                    final EnumFacing side = BlockUtil.getPlaceableSide(pos);
                    if (side != null) {
                        if (this.rotate.getValue()) {
                            final BlockPos neighbour = pos.offset(side);
                            final EnumFacing opposite = side.getOpposite();
                            final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 1.0, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                            BlockUtil.faceVectorPacketInstant(hitVec);
                        }
                        PistonAura.mc.player.swingArm(EnumHand.MAIN_HAND);
                        PistonAura.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
                        PistonAura.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
                            this.brokenRedstoneTorch = true;
                    }
                }
            }
            else {
                boolean ext = false;
                for (final Entity t3 : PistonAura.mc.world.loadedEntityList) {
                    if (t3 instanceof EntityEnderCrystal && (int)t3.posX == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).x && (int)t3.posZ == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).z) {
                        ext = true;
                        break;
                    }
                }
                    final int n3 = 0;
                    this.stuck = n3;
                    this.stage = n3;
                    this.brokenCrystalBug = false;
                if (ext) {
                    this.breakCrystalPiston(null);
                        this.brokenCrystalBug = true;
                }
            }
        }
    }

    public BlockPos compactBlockPos(final int step) {
        final BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + step - 1));
        return new BlockPos(this.closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
    }
    private void breakCrystalPiston(final Entity crystal) {
        if (this.antiWeakness.getValue()) {
            PistonAura.mc.player.inventory.currentItem = this.slot_mat[4];
        }
        if (this.rotate.getValue()) {
            this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, PistonAura.mc.player);
        }
        if (this.breakMode.getValue().equals(BreakModes.swing)) {
            this.breakCrystal(crystal);
            PistonAura.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            PistonAura.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (this.rotate.getValue()) {
            resetRotation();
        }
    }
    private boolean trapPlayer() {
        int i = 0;
        int blockPlaced = 0;
        if (this.toPlace.to_place.size() <= 0 || this.toPlace.supportBlock <= 0) {
            this.stage = ((this.stage == 0) ? 1 : this.stage);
            return true;
        }
        while (true) {
            final BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(i));
            final BlockPos targetPos = new BlockPos(this.closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            if (this.placeBlock(targetPos, 0, 0.0, 0.0)) {
                ++blockPlaced;
            }
            if (blockPlaced == this.blocksPerTick.getValue()) {
                return false;
            }
            if (++i >= this.toPlace.supportBlock) {
                this.stage = ((this.stage == 0) ? 1 : this.stage);
                return true;
            }
        }
    }

    private boolean placeBlock(final BlockPos pos, final int step, final double offsetX, final double offsetZ) {
        final Block block = PistonAura.mc.world.getBlockState(pos).getBlock();
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5 + offsetX, 1.0, 0.5 + offsetZ).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = PistonAura.mc.world.getBlockState(neighbour).getBlock();
        if (PistonAura.mc.player.inventory.getStackInSlot(this.slot_mat[step]) != ItemStack.EMPTY) {
            if (PistonAura.mc.player.inventory.currentItem != this.slot_mat[step]) {
                PistonAura.mc.player.inventory.currentItem = ((this.slot_mat[step] == 11) ? PistonAura.mc.player.inventory.currentItem : this.slot_mat[step]);
            }
            if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                PistonAura.mc.player.connection.sendPacket(new CPacketEntityAction(PistonAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                this.isSneaking = true;
            }
            if (this.rotate.getValue() || step == 1) {
                Vec3d positionHit = hitVec;
                if (!this.rotate.getValue() && step == 1) {
                    positionHit = new Vec3d(PistonAura.mc.player.posX + offsetX, PistonAura.mc.player.posY, PistonAura.mc.player.posZ + offsetZ);
                }
                BlockUtil.faceVectorPacketInstant(positionHit);
            }
            EnumHand handSwing = EnumHand.MAIN_HAND;
            if (this.slot_mat[step] == 11) {
                handSwing = EnumHand.OFF_HAND;
            }
            PistonAura.mc.playerController.processRightClickBlock(PistonAura.mc.player, PistonAura.mc.world, neighbour, opposite, hitVec, handSwing);
            PistonAura.mc.player.swingArm(handSwing);
            return true;
        }
        return false;
    }

    private boolean getMaterialsSlot() {
        if (PistonAura.mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            this.slot_mat[2] = 11;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = PistonAura.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemEndCrystal) {
                    this.slot_mat[2] = i;
                }
                else if (this.antiWeakness.getValue() && stack.getItem() instanceof ItemSword) {
                    this.slot_mat[4] = i;
                }
                else if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        this.slot_mat[0] = i;
                    }
                    else if (block instanceof BlockPistonBase) {
                        this.slot_mat[1] = i;
                    }
                    else if (block instanceof BlockRedstoneTorch || block.translationKey.equals("blockRedstone")) {
                        this.slot_mat[3] = i;
                    }
                }
            }
        }
        int count = 0;
        for (final int val : this.slot_mat) {
            if (val != -1) {
                ++count;
            }
        }
        return count == 4 + ((this.antiWeakness.getValue()) ? 1 : 0);
    }

    private boolean is_in_hole() {
        this.sur_block = new Double[][] { { this.closestTarget.posX + 1.0, this.closestTarget.posY, this.closestTarget.posZ }, { this.closestTarget.posX - 1.0, this.closestTarget.posY, this.closestTarget.posZ }, { this.closestTarget.posX, this.closestTarget.posY, this.closestTarget.posZ + 1.0 }, { this.closestTarget.posX, this.closestTarget.posY, this.closestTarget.posZ - 1.0 } };
        return !(this.get_block(this.sur_block[0][0], this.sur_block[0][1], this.sur_block[0][2]) instanceof BlockAir) && !(this.get_block(this.sur_block[1][0], this.sur_block[1][1], this.sur_block[1][2]) instanceof BlockAir) && !(this.get_block(this.sur_block[2][0], this.sur_block[2][1], this.sur_block[2][2]) instanceof BlockAir) && !(this.get_block(this.sur_block[3][0], this.sur_block[3][1], this.sur_block[3][2]) instanceof BlockAir);
    }

    private boolean createStructure() {
        final structureTemp addedStructure = new structureTemp(Double.MAX_VALUE, 0, null);
        int i = 0;
        final int[] meCord = { (int)PistonAura.mc.player.posX, (int)PistonAura.mc.player.posY, (int)PistonAura.mc.player.posZ };
        if (meCord[1] - this.closestTarget.posY > -1.0) {
            for (final Double[] cord_b : this.sur_block) {
                final double[] crystalCords = { cord_b[0], cord_b[1] + 1.0, cord_b[2] };
                final BlockPos positionCrystal = new BlockPos(crystalCords[0], crystalCords[1], crystalCords[2]);
                final double distance_now;
                if ((distance_now = PistonAura.mc.player.getDistance(crystalCords[0], crystalCords[1], crystalCords[2])) < addedStructure.distance && (positionCrystal.getY() != meCord[1] || meCord[0] != positionCrystal.getX() || (Math.abs(meCord[2] - positionCrystal.getZ()) > 3 && meCord[2] != positionCrystal.getZ()) || Math.abs(meCord[0] - positionCrystal.getX()) > 3)) {
                    ++cord_b[1];
                    if (this.get_block(crystalCords[0], crystalCords[1], crystalCords[2]) instanceof BlockAir) {
                        final double[] pistonCord = { crystalCords[0] + this.disp_surblock[i][0], crystalCords[1], crystalCords[2] + this.disp_surblock[i][2] };
                        final Block blockPiston = this.get_block(pistonCord[0], pistonCord[1], pistonCord[2]);
                        if ((blockPiston instanceof BlockAir || blockPiston instanceof BlockPistonBase) && this.someoneInCoords(pistonCord[0], pistonCord[1], pistonCord[2])) {
                            boolean b = false;
                            Label_0678: {
                                Label_0673: {
                                    if (this.rotate.getValue()) {
                                        if ((int)pistonCord[0] == meCord[0]) {
                                            if (this.closestTarget.posZ > PistonAura.mc.player.posZ != this.closestTarget.posZ > pistonCord[2]) {
                                                break Label_0673;
                                            }
                                            if (Math.abs((int)this.closestTarget.posZ - (int)PistonAura.mc.player.posZ) == 1) {
                                                break Label_0673;
                                            }
                                        }
                                        else if ((int)pistonCord[2] != meCord[2] || ((this.closestTarget.posX > PistonAura.mc.player.posX != this.closestTarget.posX > pistonCord[0] || Math.abs((int)this.closestTarget.posX - (int)PistonAura.mc.player.posX) == 1) && (Math.abs((int)this.closestTarget.posX - (int)PistonAura.mc.player.posX) <= 1 || pistonCord[0] > this.closestTarget.posX != meCord[0] > this.closestTarget.posX))) {
                                            break Label_0673;
                                        }
                                        break Label_0678;
                                    }
                                }
                                b = true;
                            }
                            final boolean join = b;
                            if (join) {
                                boolean b2 = false;
                                Label_0867: {
                                    Label_0862: {
                                        if (this.rotate.getValue()) {
                                            if (meCord[0] == (int)this.closestTarget.posX || meCord[2] == (int)this.closestTarget.posZ) {
                                                if (PistonAura.mc.player.getDistance(crystalCords[0], crystalCords[1], crystalCords[2]) <= 3.5 || meCord[0] == (int)crystalCords[0]) {
                                                    break Label_0862;
                                                }
                                                if (meCord[2] == (int)crystalCords[2]) {
                                                    break Label_0862;
                                                }
                                            }
                                            else if (meCord[0] != (int)pistonCord[0] || Math.abs((int)this.closestTarget.posZ - (int)PistonAura.mc.player.posZ) == 1 || (meCord[2] == (int)pistonCord[2] && Math.abs((int)this.closestTarget.posZ - (int)PistonAura.mc.player.posZ) != 1)) {
                                                break Label_0862;
                                            }
                                            break Label_0867;
                                        }
                                    }
                                    b2 = true;
                                }
                                final boolean enter = b2;
                                if (enter) {
                                    int[] poss = null;
                                    for (final int[] possibilites : this.disp_surblock) {
                                        final double[] coordinatesTemp = { cord_b[0] + this.disp_surblock[i][0] + possibilites[0], cord_b[1], cord_b[2] + this.disp_surblock[i][2] + possibilites[2] };
                                        final int[] torchCoords = { (int)coordinatesTemp[0], (int)coordinatesTemp[1], (int)coordinatesTemp[2] };
                                        final int[] crystalCoords = { (int)crystalCords[0], (int)crystalCords[1], (int)crystalCords[2] };
                                        if (this.get_block(coordinatesTemp[0], coordinatesTemp[1], coordinatesTemp[2]) instanceof BlockAir && (torchCoords[0] != crystalCoords[0] || torchCoords[1] != crystalCoords[1] || crystalCoords[2] != torchCoords[2]) && this.someoneInCoords(coordinatesTemp[0], coordinatesTemp[1], coordinatesTemp[2])) {
                                            poss = possibilites;
                                            break;
                                        }
                                    }
                                    if (poss != null) {
                                        final List<Vec3d> toPlaceTemp = new ArrayList<>();
                                        int supportBlock = 0;
                                        if (this.get_block(cord_b[0] + this.disp_surblock[i][0], cord_b[1] - 1.0, cord_b[2] + this.disp_surblock[i][2]) instanceof BlockAir) {
                                            toPlaceTemp.add(new Vec3d((this.disp_surblock[i][0] * 2), this.disp_surblock[i][1], this.disp_surblock[i][2] * 2));
                                            ++supportBlock;
                                        }
                                        if (this.get_block(cord_b[0] + this.disp_surblock[i][0] + poss[0], cord_b[1] - 1.0, cord_b[2] + this.disp_surblock[i][2] + poss[2]) instanceof BlockAir) {
                                            toPlaceTemp.add(new Vec3d((this.disp_surblock[i][0] * 2 + poss[0]), this.disp_surblock[i][1], (this.disp_surblock[i][2] * 2 + poss[2])));
                                            ++supportBlock;
                                        }
                                        toPlaceTemp.add(new Vec3d((this.disp_surblock[i][0] * 2), (this.disp_surblock[i][1] + 1), (this.disp_surblock[i][2] * 2)));
                                        toPlaceTemp.add(new Vec3d(this.disp_surblock[i][0], (this.disp_surblock[i][1] + 1), this.disp_surblock[i][2]));
                                        toPlaceTemp.add(new Vec3d((this.disp_surblock[i][0] * 2 + poss[0]), (this.disp_surblock[i][1] + 1), (this.disp_surblock[i][2] * 2 + poss[2])));
                                        float offsetX;
                                        float offsetZ;
                                        if (this.disp_surblock[i][0] != 0) {
                                            offsetX = (this.rotate.getValue() ? (this.disp_surblock[i][0] / 2.0f) : ((float)this.disp_surblock[i][0]));
                                            if (this.rotate.getValue()) {
                                                if (PistonAura.mc.player.getDistanceSq(pistonCord[0], pistonCord[1], pistonCord[2] + 0.5) > PistonAura.mc.player.getDistanceSq(pistonCord[0], pistonCord[1], pistonCord[2] - 0.5)) {
                                                    offsetZ = -0.5f;
                                                }
                                                else {
                                                    offsetZ = 0.5f;
                                                }
                                            }
                                            else {
                                                offsetZ = (float)this.disp_surblock[i][2];
                                            }
                                        }
                                        else {
                                            offsetZ = (this.rotate.getValue() ? (this.disp_surblock[i][2] / 2.0f) : ((float)this.disp_surblock[i][2]));
                                            if (this.rotate.getValue()) {
                                                if (PistonAura.mc.player.getDistanceSq(pistonCord[0] + 0.5, pistonCord[1], pistonCord[2]) > PistonAura.mc.player.getDistanceSq(pistonCord[0] - 0.5, pistonCord[1], pistonCord[2])) {
                                                    offsetX = -0.5f;
                                                }
                                                else {
                                                    offsetX = 0.5f;
                                                }
                                            }
                                            else {
                                                offsetX = (float)this.disp_surblock[i][0];
                                            }
                                        }
                                        addedStructure.replaceValues(distance_now, supportBlock, toPlaceTemp, -1, offsetX, offsetZ);
                                    }
                                }
                            }
                        }
                    }
                }
                ++i;
            }
            if (addedStructure.to_place != null) {
                if (this.blockPlayer.getValue()) {
                    final Vec3d valuesStart = addedStructure.to_place.get(addedStructure.supportBlock + 1);
                    final int[] valueBegin = { (int)(-valuesStart.x), (int)valuesStart.y, (int)(-valuesStart.z) };
                    addedStructure.to_place.add(0, new Vec3d(0.0, 2.0, 0.0));
                    addedStructure.to_place.add(0, new Vec3d(valueBegin[0], valueBegin[1] + 1, valueBegin[2]));
                    addedStructure.to_place.add(0, new Vec3d(valueBegin[0], valueBegin[1], valueBegin[2]));
                    addedStructure.supportBlock += 3;
                }
                this.toPlace = addedStructure;
                return true;
            }
        }
        return false;
    }

    private boolean someoneInCoords(final double x, final double y, final double z) {
        final int xCheck = (int)x;
        final int yCheck = (int)y;
        final int zCheck = (int)z;
        final List<EntityPlayer> playerList = PistonAura.mc.world.playerEntities;
        for (final EntityPlayer player : playerList) {
            if ((int)player.posX == xCheck && (int)player.posZ == zCheck && (int)player.posY >= yCheck - 1 && (int)player.posY <= yCheck + 1) {
                return false;
            }
        }
        return true;
    }

    private Block get_block(final double x, final double y, final double z) {
        return PistonAura.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    private void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float)v[0], (float)v[1]);
    }

    public static double[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        yaw += 90.0;
        return new double[] { yaw, pitch };
    }

    private static void setYawAndPitch(final float yaw1, final float pitch1) {
        PistonAura.yaw = yaw1;
        PistonAura.pitch = pitch1;
        PistonAura.isSpoofingAngles = true;
    }

    private void breakCrystal(final Entity crystal) {
        PistonAura.mc.playerController.attackEntity(PistonAura.mc.player, crystal);
        PistonAura.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof CPacketPlayer && PistonAura.isSpoofingAngles) {
            ((CPacketPlayer)packet).yaw = (float)PistonAura.yaw;
            ((CPacketPlayer)packet).pitch = (float)PistonAura.pitch;
        }
    }

    private static void resetRotation() {
        if (PistonAura.isSpoofingAngles) {
            PistonAura.yaw = PistonAura.mc.player.rotationYaw;
            PistonAura.pitch = PistonAura.mc.player.rotationPitch;
            PistonAura.isSpoofingAngles = false;
        }
    }

    static class structureTemp
    {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public int direction;
        public float offsetX;
        public float offsetZ;

        public structureTemp(final double distance, final int supportBlock, final List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }

        public void replaceValues(final double distance, final int supportBlock, final List<Vec3d> to_place, final int direction, final float offsetX, final float offsetZ) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
        }
    }

    private enum BreakModes
    {
        packet,
        swing
    }
}
