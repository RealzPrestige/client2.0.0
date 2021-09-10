package client.modules.visual;

import client.Client;
import client.events.Render3DEvent;
import client.modules.Module;
import client.util.ColorUtil;
import client.util.EntityUtil;
import client.util.PlayerUtil;
import client.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.Objects;

public class NameTags extends Module {
    private static NameTags INSTANCE = new NameTags();
    public NameTags() {
        super("Nametags", "Better Nametags", Category.VISUAL);
        this.setInstance();
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!NameTags.fullNullCheck()) {

            Iterator iterator = mc.world.playerEntities.iterator();

            while (iterator.hasNext()) {
                Object o = iterator.next();
                Entity entity = (Entity) o;
                if (entity instanceof EntityPlayer && entity.isEntityAlive() && !entity.isInvisible()) {
                    double x = interpolate(entity.lastTickPosX, entity.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                    double y = interpolate(entity.lastTickPosY, entity.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                    double z = interpolate(entity.lastTickPosZ, entity.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                    this.renderNameTag((EntityPlayer) entity, x, y, z, event.getPartialTicks());
                }
            }
        }
    }


    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        if(player.getName().equals("FakePlayer")){
            return;
        }
        if(player.getName().equals("Pop")) {
            return;
        }
        double tempY = y;
        tempY += player.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + NameTags.mc.getRenderManager().viewerPosX, y + NameTags.mc.getRenderManager().viewerPosY, z + NameTags.mc.getRenderManager().viewerPosZ);
        int width = renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) 10 * (distance * 0.3)) / 1000.0;
        if (distance <= 8.0) {
            scale = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX, NameTags.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        RenderUtil.drawRect(-width - 2, -10, (float) width + 2.0f, 0.5f, 0x55000000);
        GlStateManager.disableBlend();
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }
            GL11.glPushMatrix();
            GL11.glScalef(0.75f, 0.75f, 0.0f);
            GL11.glScalef(1.5f, 1.5f, 1.0f);
            GL11.glPopMatrix();
            GlStateManager.pushMatrix();
            int xOffset = -8;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) continue;
                xOffset -= 8;
            }
            xOffset -= 8;
            ItemStack renderOffhand = player.getHeldItemOffhand().copy();
            if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
                renderOffhand.stackSize = 1;
            }
            this.renderItemStack(renderOffhand, xOffset);
            xOffset += 16;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) continue;
                ItemStack armourStack = stack.copy();
                if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                    armourStack.stackSize = 1;
                }
                this.renderItemStack(armourStack, xOffset);
                xOffset += 16;
            }
            this.renderItemStack(renderMainHand, xOffset);
            GlStateManager.popMatrix();
        this.renderer.drawStringWithShadow(displayTag, -width, -10, Client.friendManager.isFriend(player) ? ColorUtil.toRGBA(0, 191, 255) : -1);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        NameTags.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26);
        mc.getRenderItem().renderItemOverlays(NameTags.mc.fontRenderer, stack, x, -26);
        NameTags.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        this.renderEnchantmentText(stack, x);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x) {
        if (PlayerUtil.hasDurability(stack)) {
            int percent = PlayerUtil.getRoundedDamage(stack);
            String color = percent >= 60 ? "\u00a7a" : (percent >= 25 ? "\u00a7e" : "\u00a7c");
            this.renderer.drawStringWithShadow(color + percent + "%", x * 2, -26, -1);
        }
    }


    private String getDisplayTag(EntityPlayer player) {
            String name = player.getDisplayName().getFormattedText();
            float health = Math.round(EntityUtil.getHealth(player));
            String color = health > 18.0f ? "\u00a7a" : (health > 16.0f ? "\u00a72" : (health > 12.0f ? "\u00a7e" : (health > 8.0f ? "\u00a76" : (health > 5.0f ? "\u00a7c" : "\u00a74"))));
            String pingStr = "";
            try {
                int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr = pingStr + responseTime + "ms ";
            } catch (Exception ignored) {
            }
       return name + " " +  pingStr + color + health;
    }


    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}
