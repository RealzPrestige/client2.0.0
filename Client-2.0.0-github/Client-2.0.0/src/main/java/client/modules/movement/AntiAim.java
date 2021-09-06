package client.modules.movement;

import client.modules.Module;
import java.util.Random;

public class AntiAim extends Module {

    public AntiAim() {
        super("AntiAim", "Spins ur head in random directions.", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        Random random = new Random();
        mc.player.rotationPitch = random.nextInt(150);
        mc.player.rotationYaw = random.nextInt(150);
    }
}