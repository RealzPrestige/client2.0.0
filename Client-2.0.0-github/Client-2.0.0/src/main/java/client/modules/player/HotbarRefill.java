package client.modules.player;

import client.modules.Module;
import client.setting.Setting;
import client.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class HotbarRefill extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Boolean> gapples;
    private final Setting<Integer> gapAmount;
    private final Setting<Boolean> exp;
    private final Setting<Integer> expAmount;
    private final Setting<Boolean> crystal;
    private final Setting<Integer> crystalAmount;
    private final Timer timer;
    private final ArrayList<Item> Hotbar;

    public HotbarRefill() {
        super("HotbarRefill", "Refills item stacks in your hotbar", Category.PLAYER);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 0, 0, 10));
        this.gapples = (Setting<Boolean>)this.register(new Setting("Gapples", true));
        this.gapAmount = (Setting<Integer>)this.register(new Setting("GapAmount", 1, 1, 64, v -> this.gapples.getValue()));
        this.exp = (Setting<Boolean>)this.register(new Setting("Exp", true));
        this.expAmount = (Setting<Integer>)this.register(new Setting("ExpAmount", 1, 1, 64, v -> this.exp.getValue()));
        this.crystal = (Setting<Boolean>)this.register(new Setting("Crystals", true));
        this.crystalAmount = (Setting<Integer>)this.register(new Setting("CrystalAmount", 1, 1, 64, v -> this.crystal.getValue()));
        this.timer = new Timer();
        this.Hotbar = new ArrayList<Item>();
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.Hotbar.clear();
        for (int l_I = 0; l_I < 9; ++l_I) {
            final ItemStack l_Stack = HotbarRefill.mc.player.inventory.getStackInSlot(l_I);
            if (!l_Stack.isEmpty() && !this.Hotbar.contains(l_Stack.getItem())) {
                this.Hotbar.add(l_Stack.getItem());
            }
            else {
                this.Hotbar.add(Items.AIR);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (HotbarRefill.mc.currentScreen != null) {
            return;
        }
        if (!this.timer.passedMs(this.delay.getValue() * 1000)) {
            return;
        }
        for (int l_I = 0; l_I < 9; ++l_I) {
            if (this.RefillSlotIfNeed(l_I)) {
                this.timer.reset();
                return;
            }
        }
    }

    private boolean RefillSlotIfNeed(final int p_Slot) {
        final ItemStack l_Stack = HotbarRefill.mc.player.inventory.getStackInSlot(p_Slot);
        if (l_Stack.isEmpty() || l_Stack.getItem() == Items.AIR) {
            return false;
        }
        if (!l_Stack.isStackable()) {
            return false;
        }
        if (l_Stack.getCount() >= l_Stack.getMaxStackSize()) {
            return false;
        }
        if (this.gapples.getValue() && l_Stack.getItem().equals(Items.GOLDEN_APPLE) && l_Stack.getCount() >= this.gapAmount.getValue()) {
            return false;
        }
        if (this.exp.getValue() && l_Stack.getItem().equals(Items.EXPERIENCE_BOTTLE) && l_Stack.getCount() > this.expAmount.getValue()) {
            return false;
        }
        if (this.crystal.getValue() && l_Stack.getItem().equals(Items.END_CRYSTAL) && l_Stack.getCount() > this.crystalAmount.getValue()) {
            return false;
        }
        for (int l_I = 9; l_I < 36; ++l_I) {
            final ItemStack l_Item = HotbarRefill.mc.player.inventory.getStackInSlot(l_I);
            if (!l_Item.isEmpty() && this.CanItemBeMergedWith(l_Stack, l_Item)) {
                HotbarRefill.mc.playerController.windowClick(HotbarRefill.mc.player.inventoryContainer.windowId, l_I, 0, ClickType.QUICK_MOVE, (EntityPlayer)HotbarRefill.mc.player);
                HotbarRefill.mc.playerController.updateController();
                return true;
            }
        }
        return false;
    }

    private boolean CanItemBeMergedWith(final ItemStack p_Source, final ItemStack p_Target) {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}
