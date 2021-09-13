package client.modules.visual;

import client.Client;
import client.events.Render3DEvent;
import client.events.TotemPopEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.RenderUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static client.util.RenderUtil.renderEntity;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class PopChamsRewrite extends Module {
    EntityPlayer player = null;
    ModelPlayer playerModel = null;
    long startTime;
    public static PopChamsRewrite INSTANCE = new PopChamsRewrite();

    /**
     * from: saturn
     * skidded on 9/9/21
     * by kambing
     */

    public Setting<Boolean> self = register(new Setting<>("Self", true));
    public Setting<Integer> fadeStart = register(new Setting<>("FadeTime", 1000, 0, 5000));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255));

    public PopChamsRewrite() {
        super("PopChamsRewrite", "Rewrote Op module.", Category.VISUAL);}

        public void k(EntityPlayer poppedPlayer) {
            if (self.getCurrentState() || poppedPlayer != mc.player) {
                GameProfile profile = new GameProfile(mc.player.getUniqueID(), "");
                player = new EntityOtherPlayerMP(mc.world, profile);
                player.copyLocationAndAnglesFrom(poppedPlayer);
                startTime = System.currentTimeMillis();
            }
        }
        @Override
        public void onRender3D(Render3DEvent event) {
            playerModel = new ModelPlayer(0, false);

            GL11.glLineWidth(1);

            int lineA = 255;
            int fillA = alpha.getCurrentState();

            if (System.currentTimeMillis() - startTime > fadeStart.getCurrentState()) {
                long time = System.currentTimeMillis() - startTime - fadeStart.getCurrentState();
                double normal = normalize(((double) time), 0, fadeStart.getCurrentState());
                normal = MathHelper.clamp(normal, 0, 1);
                normal = (-normal) + 1;
                lineA = (int) (normal * lineA);
                fillA = (int) (normal * fillA);
            }
            //Color lineColor = new Color(PopChams.red.getValInt(), PopChams.green.getValInt(), PopChams.blue.getValInt(), lineA);
            //Color fillColor = new Color(PopChams.red.getValInt(), PopChams.green.getValInt(), PopChams.blue.getValInt(), fillA);

            if (player != null) {
                RenderUtil.RenderTesselator.prepareGL();
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                glDisable(3553);
                glDisable(2896);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f(red.getCurrentState()/255.0f, green.getCurrentState()/255.0f, blue.getCurrentState()/255.0f, fillA/255.0f);
                renderEntity(player, playerModel, 0, 0, player.ticksExisted, player.rotationYawHead, player.rotationPitch, 1);
                glDisable(2896);
                glEnable(2929);
                renderEntity(player, playerModel, 0, 0, player.ticksExisted, player.rotationYawHead, player.rotationPitch, 1);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
                RenderUtil.RenderTesselator.releaseGL();
            }
        }

    double normalize(double value, double min, double max) {
        return ((value - min) / (max - min));

        }
        @Override
        public void onEnable(){
        k(mc.player);
        }
}


