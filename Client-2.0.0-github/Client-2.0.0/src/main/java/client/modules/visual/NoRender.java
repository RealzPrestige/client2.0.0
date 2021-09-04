package client.modules.visual;

import client.events.PacketEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
    static NoRender INSTANCE = new NoRender();
    public Setting<Boolean> hurtcam = this.register(new Setting<>("HurtCam", false));
    public Setting<Boolean> fire = register(new Setting<>("Fire", false));
    public Setting<Boolean> insideBlocks = this.register(new Setting<>("InsideBlocks", false));
    public Setting<Boolean> explosions = this.register(new Setting<>("Explosions", false));
    public NoRender(){
        super("NoRenders", "Prevents things from being rendered.", Category.VISUAL);
        this.setInstance();
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(final RenderBlockOverlayEvent event) {
        if (fire.getCurrentState()) {
            if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE)) {
                event.setCanceled(true);
            }
        }
        if(insideBlocks.getCurrentState()){
            if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.BLOCK)) {
                event.setCanceled(true);
            }
        }
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if(explosions.getCurrentState() && event.getPacket() instanceof SPacketExplosion){
            event.setCanceled(true);
        }
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


}
