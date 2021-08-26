package client.modules.visual;
import client.events.Render2DEvent;
import client.modules.Module;
import client.modules.client.ClickGui;
import client.util.ColorUtil;
import client.util.RenderUtil;
import client.util.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ShulkerViewer extends Module {
    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static ShulkerViewer INSTANCE = new ShulkerViewer();
    public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap<>();
    public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap<>();
    private int textRadarY = 0;

    public ShulkerViewer() {
        super("ShulkerViewer", "Shows whats inside a shulker even when not opened.", Module.Category.MISC);
        this.setInstance();
    }

    public static ShulkerViewer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShulkerViewer();
        }
        return INSTANCE;
    }

    public static void displayInv(ItemStack stack, String name) {
        try {
            Item item = stack.getItem();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox) item;
            entityBox.blockType = shulker.getBlock();
            entityBox.setWorld(ShulkerViewer.mc.world);
            ItemStackHelper.loadAllItems( Objects.requireNonNull ( stack.getTagCompound ( ) ).getCompoundTag("BlockEntityTag"), entityBox.items);
            entityBox.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            entityBox.setCustomName(name == null ? stack.getDisplayName() : name);
            new Thread(() -> {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException ignored ) {
                }
                ShulkerViewer.mc.player.displayGUIChest(entityBox);
            }).start();
        } catch (Exception ignored ) {
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (ShulkerViewer.fullNullCheck()) {
            return;
        }
        for (EntityPlayer player : ShulkerViewer.mc.world.playerEntities) {
            if (player == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) || ShulkerViewer.mc.player == player)
                continue;
            ItemStack stack = player.getHeldItemMainhand();
            this.spiedPlayers.put(player, stack);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (ShulkerViewer.fullNullCheck()) {
            return;
        }
        int x = -3;
        int y = 124;
        this.textRadarY = 0;
            for (EntityPlayer player : ShulkerViewer.mc.world.playerEntities) {
                Timer playerTimer;
                if (this.spiedPlayers.get(player) == null) continue;
                player.getHeldItemMainhand();
                if (!(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox)) {
                    playerTimer = this.playerTimers.get(player);
                    if (playerTimer == null) {
                        Timer timer = new Timer();
                        timer.reset();
                        this.playerTimers.put(player, timer);
                    } else if (playerTimer.passedS(3.0)) {
                        continue;
                    }
                } else if (player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox && (playerTimer = this.playerTimers.get(player)) != null) {
                    playerTimer.reset();
                    this.playerTimers.put(player, playerTimer);
                }
                ItemStack stack = this.spiedPlayers.get(player);
                this.renderShulkerToolTip(stack, x, y, player.getName());
                this.textRadarY = (y += 78) - 10 - 114 + 2;
        }
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
            RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 57, 500);
            RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
            GlStateManager.disableDepth();
            Color color = new Color(ClickGui.getInstance().red.getCurrentState(), ClickGui.getInstance().green.getCurrentState(), ClickGui.getInstance().blue.getCurrentState(), 200);
            this.renderer.drawStringWithShadow(name == null ? stack.getDisplayName() : name, x + 8, y + 6, ColorUtil.toRGBA(color));
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            NonNullList nonnulllist = NonNullList.withSize(27, (Object) ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);
            for (int i = 0; i < nonnulllist.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack) nonnulllist.get(i);
                ShulkerViewer.mc.getItemRenderer().itemRenderer.zLevel = 501.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(ShulkerViewer.mc.fontRenderer, itemStack, iX, iY, null);
                ShulkerViewer.mc.getItemRenderer().itemRenderer.zLevel = 0.0f;
            }
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

