package client.modules.player;

import client.Client;
import client.events.BlockEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Bind;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.modules.visual.PopChams;
import client.util.BlockUtil;
import client.util.MathUtil;
import client.util.RenderUtil;
import client.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Speedmine extends Module {
    public static Speedmine INSTANCE = new Speedmine();
    Timer timer = new Timer();
    public Setting<Mode> mode = this.register(new Setting("Mode", Mode.PACKET));
    public Setting<Boolean> render = this.register(new Setting("Render", false));
    public Setting<Boolean> silentSwitch = this.register(new Setting("SilentSwitch", false));
    public Setting<SwitchMode> switchMode = this.register(new Setting("SwitchMode", SwitchMode.AUTO, v-> silentSwitch.getCurrentState()));
    public enum SwitchMode{AUTO, KEYBIND}
    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1), v-> switchMode.getCurrentState() == SwitchMode.KEYBIND));
    private final Setting<Integer> range = this.register(new Setting("Range", 10, 1, 15));
    public Setting<Integer> red = register(new Setting("Red", 120, 0, 255, v-> render.getCurrentState()));
    public Setting<Integer> green = register(new Setting("Green", 120, 0, 255, v-> render.getCurrentState()));
    public Setting<Integer> blue = register(new Setting("Blue", 120, 0, 255, v-> render.getCurrentState()));

    int currentAlpha;
    public BlockPos currentPos;
    IBlockState currentBlockState;
    public Speedmine() {
        super("Speedmine", "Speeds up mining and tweaks.", Category.PLAYER);
    }

    @Override
    public void onLogin(){
        currentPos = null;
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
    @Override
    public void onLogout(){
        currentPos = null;
    }
    @Override
    public void onTick() {
        if(currentPos != null) {
            if (Speedmine.mc.player != null && Speedmine.mc.player.getDistanceSq(this.currentPos) > MathUtil.square(this.range.getCurrentState())) {
                this.currentPos = null;
                this.currentBlockState = null;
                return;
            }
        }
        if (Speedmine.mc.player != null && this.silentSwitch.getCurrentState() && this.timer.passedMs((int) (2000.0f * Client.serverManager.getTpsFactor())) && this.getPickSlot() != -1) {
           if(switchMode.getCurrentState() == SwitchMode.AUTO) {
               Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.getPickSlot()));
           }  else if(switchMode.getCurrentState() == SwitchMode.KEYBIND) {
               if (Keyboard.isKeyDown(switchBind.getCurrentState().getKey())) {
                   Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.getPickSlot()));
               }
           }
        }
        if(switchMode.getCurrentState() == SwitchMode.AUTO) {
            if (Speedmine.mc.player != null && this.silentSwitch.getCurrentState() && this.timer.passedMs((int) (2200.0f * Client.serverManager.getTpsFactor()))) {
                int oldSlot = mc.player.inventory.currentItem;
                Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            }
        } else if(switchMode.getCurrentState() == SwitchMode.KEYBIND){
                int oldSlot = mc.player.inventory.currentItem;
                Speedmine.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
        if (fullNullCheck()) return;


        if (this.currentPos != null) {
            if (!Speedmine.mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || Speedmine.mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
                this.currentPos = null;
                this.currentBlockState = null;
            }
        }
        if(currentAlpha < 253){
            currentAlpha = currentAlpha + 3;
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
        if (this.render.getCurrentState() && this.currentPos != null && (this.currentBlockState.getBlock() == Blocks.OBSIDIAN || this.currentBlockState.getBlock() == Blocks.ENDER_CHEST)) {
            Color color = new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), currentAlpha);
            RenderUtil.drawBoxESP(this.currentPos, color, true, color, 1, true,true, currentAlpha, false);
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

    @Override
    public String hudInfoString() {
        return this.mode.currentEnumName();
    }

    public enum Mode {
        PACKET,
        INSTANT
    }

        private int getPickSlot() {
            for (int i = 0; i < 9; ++i) {
                if (Speedmine.mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
                return i;
            }
            return -1;
        }
}

