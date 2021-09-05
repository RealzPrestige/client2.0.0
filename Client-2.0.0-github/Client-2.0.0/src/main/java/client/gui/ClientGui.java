package client.gui;

import client.Client;
import client.gui.impl.Component;
import client.gui.impl.Item;
import client.gui.impl.background.Snow;
import client.gui.impl.background.particles.ParticleSystem;
import client.gui.impl.button.ModuleButton;
import client.modules.Feature;
import client.modules.Module;
import client.modules.client.ClickGui;
import client.modules.combat.Criticals;
import client.modules.visual.Chams;
import client.util.ColorUtil;
import client.util.EntityUtil;
import client.util.RenderUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

public class ClientGui extends GuiScreen {
    private static ClientGui INSTANCE;
    private final ArrayList<Snow> _snowList = new ArrayList<>();
    public ParticleSystem particleSystem;
    static {
        INSTANCE = new ClientGui();
    }
    private final ArrayList<Component> components = new ArrayList();

    public ClientGui() {
        this.setInstance();
        this.load();
    }

    public static ClientGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientGui();
        }
        return INSTANCE;
    }

    @Override
    public void initGui() {
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer && ClickGui.getInstance().blur.getCurrentState() &&  ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
                if (mc.entityRenderer.getShaderGroup() != null) {
                    mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer &&  ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW) {
            if (mc.entityRenderer.getShaderGroup() != null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }
    public void onGuiClosed() {
        if (this.mc.entityRenderer.getShaderGroup() != null)
            this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
    }
    public static ClientGui getClickGui() {
        return ClientGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }


    private void load() {

        int x = -84;
        int x2 = -109;
        Random random = new Random(); {
            for (int i = 0; i < 100; ++i) {
                for (int y = 0; y < 3; ++y) {
                    Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2)+1);
                    _snowList.add(snow);
                }
            }
        }

        for (final Module.Category category : Client.moduleManager.getCategories()) {
                this.components.add(new Component(category.getName(), x += 110, 10, true) {

                    @Override
                    public void setupItems() {
                        counter1 = new int[]{1};
                        Client.moduleManager.getModulesByCategory(category).forEach(module -> this.addButton(new ModuleButton(module)));
                    }
                });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));

    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }
    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawImageLogo() {
        ResourceLocation logo = new ResourceLocation("textures/logo.png");
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo);
        drawCompleteImage(0, 464, 250, 48);
        mc.getTextureManager().deleteTexture(logo);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScaledResolution res = new ScaledResolution(mc);
        if (!_snowList.isEmpty() && ClickGui.getInstance().snowing.getCurrentState() && ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            _snowList.forEach(snow -> snow.Update(res));
        }
        if (particleSystem != null  && ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.OLD) {
            particleSystem.render(mouseX, mouseY);
        } else {
            this.particleSystem = new ParticleSystem(new ScaledResolution(mc));
        }
        this.checkMouseWheel();
        if(ClickGui.getInstance().logo.getCurrentState()) {
            this.drawImageLogo();
        }
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));

        if(ClickGui.getInstance().gui.getCurrentState() == ClickGui.Gui.NEW){
            if(ClickGui.getInstance().chamsViewer.getCurrentState()){
                    drawPlayer();
                RenderUtil.drawRect(700, 100, 900, 390, ColorUtil.toRGBA(20, 20, 20, 80));
                RenderUtil.drawRect(700, 360, 900, 390, ColorUtil.toRGBA(20, 20, 20, 80));
                Client.textManager.drawStringWithShadow("Box: " + (Chams.getInstance().rainbow.getCurrentState() ? "Rainbow | Hue: " +Chams.getInstance().rainbowHue.getCurrentState() : "R = " + Chams.getInstance().red.getCurrentState() + " | G = " + Chams.getInstance().green.getCurrentState() + " | B = " + Chams.getInstance().blue.getCurrentState() + " | A = " + Chams.getInstance().alpha.getCurrentState()),  700, 360, -1);
                Client.textManager.drawStringWithShadow("Outline: " + (Chams.getInstance().o_rainbow.getCurrentState() ? "Rainbow | Hue: " +Chams.getInstance().o_rainbowHue.getCurrentState() : "R = " + Chams.getInstance().o_red.getCurrentState() + " | G = " + Chams.getInstance().o_green.getCurrentState() + " | B = " + Chams.getInstance().o_blue.getCurrentState() + " | A = " + Chams.getInstance().o_alpha.getCurrentState()),  700, 370, -1);
                Client.textManager.drawStringWithShadow("LineWidth: " + Chams.getInstance().lineWidth.getCurrentState(),  700, 380, -1);
                RenderUtil.drawBorder(700, 100,200, 260, new Color(ColorUtil.toRGBA(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), 255)));
                RenderUtil.drawBorder(700, 361,200, 29, new Color(ColorUtil.toRGBA(ClickGui.getInstance().newared.getCurrentState(), ClickGui.getInstance().newagreen.getCurrentState(), ClickGui.getInstance().newablue.getCurrentState(), 255)));
            }
        }

    }
    public void drawPlayer() {
        EntityPlayer target = EntityUtil.getTarget(200.0f);
        EntityPlayer ent = target == null ? mc.player : target;
        ent.rotationPitch = 0;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(783 + 25), (float)(296 + 25), 50.0f);
        GlStateManager.scale((float)(-50.0f * 2.0), (float)(50.0f * 2.0), (float)(50.0f * 2.0));
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-((float)Math.atan((float) 296 / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        }
        catch (Exception ignored) {
        }
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }
    @Override
    public void updateScreen() {
        if (particleSystem != null)
            particleSystem.update();
    }
}

