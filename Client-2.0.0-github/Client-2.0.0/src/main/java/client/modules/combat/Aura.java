package client.modules.combat;
import client.Client;
import client.events.UpdateWalkingPlayerEvent;
import client.modules.Module;
import client.setting.Setting;
import client.util.EntityUtil;
import client.util.MathUtil;
import client.util.PlayerUtil;
import client.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aura extends Module {
    public static Entity target;
    private final Timer timer = new Timer();
    public Setting<Float> range = register(new Setting("Range", 6.0F, 0.1F, 7.0F));
    public Setting<Float> wallRange = register(new Setting("WallRange", 6.0F, 0.1F, 7.0F));
    public Setting<Boolean> delay = register(new Setting("Delay", true));
    public Setting<Boolean> rotate = register(new Setting("Rotate", true));
    public Setting<Boolean> swordOnly = register(new Setting("SwordOnly", true));
    public Setting<Boolean> players = register(new Setting("Players", true));
    public Setting<Boolean> mobs = register(new Setting("Mobs", false));
    public Setting<Boolean> animals = register(new Setting("Animals", false));
    public Setting<Boolean> vehicles = register(new Setting("Entities", false));
    public Setting<Boolean> projectiles = register(new Setting("Projectiles", false));
    public Setting<Boolean> tps = register(new Setting("TpsSync", true));
    public Setting<Boolean> packet = register(new Setting("Packet", false));

    public Aura() {
        super("Aura", "Attacks enemies.", Category.COMBAT);
    }

    public void onTick() {
        if (!this.rotate.getValue())
            attackEnemy();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue())
            attackEnemy();
    }

    private void attackEnemy() {
        if (this.swordOnly.getValue() && !EntityUtil.holdingWeapon(mc.player)) {
            target = null;
            return;
        }
        int wait = !this.delay.getValue() ? 0 : (int) (EntityUtil.getCooldownByWeapon(mc.player) * (tps.getValue() ? Client.serverManager.getTpsFactor() : 1.0F));
        if (!this.timer.passedMs(wait))
            return;
        target = getTarget();
        if (target == null)
            return;
        if (this.rotate.getValue())
            Client.rotationManager.lookAtEntity(target);
        EntityUtil.attackEntity(target, this.packet.getValue(), true);
        this.timer.reset();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue();
        double maxHealth = 36.0D;
        for (Entity entity : mc.world.playerEntities) {
            if (((!this.players.getValue() || !(entity instanceof EntityPlayer)) && (!this.animals.getValue() || !EntityUtil.isPassive(entity)) && (!this.mobs.getValue() || !EntityUtil.isMobAggressive(entity)) && (!this.vehicles.getValue() || !EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue() || !EntityUtil.isProjectile(entity))) || (entity instanceof net.minecraft.entity.EntityLivingBase &&
                    EntityUtil.isntValid(entity, distance)))
                continue;
            if (!mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && mc.player.getDistanceSq(entity) > MathUtil.square(this.wallRange.getValue()))
                continue;
            if (target == null) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && PlayerUtil.isArmorLow((EntityPlayer) entity, 18)) {
                target = entity;
                break;
            }
            if (mc.player.getDistanceSq(entity) < distance) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (EntityUtil.getHealth(entity) < maxHealth) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
        }
        return target;
    }

    public String getDisplayInfo() {
        if (target instanceof EntityPlayer)
            return target.getName();
        return null;
    }
}