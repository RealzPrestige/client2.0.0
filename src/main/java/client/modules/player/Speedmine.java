package client.modules.player;

import client.Client;
import client.events.BlockEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.BlockUtil;
import client.util.InventoryUtil;
import client.util.RenderUtil;
import client.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Speedmine extends Module {
    private static Speedmine INSTANCE = new Speedmine();
    int delay;
    public Timer timer = new Timer();
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.PACKET));
    public enum Mode {PACKET, INSTANT}
    public Setting<Boolean> silentSwitch = register(new Setting<>("SilentSwitch", false, v-> mode.getCurrentState() == Mode.PACKET));
    public Setting<SilentSwitchMode> silentSwitchMode = register(new Setting<>("SilentSwitchMode", SilentSwitchMode.AUTO, v-> mode.getCurrentState() == Mode.PACKET && silentSwitch.getCurrentState()));
    public enum SilentSwitchMode{AUTO, KEYBIND}
    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1), v ->  silentSwitch.getCurrentState() && silentSwitchMode.getCurrentState() == SilentSwitchMode.KEYBIND));
    public Setting<Boolean> render = register(new Setting<>("Render", false));
    public Setting<Integer> red = register(new Setting<>("Red", 120, 0, 255, v -> render.getCurrentState()));
    public Setting<Integer> green = register(new Setting<>("Green", 120, 0, 255, v -> render.getCurrentState()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 120, 0, 255, v -> render.getCurrentState()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255, v -> render.getCurrentState()));

    int currentAlpha;
    public BlockPos currentPos;
    public IBlockState currentBlockState;

    public Speedmine() {
        super("Speedmine", "Speeds up mining and tweaks.", Category.PLAYER);
        this.setInstance();
    }

    public static Speedmine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Speedmine();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @Override
    public void onTick() {
        if(delay > 5){
            delay = 0;
        } else {
            ++delay;
        }
        if (this.currentPos != null) {
            if (!Speedmine.mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || Speedmine.mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
                this.currentPos = null;
                this.currentBlockState = null;
            }
        }
        if (currentAlpha < (alpha.getCurrentState() - 2)) {
            currentAlpha = currentAlpha + 3;
        }
        int pickSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
         if (Speedmine.mc.player != null && this.silentSwitch.getCurrentState() && silentSwitchMode.getCurrentState() == SilentSwitchMode.AUTO && this.timer.passedMs((int) (2000.0f * Client.serverManager.getTpsFactor())) && this.getPickSlot() != -1) {
             if (pickSlot == -1) {
                 TextComponentString text = new TextComponentString(Client.commandManager.getClientMessage() + ChatFormatting.WHITE + ChatFormatting.BOLD + " Speedmine: " + ChatFormatting.RESET + ChatFormatting.GRAY + "No pickaxe found, stopped" + ChatFormatting.WHITE + ChatFormatting.BOLD + " SilentSwitch");
                 Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
             } else {
                 Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.getPickSlot()));
             }
         }
         if (Speedmine.mc.player != null && this.silentSwitch.getCurrentState() && silentSwitchMode.getCurrentState() == SilentSwitchMode.AUTO && this.timer.passedMs((int) (2200.0f * Client.serverManager.getTpsFactor()))) {
            int oldSlot = mc.player.inventory.currentItem;
            Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
         if(Speedmine.mc.player != null && this.silentSwitch.getCurrentState() && silentSwitchMode.getCurrentState() == SilentSwitchMode.KEYBIND) {
           if(switchBind.getCurrentState().getKey() != -1) {
                   if (Keyboard.isKeyDown(switchBind.getCurrentState().getKey())) {
                       if (pickSlot == -1) {
                           TextComponentString text = new TextComponentString(Client.commandManager.getClientMessage() + ChatFormatting.WHITE + ChatFormatting.BOLD + " Speedmine: " + ChatFormatting.RESET + ChatFormatting.GRAY + "No pickaxe found, stopped" + ChatFormatting.WHITE + ChatFormatting.BOLD + " SilentSwitch");
                           Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
                       } else {
                       Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.getPickSlot()));

                       if(delay == 5) {
                           int oldSlot = mc.player.inventory.currentItem;
                           Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                       }

                   }
               }
           }
        }
    }

    @Override
    public void onUpdate() {
        if (Speedmine.fullNullCheck()) {
            return;
        }
        Speedmine.mc.playerController.blockHitDelay = 0;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getCurrentState() && this.currentPos != null && (currentBlockState.getBlock() == Blocks.OBSIDIAN || currentBlockState.getBlock() == Blocks.ENDER_CHEST)) {
            Color color = new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), currentAlpha);
            RenderUtil.drawBoxESP(this.currentPos, color, true, color, 1, true, true, currentAlpha, false);
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (Speedmine.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && Speedmine.mc.playerController.curBlockDamageMP > 0.1f) {
            Speedmine.mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4) {
            if (BlockUtil.canBreak(event.pos)) {
                Speedmine.mc.playerController.isHittingBlock = false;
                switch (this.mode.getCurrentState()) {
                    case PACKET: {
                        if (this.currentPos == null) {
                            this.currentPos = event.pos;
                            this.currentBlockState = Speedmine.mc.world.getBlockState(this.currentPos);
                            this.timer.reset();
                        }
                        currentAlpha = 0;
                        Speedmine.mc.player.swingArm(EnumHand.MAIN_HAND);
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.setCanceled(true);
                        break;
                    }
                    case INSTANT: {
                        currentAlpha = 0;
                        Speedmine.mc.player.swingArm(EnumHand.MAIN_HAND);
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        Speedmine.mc.playerController.onPlayerDestroyBlock(event.pos);
                        Speedmine.mc.world.setBlockToAir(event.pos);
                    }
                }
            }
        }
    }
    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (Speedmine.mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
            return i;
        }
        return -1;
    }

    @Override
    public String hudInfoString() {
        return this.mode.currentEnumName();
    }


}