package client.modules.movement;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.EntityUtil;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashSet;

public class Anchor extends Module {

    HashSet<BlockPos> holes = Sets.newHashSet();
    public Setting<Double> onGroundDistance = register(new Setting("HoleDistance", 0.5, 0.1, 3.0));
    public Setting<Float> walkSpeed = register(new Setting("SlowWalkSpeed", 0.1f, 0.0f, 1.0f));

    public Anchor(){
        super("Anchor", "Slows down walking speed when near safe spots.", Category.MOVEMENT);
    }

    public void onUpdate(){
        for(BlockPos pos : holes) {
            BlockPos posUp = pos.up();
            if (mc.player.getDistanceSq(posUp) < onGroundDistance.getCurrentState() && mc.player.onGround) {
                EntityUtil.setSpeed(mc.player, walkSpeed.getCurrentState());
            }
        }
    }
    public void onTick(){
        holes.clear();
        findHoles();
    }

    public void findHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - 2; x < playerPos.getX() + 2; ++x) {
            for (int z = playerPos.getZ() - 2; z < playerPos.getZ() + 2; ++z) {
                for (int y = playerPos.getY() + 2; y > playerPos.getY() - 2; --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            holes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                            holes.add(pos);
                        }
                }
            }
        }
    }
}
