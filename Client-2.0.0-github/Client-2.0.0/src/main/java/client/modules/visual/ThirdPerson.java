package client.modules.visual;


import client.modules.Module;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import org.lwjgl.input.Keyboard;

public class ThirdPerson extends Module {
    public Setting<Boolean> onlyHold = this.register( new Setting <> ( "OnlyHoldBind" , false ));
    public Setting<Bind> bind = this.register(new Setting<Object>("Bind:", new Bind(-1)));
    public ThirdPerson() {
        super("ThirdPerson","Third person camera but hold bind.", Category.VISUAL);
    }
    @Override
    public void onUpdate(){
        if(this.bind.getValue().getKey() > -1) {
            if (Keyboard.isKeyDown(this.bind.getValue().getKey())) {
                mc.gameSettings.thirdPersonView = 1;
            } else {
                mc.gameSettings.thirdPersonView = 0;
            }
        }
    }
    @Override
    public void onEnable(){
        if(!this.onlyHold.getValue()){
            mc.gameSettings.thirdPersonView = 1;
        }
    }
    @Override
    public void onDisable(){
        if(!this.onlyHold.getValue()){
            mc.gameSettings.thirdPersonView = 0;
        }
    }
}
