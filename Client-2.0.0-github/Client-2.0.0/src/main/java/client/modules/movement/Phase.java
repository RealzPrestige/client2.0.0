package client.modules.movement;

import client.events.PacketEvent;
import client.events.PushEvent;
import client.events.UpdateWalkingPlayerEvent;
import client.modules.Module;
import client.setting.Bind;
import client.setting.Setting;
import client.util.EntityUtil;
import client.util.Timer;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Phase extends Module {
    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.SEQUENTIAL));
    public enum Mode {
        SEQUENTIAL,
        DYNAMIC,
        VANILLA,
        NONPARALLEL,
        SERIAL,
        CONSECUTIVE,
        INCIDENTAL,
        STRICT,
        TWOBSTRICT,
        NCP
    }
    public Setting<Double> speedH = this.register(new Setting<>("HorizontalVelocity", 0.2, 0.00, 1.0));
    public Setting<Double> speedV = this.register(new Setting<>("VerticalVelocity", 1.0, 0.0, 5.0));
    public Setting<Integer> motionFactor = this.register(new Setting<>("MotionFactor", 2, 1, 5));
    public Setting<Double> timertime = this.register(new Setting<>("Timer", 200.0, 0.0, 1000.0));
    public Setting<Double> loops = this.register(new Setting<>("Loops", 0.5, 0.0, 1.0));
    public Setting<Boolean> extraMotion = this.register(new Setting<>("ExtraMotion", false));
    public Setting<Integer> motionCount = this.register(new Setting<>("ExtraMotionCountTicks", 2, 0, 10, v->extraMotion.getValue()));
    public Setting<Boolean> bounds = this.register(new Setting<>("Bounds", true));
    public Setting<Boolean> instant = this.register(new Setting<>("Instant", true));
    public Setting<Boolean> bypass = this.register(new Setting<>("Bypass", false));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false));
    public Setting<Boolean> antiKick = this.register(new Setting<>("AntiKick", true));
    public Setting<Integer> increaseTicks = this.register(new Setting<>("IncreaseTicks", 10, 0, 15));
    public Setting<Double> teleportBackBypass = this.register(new Setting<>("TeleportBackBypass", 0.5, 0.0, 3.0));
    public Setting<Boolean> constrict = this.register(new Setting<>("Constrict", true));
    public Setting<Boolean> limit = this.register(new Setting<>("Limit", true));
    public Setting<Boolean> jitter = this.register(new Setting<>("Jitter", true));
    public Setting<Double> limitJitter = this.register(new Setting<>("JitterLimit", 1.5, 0.0, 20.0, v->jitter.getValue()));
    public Setting<Directions> directions = this.register(new Setting<>("Directions", Directions.PRESERVE));
    public Setting<Bind> bind = this.register(new Setting<Object>("LoopsBind:", new Bind(-1)));
    public enum Directions {
        PRESERVE,
        UP,
        DOWN,
        SEMI,
        FULL,
        MULTIAXIS,
        DOUBLEAXIS,
        SINGLEAXIS
    }
    public Setting<Type> rotationType = this.register(new Setting<>("RotationType", Type.PACKET));
    public enum Type {
        PACKET,
        BYPASS,
        VANILLA,
        STRICT
    }
    public Setting<RotateMode> rotateMode = this.register(new Setting<>("RotateMode", RotateMode.FULL));
    public enum RotateMode {
        FULL,
        FULLSTRICT,
        SEMI,
        SEMISTRICT,
    }
    public Setting<Boolean> rotationSpoofer = this.register(new Setting<>("RotationSpoofer", false, v-> this.rotate.getValue()));
    public Setting<Boolean> extraPacket = this.register(new Setting<>("ExtraPacket", false));
    public Setting<Integer> extraPacketPackets = this.register(new Setting<>("Packets", 5, 0, 20));
    private final Set<CPacketPlayer> packets = new ConcurrentSet<>();
    private final double[] positionSpoofer = new double[]{0.42, 0.75};
    private final double[] twoblockpositionSpoofer = new double[]{0.4, 0.75, 0.5, 0.41, 0.83, 1.16, 1.41, 1.57, 1.58, 1.42};
    private final double[] predictpositionSpoofer = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    final double[] positionSpooferOffset = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    private final double[] fourBlockpositionSpoofer = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43, 2.78, 2.63, 2.51, 2.9, 3.21, 3.45, 3.43};
    private final double[] selectedSpoofedPositions = new double[0];
    private final Map<Integer, IDtime> teleportmap = new ConcurrentHashMap<>();
    private final Timer timer;
    private int flightCounter = 0;
    private int teleportID = 0;
    private static Phase instance;
    public Phase() {
        super("Phase", "Phases you through blocks", Category.MOVEMENT);
        this.timer = new Timer();
        instance = this;
    }

    public static Phase getInstance() {
        if (instance == null) {
            instance = new Phase();
        }
        return instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        mc.player.setVelocity(0.0, 0.0, 0.0);
        double speed;
        boolean checkCollisionBoxes = this.checkHitBoxes();
        speed = mc.player.movementInput.jump && (checkCollisionBoxes || !EntityUtil.isMoving()) ? !checkCollisionBoxes ? this.resetCounter(10) ? -0.032 : 0.062 : 0.062 : mc.player.movementInput.sneak ? -0.062 : !checkCollisionBoxes ? this.resetCounter(4) ? -0.04 : 0.0 : 0.0;
        if (checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0) {
            double antiFactor = 2.5;
            speed /= antiFactor;
        }
        double[] strafing = this.getMotion(checkCollisionBoxes ? 0.031 : 0.26);
        double loops = bypass.getValue() ? this.loops.getValue() : 0.0;
        for (int i = 1; i < loops + 1; ++i) {
            double extraFactor = 1.0;
            mc.player.motionX = strafing[0] * (double)i * extraFactor;
            mc.player.motionY = speed * (double)i;
            mc.player.motionZ = strafing[1] * (double)i * extraFactor;
            this.sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
            if (rotationSpoofer.getValue()){
                mc.shutdown();
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove(event.getPacket())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPushOutOfBlocks(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !fullNullCheck()) {
            SPacketPlayerPosLook packet = event.getPacket();
            if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
                this.teleportmap.remove(packet.getTeleportId());
            }
            this.teleportID = packet.getTeleportId();
        }
    }

    private boolean checkHitBoxes() {
        return !mc.world.getCollisionBoxes( mc.player, mc.player.getEntityBoundingBox().expand(-0.0, -0.1, -0.0)).isEmpty();
    }

    private boolean resetCounter(int counter) {
        if (++this.flightCounter >= counter) {
            this.flightCounter = 0;
            return true;
        }
        return false;
    }

    private double[] getMotion(double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    private void sendPackets(double x, double y, double z) {
        Vec3d vec = new Vec3d(x, y, z);
        Vec3d position = mc.player.getPositionVector().add(vec);
        Vec3d outOfBoundsVec = this.outOfBoundsVec(position);
        this.packetSender(new CPacketPlayer.Position(position.x, position.y, position.z, mc.player.onGround));
        this.packetSender(new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, mc.player.onGround));
        this.teleportPacket(position);
    }

    private void teleportPacket(Vec3d pos) {
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(++this.teleportID));
        this.teleportmap.put(this.teleportID, new IDtime(pos, new Timer()));
    }

    private Vec3d outOfBoundsVec(Vec3d position) {
        return position.add(0.0, 1337.0, 0.0);
    }

    private void packetSender(CPacketPlayer packet) {
        this.packets.add(packet);
        mc.player.connection.sendPacket(packet);
    }

    public static class IDtime {
        private final Vec3d pos;
        private final Timer timer;

        public IDtime(Vec3d pos, Timer timer) {
            this.pos = pos;
            this.timer = timer;
            this.timer.reset();
        }

        public Vec3d getPos() {
            return this.pos;
        }

        public Timer getTimer() {
            return this.timer;
        }
    }
}

