package client.modules.visual;

import client.events.PacketEvent;
import client.events.RenderEntityModelEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalChanger extends Module {

    private static CrystalChanger INSTANCE;

public CrystalChanger(){
    super("CrystalChanger", "Modifies looks of end crystals.", Category.VISUAL);
    this.setInstance();
}

    private void setInstance() {
        CrystalChanger.INSTANCE = this;
    }

    public static CrystalChanger getInstance() {
        if (CrystalChanger.INSTANCE == null) {
            CrystalChanger.INSTANCE = new CrystalChanger();
        }
        return CrystalChanger.INSTANCE;
    }

    public Setting<Boolean> chams = this.register( new Setting <> ( "Chams" , true ));
    public Setting<Boolean> glint = this.register( new Setting <> ( "Glint" , true ));
    public Setting<Boolean> wireframe = this.register( new Setting <> ( "Wireframe" , true ));
    public Setting<Boolean> throughwalls = this.register( new Setting <> ( "Walls" , true ));
    public Setting<Boolean> XQZ = this.register( new Setting <> ( "Depth" , true ));

    public Setting<Integer> red = this.register( new Setting <> ( "Red" , 255 , 0 , 255 , v -> this.chams.getCurrentState( ) ));
    public Setting<Integer> green = this.register( new Setting <> ( "Green" , 255 , 0 , 255 , v -> this.chams.getCurrentState( ) ));
    public Setting<Integer> blue = this.register( new Setting <> ( "Blue" , 255 , 0 , 255 , v -> this.chams.getCurrentState( ) ));
    public Setting<Integer> alpha = this.register( new Setting <> ( "Alpha" , 150 , 0 , 255 , v -> this.chams.getCurrentState( ) ));

    public Setting<Integer> w_red = this.register( new Setting <> ( "WireframeRed" , 255 , 0 , 255 , v -> this.wireframe.getCurrentState( ) ));
    public Setting<Integer> w_green = this.register( new Setting <> ( "WireframeGreen" , 255 , 0 , 255 , v -> this.wireframe.getCurrentState( ) ));
    public Setting<Integer> w_blue = this.register( new Setting <> ( "WireframeBlue" , 255 , 0 , 255 , v -> this.wireframe.getCurrentState( ) ));
    public Setting<Integer> w_alpha = this.register( new Setting <> ( "WireframeAlpha" , 150 , 0 , 255 , v -> this.wireframe.getCurrentState( ) ));

    public Setting<Integer> h_red = this.register( new Setting <> ( "WallsRed" , 255 , 0 , 255 , v -> this.throughwalls.getCurrentState( ) ));
    public Setting<Integer> h_green = this.register( new Setting <> ( "WallsGreen" , 255 , 0 , 255 , v -> this.throughwalls.getCurrentState( ) ));
    public Setting<Integer> h_blue = this.register( new Setting <> ( "WallsBlue" , 255 , 0 , 255 , v -> this.throughwalls.getCurrentState( ) ));
    public Setting<Integer> h_alpha = this.register( new Setting <> ( "WallsAlpha" , 150 , 0 , 255 , v -> this.throughwalls.getCurrentState( ) ));

    public Setting<Double> width = this.register( new Setting <> ( "Width" , 3.0 , 0.1 , 5.0 ));
    public Setting<Double> scale = this.register( new Setting <> ( "Scale" , 1.0 , 0.1 , 3.0 ));
    public Map<EntityEnderCrystal, Float> scaleMap = new ConcurrentHashMap<>();
    private final int color = ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState());
    private final int wireColor = ColorUtil.toRGBA(w_red.getCurrentState(), w_green.getCurrentState(), w_blue.getCurrentState(), w_alpha.getCurrentState());
    private final int hiddenColor = ColorUtil.toRGBA(h_red.getCurrentState(), h_green.getCurrentState(), h_blue.getCurrentState(), h_alpha.getCurrentState());
    @Override
    public void onUpdate() {
        for (Entity crystal : mc.world.loadedEntityList) {
            if (crystal instanceof EntityEnderCrystal) {
                if (!this.scaleMap.containsKey(crystal)) {
                    this.scaleMap.put((EntityEnderCrystal) crystal, 3.125E-4f);
                } else {
                    try {
                        this.scaleMap.put((EntityEnderCrystal) crystal, this.scaleMap.get(crystal) + 3.125E-4f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!(this.scaleMap.get(crystal) >= 0.0625f * this.scale.getCurrentState()))
                    continue;
                this.scaleMap.remove(crystal);
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = event.getPacket();
            for (int id : packet.getEntityIDs()) {
                try {
                    Entity entity = mc.world.getEntityByID(id);
                    if (entity instanceof EntityEnderCrystal) {
                        this.scaleMap.remove(entity);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public void onRenderModel(RenderEntityModelEvent event) {
        if (event.getStage() != 0 || !(event.entity instanceof EntityEnderCrystal) || !this.wireframe.getCurrentState()) {
            return;
        }
        mc.gameSettings.fancyGraphics = false;
        mc.gameSettings.gammaSetting = 10000.0f;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glPolygonMode(1032, 6913);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        if (this.throughwalls.getCurrentState()) {
            GL11.glDisable(2929);
        }
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float) red.getCurrentState() / 255.0f, (float) green.getCurrentState() / 255.0f, (float) blue.getCurrentState() / 255.0f, (float) alpha.getCurrentState() / 255.0f);
        GlStateManager.glLineWidth(this.width.getCurrentState().floatValue());
        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}
