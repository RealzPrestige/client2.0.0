package client.modules.render;


import client.modules.Module;
import client.setting.Setting;

public class Viewmodel extends Module {
    public Setting<Float> sizeX = register(new Setting("SizeX", 1f, 0f, 2f));
    public Setting<Float> sizeY = register(new Setting("SizeY", 1f, 0f, 2f));
    public Setting<Float> sizeZ = register(new Setting("SizeZ", 1f, 0f, 2f));
    public final Setting<Float> offsetX;
    public final Setting<Float> offsetY;
    public final Setting<Float> offsetZ;
    public final Setting<Float> offhandX;
    public final Setting<Float> offhandY;
    public final Setting<Float> offhandZ;
    private static Viewmodel INSTANCE = new Viewmodel();

    public Viewmodel() {
        super("Viewmodel", "Changes to the size and positions of your hands.", Category.RENDER);
        this.offsetX = (Setting<Float>)this.register(new Setting("OffsetX", 0.0f, (-1.0f), 1.0f));
        this.offsetY = (Setting<Float>)this.register(new Setting("OffsetY", 0.0f, (-1.0f), 1.0f));
        this.offsetZ = (Setting<Float>)this.register(new Setting("OffsetZ", 0.0f, (-1.0f), 1.0f));
        this.offhandX = (Setting<Float>)this.register(new Setting("OffhandX", 0.0f, (-1.0f), 1.0f));
        this.offhandY = (Setting<Float>)this.register(new Setting("OffhandY", 0.0f, (-1.0f), 1.0f));
        this.offhandZ = (Setting<Float>)this.register(new Setting("OffhandZ", 0.0f, (-1.0f), 1.0f));
        setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }
    public static Viewmodel getINSTANCE() {
        if(INSTANCE == null) {
            INSTANCE = new Viewmodel();
        }
        return INSTANCE;
    }

}