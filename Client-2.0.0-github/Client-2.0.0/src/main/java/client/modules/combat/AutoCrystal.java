package client.modules.combat;

import client.events.PacketEvent;
import client.events.Render3DEvent;
import client.modules.Module;
import client.modules.client.ClickGui;
import client.setting.Setting;
import client.util.Timer;
import client.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

public class AutoCrystal extends Module {
    public Setting<Settings> setting;
    public enum Settings{AUTOCRYSTAL, RENDER}
    public Setting<SpeedFactor> speedFactor;
    public enum SpeedFactor {TICK, UPDATE}
    public Setting<Float> targetRange;
    public Setting<Boolean> cancel;
    public Setting<Float> breakRange;
    public Setting<Float> breakWallRange;
    public Setting<Integer> breakDelay;
    public Setting<Boolean> instant;
    public Setting<Priority> priority;
    public enum Priority{SELF, ENEMY}
    public Setting<Float> placeRange;
    public Setting<Float> placeRangeWall;
    public Setting<Integer> armorPercent;
    public Setting<Float> facePlaceHP;
    public Setting<Float> minDamage;
    public Setting<Float> maxSelfDamage;
    public Setting<Boolean> swing;
    public Setting<Boolean> announceOnly;
    public Setting<Boolean> text;
    public Setting<Boolean> box;
    public Setting<Enum> fade;
    public enum Enum{FAST, MEDIUM, SLOW, NONE}
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    public Setting<Boolean> rainbow;
    public Setting<Boolean> outline;
    public Setting<Integer> cRed;
    public Setting<Integer> cGreen;
    public Setting<Integer> cBlue;
    public Setting<Integer> cAlpha;
    public Setting<Integer> lineWidth;
    public Setting<Boolean> cRainbow;
    public Set<BlockPos> placeSet;
    public Timer clearTimer;
    public Timer breakTimer;
    public int predictedId;
    public BlockPos renderPos;
    public BlockPos pos2;
    public EntityPlayer target;
    public boolean offhand;
    public boolean mainhand;
    public static AutoCrystal INSTANCE;
    private final ArrayList<RenderPos> renderMap = new ArrayList<>();
    private final ArrayList<BlockPos> currentTargets = new ArrayList<>();

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places/breaks crystals to deal damage to opponents.", Category.COMBAT);
        this.setting = (Setting<Settings>)this.register(new Setting<>("Setting", Settings.AUTOCRYSTAL));
        this.speedFactor = (Setting<SpeedFactor>)this.register(new Setting<>("SpeedFactor", SpeedFactor.UPDATE));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", 5.0f, 1.0f, 6.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 5.0f, 1.0f, 6.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.targetRange = (Setting<Float>)this.register(new Setting("TargetRange",9.0f, 1.0f, 15.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.breakWallRange = (Setting<Float>)this.register(new Setting("BreakRangeWall", 5.0f, 1.0f, 6.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.placeRangeWall = (Setting<Float>)this.register(new Setting("PlaceRangeWall", 5.0f, 1.0f, 6.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", 0, 0, 200, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.instant = (Setting<Boolean>)this.register(new Setting("Predict", false, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.priority = (Setting<Priority>)this.register(new Setting("PrioritizeSelf", Priority.SELF, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.cancel = (Setting<Boolean>)this.register(new Setting("Cancel", true, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.armorPercent = (Setting<Integer>)this.register(new Setting("Armor%", 10, 0, 100, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.facePlaceHP = (Setting<Float>)this.register(new Setting("FaceplaceHP", 8.0f, 0.0f, 36.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", 4.0f, 1.0f, 36.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.maxSelfDamage = (Setting<Float>)this.register(new Setting("MaxSelfDmg",8.0f, 1.0f, 36.0f, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.swing = (Setting<Boolean>)this.register(new Setting("Swing", false, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.announceOnly = (Setting<Boolean>)this.register(new Setting("AnnounceOnly", false, v-> this.setting.getValue() == Settings.AUTOCRYSTAL));
        this.box = (Setting<Boolean>)this.register(new Setting<>("Box", true, v-> this.setting.getValue() == Settings.RENDER));
        this.fade = (Setting<Enum>)this.register(new Setting<>("Fade", Enum.FAST, v-> this.setting.getValue() == Settings.RENDER));
        this.red = (Setting<Integer>)this.register(new Setting<>("BoxRed", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.green = (Setting<Integer>)this.register(new Setting<>("BoxGreen", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.blue = (Setting<Integer>)this.register(new Setting<>("BoxBlue", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.alpha = (Setting<Integer>)this.register(new Setting<>("BoxAlpha", 120, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.rainbow = (Setting<Boolean>)this.register(new Setting<>("BoxRainbow", true, v-> this.setting.getValue() == Settings.RENDER));
        this.outline = (Setting<Boolean>)this.register(new Setting<>("Outline", true, v-> this.setting.getValue() == Settings.RENDER));
        this.cRed = (Setting<Integer>)this.register(new Setting<>("OutlineRed", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.cGreen = (Setting<Integer>)this.register(new Setting<>("OutlineGreen", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.cBlue = (Setting<Integer>)this.register(new Setting<>("OutlineBlue", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.cAlpha = (Setting<Integer>)this.register(new Setting<>("OutlineAlpha", 255, 0, 255, v-> this.setting.getValue() == Settings.RENDER));
        this.lineWidth = (Setting<Integer>)this.register(new Setting<>("OutlineWidth", 1, 0, 5, v-> this.setting.getValue() == Settings.RENDER));
        this.cRainbow = (Setting<Boolean>)this.register(new Setting<>("OutlineRainbow", true, v-> this.setting.getValue() == Settings.RENDER));
        this.placeSet = new HashSet<>();
        this.clearTimer = new Timer();
        this.breakTimer = new Timer();
        this.predictedId = -1;
        this.renderPos = null;
        this.pos2 = null;
        this.target = null;
        AutoCrystal.INSTANCE = this;
    }

    public static AutoCrystal getInstance() {
        return AutoCrystal.INSTANCE;
    }

    private boolean update() {
        if (fullNullCheck()) {
            return false;
        }
        if (this.clearTimer.hasReached(500L)) {
            this.placeSet.clear();
            this.predictedId = -1;
            this.renderPos = null;
            this.clearTimer.reset();
        }
        this.offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.mainhand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        return true;
    }

    @Override
    public void onToggle() {
        this.placeSet.clear();
        this.predictedId = -1;
        this.renderPos = null;
    }

    @Override
    public void onUpdate() {
        if (!this.update()) {
            return;
        }
        this.target = EntityUtil.getTarget(this.targetRange.getValue());
        if (this.target == null) {
            return;
        }
        if (speedFactor.getValue() == SpeedFactor.UPDATE && !announceOnly.getValue()) {
            this.doPlace();
            this.doBreak();
        }
    }
    @Override
    public void onTick() {
        if (speedFactor.getValue() == SpeedFactor.TICK && !announceOnly.getValue()) {
            this.doPlace();
            this.doBreak();
        }
    }


    private void doPlace() {
        BlockPos placePos = null;
        float maxDamage = 0.5f;
        final List<BlockPos> sphere = BlockUtil.getSphere(this.placeRange.getValue(), true);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            final float self = this.calculate(pos, mc.player);
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                final float damage;
                // ( If health is over self(damage your taking (+0.5hp))      && maxSelfDamage = over selfdamage      && damage(enemy) is over maxdamage(0.5)                    && damage is over the damage you take
                if (priority.getValue() == Priority.SELF) {
                    if (EntityUtil.getHealth(mc.player) > self + 0.5f && this.maxSelfDamage.getValue() > self && (damage = this.calculate(pos, this.target)) > maxDamage && damage > self) {
                        if (damage <= this.minDamage.getValue()) {
                            if (this.facePlaceHP.getValue() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getValue())) {
                                continue;
                            }
                            if (damage <= 2.0f) {
                                continue;
                            }
                        }
                        maxDamage = damage;
                        placePos = pos;
                        pos2 = placePos;
                        currentTargets.clear();
                        currentTargets.add(pos);
                    }
                } else if (priority.getValue() == Priority.ENEMY){
                    if (EntityUtil.getHealth(mc.player) > self + 0.5f && this.maxSelfDamage.getValue() > self && (damage = this.calculate(pos, this.target)) > maxDamage) {
                        if (damage <= this.minDamage.getValue()) {
                            if (this.facePlaceHP.getValue() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getValue())) {
                                continue;
                            }
                            if (damage <= 2.0f) {
                                continue;
                            }
                        }
                        maxDamage = damage;
                        placePos = pos;
                        pos2 = placePos;
                        currentTargets.clear();
                        currentTargets.add(pos);
                    }
                }
            }
        }
        if (!this.offhand && !this.mainhand) {
            this.renderPos = null;
            return;
        }
        if (placePos != null) {
            clearMap(placePos);
            renderMap.add(new RenderPos(placePos, 0.0));
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND , 0.5f, 0.5f, 0.5f));
            this.placeSet.add(placePos);
            this.renderPos = placePos;
        } else {
            this.renderPos = null;
        }
    }
    public void onLogout(){
        this.disable();
    }

    private void doBreak() {
        Entity entity = null;
        for (int size = mc.world.loadedEntityList.size(), i = 0; i < size; ++i) {
            final Entity crystal = mc.world.loadedEntityList.get(i);
            if (crystal.getClass() == EntityEnderCrystal.class && this.isValid(crystal)) {
                if (crystal.getEntityId() != this.predictedId) {
                    final float self = this.calculate(crystal, mc.player);
                    final float damage;
                    if (priority.getValue() == Priority.SELF) {
                        if (EntityUtil.getHealth(mc.player) > self + 0.5f && (damage = this.calculate(crystal, this.target)) > self && damage > self) {
                            if (damage <= this.minDamage.getValue()) {
                                if (this.facePlaceHP.getValue() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getValue())) {
                                    continue;
                                }
                                if (damage <= 2.0f) {
                                    continue;
                                }
                            }
                            entity = crystal;
                        }
                    } else if (priority.getValue() == Priority.ENEMY) {
                        if (EntityUtil.getHealth(mc.player) > self + 0.5f && (damage = this.calculate(crystal, this.target)) > self) {
                            if (damage <= this.minDamage.getValue()) {
                                if (this.facePlaceHP.getValue() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getValue())) {
                                    continue;
                                }
                                if (damage <= 2.0f) {
                                    continue;
                                }
                            }
                            entity = crystal;
                        }
                    }
                }
            }
            if (entity != null && this.breakTimer.passedMs(this.breakDelay.getValue())) {
                BlockPos renderPos = entity.getPosition().down();
                clearMap(renderPos);
                renderMap.add(new RenderPos(renderPos, 0.0));
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
                if(swing.getValue()) {
                    mc.player.swingArm(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }
                this.breakTimer.reset();
            }
        }
    }

    private boolean isValid(final Entity crystal) {
        return (mc.player.canEntityBeSeen(crystal) ? (this.breakRange.getValue() * this.breakRange.getValue()) : (this.breakWallRange.getValue() * this.breakWallRange.getValue())) > mc.player.getDistanceSq(crystal);
    }

    private float calculate(final Entity crystal, final EntityPlayer target) {
        return EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, target);
    }

    private float calculate(final BlockPos pos, final EntityPlayer entity) {
        return EntityUtil.calculate(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, entity);
    }

    public void instantHit(final int id) {
        final CPacketUseEntity hitPacket = new CPacketUseEntity();
        hitPacket.entityId = id;
        hitPacket.action = CPacketUseEntity.Action.ATTACK;
        Objects.requireNonNull(mc.getConnection()).sendPacket(hitPacket);
        this.predictedId = id;
        if(swing.getValue()) {
            mc.player.swingArm(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && this.instant.getValue()) {
            final Object packet = event.getPacket();
            final BlockPos pos = new BlockPos(((SPacketSpawnObject)packet).getX(), ((SPacketSpawnObject)packet).getY(), ((SPacketSpawnObject)packet).getZ());
            if (((SPacketSpawnObject)packet).getType() == 51 && this.placeSet.contains(pos.down())) {
                if (mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) > this.breakRange.getValue()) {
                    return;
                }
                this.instantHit(((SPacketSpawnObject)packet).getEntityID());
            }
        }
        Object packet;
        if (event.getPacket() instanceof SPacketSoundEffect && this.cancel.getValue() && ((SPacketSoundEffect)(packet = event.getPacket())).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect)packet).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            final ArrayList<Entity> entities = new ArrayList<>(mc.world.loadedEntityList);
            for (int size = entities.size(), i = 0; i < size; ++i) {
                final Entity entity = entities.get(i);
                if (entity instanceof EntityEnderCrystal) {
                    if (entity.getDistanceSq(((SPacketSoundEffect)packet).getX(), ((SPacketSoundEffect)packet).getY(), ((SPacketSoundEffect)packet).getZ()) < 36.0) {
                        entity.setDead();
                    }
                }
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {

        if (renderMap.isEmpty()) return;
        List<RenderPos> toRemove = new ArrayList<>();
        for (Iterator<RenderPos> it = renderMap.iterator(); it.hasNext(); ) {
            RenderPos renderPos = it.next();
            Color color;
            Color color2;
            color = new Color(red.getValue(), green.getValue(), blue.getValue(), (int) Math.max(alpha.getValue() - renderPos.alpha, 0));
            color2 = new Color(cRed.getValue(), cGreen.getValue(), cBlue.getValue(), (int) Math.max(cAlpha.getValue() - renderPos.alpha, 0));
            RenderUtil.drawBoxESP(renderPos.pos, rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : color , this.outline.getValue(), cRainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : color2, this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), (int) Math.max(cAlpha.getValue() - renderPos.alpha, 0), true);
            if (renderPos.alpha > Math.max(alpha.getValue(), rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue())))
                toRemove.add(renderPos);
            renderPos.alpha = renderPos.alpha + (fade.getValue() == Enum.FAST ? 1.5 : fade.getValue() == Enum.SLOW ? 0.5 : 1);
            if (currentTargets.contains(renderPos.pos)) {
                renderPos.alpha = 0;
            } else if (fade.getValue() == Enum.NONE) {
                toRemove.add(renderPos);
            }
        }
        renderMap.removeAll(toRemove);
    }
    static {
        AutoCrystal.INSTANCE = new AutoCrystal();
    }



    class RenderPos {
        public RenderPos(BlockPos pos, Double damage) {
            this.pos = pos;
            this.damage = damage;
        }

        Double damage;
        double alpha;
        BlockPos pos;
    }

    private void clearMap(BlockPos checkBlock) {
        List<RenderPos> toRemove = new ArrayList<>();
        if (checkBlock == null || renderMap.isEmpty()) return;
        for (RenderPos pos : renderMap) {
            if (pos.pos.getX() == checkBlock.getX() && pos.pos.getY() == checkBlock.getY() && pos.pos.getZ() == checkBlock.getZ())
                toRemove.add(pos);
        }
        renderMap.removeAll(toRemove);
    }
}
