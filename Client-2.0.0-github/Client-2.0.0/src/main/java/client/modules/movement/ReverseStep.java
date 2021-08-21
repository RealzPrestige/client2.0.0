package client.modules.movement;

import client.modules.Module;
import client.gui.impl.setting.Setting;

public class ReverseStep extends Module {
    private final Setting<Integer> speed = this.register( new Setting <> ( "Speed" , 0 , 0 , 20 ));

    public ReverseStep() {
        super("ReverseStep", "Speeds up downwards motion", Category.MOVEMENT);
    }

    @Override
    public void onUpdate(){
        if (ReverseStep.mc.player.isInLava() || ReverseStep.mc.player.isInWater() || Phase.getInstance().isOn()) {
            return;
        }
        if (mc.player.onGround){
            ReverseStep.mc.player.motionY -= this.speed.getValue();
        }
    }
}