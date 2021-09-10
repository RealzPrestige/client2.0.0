package client.modules.visual;

import client.events.PacketEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.manager.ModuleManager;
import client.modules.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PopChams extends Module {
    public static PopChams INSTANCE = new PopChams();

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

    public PopChams() {
        super("PopChams", "Op module.", Category.VISUAL);
    }

    EntityPlayer player = null;
    ModelPlayer playerModel = null;
    long startTime;

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getStage() != 0) return;
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                if (self.getCurrentState() || packet.getEntity(mc.world).getEntityId() != mc.player.getEntityId()) {
                    GameProfile profile = new GameProfile(mc.player.getUniqueID(), "");
                    player = new EntityOtherPlayerMP(mc.world, profile);
                    player.copyLocationAndAnglesFrom(packet.getEntity(mc.world));
                    playerModel = new ModelPlayer(0, false);
                    startTime = System.currentTimeMillis();
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }

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
            RenderTesselator.prepareGL();
            GlStateManager.color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), fillA);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            renderEntity(player, playerModel, player.limbSwing, player.limbSwingAmount, player.ticksExisted, player.rotationYawHead, player.rotationPitch, 1);

            GlStateManager.color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), fillA);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            renderEntity(player, playerModel, player.limbSwing, player.limbSwingAmount, player.ticksExisted, player.rotationYawHead, player.rotationPitch, 1);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            //       glPopAttrib();
            RenderTesselator.releaseGL();
        }

        mc.profiler.startSection("saturn");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0F);
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        ModuleManager.onWorldRender(render3dEvent);
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

    //Kami5
    public static void renderEntity(EntityLivingBase entity, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (mc.getRenderManager() == null) return;

        if (modelBase instanceof ModelPlayer) {
            ModelPlayer modelPlayer = ((ModelPlayer) modelBase);
            modelPlayer.bipedHeadwear.showModel = false;
            modelPlayer.bipedBodyWear.showModel = false;
            modelPlayer.bipedLeftLegwear.showModel = false;
            modelPlayer.bipedRightLegwear.showModel = false;
            modelPlayer.bipedLeftArmwear.showModel = false;
            modelPlayer.bipedRightArmwear.showModel = false;
        }

        float partialTicks = mc.getRenderPartialTicks();
        double x = entity.posX - mc.getRenderManager().viewerPosX;
        double y = entity.posY - mc.getRenderManager().viewerPosY;
        double z = entity.posZ - mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();

        if (entity.isSneaking()) {
            y -= 0.125D;
        }
        renderLivingAt(x, y, z);
        prepareRotations(entity);
        float f4 = prepareScale(entity, scale);
        float yaw = handleRotationFloat(entity, partialTicks);

        GlStateManager.enableAlpha();
        modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        modelBase.setRotationAngles(limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4, entity);
        modelBase.render(entity, limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4);

        //  GlStateManager.depthMask(true);

        //   GlStateManager.disableRescaleNormal();
        //   GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        GlStateManager.popMatrix();
    }

    public static void renderLivingAt(double x, double y, double z) {
        GlStateManager.translate((float) x, (float) y, (float) z);

    }

    public static float prepareScale(EntityLivingBase entity, float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
        double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;

        GlStateManager.scale(scale + widthX, scale * entity.height, scale + widthZ);
        //  preRenderCallback();
        float f = 0.0625F;

        GlStateManager.translate(0.0F, -1.501F, 0.0F);
        //  GlStateManager.translate(0.0F, -f * 4, 0.0F);
        return f;
    }

    public static void prepareRotations(EntityLivingBase entityLivingBase) {
        GlStateManager.rotate(180 - entityLivingBase.rotationYaw, 0, 1, 0);
    }

    public static float handleRotationFloat(EntityLivingBase livingBase, float partialTicks) {
        return livingBase.rotationYawHead;
    }

    public static class RenderTesselator extends Tessellator {

        public static RenderTesselator INSTANCE = new RenderTesselator();

        public RenderTesselator() {
            super(0x200000);
        }

        public static void prepare(int mode) {
            prepareGL();
            begin(mode);
        }

        public static void prepareGL() {
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(1.5F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1);
        }

        public static void begin(int mode) {
            INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
        }

        public static void release() {
            render();
            releaseGL();
        }

        public static void render() {
            INSTANCE.draw();
        }

        public static void releaseGL() {
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }
    }
}
