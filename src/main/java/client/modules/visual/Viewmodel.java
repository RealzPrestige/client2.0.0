package client.modules.visual;


import client.events.PacketEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Viewmodel extends Module {
    public Setting<Float> sizeX = register(new Setting("SizeX", 1f, 0f, 2f));
    public Setting<Float> sizeY = register(new Setting("SizeY", 1f, 0f, 2f));
    public Setting<Float> sizeZ = register(new Setting("SizeZ", 1f, 0f, 2f));
    public Setting<Float> offsetX = register(new Setting("OffsetX", 0.0f, -1.0f, 1.0f));
    public Setting<Float> offsetY = register(new Setting("OffsetY", 0.0f, -1.0f, 1.0f));
    public Setting<Float> offsetZ = register(new Setting("OffsetZ", 0.0f, -1.0f, 1.0f));
    public Setting<Float> offhandX = register(new Setting("OffhandX", 0.0f, -1.0f, 1.0f));
    public Setting<Float> offhandY = register(new Setting("OffhandY", 0.0f, -1.0f, 1.0f));
    public Setting<Float> offhandZ = register(new Setting("OffhandZ", 0.0f, -1.0f, 1.0f));
    private static Viewmodel INSTANCE = new Viewmodel();

    public Viewmodel() {
        super("Viewmodel", "Tweaks the size and positions of items in your hand.", Category.VISUAL);
        setInstance();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Send event){
        if(event.getPacket() instanceof CPacketAnimation){

        }
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