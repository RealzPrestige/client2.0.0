package client.modules.visual;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static client.util.RenderUtil.renderEntity;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class PopChamsRewrite extends Module {
    int fillA;
    int lineA;

    ModelPlayer playerModel = null;
    long startTime;
    public static PopChamsRewrite INSTANCE = new PopChamsRewrite();

    /**
     * by kambing
     */

    public Setting<Boolean> self = register(new Setting<>("Self", true));
    public Setting<Boolean> solid = register(new Setting<>("Solid", true));
    public Setting<Boolean> outline = register(new Setting<>("Outline", true));
    public Setting<Integer> yTravel = register(new Setting<>("YTravel", 0, 0, 10));
    public Setting<Integer> range = register(new Setting<>("Range", 30, 10, 100));
    public Setting<Integer> fadeStep = register(new Setting<>("FadeStep", 10, 10, 100));
    public Setting<Integer> fadeStart = register(new Setting<>("AliveTime", 1000, 0, 5000));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255));

    public HashMap<EntityPlayer, Integer> poppedPlayers = new HashMap<>();

    public PopChamsRewrite() {
        super("PopChamsRewrite", "Rewrote Op module.", Category.VISUAL);
    }



    @Override
    public void onRender3D(Render3DEvent event) {
        List<EntityPlayer> playersToRemove = new ArrayList<>();
        for (Map.Entry<EntityPlayer, Integer> player : poppedPlayers.entrySet()) {
            if (System.currentTimeMillis() - player.getValue() > fadeStart.getValue().longValue()) {
                playersToRemove.add(player.getKey());
            }
            playerModel = new ModelPlayer(0, false);

            GL11.glLineWidth(1);

            for (Map.Entry<EntityPlayer,Integer> pop : poppedPlayers.entrySet()) {
                poppedPlayers.put(pop.getKey(),pop.getValue() - (fadeStep.getCurrentState()));
                if (pop.getValue() <= 0) {
                    poppedPlayers.remove(pop.getKey());
                    return;
                }

                  fillA = pop.getValue();
            }


            player.getKey().posY += yTravel.getValue();
            RenderUtil.RenderTesselator.prepareGL();
            if (solid.getValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                glDisable(3553);
                glDisable(2896);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f(red.getCurrentState() / 255.0f, green.getCurrentState() / 255.0f, blue.getCurrentState() / 255.0f, fillA / 255.0f);
                renderEntity(player.getKey(), playerModel, 0, 0, player.getKey().ticksExisted, player.getKey().rotationYawHead, player.getKey().rotationPitch, 1);
                glDisable(2896);
                glEnable(2929);
                renderEntity(player.getKey(), playerModel, 0, 0, player.getKey().ticksExisted, player.getKey().rotationYawHead, player.getKey().rotationPitch, 1);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            if (outline.getValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                glDisable(3553);
                glDisable(2896);
                glDisable(2929);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(red.getCurrentState()/255.0f, green.getCurrentState()/255.0f, blue.getCurrentState()/255.0f, lineA/255.0f);
                renderEntity(player.getKey(), playerModel, 0, 0, player.getKey().ticksExisted, player.getKey().rotationYawHead, player.getKey().rotationPitch, 1);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            RenderUtil.RenderTesselator.releaseGL();

        }
        for (EntityPlayer player : playersToRemove) {
            poppedPlayers.remove(player);
        }
    }

    double normalize(double value, double min, double max) {
        return ((value - min) / (max - min));

        }
        @Override
        public void onEnable(){
        poppedPlayers.put(mc.player,(int)255);
        startTime = System.currentTimeMillis();
        }
}



