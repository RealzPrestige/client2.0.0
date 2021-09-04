package client.modules.player;

import client.events.BlockEvent;
import client.events.Render3DEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.BlockUtil;
import client.util.RenderUtil;
import client.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Speedmine extends Module {
    Timer timer = new Timer();
    public Setting<Mode> mode = this.register(new Setting("Mode", Mode.PACKET));
    public Setting<Boolean> render = this.register(new Setting("Render", false));
    public Setting<Integer> red = register(new Setting("Red", 120, 0, 255, v-> render.getCurrentState()));
    public Setting<Integer> green = register(new Setting("Green", 120, 0, 255, v-> render.getCurrentState()));
    public Setting<Integer> blue = register(new Setting("Green", 120, 0, 255, v-> render.getCurrentState()));

    int currentAlpha;
    BlockPos currentPos;
    IBlockState currentBlockState;

    public Speedmine() {
        super("Speedmine", "Speeds up mining and tweaks.", Category.PLAYER);
    }

    @Override
    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
    @Override
    public void onTick() {
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
        if (this.render.getCurrentState() && this.currentPos != null && this.currentBlockState.getBlock() == Blocks.OBSIDIAN) {
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
}

