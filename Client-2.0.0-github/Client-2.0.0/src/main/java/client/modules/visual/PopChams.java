package client.modules.visual;

import client.Client;
import client.events.NewPopEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import java.util.Map;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.glEnable;

public class PopChams extends Module {
    public static PopChams INSTANCE = new PopChams();
    public Setting<Float> fadeTime = register(new Setting<>("FadeTime", 3000.0f, 1.0f, 5000.0f));

    public final HashMap<EntityOtherPlayerMP, Long> popFakePlayerMap;

    public PopChams() {
        super("PopChams", "Draws fake entities when someone pops.", Category.VISUAL);
        this.popFakePlayerMap = new HashMap<>();
    }

    @SubscribeEvent
    public void onRenderLast(final RenderWorldLastEvent event) {
        for (final Map.Entry<EntityOtherPlayerMP, Long> entry : new HashMap<>(this.popFakePlayerMap).entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > (long)fadeTime.getCurrentState().floatValue()) {
                this.popFakePlayerMap.remove(entry.getKey());
            }
            else {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glLineWidth((Chams.getInstance()).lineWidth.getCurrentState());
                this.renderEntity(entry.getKey(), event.getPartialTicks(), false);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }
    }

    @SubscribeEvent
    public void onPop(final NewPopEvent event) {
        if (mc.world.getEntityByID(event.getEntityId()) != null) {
            final Entity entity = mc.world.getEntityByID(event.getEntityId());
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)entity;
                final EntityOtherPlayerMP fakeEntity = new EntityOtherPlayerMP(mc.world, player.getGameProfile());
                fakeEntity.copyLocationAndAnglesFrom(player);
                fakeEntity.rotationYawHead = player.rotationYawHead;
                fakeEntity.prevRotationYawHead = player.rotationYawHead;
                fakeEntity.rotationYaw = player.rotationYaw;
                fakeEntity.prevRotationYaw = player.rotationYaw;
                fakeEntity.rotationPitch = player.rotationPitch;
                fakeEntity.prevRotationPitch = player.rotationPitch;
                fakeEntity.cameraYaw = fakeEntity.rotationYaw;
                fakeEntity.cameraPitch = fakeEntity.rotationPitch;
                this.popFakePlayerMap.put(fakeEntity, System.currentTimeMillis());
            }
        }
    }

    public void renderEntity(final Entity entityIn, final float partialTicks, final boolean p_188388_3_) {
        if (entityIn.ticksExisted == 0) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
        }
        final double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        final double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        final double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        final float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
        int i = entityIn.getBrightnessForRender();
        if (entityIn.isBurning()) {
            i = 15728880;
        }
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        mc.getRenderManager().renderEntity(entityIn, d0 - mc.getRenderManager().viewerPosX, d2 - mc.getRenderManager().viewerPosY, d3 - mc.getRenderManager().viewerPosZ, f, partialTicks, p_188388_3_);
    }
}
