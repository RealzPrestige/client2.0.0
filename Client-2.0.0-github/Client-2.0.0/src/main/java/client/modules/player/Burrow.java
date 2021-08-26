package client.modules.player;

import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.BlockUtil;
import client.util.InventoryUtil;
import client.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;

public class Burrow extends Module {
    private static Burrow INSTANCE = new Burrow();
    private final Setting<Double> force = this.register(new Setting<>("Offset", 1.5, -5.0, 10.0));
    private final Setting<Boolean> instant = this.register(new Setting<>("Instant", true));
    private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false));
    private final Setting<Boolean> anvil = this.register(new Setting<>("Anvil", false));
    int swapBlock = -1;
    BlockPos oldPos;
    Block blockW = anvil.getCurrentState() ? Blocks.ANVIL : Blocks.OBSIDIAN;
    boolean flag;

    public Burrow() {
        super("Burrow", "Tps you inside a block", Category.PLAYER);
        this.setInstance();
    }

    public static Burrow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Burrow();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        flag = false;

        mc.player.motionX = 0;
        mc.player.motionZ = 0;


        oldPos = PlayerUtil.getPlayerPos();
        if (anvil.getCurrentState()) {
            if (InventoryUtil.findHotbarBlock(BlockAnvil.class) > 1) {
                swapBlock = InventoryUtil.findHotbarBlock(BlockAnvil.class);
            } else {
                this.disable();
            }
        } else {
            if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) > 1) {
                swapBlock = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            } else if (InventoryUtil.findHotbarBlock(BlockObsidian.class) > 1) {
                swapBlock = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            } else {
                this.disable();
            }
            if (swapBlock == -1) {
                this.disable();
                return;
            }
            if (instant.getCurrentState()) {
                this.setTimer(50f);
            }
        }
    }

    @Override
    public void onUpdate() {
        if(nullCheck())return;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true));
            int old = mc.player.inventory.currentItem;
            this.switchToSlot(swapBlock);
            BlockUtil.placeBlock(oldPos, EnumHand.MAIN_HAND, rotate.getCurrentState(), true, false);
            this.switchToSlot(old);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + force.getCurrentState(), mc.player.posZ, false));
            this.disable();
    }

    @Override
    public void onDisable(){
        if(instant.getCurrentState() && !nullCheck()){
            this.setTimer(1f);
        }
    }

    private void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    private void setTimer(float value) {
        try {
            Field timer = Minecraft.class.getDeclaredField(client.util.Timer.timer);
            timer.setAccessible(true);
            Field tickLength = Timer.class.getDeclaredField(client.util.Timer.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0F / value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDisplayInfo() {
            return "PACKET";
    }

    public void setBlock(Block b){
        this.blockW = b;
    }

    public Block getBlock() {
        return this.blockW;
    }
}