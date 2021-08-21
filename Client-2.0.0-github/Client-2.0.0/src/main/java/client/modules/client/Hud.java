package client.modules.client;

import client.Client;
import client.events.Render2DEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.ColorUtil;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class Hud extends Module {
    private static Hud INSTANCE = new Hud();
    private int color;
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", true));
    public Setting<Boolean> sideway = register(new Setting("RainbowSideway", true, v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowDelay = this.register(new Setting<Object>("Delay", 200, 0, 600, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));
    public Setting<Boolean> fovSetting = this.register(new Setting("Fov", false));
    public Setting<Float> fov = this.register(new Setting("FovValue", 150.0f, 0.0f, 180.0f));
    Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
    public Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
    public Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
    public Setting<Boolean> watermark = register(new Setting("Watermark", false));
    public Setting<Integer> watermarkX = register(new Setting("WatermarkX", 0, 0, 900, v -> watermark.getValue()));
    public Setting<Integer> watermarkY = register(new Setting("WatermarkY", 0, 0, 530, v -> watermark.getValue()));
    public Setting<Boolean> welcomer = register(new Setting("Welcomer", false));
    public Setting<Integer> welcomerX = register(new Setting("WelcomerX", 0, 0, 900, v -> welcomer.getValue() && !this.welcomerAlign.getValue()));
    public Setting<Integer> welcomerY = register(new Setting("WelcomerY", 0, 0, 530, v -> welcomer.getValue() && !this.welcomerAlign.getValue()));
    public Setting<Boolean> welcomerAlign = register(new Setting("WelcomerAlign", false, v -> welcomer.getValue()));
    public Setting<Boolean> nameHider = register(new Setting("NameHider", false));
    public Setting<String> name = register(new Setting("Name...", "popbob"));
    private final Setting<Boolean> potionEffects = register(new Setting("PotionEffects", false));
    private final Setting<Boolean> bottomAlign = register(new Setting("BottomAlign", false,v-> this.potionEffects.getValue()));
    private final Setting<Boolean> coords = register(new Setting("Coords", false, "Your current coordinates"));
    private final Setting<Boolean> armor = this.register( new Setting <> ( "Armor" , false , "ArmorHUD" ));
    private final Setting<Boolean> percent = this.register(new Setting<Object>("Percent", true, v -> this.armor.getValue()));
    public Hud() {
        super("Hud", "Displays strings on your screen", Category.CORE);
        this.setInstance();
    }

    public static Hud getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Hud();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (fovSetting.getValue()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue ( ) );
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }
    public void onRender2D(Render2DEvent event) {
        if (fullNullCheck())
            return;
        color = ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
        String string = Client.MODNAME + " " + Client.MODVER;
        String welcome = nameHider.getValue() ? "Weclome to " + Client.MODNAME + " " + Client.MODVER + " " + name.getValue() : "Weclome to " + Client.MODNAME + " " + Client.MODVER + " " + mc.player.getName();
        //WATERMARK
        if (watermark.getValue()) {
            if (rainbow.getValue()) {
                if (!sideway.getValue()) {
                    renderer.drawString(string, watermarkX.getValue(), watermarkY.getValue(), ColorUtil.rainbowHud(rainbowDelay.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0F;
                    for (char c : stringToCharArray) {
                        renderer.drawString(String.valueOf(c), watermarkX.getValue() + f, watermarkY.getValue(), ColorUtil.rainbowHud(arrayOfInt[0] * rainbowDelay.getValue()).getRGB(), true);
                        f += renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                renderer.drawString(string, watermarkX.getValue(), watermarkY.getValue(), this.color, true);
            }
        }

        if (welcomer.getValue()) {
            float f = 0.0F;
            if (rainbow.getValue()) {
                if (!sideway.getValue()) {
                    renderer.drawString(welcome, welcomerAlign.getValue() ? 400 + f : welcomerX.getValue() + f, welcomerY.getValue(), ColorUtil.rainbowHud(rainbowDelay.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = welcome.toCharArray();
                    for (char c : stringToCharArray) {
                        renderer.drawString(String.valueOf(c), welcomerAlign.getValue() ? 400 + f : welcomerX.getValue() + f, welcomerY.getValue(), ColorUtil.rainbowHud(arrayOfInt[0] * rainbowDelay.getValue()).getRGB(), true);
                        f += renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                renderer.drawString(welcome, welcomerAlign.getValue() ? 400 + f : welcomerX.getValue() + f, welcomerY.getValue(), this.color, true);
            }
        }
        if (this.potionEffects.getValue()) {
            if (fullNullCheck()) return;
            int width = this.renderer.scaledWidth;
            int height = this.renderer.scaledHeight;
            this.color = ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
            int i = (mc.currentScreen instanceof GuiChat && this.bottomAlign.getValue()) ? 13 : (this.bottomAlign.getValue() ? -3 : 0);
            List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
            if (this.bottomAlign.getValue()) {
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
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);
        String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET) : (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]"));
        String coords = this.coords.getValue ( ) ? coordinates : "";
        i += 10;
        if ((ClickGui.getInstance()).rainbow.getValue()) {
            String rainbowCoords = this.coords.getValue() ? ("XYZ " + (inHell ? (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]"))) : "";
            if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(rainbowCoords, 2.0F, (height - i), ColorUtil.rainbow( ( ClickGui.getInstance ( ) ).rainbowHue.getValue ( ) ).getRGB(), true);
            } else {
                int[] counter3 = {1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0F;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0F + u, (height - i), ColorUtil.rainbow(counter3[0] * ( ClickGui.getInstance ( ) ).rainbowHue.getValue ( ) ).getRGB(), true);
                    u += this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(coords, 2.0F, (height - i), this.color, true);
        }
        if (this.armor.getValue()) {
            this.renderArmorHUD(this.percent.getValue());
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
            this.renderer.drawStringWithShadow(s, (float) (x + 19 - 2 - this.renderer.getStringWidth(s)), (float) (y + 9), 16777215);
            if (!percent) {
                continue;
            }
            int dmg;
            final int itemDurability = is.getMaxDamage() - is.getItemDamage();
            final float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            if (percent) {
                dmg = 100 - (int) (red * 100.0f);
            } else {
                dmg = itemDurability;
            }
            this.renderer.drawStringWithShadow(dmg + "", (float) (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toRGBA((int) (red * 255.0f), (int) (green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }


}
