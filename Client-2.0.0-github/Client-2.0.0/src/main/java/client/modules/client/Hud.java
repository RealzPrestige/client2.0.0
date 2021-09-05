package client.modules.client;

import client.Client;
import client.events.PacketEvent;
import client.events.Render2DEvent;
import client.manager.EventManager;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.ColorUtil;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hud extends Module {
    private static Hud INSTANCE = new Hud();
    private int color;
    int packetsSent;
    int packetsReceived;
    public int count = 0;

    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static final ItemStack crystals = new ItemStack(Items.END_CRYSTAL);
    private static final ItemStack gapples = new ItemStack(Items.GOLDEN_APPLE);
    private static final ItemStack exp = new ItemStack(Items.EXPERIENCE_BOTTLE);
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", true));
    public Setting<Boolean> sideway = register(new Setting("RainbowSideway", true, v -> this.rainbow.getCurrentState()));
    public Setting<Integer> rainbowDelay = this.register(new Setting<Object>("Delay", 200, 0, 600, v -> this.rainbow.getCurrentState()));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getCurrentState()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getCurrentState()));
    public Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
    public Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
    public Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
    public Setting<Boolean> watermark = register(new Setting("Watermark", false));
    public Setting<Integer> watermarkX = register(new Setting("WatermarkX", 0, 0, 900, v -> watermark.getCurrentState()));
    public Setting<Integer> watermarkY = register(new Setting("WatermarkY", 0, 0, 530, v -> watermark.getCurrentState()));
    public Setting<Boolean> packets = register(new Setting("Packets", false));
    public Setting<Boolean> welcomer = register(new Setting("Welcomer", false));
    public Setting<Integer> welcomerX = register(new Setting("WelcomerX", 0, 0, 900, v -> welcomer.getCurrentState() && !this.welcomerAlign.getCurrentState()));
    public Setting<Integer> welcomerY = register(new Setting("WelcomerY", 0, 0, 530, v -> welcomer.getCurrentState() && !this.welcomerAlign.getCurrentState()));
    public Setting<Boolean> welcomerAlign = register(new Setting("WelcomerAlign", false, v -> welcomer.getCurrentState()));
    public Setting<Boolean> nameHider = register(new Setting("NameHider", false));
    public Setting<String> name = register(new Setting("Name...", "popbob"));
    private final Setting<Boolean> potionEffects = register(new Setting("PotionEffects", false));
    private final Setting<Boolean> bottomAlign = register(new Setting("BottomAlign", false,v-> this.potionEffects.getCurrentState()));
    private final Setting<Boolean> coords = register(new Setting("Coords", false, "Your current coordinates"));
    private final Setting<Boolean> armor = this.register( new Setting <> ( "Armor" , false , "ArmorHUD" ));
    private final Setting<Boolean> percent = this.register(new Setting<Object>("Percent", true, v -> this.armor.getCurrentState()));
    private final Setting<Boolean> itemInfo = this.register(new Setting<Object>("ItemInfo", true));
    private final Setting<Integer> itemInfoY = this.register(new Setting<>("ItemInfoY", 10, 0, 400));
    private final Setting<Boolean> activeModules = register(new Setting("ActiveModules", false));
    private final Setting<ColorMode> colorMode = register(new Setting("ColorMode", ColorMode.NORMAL, v-> activeModules.getCurrentState()));
    public enum ColorMode{NORMAL, ALPHASTEP, RAINBOW}
    public Setting<Integer> index = this.register(new Setting("Index", 30, 0, 100, v-> colorMode.getCurrentState() == ColorMode.ALPHASTEP));
    public Setting<Integer> countt = this.register(new Setting("Count", 25, 0, 30, v-> colorMode.getCurrentState() == ColorMode.ALPHASTEP));
    public Hud() {
        super("Hud", "Displays strings on your screen.", Category.CORE);
        this.setInstance();
    }

    public static Hud getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Hud();
        }
        return INSTANCE;
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        ++packetsSent;
    }
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        ++packetsReceived;
    }
    private void setInstance() {
        INSTANCE = this;
    }
    public void onRender2D(Render2DEvent event) {
        if (fullNullCheck())
            return;
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int[] counter1 = {1};
        int j = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && bottomAlign.getCurrentState()) ? 14 : 0;
        if(activeModules.getCurrentState()) {
            if(colorMode.getCurrentState() == ColorMode.NORMAL) {
                for (int k = 0; k < Client.moduleManager.sortedModules.size(); k++) {
                    Module module = Client.moduleManager.sortedModules.get(k);
                    String str = module.getDisplayName() + ((module.hudInfoString() != null) ? (ChatFormatting.WHITE + " [" + module.hudInfoString() + "]") : "");
                    renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (2 + j * 10), color, true);
                    j++;
                    counter1[0] = counter1[0] + 1;
                }
            } else if(colorMode.getCurrentState() == ColorMode.ALPHASTEP) {
                for (int k = 0; k < Client.moduleManager.sortedModules.size(); k++) {
                    Module module = Client.moduleManager.sortedModules.get(k);
                    String str = module.getDisplayName() + ((module.hudInfoString() != null) ? (ChatFormatting.WHITE + " [" + module.hudInfoString() + "]") : "");
                    renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (2 + j * 10), ColorUtil.alphaStep(new Color(color), index.getCurrentState(), (counter1[0] + countt.getCurrentState())).getRGB(), true);
                    j++;
                    counter1[0] = counter1[0] + 1;
                    count++;
                }
            } else if(colorMode.getCurrentState() == ColorMode.RAINBOW) {
                for (int k = 0; k < Client.moduleManager.sortedModules.size(); k++) {
                    Module module = Client.moduleManager.sortedModules.get(k);
                    String str = module.getDisplayName() + ((module.hudInfoString() != null) ? (ChatFormatting.WHITE + " [" + module.hudInfoString() + "]") : "");
                    renderer.drawString(str, (width - 2 - renderer.getStringWidth(str)), (2 + j * 10), ColorUtil.rainbowHud(counter1[0] * rainbowDelay.getCurrentState()).getRGB(), true);
                    j++;
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        color = ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState());
        String string = Client.MODNAME + " " + Client.MODVER;

        String welcome = nameHider.getCurrentState() ? "Weclome to " + Client.MODNAME + " " + Client.MODVER + " " + name.getCurrentState() : "Weclome to " + Client.MODNAME + " " + Client.MODVER + " " + mc.player.getName();
        //WATERMARK
        if (watermark.getCurrentState()) {
            if (rainbow.getCurrentState()) {
                if (!sideway.getCurrentState()) {
                    renderer.drawString(string, watermarkX.getCurrentState(), watermarkY.getCurrentState(), ColorUtil.rainbowHud(rainbowDelay.getCurrentState()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0F;
                    for (char c : stringToCharArray) {
                         renderer.drawString(String.valueOf(c), watermarkX.getCurrentState() + f, watermarkY.getCurrentState(), ColorUtil.rainbowHud(arrayOfInt[0] * rainbowDelay.getCurrentState()).getRGB(), true);
                        f += renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                if(packets.getCurrentState()) {
                    renderer.drawString("PacketsSent: " + ChatFormatting.WHITE + Client.eventManager.sendingpackets, watermarkX.getCurrentState(), watermarkY.getCurrentState() + 10, this.color, true);
                    renderer.drawString("PacketsReceived: " + ChatFormatting.WHITE + Client.eventManager.incomingpackets, watermarkX.getCurrentState(), watermarkY.getCurrentState() + 20, this.color, true);
                }
                renderer.drawString(string, watermarkX.getCurrentState(), watermarkY.getCurrentState(), this.color, true);
            }
        }

        if (welcomer.getCurrentState()) {
            float f = 0.0F;
            if (rainbow.getCurrentState()) {
                if (!sideway.getCurrentState()) {
                    renderer.drawString(welcome, welcomerAlign.getCurrentState() ? 400 + f : welcomerX.getCurrentState() + f, welcomerY.getCurrentState(), ColorUtil.rainbowHud(rainbowDelay.getCurrentState()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = welcome.toCharArray();
                    for (char c : stringToCharArray) {
                        renderer.drawString(String.valueOf(c), welcomerAlign.getCurrentState() ? 400 + f : welcomerX.getCurrentState() + f, welcomerY.getCurrentState(), ColorUtil.rainbowHud(arrayOfInt[0] * rainbowDelay.getCurrentState()).getRGB(), true);
                        f += renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                renderer.drawString(welcome, welcomerAlign.getCurrentState() ? 400 + f : welcomerX.getCurrentState() + f, welcomerY.getCurrentState(), this.color, true);
            }
        }
        if (this.potionEffects.getCurrentState()) {
            if (fullNullCheck()) return;
            this.color = ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState());
            int i = (mc.currentScreen instanceof GuiChat && this.bottomAlign.getCurrentState()) ? 13 : (this.bottomAlign.getCurrentState() ? -3 : 0);
            List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
            if (this.bottomAlign.getCurrentState()) {
                for (PotionEffect potionEffect : effects) {
                    String str = Client.potionManager.getColoredPotionString(potionEffect);
                    i += 10;
                    this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), potionEffect.getPotion().getLiquidColor(), true);
                }
            } else {
                for (PotionEffect potionEffect : effects) {
                    String str = Client.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
        }
        boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
        int i;
        i = (mc.currentScreen instanceof GuiChat) ? 14 : 0;
        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);
        String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET) : (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]"));
        String coords = this.coords.getCurrentState( ) ? coordinates : "";
        i += 10;
        if ((ClickGui.getInstance()).rainbow.getCurrentState()) {
            String rainbowCoords = this.coords.getCurrentState() ? ("XYZ " + (inHell ? (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]"))) : "";
            if (!sideway.getCurrentState()) {
                this.renderer.drawString(rainbowCoords, 2.0F, (height - i), ColorUtil.rainbowHud( (rainbowDelay.getCurrentState( ) )).getRGB(), true);
            } else {
                int[] counter3 = {1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0F;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0F + u, (height - i), ColorUtil.rainbowHud(counter3[0] * (rainbowDelay.getCurrentState())).getRGB(), true);
                    u += this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(coords, 2.0F, (height - i), this.color, true);
        }
        if (this.armor.getCurrentState()) {
            this.renderArmorHUD(this.percent.getCurrentState());
        }
        if(itemInfo.getCurrentState()){
            renderTotemHUD();
            renderCrystalHud();
            renderExpHud();
            renderGapsHud();
        }
    }
    public void renderArmorHUD(final boolean percent) {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((Hud.mc.player.isInWater() && Hud.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : Hud.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(Hud.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.renderer.drawStringWithShadow(s, (float) (x + 19 - 2 - this.renderer.getStringWidth(s)), (float) (y + 9) , 16777215);
            if (!percent) {
                continue;
            }
            int dmg;
            final float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            dmg = 100 - (int) (red * 100.0f);
            this.renderer.drawStringWithShadow(dmg + "", (float) (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toRGBA((int) (red * 255.0f), (int) (green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    public void renderTotemHUD() {
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, 0, 20 + itemInfoY.getCurrentState());
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, 0, 20 + itemInfoY.getCurrentState(), "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", 10, 30 + itemInfoY.getCurrentState(), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
    public void renderCrystalHud() {
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(crystals, 0, 37 + itemInfoY.getCurrentState());
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, crystals, 0, 37 + itemInfoY.getCurrentState(), "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", 10, 47 + itemInfoY.getCurrentState(), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
    public void renderExpHud() {
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.EXPERIENCE_BOTTLE)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(exp, 0, 54 + itemInfoY.getCurrentState());
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, exp, 10, 54 + itemInfoY.getCurrentState(), "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", 10, 64 + itemInfoY.getCurrentState(), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
    public void renderGapsHud() {
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(gapples, 0, 69 + itemInfoY.getCurrentState());
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, gapples, 0, 69 + itemInfoY.getCurrentState(), "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", 10, 79 + itemInfoY.getCurrentState(), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
}
