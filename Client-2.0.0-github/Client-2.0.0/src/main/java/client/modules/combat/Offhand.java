package client.modules.combat;

import client.events.PacketEvent;
import client.events.ProcessRightClickBlockEvent;
import client.modules.Feature;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.EntityUtil;
import client.util.InventoryUtil;
import client.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Offhand extends Module {

    private static Offhand instance;
    private final Queue<InventoryUtil.Task> taskList;
    private final Timer timer;
    private final Timer secondTimer;
    public Setting<Mode> mode;
    public Setting<OffhandItem> offhanditem;
    public Setting<Float> totemHealth;
    public Setting<Float> totem;
    public Setting<Boolean> gapple;
    public CurrentItem currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private boolean second;
    private boolean switchedForHealthReason;

    public Offhand() {
        super("Offhand", "Sets items in Offhand slot.", Category.COMBAT);
        this.taskList = new ConcurrentLinkedQueue <> ( );
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.mode = (Setting<Mode>)this.register(new Setting("Mode:", Mode.NORMAL));
        this.offhanditem = (Setting<OffhandItem>)this.register(new Setting("Item", OffhandItem.CRYSTALS));
        this.totemHealth = (Setting<Float>)this.register(new Setting("TotemHP", 14.0f, 0.1f, 36.0f));
        this.totem = (Setting<Float>)this.register(new Setting("TotemHoleHP", 10f, 0.1f, 36.0f));
        this.gapple = (Setting<Boolean>)this.register(new Setting("SwordGapple", true));
        this.currentMode = CurrentItem.TOTEMS;
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.second = false;
        this.switchedForHealthReason = false;
        Offhand.instance = this;
    }

    public static Offhand getInstance() {
        if (Offhand.instance == null) {
            Offhand.instance = new Offhand();
        }
        return Offhand.instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.objectMouseOver != null && event.pos == Offhand.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
            Offhand.mc.playerController.processRightClick( Offhand.mc.player , Offhand.mc.world , EnumHand.OFF_HAND);
        }
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (Offhand.mc.player != null && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                Offhand.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        }
        else if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            Offhand.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!Feature.fullNullCheck() && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet2 = event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        Offhand.mc.player.connection.sendPacket( new CPacketPlayerTryUseItem(EnumHand.OFF_HAND) );
                    }
                    event.setCanceled(true);
                }
            }
            else {
                final CPacketPlayerTryUseItem packet3;
                if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet3 = event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystal";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totem";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapple";
        }
        return null;
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.holdingTotem = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
        this.holdingGapple = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
        this.totems = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.holdingTotem) {
            this.totems += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        }
        this.crystals = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (this.holdingCrystal) {
            this.crystals += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        }
        this.gapples = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (this.holdingGapple) {
            this.gapples += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        }
        this.doSwitch();
    }

    public void doSwitch() {
        this.currentMode = CurrentItem.TOTEMS;
        if (mode.getCurrentState() == Mode.NORMAL) {
            if ( this.gapple.getCurrentState( ) && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totem.getCurrentState( ) ) {
                this.currentMode = CurrentItem.GAPPLES;
            } else if (this.currentMode != CurrentItem.CRYSTALS && this.offhanditem.getCurrentState() == OffhandItem.CRYSTALS && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totem.getCurrentState( ) || EntityUtil.getHealth(Offhand.mc.player, true) > this.totemHealth.getCurrentState( ) )) {
                this.currentMode = CurrentItem.CRYSTALS;
            }
            if ( this.gapple.getCurrentState( ) && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && !EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totemHealth.getCurrentState( ) ) {
                this.currentMode = CurrentItem.GAPPLES;
            }
            if (this.currentMode == CurrentItem.CRYSTALS && this.crystals == 0) {
                this.setMode(CurrentItem.TOTEMS);
            }
            if (this.currentMode == CurrentItem.CRYSTALS && (!EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) <= this.totemHealth.getCurrentState( ) || EntityUtil.getHealth(Offhand.mc.player, true) <= this.totem.getCurrentState( ) )) {
                if (this.currentMode == CurrentItem.CRYSTALS) {
                    this.switchedForHealthReason = true;
                }
                this.setMode(CurrentItem.TOTEMS);
            }
            if (this.switchedForHealthReason && (EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totem.getCurrentState( ) || EntityUtil.getHealth(Offhand.mc.player, true) > this.totemHealth.getCurrentState( ) )) {
                this.setMode(CurrentItem.CRYSTALS);
                this.switchedForHealthReason = false;
            }
            if (this.currentMode == CurrentItem.CRYSTALS && (Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
                this.setMode(CurrentItem.TOTEMS);
            }
            if (Offhand.mc.currentScreen instanceof GuiContainer && !(Offhand.mc.currentScreen instanceof GuiInventory)) {
                return;
            }
            final Item currentOffhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
            switch (this.currentMode) {
                case TOTEMS: {
                    if (this.totems <= 0) {
                        break;
                    }
                    if (this.holdingTotem) {
                        break;
                    }
                    this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                    this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                    break;
                }
                case GAPPLES: {
                    if (this.gapples <= 0) {
                        break;
                    }
                    if (this.holdingGapple) {
                        break;
                    }
                    this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                    this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                    break;
                }
                default: {
                    if (this.crystals <= 0) {
                        break;
                    }
                    if (this.holdingCrystal) {
                        break;
                    }
                    this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                    this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                    break;
                }
            }
            for (int i = 0; i < 4; ++i) {
                final InventoryUtil.Task task = this.taskList.poll();
                if (task != null) {
                    task.run();
                    if (task.isSwitching()) {
                        this.didSwitchThisTick = true;
                    }
                }
            }
        } else {
            if (Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totem.getCurrentState( ) ){
                this.currentMode = CurrentItem.CRYSTALS;
            }
            if (Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && !EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totemHealth.getCurrentState( ) ){
                this.currentMode = CurrentItem.CRYSTALS;
            }
            if ( this.gapple.getCurrentState( ) && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totem.getCurrentState( ) ) {
                this.currentMode = CurrentItem.GAPPLES;
            }
            if ( this.gapple.getCurrentState( ) && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && !EntityUtil.isSafe(Offhand.mc.player) && EntityUtil.getHealth(Offhand.mc.player, true) > this.totemHealth.getCurrentState( ) ) {
                this.currentMode = CurrentItem.GAPPLES;
            }
            if (this.currentMode == CurrentItem.CRYSTALS && (Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
                this.setMode(CurrentItem.TOTEMS);
            }
            if (Offhand.mc.currentScreen instanceof GuiContainer && !(Offhand.mc.currentScreen instanceof GuiInventory)) {
                return;
            }
            final Item currentOffhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
            switch (this.currentMode) {
                case TOTEMS: {
                    if (this.totems <= 0) {
                        break;
                    }
                    if (this.holdingTotem) {
                        break;
                    }
                    this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                    this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                    break;
                }
                case GAPPLES: {
                    if (this.gapples <= 0) {
                        break;
                    }
                    if (this.holdingGapple) {
                        break;
                    }
                    this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                    this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                    break;
                }
                default: {
                    if (this.crystals <= 0) {
                        break;
                    }
                    if (this.holdingCrystal) {
                        break;
                    }
                    this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                    this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                    break;
                }
            }
            for (int i = 0; i < 4; ++i) {
                final InventoryUtil.Task task = this.taskList.poll();
                if (task != null) {
                    task.run();
                    if (task.isSwitching()) {
                        this.didSwitchThisTick = true;
                    }
                }
            }
        }
    }

    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }

    private void putItemInOffhand(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    public void setMode(final CurrentItem mode) {
        this.currentMode = ((this.currentMode == mode) ? CurrentItem.TOTEMS : mode);
    }
    public enum OffhandItem {
        CRYSTALS,
        TOTEMS
    }
    public enum CurrentItem {
        TOTEMS,
        GAPPLES,
        CRYSTALS
    }
    public enum Mode {
        NORMAL,
        ELITEANARCHY
    }
}
