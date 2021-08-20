package client.modules.movement;

import client.events.MoveEvent;
import client.events.UpdateWalkingPlayerEvent;
import client.modules.Module;
import client.setting.Setting;
import client.util.PlayerUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFlight extends Module {
    public Setting<Double> speed = this.register(new Setting("Speed", 1.0, 0.1, 10.0));
    public Setting<Double> speedDown = this.register(new Setting("SpeedDown", 1.0, 0.1, 10.0));

    public ElytraFlight() {
        super("ElytraFlight", "Allows travel with Elytras to be more easy.", Category.MOVEMENT);
    }


    @SubscribeEvent
    public void onMove(MoveEvent event) {
            if (!mc.player.isElytraFlying()) {
                return;
            }
                if (!mc.player.movementInput.jump) {
                    if (mc.player.movementInput.sneak) {
                        mc.player.motionY = -speedDown.getValue();
                    } else if (event.getY() != -1.01E-4) {
                        event.setY(-1.01E-4);
                        ElytraFlight.mc.player.motionY = -1.01E-4;
                    }
                } else {
                    return;
                }
            PlayerUtil.setMoveSpeed(event, speed.getValue());
    }

    public void onTick() {
        if (!ElytraFlight.mc.player.isElytraFlying()) {
            return;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (ElytraFlight.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
            return;
        }
    }

    public void onLogin(){
        if(this.isEnabled()){
            this.disable();
            this.enable();
        }
    }

    public void onDisable() {
        if (ElytraFlight.fullNullCheck() || ElytraFlight.mc.player.capabilities.isCreativeMode) {
            return;
        }
        ElytraFlight.mc.player.capabilities.isFlying = false;
    }
}