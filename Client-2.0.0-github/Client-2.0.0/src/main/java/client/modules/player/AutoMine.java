package client.modules.player;

import client.Client;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
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

    public Setting<Mode> mode = this.register(new Setting("Mode", Mode.COMBAT));

    private enum Mode {NORMAL, COMBAT}

    public Setting<Boolean> gayIdiots = this.register(new Setting("Burrow", true, v -> mode.getCurrentState() == Mode.COMBAT));
    public Setting<Boolean> surround = this.register(new Setting("Surrounds", true, v -> mode.getCurrentState() == Mode.COMBAT));
    public Setting<Integer> range = this.register(new Setting("Range", 5, 1, 9, v -> mode.getCurrentState() == Mode.COMBAT));
    public Setting<Boolean> endCrystal = this.register(new Setting("EndCrystal", true, v -> mode.getCurrentState() == Mode.COMBAT && surround.getCurrentState()));


    public AutoMine() {
        super("AutoMine", "Mine blocks (need speedmine if combat mode).", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        if (mode.getCurrentState() == Mode.COMBAT) {
            if (Speedmine.INSTANCE.currentPos == null && findLookingPlayer(range.getCurrentState()) != null && !Client.friendManager.isFriend(findLookingPlayer(range.getCurrentState())) && isBurrowed(findLookingPlayer(range.getCurrentState())) && gayIdiots.getCurrentState()) {
                target = findLookingPlayer(range.getCurrentState());
                BlockPos gay = new BlockPos(target.posX, target.posY + 0.2, target.posZ);
                if (!(mc.world.getBlockState(gay).getBlock() == Blocks.AIR)) {
                    Speedmine.INSTANCE.currentPos = gay;
                }

            }
        }
        if (mode.getCurrentState() == Mode.COMBAT) {
            if (Speedmine.INSTANCE.currentPos == null && findLookingPlayer(range.getCurrentState()) != null && !Client.friendManager.isFriend(findLookingPlayer(range.getCurrentState())) && surround.getCurrentState() && isCityable(findLookingPlayer(range.getCurrentState()), endCrystal.getCurrentState()) != null) {
                Speedmine.INSTANCE.currentPos = isCityable(findLookingPlayer(range.getCurrentState()), endCrystal.getCurrentState());
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

    public static EntityPlayer findLookingPlayer(double rangeMax) {
        // Get player
        ArrayList<EntityPlayer> listPlayer = new ArrayList<>();
        // Only who is in a distance of enemyRange
        for (EntityPlayer playerSin : mc.world.playerEntities) {
            if (basicChecksEntity(playerSin))
                continue;
            if (mc.player.getDistance(playerSin) <= rangeMax) {
                listPlayer.add(playerSin);
            }
        }

        EntityPlayer target = null;
        // Get coordinate eyes + rotation
        Vec3d positionEyes = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        Vec3d rotationEyes = mc.player.getLook(mc.getRenderPartialTicks());
        // Precision
        int precision = 2;
        // Iterate for every blocks
        for (int i = 0; i < (int) rangeMax; i++) {
            // Iterate for the precision
            for (int j = precision; j > 0; j--) {
                // Iterate for all players
                for (EntityPlayer targetTemp : listPlayer) {
                    // Get box of the player
                    AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    // Get coordinate of the vec3d
                    double xArray = positionEyes.x + (rotationEyes.x * i) + rotationEyes.x / j;
                    double yArray = positionEyes.y + (rotationEyes.y * i) + rotationEyes.y / j;
                    double zArray = positionEyes.z + (rotationEyes.z * i) + rotationEyes.z / j;
                    // If it's inside
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray
                            && playerBox.maxX >= xArray && playerBox.minX <= xArray
                            && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        // Get target
                        target = targetTemp;
                    }
                }
            }
        }

        return target;
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
