package client.util;

import client.command.Command;
import client.events.MoveEvent;
import client.modules.movement.ElytraFlight;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerUtil implements Util {
    private static final JsonParser PARSER = new JsonParser();

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32) {
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i1 = MathHelper.floor(vec31.y);
        int j1 = MathHelper.floor(vec31.z);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block.canCollideCheck(iblockstate, false) && block != Blocks.WEB) {
            return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
        }

        int k1 = 200;
        final double d6 = vec32.x - vec31.x;
        final double d7 = vec32.y - vec31.y;
        final double d8 = vec32.z - vec31.z;
        while (k1-- >= 0) {
            if (l == i && i1 == j && j1 == k) {
                return null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i1, j1);
            iblockstate = mc.world.getBlockState(blockpos);
            block = iblockstate.getBlock();

            if (block.canCollideCheck(iblockstate, false) && block != Blocks.WEB) {
                return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
            }
        }
        return null;
    }

    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb) {
        final double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        final double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        final double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        final double d3 = (1.0D - java.lang.Math.floor(1.0D / d0) * d0) / 2.0D;
        final double d4 = (1.0D - java.lang.Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        final double d5 = bb.minX + (bb.maxX - bb.minX) * (double) f;
                        final double d6 = bb.minY + (bb.maxY - bb.minY) * (double) f1;
                        final double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double) f2;

                        if (rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null) {
                            ++j2;
                        }

                        ++k2;
                    }
                }
            }

            return (float) j2 / (float) k2;
        } else {
            return 0.0F;
        }
    }

    public static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            final int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            final float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }
    public static int getRoundedDamage(ItemStack stack) {
        return (int) getDamageInPercent(stack);
    }
    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }
    public static void setMoveSpeed(MoveEvent event, double speed) {
        double forward = ElytraFlight.mc.player.movementInput.moveForward;
        double strafe = ElytraFlight.mc.player.movementInput.moveStrafe;
        float yaw = ElytraFlight.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
            ElytraFlight.mc.player.motionX = 0.0;
            ElytraFlight.mc.player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double x = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            double z = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
            event.setX(x);
            event.setZ(z);
            ElytraFlight.mc.player.motionX = x;
            ElytraFlight.mc.player.motionZ = z;
        }
    }
    public static double getDirection() {
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.moveForward < 0f) rotationYaw += 180f;

        float forward = 1f;

        if (mc.player.moveForward < 0f) forward = -0.5f;
        else if (mc.player.moveForward > 0f) forward = 0.5f;

        if (mc.player.moveStrafing > 0f) rotationYaw -= 90f * forward;
        if (mc.player.moveStrafing < 0f) rotationYaw += 90f * forward;

        return Math.toRadians(rotationYaw);
    }
    public static boolean isMoving() {
        return Minecraft.getMinecraft().player.moveForward != 0.0 || Minecraft.getMinecraft().player.moveStrafing != 0.0;
    }

    public static double vanillaSpeed() {
        double baseSpeed = 0.272;
        if (Minecraft.getMinecraft().player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * amplifier;
        }
        return baseSpeed;
    }
    public static boolean isArmorLow(final EntityPlayer player, final int durability) {
        for (final ItemStack piece : player.inventory.armorInventory) {
            if (piece != null && getDamageInPercent(piece) >= durability) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static float getDamageInPercent(final ItemStack stack) {
        final float green = (stack.getMaxDamage() - (float)stack.getItemDamage()) / stack.getMaxDamage();
        final float red = 1.0f - green;
        return (float)(100 - (int)(red * 100.0f));
    }

    public static Vec3d getCenter(final double posX, final double posY, final double posZ) {
        return new Vec3d(Math.floor(posX) + 0.5, Math.floor(posY), Math.floor(posZ) + 0.5);
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = EntityUtil.getLegitRotations(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? (float) MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

    }   public static BlockPos getPlayerPos1() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY - 1), Math.floor(mc.player.posZ));
    }
    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(mc.world);
        var4.posX = (double) var0 + 0.5D;
        var4.posY = (double) var1 + 0.5D;
        var4.posZ = (double) var2 + 0.5D;
        var4.posX += (double) var3.getDirectionVec().getX() * 0.25D;
        var4.posY += (double) var3.getDirectionVec().getY() * 0.25D;
        var4.posZ += (double) var3.getDirectionVec().getZ() * 0.25D;
        return getDirectionToEntity(var4);
    }
    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{getYaw(var0) + mc.player.rotationYaw, getPitch(var0) + mc.player.rotationPitch};
    }
    public static float getYaw(Entity var0) {
        double var1 = var0.posX - mc.player.posX;
        double var3 = var0.posZ - mc.player.posZ;
        double var5;

        if (var3 < 0.0D && var1 < 0.0D) {
            var5 = 90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else if (var3 < 0.0D && var1 > 0.0D) {
            var5 = -90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else {
            var5 = Math.toDegrees(-Math.atan(var1 / var3));
        }

        return MathHelper.wrapDegrees(-(mc.player.rotationYaw - (float) var5));
    }

    public static float getPitch(Entity var0) {
        double var1 = var0.posX - mc.player.posX;
        double var3 = var0.posZ - mc.player.posZ;
        double var5 = var0.posY - 1.6D + (double) var0.getEyeHeight() - mc.player.posY;
        double var7 = MathHelper.sqrt(var1 * var1 + var3 * var3);
        double var9 = -Math.toDegrees(Math.atan(var5 / var7));
        return -MathHelper.wrapDegrees(mc.player.rotationPitch - (float) var9);
    }

    public static UUID getUUIDFromName(String name) {
        try {
            lookUpUUID process = new lookUpUUID(name);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        } catch (Exception e) {
            return null;
        }
    }
    public static int convertToMouse(int key){
        switch (key){
            case -2:
                return 0;
            case -3:
                return 1;
            case -4:
                return 2;
            case -5:
                return 3;
            case -6:
                return 4;
        }
        return -1;
    }

    public static EntityPlayer findClosestTarget(double rangeMax, EntityPlayer aimTarget) {
        rangeMax *= rangeMax;
        List<EntityPlayer> playerList = mc.world.playerEntities;

        EntityPlayer closestTarget = null;

        for (EntityPlayer entityPlayer : playerList) {

            if (EntityUtil.basicChecksEntity(entityPlayer))
                continue;

            if (aimTarget == null && mc.player.getDistanceSq(entityPlayer) <= rangeMax) {
                closestTarget = entityPlayer;
                continue;
            }
            if (aimTarget != null && mc.player.getDistanceSq(entityPlayer) <= rangeMax && mc.player.getDistanceSq(entityPlayer) < mc.player.getDistanceSq(aimTarget)) {
                closestTarget = entityPlayer;
            }
        }
        return closestTarget;
    }

    // 0b00101010: replaced getDistance with getDistanceSq as speeds up calculation
    public static EntityPlayer findClosestTarget() {
        List<EntityPlayer> playerList = mc.world.playerEntities;

        EntityPlayer closestTarget = null;

        for (EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity(entityPlayer))
                continue;

            if (closestTarget == null) {
                closestTarget = entityPlayer;
                continue;
            }
            if (mc.player.getDistanceSq(entityPlayer) < mc.player.getDistanceSq(closestTarget)) {
                closestTarget = entityPlayer;
            }
        }

        return closestTarget;
    }

    // Find player you are looking
    public static EntityPlayer findLookingPlayer(double rangeMax) {
        // Get player
        ArrayList<EntityPlayer> listPlayer = new ArrayList<>();
        // Only who is in a distance of enemyRange
        for (EntityPlayer playerSin : mc.world.playerEntities) {
            if (EntityUtil.basicChecksEntity(playerSin))
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

    public static String requestIDs(String data) {
        try {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = (new Scanner(is)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

    public static List<String> getHistoryOfNames(UUID id) {
        try {
            JsonArray array = getResources(new URL("https://api.mojang.com/user/profiles/" + getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            List<String> temp = Lists.newArrayList();
            for (JsonElement e : array) {
                JsonObject node = e.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                temp.add(name + "Â§8" + (new Date(changedAt)) );
            }
            Collections.sort(temp);
            return temp;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getIdNoHyphens(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(URL url, String request) throws Exception {
        return getResources(url, request, null);
    }

    private static JsonElement getResources(URL url, String request, JsonElement element) throws Exception {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.writeBytes(AdvancementManager.GSON.toJson(element));
                output.close();
            }
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }
            scanner.close();
            String json = builder.toString();
            return PARSER.parse(json);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public static class lookUpUUID implements Runnable {
        private final String name;
        private volatile UUID uuid;

        public lookUpUUID(String name) {
            this.name = name;
        }

        public void run() {
            NetworkPlayerInfo profile;
            try {
                ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<>(Objects.requireNonNull(mc.getConnection()).getPlayerInfoMap());
                profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
                assert profile != null;
                this.uuid = profile.getGameProfile().getId();
            } catch (Exception e) {
                profile = null;
            }
            if (profile == null) {
                Command.sendMessage("Player isn't online. Looking up UUID..");
                String s = PlayerUtil.requestIDs("[\"" + this.name + "\"]");
                if (s == null || s.isEmpty()) {
                    Command.sendMessage("Couldn't find player ID. Are you connected to the internet? (0)");
                } else {
                    JsonElement element = (new JsonParser()).parse(s);
                    if (element.getAsJsonArray().size() == 0) {
                        Command.sendMessage("Couldn't find player ID. (1)");
                    } else {
                        try {
                            String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            this.uuid = UUIDTypeAdapter.fromString(id);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Command.sendMessage("Couldn't find player ID. (2)");
                        }
                    }
                }
            }
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }
}
