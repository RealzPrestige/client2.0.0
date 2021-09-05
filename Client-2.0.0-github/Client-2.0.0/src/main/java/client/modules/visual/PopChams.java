package client.modules.visual;

import client.events.NewPopEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import java.util.HashMap;

public class PopChams extends Module {
    int a;
    int b;
    int c;
    int d;
    int e;
    int f;
    int g;
    int h;
    int i;
    int j;

    public Setting<Float> fadeTime = register(new Setting<>("FadeTime", 3000.0f, 1.0f, 5000.0f));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 3.0, 0.0, 10.0));

    private final HashMap<EntityOtherPlayerMP, Long> popFakePlayerMap;

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
                GL11.glPushMatrix();
                GL11.glDepthRange(0.0, 0.01);
                GL11.glDisable(2896);
                GL11.glDisable(3553);
                GL11.glPolygonMode(1032, 6913);
                GL11.glEnable(3008);
                GL11.glEnable(3042);
                GL11.glLineWidth(lineWidth.getCurrentState());
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                this.renderEntity(entry.getKey(), event.getPartialTicks(), false);
                GL11.glHint(3154, 4352);
                GL11.glPolygonMode(1032, 6914);
                GL11.glEnable(2896);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glEnable(3553);
                GL11.glPopMatrix();
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
