package client.modules.visual;

import client.Client;
import client.events.Render3DEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.EntityUtil;
import client.util.PlayerUtil;
import client.util.RenderUtil;
import client.util.RotationUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.util.Objects;

public class NameTags extends Module {
    public static NameTags INSTANCE = new NameTags();
    public Setting<Boolean> healthSetting = register(new Setting<>("Health", true));
    public Setting<Boolean> armor = register(new Setting<>("Armor", true));
    public Setting<Boolean> ping = register(new Setting<>("Ping", true));
    public Setting<Double> size = register(new Setting<>("Size", 0.3, 0.1, 20.0));

    public NameTags() {
        super("NameTags", "Shows information above entities.", Category.VISUAL);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!fullNullCheck()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player == null || player.equals(mc.player) || !player.isEntityAlive() || player.isInvisible() && !RotationUtil.isInFov(player)) continue;
                double x = RenderUtil.interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                double y = RenderUtil.interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                double z = RenderUtil.interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                renderFinalResult(player, x, y, z, event.getPartialTicks());
            }
        }
    }

    private void renderFinalResult(EntityPlayer player, double x, double y, double z, float delta) {
        if(fullNullCheck()){
            return;
        }
        if(player.getName() == "FakePlayer") {
            return;
        } else {
            float health = (float) Math.ceil(EntityUtil.getHealth(player));
            String color = health > 18.0f ? "\u00a7a" : (health > 16.0f ? "\u00a72" : (health > 12.0f ? "\u00a7e" : (health > 8.0f ? "\u00a76" : (health > 5.0f ? "\u00a7c" : "\u00a74"))));
            double theY = y;
            theY += player.isSneaking() ? 0.5 : 0.7;
            Entity camera = mc.getRenderViewEntity();
            assert (camera != null);
            double originalPositionX = camera.posX;
            double originalPositionY = camera.posY;
            double originalPositionZ = camera.posZ;
            camera.posX = RenderUtil.interpolate(camera.prevPosX, camera.posX, delta);
            camera.posY = RenderUtil.interpolate(camera.prevPosY, camera.posY, delta);
            camera.posZ = RenderUtil.interpolate(camera.prevPosZ, camera.posZ, delta);
            int width = renderer.getStringWidth(player.getDisplayName().getFormattedText() + " " + (ping.getCurrentState() ? Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime() + "ms" : "") + " " + (healthSetting.getCurrentState() ? color + health : "")) / 2;
            double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
            double scale = (0.0018 + size.getCurrentState() * (distance * 0.2)) / 1000.0;
            if (distance <= 8.0) {
                scale = 0.0245;
            }
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.disableLighting();
            GlStateManager.translate((float) x, (float) theY + 1.4f, (float) z);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-scale, -scale, scale);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            RenderUtil.drawRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), (float) width + 2.0f, 1.0f, 0x55000000);
            GlStateManager.enableAlpha();
            ItemStack renderMainHand = player.getHeldItemMainhand().copy();
            if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
                renderMainHand.stackSize = 1;
            }
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
            renderItems(renderOffhand, xOffset, armor.getCurrentState());
            xOffset += 16;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) continue;
                ItemStack armourStack = stack.copy();
                if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                    armourStack.stackSize = 1;
                }
                renderItems(armourStack, xOffset, armor.getCurrentState());
                xOffset += 16;
            }
            renderItems(renderMainHand, xOffset, armor.getCurrentState());
            GlStateManager.popMatrix();
            renderer.drawStringWithShadow(player.getDisplayName().getFormattedText() + " " + (ping.getCurrentState() ? Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime() + "ms" : "") + " " + (healthSetting.getCurrentState() ? color + health : ""), -width, -8, (Client.friendManager.isFriend(player) ? -11157267 : -1));
            camera.posX = originalPositionX;
            camera.posY = originalPositionY;
            camera.posZ = originalPositionZ;
            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.popMatrix();
        }
    }

    private void renderItems(ItemStack stack, int x, boolean item) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        if (item) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26);
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -26);
        }
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        renderArmorPercentage(stack, x);
        GlStateManager.enableDepth();
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        GlStateManager.popMatrix();
    }

    private void renderArmorPercentage(ItemStack stack, int x) {
        if (PlayerUtil.hasDurability(stack)) {
            String percentColor = PlayerUtil.getRoundedDamage(stack) >= 60 ? "\u00a7a" : (PlayerUtil.getRoundedDamage(stack) >= 25 ? "\u00a7e" : "\u00a7c");
            renderer.drawStringWithShadow(percentColor + PlayerUtil.getRoundedDamage(stack) + "%", x * 2, -30, -1);
        }
    }
}