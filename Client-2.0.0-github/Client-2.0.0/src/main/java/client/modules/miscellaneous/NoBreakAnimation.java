package client.modules.miscellaneous;

import client.events.PacketEvent;
import client.gui.impl.Item;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoBreakAnimation extends Module {
    public Setting<Boolean> onlyPickaxe = this.register(new Setting("OnlyPickaxe", false));
    private boolean isMining;
    private BlockPos lastPos;
    private EnumFacing lastFacing;

    public NoBreakAnimation() {
        super("NoBreakAnimation", "Prevents serverside break animations.", Category.MISC);
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if(onlyPickaxe.getCurrentState()) {
            if (event.getPacket() instanceof CPacketPlayerDigging && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE) {
                CPacketPlayerDigging cPacketPlayerDigging = event.getPacket();
                final Iterator<Entity> iterator = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(cPacketPlayerDigging.getPosition())).iterator();
                Entity entity;
                while (iterator.hasNext()) {
                    entity = iterator.next();
                    if (entity instanceof EntityEnderCrystal) {
                        this.resetMining();
                        return;
                    } else if (entity instanceof EntityLivingBase) {
                        this.resetMining();
                        return;
                    } else {
                        continue;
                    }
                }
                if (cPacketPlayerDigging.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                    this.isMining = true;
                    this.setMiningInfo(cPacketPlayerDigging.getPosition(), cPacketPlayerDigging.getFacing());
                }
                if (cPacketPlayerDigging.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                    this.resetMining();
                }
            }
        } else {
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                CPacketPlayerDigging cPacketPlayerDigging = event.getPacket();
                final Iterator<Entity> iterator = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(cPacketPlayerDigging.getPosition())).iterator();
                Entity entity;
                while (iterator.hasNext()) {
                    entity = iterator.next();
                    if (entity instanceof EntityEnderCrystal) {
                        this.resetMining();
                        return;
                    } else if (entity instanceof EntityLivingBase) {
                        this.resetMining();
                        return;
                    } else {
                        continue;
                    }
                }
                if (cPacketPlayerDigging.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                    this.isMining = true;
                    this.setMiningInfo(cPacketPlayerDigging.getPosition(), cPacketPlayerDigging.getFacing());
                }
                if (cPacketPlayerDigging.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                    this.resetMining();
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.resetMining();
            return;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
    }

    private void setMiningInfo(final BlockPos lastPos, final EnumFacing lastFacing) {
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void resetMining() {
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
    }
}