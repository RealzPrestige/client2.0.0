package client.modules.visual;

import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import client.util.EntityUtil;
import client.util.RenderUtil;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashSet;


public class CityESP extends Module {
    HashSet<EntityPlayer> entities = Sets.newHashSet();
    public Setting<Integer> red = register(new Setting("Red", 120, 0, 255));
    public Setting<Integer> green = register(new Setting("Green", 120, 0, 255));
    public Setting<Integer> blue = register(new Setting("Blue", 120, 0, 255));
    public Setting<Integer> alpha = register(new Setting("Alpha", 120, 0, 255));
    public Setting<Integer> selfred = register(new Setting("SelfRed", 120, 0, 255));
    public Setting<Integer> selfgreen = register(new Setting("SelfGreen", 50, 0, 255));
    public Setting<Integer> selfblue = register(new Setting("SelfBlue", 0, 0, 255));
    public Setting<Integer> selfalpha = register(new Setting("SelfAlpha", 120, 0, 255));

    public CityESP(){
        super("CityESP", "Shows spots where enemies are vulnerable.", Category.VISUAL);
    }

    public void onRender3D(Render3DEvent event){
            for (EntityPlayer player : mc.world.playerEntities) {
                if(!player.equals("Pop") || player.entityId != 6900 || !player.isDead || player.getDistance(mc.player) < 300) {
                    entities.add(player);
                } else {
                    entities.remove(player);
                }
            }
            for(EntityPlayer player : entities){
                cityBlocks(player);
            }
        }

    public void cityBlocks(EntityPlayer player){
        BlockPos pos = EntityUtil.getPlayerPos(player);
        if(EntityUtil.isSafe(player)) {
            if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.north().north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().north().down()).getBlock() == Blocks.BEDROCK)) {
                 if(player.getName().contains(mc.getSession().getUsername())){
                     RenderUtil.drawBoxESP(pos.north(), new Color(ColorUtil.toRGBA(selfred.getCurrentState(), selfgreen.getCurrentState(), selfblue.getCurrentState(), selfalpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, selfalpha.getCurrentState(), true);
                 } else {
                     RenderUtil.drawBoxESP(pos.north(), new Color(ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, alpha.getCurrentState(), true);
                 }
            }
            if (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.east().east()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east().east().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east().east().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east().east().down()).getBlock() == Blocks.BEDROCK)) {
                if(player.getName().contains(mc.getSession().getUsername())) {
                    RenderUtil.drawBoxESP(pos.east(), new Color(ColorUtil.toRGBA(selfred.getCurrentState(), selfgreen.getCurrentState(), selfblue.getCurrentState(), selfalpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, selfalpha.getCurrentState(), true);
                } else {
                    RenderUtil.drawBoxESP(pos.east(), new Color(ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, alpha.getCurrentState(), true);
                }
            }
            if (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.south().south()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.south().south().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south().south().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south().south().down()).getBlock() == Blocks.BEDROCK)) {
                if(player.getName().contains(mc.getSession().getUsername())) {
                    RenderUtil.drawBoxESP(pos.south(), new Color(ColorUtil.toRGBA(selfred.getCurrentState(), selfgreen.getCurrentState(), selfblue.getCurrentState(), selfalpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, selfalpha.getCurrentState(), true);
                } else {
                    RenderUtil.drawBoxESP(pos.south(), new Color(ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, alpha.getCurrentState(), true);
                }
            }
            if (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().west().up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.west().west().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west().west().down()).getBlock() == Blocks.BEDROCK)) {
                if (player.getName().contains(mc.getSession().getUsername())) {
                    RenderUtil.drawBoxESP(pos.west(), new Color(ColorUtil.toRGBA(selfred.getCurrentState(),selfgreen.getCurrentState(), selfblue.getCurrentState(), selfalpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, selfalpha.getCurrentState(), true);
                } else {
                    RenderUtil.drawBoxESP(pos.west(), new Color(ColorUtil.toRGBA(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState())), false, new Color(ColorUtil.toRGBA(0, 0, 0, 0)), 0, false, true, alpha.getCurrentState(), true);
                }
            }
        }
    }
}
