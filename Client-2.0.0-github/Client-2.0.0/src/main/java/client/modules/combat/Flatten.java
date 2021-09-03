package client.modules.combat;


import client.Client;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.BlockUtil;
import client.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Flatten extends Module {
    private final Setting<Integer> blocksPerTick = this.register(new Setting<>("BlocksPerTick", 8, 1, 30));
    private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false));
    private final Setting<Boolean> packet = this.register(new Setting<>("PacketPlace", false));

    private final Vec3d[] offsetsDefault = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(-1.0, 0.0, 0.0),
    };

    private int offsetStep = 0;
    private int oldSlot = -1;
    private boolean placing = false;

    public Flatten() {
        super("Flatten", "Flatter than 19xp's 14yr old girlfriend.", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        oldSlot = mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        oldSlot = -1;
    }

    @Override
    public void onTick(){
        final int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (obbySlot == -1) {
            this.toggle();
        }
    }

    @Override
    public void onUpdate() {
        final int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (obbySlot == -1) {
            this.toggle();
        }
        EntityPlayer closest_target = findClosestTarget();

        if (closest_target == null) {
            return;
        }

        final List<Vec3d> place_targets = new ArrayList<>();
        Collections.addAll(place_targets, offsetsDefault);

        int blocks_placed = 0;
        while (blocks_placed < blocksPerTick.getCurrentState()) {
            if (offsetStep >= place_targets.size()) {
                offsetStep = 0;
                break;
            }
            placing = true;
            final BlockPos offset_pos = new BlockPos(place_targets.get(offsetStep));
            final BlockPos target_pos = new BlockPos(closest_target.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
            boolean should_try_place = mc.world.getBlockState(target_pos).getMaterial().isReplaceable();

            for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    should_try_place = false;
                    break;
                }
            }
            if (should_try_place) {
                place(target_pos, obbySlot, oldSlot);
                ++blocks_placed;
            }
            offsetStep++;
            placing = false;
        }
    }

    private void place(BlockPos pos, int slot, int oldSlot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getCurrentState(), this.packet.getCurrentState(), mc.player.isSneaking());
        mc.player.inventory.currentItem = oldSlot;
        mc.playerController.updateController();
    }
    @Override
    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }
    }

    private EntityPlayer findClosestTarget() {
        if (mc.world.playerEntities.isEmpty())
            return null;
        EntityPlayer closestTarget = null;
        for (final EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player || !target.isEntityAlive())
                continue;
            if (Client.friendManager.isFriend(target.getName()))
                continue;
            if (target.getHealth() <= 0.0f)
                continue;
            if (mc.player.getDistance(target) > 5)
                continue;
            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;
            closestTarget = target;
        }
        return closestTarget;
    }

}