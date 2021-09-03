package client.modules.movement;

import client.events.KeyEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    private static NoSlow INSTANCE = new NoSlow();
    public Setting<Boolean> items = this.register(new Setting("Items", false));
    public Setting<Boolean> guiMove = this.register(new Setting("Inventory", false));
    public Setting<Boolean> webs = this.register(new Setting("Webs", false));
    private static final KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};

    public NoSlow(){
        super("NoSlow", "Stops packets that slow you down.", Category.MOVEMENT);
        this.setInstance();
    }

    public static NoSlow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoSlow();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onLogin(){
        if(this.isEnabled()){
            this.disable();
            this.enable();
        }
    }
    @Override
    public void onUpdate() {
        if ( this.guiMove.getCurrentState( ) ) {
            if (mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiVideoSettings || mc.currentScreen instanceof GuiScreenOptionsSounds || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu) {
                for (KeyBinding bind : keys) {
                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                }
            } else if (mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
        if(webs.getCurrentState() && mc.player.isInWeb){
            mc.player.isInWeb = false;
        }
    }
    @SubscribeEvent
    public void onItemEat(InputUpdateEvent event) {
        if ( this.items.getCurrentState( ) && mc.player.isHandActive()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }
    @SubscribeEvent
    public void onKeyEvent(KeyEvent event) {
        if ( this.guiMove.getCurrentState( ) && event.getStage() == 0 && !(mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }
}
