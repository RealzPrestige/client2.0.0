package client.modules.movement;

import client.events.MoveEvent;
import client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
    private static Sprint INSTANCE = new Sprint();

    public Sprint() {
        super("Sprint", "Modifies sprinting", Module.Category.MOVEMENT);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Sprint getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sprint();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSprint(MoveEvent event) {
        if (event.getStage() == 1 || Sprint.mc.player.movementInput.moveStrafe != 0.0f) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (!Sprint.mc.gameSettings.keyBindForward.isKeyDown() && !Sprint.mc.gameSettings.keyBindBack.isKeyDown() && !Sprint.mc.gameSettings.keyBindLeft.isKeyDown() && !Sprint.mc.gameSettings.keyBindRight.isKeyDown() || Sprint.mc.player.isSneaking() || Sprint.mc.player.collidedHorizontally || (float) Sprint.mc.player.getFoodStats().getFoodLevel() <= 6.0f)
            return;
        Sprint.mc.player.setSprinting(true);
        return;
    }

    @Override
    public void onDisable() {
        if (!Sprint.nullCheck()) {
            Sprint.mc.player.setSprinting(false);
        }
    }

    @Override
    public String getDisplayInfo() {
        if (!Sprint.mc.gameSettings.keyBindForward.isKeyDown() && !Sprint.mc.gameSettings.keyBindBack.isKeyDown() && !Sprint.mc.gameSettings.keyBindLeft.isKeyDown() && !Sprint.mc.gameSettings.keyBindRight.isKeyDown() || Sprint.mc.player.isSneaking() || Sprint.mc.player.collidedHorizontally || (float) Sprint.mc.player.getFoodStats().getFoodLevel() <= 6.0f)
            return null;
        else return "Sprinting";
    }
}
