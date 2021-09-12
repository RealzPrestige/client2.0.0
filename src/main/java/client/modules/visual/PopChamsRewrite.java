package client.modules.visual;

import client.Client;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.RenderUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import static client.util.RenderUtil.renderEntity;

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

        public void totemPopChamsOnPopPacketTriggeredEventReceivedPackets(EntityPlayer player) {
            if (self.getCurrentState() || player != mc.player) {
                GameProfile profile = new GameProfile(mc.player.getUniqueID(), "");
                player = new EntityOtherPlayerMP(mc.world, profile);
                player.copyLocationAndAnglesFrom(player);
                playerModel = new ModelPlayer(0, false);
                startTime = System.currentTimeMillis();
            }
        }
        @Override
        public void onRender3D(Render3DEvent event) {
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

            if (player != null && PopChams.INSTANCE.isEnabled()) {
                RenderUtil.RenderTesselator.prepareGL();
                GlStateManager.color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), fillA);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                renderEntity(player, playerModel, player.limbSwing, player.limbSwingAmount, player.ticksExisted, player.rotationYawHead, player.rotationPitch, 1);

                GlStateManager.color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), fillA);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                renderEntity(player, playerModel, player.limbSwing, player.limbSwingAmount, player.ticksExisted, player.rotationYawHead, player.rotationPitch, 1);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                //       glPopAttrib();
                RenderUtil.RenderTesselator.releaseGL();
            }

            mc.profiler.startSection("client");
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            GlStateManager.disableDepth();
            GlStateManager.glLineWidth(1.0F);
            Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
            Client.moduleManager.onRender3D(render3dEvent);
            GlStateManager.glLineWidth(1.0F);
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            mc.profiler.endSection();
        }

    double normalize(double value, double min, double max) {
        return ((value - min) / (max - min));

        }
}



