package client.modules.player;

import client.Client;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static client.util.EntityUtil.basicChecksEntity;

public class AutoMine extends Module {

    private EntityPlayer target;

    /**
     * @author kambing
     * @since 5/9/2021
     */

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.COMBAT));

    private enum Mode {NORMAL, COMBAT}

    public Setting<Boolean> gayIdiots = register(new Setting<>("Burrow", true, v -> mode.getCurrentState() == Mode.COMBAT));
    public Setting<Boolean> surround = register(new Setting<>("Surrounds", true, v -> mode.getCurrentState() == Mode.COMBAT));
    public Setting<Integer> range = register(new Setting<>("Range", 5, 1, 9, v -> mode.getCurrentState() == Mode.COMBAT));
    public Setting<Boolean> endCrystal = register(new Setting<>("EndCrystal", true, v -> mode.getCurrentState() == Mode.COMBAT && surround.getCurrentState()));


    public AutoMine() {
        super("AutoMine", "Mine blocks (need speedmine if combat mode).", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        if (mode.getCurrentState() == Mode.COMBAT) {
            if (Speedmine.getInstance().currentPos == null && EntityUtil.getTarget(range.getCurrentState()) != null && !Client.friendManager.isFriend(EntityUtil.getTarget(range.getCurrentState())) && isBurrowed(EntityUtil.getTarget(range.getCurrentState())) && gayIdiots.getCurrentState()) {
                target = EntityUtil.getTarget(range.getCurrentState());
                BlockPos gay = new BlockPos(target.posX, target.posY + 0.2, target.posZ);
                if (!(mc.world.getBlockState(gay).getBlock() == Blocks.AIR)) {
                    Speedmine.getInstance().currentPos = gay;
                }

            }
        }
        if (mode.getCurrentState() == Mode.COMBAT) {
            if (Speedmine.getInstance().currentPos == null && EntityUtil.getTarget(range.getCurrentState()) != null && !Client.friendManager.isFriend(EntityUtil.getTarget(range.getCurrentState())) && surround.getCurrentState()) {
                if (isCityable(EntityUtil.getTarget(range.getCurrentState()), endCrystal.getCurrentState()) != null) {
                    Speedmine.getInstance().currentPos = isCityable(EntityUtil.getTarget(range.getCurrentState()), endCrystal.getCurrentState());
                }
            }
        }
        if (mode.getCurrentState() == Mode.NORMAL) {
            mc.gameSettings.keyBindAttack.pressed = true;
        }
    }

    @Override
    public void onDisable() {
        if (mode.getCurrentState() == Mode.NORMAL) {
            mc.gameSettings.keyBindAttack.pressed = false;
        }
    }

    @Override
    public String hudInfoString() {
        return this.mode.currentEnumName();
    }

    private boolean isBurrowed(EntityPlayer player) {
        BlockPos pos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY + 0.2), Math.floor(player.posZ));
        return mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST ||
                mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    public static BlockPos isCityable(final EntityPlayer player, final boolean end_crystal) {

        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);

        if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) {
            if (end_crystal) {
                return pos.north();
            } else if (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.AIR) {
                return pos.north();
            }
        }
        if (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) {
            if (end_crystal) {
                return pos.east();
            } else if (mc.world.getBlockState(pos.east().east()).getBlock() == Blocks.AIR) {
                return pos.east();
            }
        }
        if (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) {
            if (end_crystal) {
                return pos.south();
            } else if (mc.world.getBlockState(pos.south().south()).getBlock() == Blocks.AIR) {
                return pos.south();
            }

        }
        if (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN) {
            if (end_crystal) {
                return pos.west();
            } else if (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.AIR) {
                return pos.west();
            }
        }

        return null;

    }
}
