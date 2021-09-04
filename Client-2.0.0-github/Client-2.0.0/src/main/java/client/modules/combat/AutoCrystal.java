package client.modules.combat;

import client.events.PacketEvent;
import client.events.Render3DEvent;
import client.modules.Module;
import client.modules.client.ClickGui;
import client.gui.impl.setting.Setting;
import client.util.Timer;
import client.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
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
    public Setting<CalcMode> calcMode;
    public enum CalcMode{NORMAL, AUTO}
    public Setting<Boolean> doBreak;
    public Setting<Boolean> doPlace;
    public Setting<Float> targetRange;
    public Setting<Boolean> cancel;
    public Setting<Float> breakRange;
    public Setting<Float> breakWallRange;
    public Setting<Integer> breakDelay;
    public Setting<Boolean> instant;
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
    public Setting<RenderMode> renderMode;
    public enum RenderMode{NORMAL, FADE, GLIDE}
    public Setting<Float> accel;
    public Setting<Float> moveSpeed;
    public Setting<Enum> fade;
    public enum Enum{FAST, MEDIUM, SLOW}
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
    public Setting<Boolean> silentSwitch;
    public Set<BlockPos> placeSet;
    public  BlockPos placePos = null;
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
    private BlockPos lastRenderPos;
    private AxisAlignedBB renderBB;
    private float timePassed;

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places/breaks crystals to deal damage to opponents.", Category.COMBAT);
        this.setting = (Setting<Settings>)this.register(new Setting<>("Setting", Settings.AUTOCRYSTAL));
        this.speedFactor = (Setting<SpeedFactor>)this.register(new Setting<>("SpeedFactor", SpeedFactor.UPDATE, v-> setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.calcMode = (Setting<CalcMode>)this.register(new Setting<>("CalcMode", CalcMode.NORMAL, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.doPlace = (Setting<Boolean>)this.register(new Setting("Place", true, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.doBreak = (Setting<Boolean>)this.register(new Setting("Break", true, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", 5.0f, 1.0f, 6.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 5.0f, 1.0f, 6.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.targetRange = (Setting<Float>)this.register(new Setting("TargetRange",9.0f, 1.0f, 15.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.breakWallRange = (Setting<Float>)this.register(new Setting("BreakRangeWall", 5.0f, 1.0f, 6.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.placeRangeWall = (Setting<Float>)this.register(new Setting("PlaceRangeWall", 5.0f, 1.0f, 6.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", 0, 0, 200, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.instant = (Setting<Boolean>)this.register(new Setting("Predict", false, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.cancel = (Setting<Boolean>)this.register(new Setting("Cancel", true, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.armorPercent = (Setting<Integer>)this.register(new Setting("Armor%", 10, 0, 100, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.facePlaceHP = (Setting<Float>)this.register(new Setting("FaceplaceHP", 8.0f, 0.0f, 36.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", 4.0f, 1.0f, 36.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.maxSelfDamage = (Setting<Float>)this.register(new Setting("MaxSelfDmg",8.0f, 1.0f, 36.0f, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.silentSwitch = (Setting<Boolean>)this.register(new Setting("MaxSelfDmg",true, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.swing = (Setting<Boolean>)this.register(new Setting("Swing", false, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.announceOnly = (Setting<Boolean>)this.register(new Setting("AnnounceOnly", false, v-> this.setting.getCurrentState() == Settings.AUTOCRYSTAL));
        this.renderMode = (Setting<RenderMode>)this.register(new Setting<>("RenderMode", RenderMode.NORMAL, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.box = (Setting<Boolean>)this.register(new Setting<>("Box", true, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.fade = (Setting<Enum>)this.register(new Setting<>("Fade", Enum.FAST, v-> this.setting.getCurrentState() == Settings.RENDER && renderMode.getCurrentState() == RenderMode.FADE));
        this.accel = (Setting<Float>) this.register(new Setting<>("Deceleration" , 0.8f , 0.0f, 1.0f, v-> this.setting.getCurrentState() == Settings.RENDER && renderMode.getCurrentState() == RenderMode.GLIDE));
        this.moveSpeed = (Setting<Float>) this.register(new Setting<>("Speed" , 900.0f , 0.0f, 1500.0f, v-> this.setting.getCurrentState() == Settings.RENDER && renderMode.getCurrentState() == RenderMode.GLIDE));
        this.red = (Setting<Integer>)this.register(new Setting<>("BoxRed", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.green = (Setting<Integer>)this.register(new Setting<>("BoxGreen", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.blue = (Setting<Integer>)this.register(new Setting<>("BoxBlue", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.alpha = (Setting<Integer>)this.register(new Setting<>("BoxAlpha", 120, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.rainbow = (Setting<Boolean>)this.register(new Setting<>("BoxRainbow", true, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.outline = (Setting<Boolean>)this.register(new Setting<>("Outline", true, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.cRed = (Setting<Integer>)this.register(new Setting<>("OutlineRed", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.cGreen = (Setting<Integer>)this.register(new Setting<>("OutlineGreen", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.cBlue = (Setting<Integer>)this.register(new Setting<>("OutlineBlue", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.cAlpha = (Setting<Integer>)this.register(new Setting<>("OutlineAlpha", 255, 0, 255, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.lineWidth = (Setting<Integer>)this.register(new Setting<>("OutlineWidth", 1, 0, 5, v-> this.setting.getCurrentState() == Settings.RENDER));
        this.cRainbow = (Setting<Boolean>)this.register(new Setting<>("OutlineRainbow", true, v-> this.setting.getCurrentState() == Settings.RENDER));
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
        this.target = EntityUtil.getTarget(this.targetRange.getCurrentState());
        if (this.target == null) {
            return;
        }
        if (speedFactor.getCurrentState() == SpeedFactor.UPDATE && !announceOnly.getCurrentState()) {
            if(doPlace.getCurrentState()) {
                this.doPlace();
            }
            if(doBreak.getCurrentState()) {
                this.doBreak();
            }
        }
    }
    public void onDisable(){
        this.lastRenderPos = null;
    }
    @Override
    public void onTick() {
        if (speedFactor.getCurrentState() == SpeedFactor.TICK && !announceOnly.getCurrentState()) {
            if(doPlace.getCurrentState()) {
                this.doPlace();
            }
            if(doBreak.getCurrentState()) {
                this.doBreak();
            }
        }
    }


    private void doPlace() {
        float maxDamage = 0.5f;
        final List<BlockPos> sphere = BlockUtil.getSphere(this.placeRange.getCurrentState(), true);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            final float self = this.calculate(pos, mc.player);
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                final float damage;
                if (calcMode.getCurrentState() == CalcMode.NORMAL) {
                    if (EntityUtil.getHealth(mc.player) > self + 0.5f && this.maxSelfDamage.getCurrentState() > self && (damage = this.calculate(pos, this.target)) > maxDamage && damage > self) {
                        if (damage <= this.minDamage.getCurrentState()) {
                            if (this.facePlaceHP.getCurrentState() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getCurrentState())) {
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
                } else if (calcMode.getCurrentState() == CalcMode.AUTO) {
                    if ((damage = this.calculate(pos, this.target)) > self && damage > minDamage.getCurrentState() && self < maxSelfDamage.getCurrentState()) {
                        if (damage <= this.minDamage.getCurrentState()) {
                            if (this.facePlaceHP.getCurrentState() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getCurrentState())) {
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
                } {

                }
            }

        }
        if (!this.offhand && !this.mainhand) {
            this.renderPos = null;
            return;
        }
        if (placePos != null && silentSwitch.getCurrentState()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.findHotbarBlock(ItemEndCrystal.class)));
        }
        if (placePos != null) {
            clearMap(placePos);
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND , 0.5f, 0.5f, 0.5f));
            renderMap.add(new RenderPos(placePos, 0.0));
            this.placeSet.add(placePos);
            this.renderPos = placePos;
        } else {
            this.renderPos = null;
        }
        if (silentSwitch.getCurrentState()) {
            int oldSlot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
    }

    private void doBreak() {
        Entity entity = null;
        for (int size = mc.world.loadedEntityList.size(), i = 0; i < size; ++i) {
            final Entity crystal = mc.world.loadedEntityList.get(i);
            if (crystal.getClass() == EntityEnderCrystal.class && this.isValid(crystal)) {
                if (crystal.getEntityId() != this.predictedId) {
                    final float self = this.calculate(crystal, mc.player);
                    final float damage;
                    if (EntityUtil.getHealth(mc.player) > self + 0.5f && (damage = this.calculate(crystal, this.target)) > self && damage > self) {
                        if (damage <= this.minDamage.getCurrentState()) {
                            if (this.facePlaceHP.getCurrentState() <= EntityUtil.getHealth(this.target) && !PlayerUtil.isArmorLow(this.target, this.armorPercent.getCurrentState())) {
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
            if (entity != null && this.breakTimer.passedMs(this.breakDelay.getCurrentState())) {
                BlockPos renderPos = entity.getPosition().down();
                clearMap(renderPos);
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
                renderMap.add(new RenderPos(renderPos, 0.0));
                if(swing.getCurrentState()) {
                    mc.player.swingArm(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }
                this.breakTimer.reset();
            }
        }
    }

    private boolean isValid(final Entity crystal) {
        return (mc.player.canEntityBeSeen(crystal) ? (this.breakRange.getCurrentState() * this.breakRange.getCurrentState()) : (this.breakWallRange.getCurrentState() * this.breakWallRange.getCurrentState())) > mc.player.getDistanceSq(crystal);
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
        if(swing.getCurrentState()) {
            mc.player.swingArm(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && this.instant.getCurrentState()) {
            final Object packet = event.getPacket();
            final BlockPos pos = new BlockPos(((SPacketSpawnObject)packet).getX(), ((SPacketSpawnObject)packet).getY(), ((SPacketSpawnObject)packet).getZ());
            if (((SPacketSpawnObject)packet).getType() == 51 && this.placeSet.contains(pos.down())) {
                if (mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) > this.breakRange.getCurrentState()) {
                    return;
                }
                this.instantHit(((SPacketSpawnObject)packet).getEntityID());
            }
        }
        Object packet;
        if (event.getPacket() instanceof SPacketSoundEffect && this.cancel.getCurrentState() && ((SPacketSoundEffect)(packet = event.getPacket())).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect)packet).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
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
            color = new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), (int) Math.max(alpha.getCurrentState() - renderPos.alpha, 0));
            color2 = new Color(cRed.getCurrentState(), cGreen.getCurrentState(), cBlue.getCurrentState(), (int) Math.max(cAlpha.getCurrentState() - renderPos.alpha, 0));
            if(renderMode.getCurrentState() == RenderMode.NORMAL || renderMode.getCurrentState() == RenderMode.FADE) {
                RenderUtil.drawBoxESP(renderPos.pos, rainbow.getCurrentState() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getCurrentState()) : color, this.outline.getCurrentState(), cRainbow.getCurrentState() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getCurrentState()) : color2, this.lineWidth.getCurrentState(), this.outline.getCurrentState(), this.box.getCurrentState(), (int) Math.max(cAlpha.getCurrentState() - renderPos.alpha, 0), true);
            }
            if (renderPos.alpha > Math.max(alpha.getCurrentState(), rainbow.getCurrentState() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getCurrentState()).getRGB() : ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState())))
                toRemove.add(renderPos);
            renderPos.alpha = renderPos.alpha + (fade.getCurrentState() == Enum.FAST ? 1.5 : fade.getCurrentState() == Enum.SLOW ? 0.5 : 1);
            if (currentTargets.contains(renderPos.pos)) {
                renderPos.alpha = 0;
            } else if (renderMode.getCurrentState() != RenderMode.FADE) {
                toRemove.add(renderPos);
            }
        }
        renderMap.removeAll(toRemove);

        if (renderMode.getCurrentState() == RenderMode.GLIDE && renderPos != null) {
            Color color2 = new Color(cRed.getCurrentState(), cGreen.getCurrentState(), cBlue.getCurrentState(), cAlpha.getCurrentState());
            Color color = new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState());
            if ( this.lastRenderPos == null || AutoCrystal.mc.player.getDistance (this.renderBB.minX , this.renderBB.minY , this.renderBB.minZ ) > this.placeRange.getCurrentState() ) {
                this.lastRenderPos = this.renderPos;
                this.renderBB = new AxisAlignedBB( this.renderPos );
                this.timePassed = 0;
            }
            if ( !this.lastRenderPos.equals ( this.renderPos ) ) {
                this.lastRenderPos = this.renderPos;
                this.timePassed = 0;
            }
            double xDiff = this.renderPos.getX ( ) - this.renderBB.minX;
            double yDiff = this.renderPos.getY ( ) - this.renderBB.minY;
            double zDiff = this.renderPos.getZ ( ) - this.renderBB.minZ;
            float multiplier = this.timePassed / this.moveSpeed.getCurrentState ( ) * this.accel.getCurrentState ( );
            if ( multiplier > 1 ) multiplier = 1;
            this.renderBB = this.renderBB.offset ( xDiff * multiplier , yDiff * multiplier , zDiff * multiplier );
                RenderUtil.drawPerryESP(this.renderBB, color, color2, lineWidth.getCurrentState(), outline.getCurrentState(), box.getCurrentState(), 1.0f, 1.0f, 1.0f);
            if ( this.renderBB.equals ( new AxisAlignedBB ( this.renderPos ) ) ) {
                this.timePassed = 0;
            } else this.timePassed += 50.0f;
        }

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

    public String hudInfoString() {
        if(target != null) {
            return target.getName() + " | " + Math.round(target.getHealth());
        }
        return null;
    }
}
