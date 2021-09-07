package client.modules.visual;


import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import org.lwjgl.input.Keyboard;

public class ThirdPerson extends Module {
    public Setting<Boolean> onlyHold = this.register( new Setting <> ( "OnlyHoldBind" , false ));
    public Setting<Bind> bind = this.register(new Setting<Object>("Bind:", new Bind(-1)));
    public ThirdPerson() {
        super("ThirdPerson","Third person but using a hold bind.", Category.VISUAL);
    }
    @Override
    public void onUpdate(){
        if(this.bind.getCurrentState().getKey() > -1) {
            if (Keyboard.isKeyDown(this.bind.getCurrentState().getKey())) {
                mc.gameSettings.thirdPersonView = 1;
            } else {
                mc.gameSettings.thirdPersonView = 0;
            }
        }
    }
    @Override
    public void onEnable(){
        if(!this.onlyHold.getCurrentState()){
            mc.gameSettings.thirdPersonView = 1;
        }
    }
    @Override
    public void onDisable(){
        if(!this.onlyHold.getCurrentState()){
            mc.gameSettings.thirdPersonView = 0;
        }
    }
}
