package client.modules.render;

import client.modules.Module;
import client.setting.Setting;
import net.minecraft.client.settings.GameSettings;

public class Fov extends Module {
    public Setting<Float> fov = this.register( new Setting <> ( "Fov" , 150.0f , 0.0f , 180.0f ));

    public Fov() {
        super("Fov", "Changes ur FOV", Category.RENDER);
    }
    @Override
    public void onUpdate() {
        Fov.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue ( ) );
    }
}