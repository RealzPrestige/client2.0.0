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
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerUtil{
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static final JsonParser PARSER = new JsonParser();

    public static int getRoundedDamage(ItemStack stack) {
        return (int) getDamageInPercent(stack);
    }
    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }
    public static void setMoveSpeed(MoveEvent event, double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
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
            mc.player.motionX = x;
            mc.player.motionZ = z;
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

    public static Vec3d getCenterVec3d(final double posX, final double posY, final double posZ) {
        return new Vec3d(Math.floor(posX) + 0.5, Math.floor(posY), Math.floor(posZ) + 0.5);
    }
    public static BlockPos getCenterPos(final double posX, final double posY, final double posZ) {
        return new BlockPos(Math.floor(posX) + 0.5, Math.floor(posY), Math.floor(posZ) + 0.5);
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
