package client.modules.visual;

import client.events.ClientEvent;
import client.events.PacketEvent;
import client.events.Render2DEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.*;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;

public class ESP extends Module {
    public int updates;
    HashSet<BlockPos> bedrockholes = Sets.newHashSet();
    HashSet<BlockPos> obsidianholes = Sets.newHashSet();

    public Setting<Boolean> holes = register(new Setting<>("Holes", true));
    public Setting<Boolean> renderPerformance = register(new Setting<>("RenderPerformance", true));
    public Setting<Integer> range = register(new Setting<>("X-Range", 0, 1, 20, v-> holes.getValue()));
    public Setting<Integer> rangeY = register(new Setting<>("Y-Range", 0, 1, 20, v-> holes.getValue()));
    public Setting<Integer> updateDelay = register(new Setting<>("UpdateDelay", 1, 0, 30, v-> holes.getValue()));

    public Setting<Boolean> bedrockBox = register(new Setting<>("BedrockBox", true, v-> holes.getValue()));
    public Setting<Boolean> bedrockFlat = register(new Setting<>("BedrockFlat", true, v-> holes.getValue()));
    public Setting<Integer> bedrockBoxRed = register(new Setting<>( "BedrockBoxRed", 0, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Integer> bedrockBoxGreen = register(new Setting<>("BedrockBoxGreen", 255, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Integer> bedrockBoxBlue = register(new Setting<>("BedrockBoxBlue", 0, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Integer> bedrockBoxAlpha = register(new Setting<>("BedrockBoxAlpha", 120, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Boolean> bedrockOutline = register(new Setting<>("BedrockOutline", true, v-> holes.getValue()));
    public Setting<Integer> bedrockOutlineRed = register(new Setting<>( "BedrockOutlineRed", 0, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineGreen = register(new Setting<>("BedrockOutlineGreen", 255, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineBlue = register(new Setting<>("BedrockOutlineBlue", 0, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineAlpha = register(new Setting<>("BedrockOutlineAlpha", 255, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineLineWidth = register(new Setting<>("BedrockOutlineLineWidth", 1, 0.1, 5, v-> holes.getValue() && bedrockOutline.getValue()));

    public Setting<Boolean> obsidianBox = register(new Setting<>("ObsidianBox", true, v-> holes.getValue()));
    public Setting<Boolean> obsidianFlat = register(new Setting<>("ObsidianFlat", false, v-> holes.getValue()));
    public Setting<Integer> obsidianBoxRed = register(new Setting<>( "ObsidianBoxRed", 255, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Integer> obsidianBoxGreen = register(new Setting<>("ObsidianBoxGreen", 0, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Integer> obsidianBoxBlue = register(new Setting<>("ObsidianBoxBlue", 0, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Integer> obsidianBoxAlpha = register(new Setting<>("ObsidianBoxAlpha", 120, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Boolean> obsidianOutline = register(new Setting<>("ObsidianOutline", true, v-> holes.getValue()));
    public Setting<Integer> obsidianOutlineRed = register(new Setting<>( "ObsidianOutlineRed", 255, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineGreen = register(new Setting<>("ObsidianOutlineGreen", 0, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineBlue = register(new Setting<>("ObsidianOutlineBlue", 0, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineAlpha = register(new Setting<>("ObsidianOutlineAlpha", 255, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineLineWidth = register(new Setting<>("obsidianOutlineLineWidth", 1, 0.1, 5, v-> holes.getValue() && obsidianOutline.getValue()));

    public Setting<Boolean> bottles = register(new Setting<>("Bottles", true));
    public Setting<Integer> bottlesred = register(new Setting<>( "BottlesRed", 255, 0, 255, v -> this.bottles.getValue ( ) ));
    public Setting<Integer> bottlesgreen = register(new Setting<>("BottlesGreen", 255, 0, 255, v -> this.bottles.getValue ( ) ));
    public Setting<Integer> bottlesblue = register(new Setting<>("BottlesBlue", 255, 0, 255, v -> this.bottles.getValue ( ) ));
    public Setting<Integer> bottlesalpha = register(new Setting<>("BottlesAlpha", 150, 0, 255, v -> this.bottles.getValue ( ) ));

    public Setting<Boolean> orbs = register(new Setting<>("Orbs", true));
    public Setting<Integer> o_red = register(new Setting<>("OrbsRed", 255, 0, 255, v -> this.orbs.getValue ( ) ));
    public Setting<Integer> o_green = register(new Setting<>("OrbsGreen", 255, 0, 255, v -> this.orbs.getValue ( ) ));
    public Setting<Integer> o_blue = register(new Setting<>("OrbsBlue", 255, 0, 255, v -> this.orbs.getValue ( ) ));
    public Setting<Integer> o_alpha = register(new Setting<>("OrbsAlpha", 150, 0, 255, v -> this.orbs.getValue ( ) ));

    public ESP() {
    super("ESP", "", Category.VISUAL);
    }

    public void onTick() {
        if (holes.getValue()) {
            if (updates > updateDelay.getValue()) {
                updates = 0;
            } else {
                ++updates;
            }
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if(!holes.getValue()) {
            obsidianholes.clear();
            bedrockholes.clear();
            updates = 0;
        }
    }

    public void onEnable(){
        updates = 0;
    }

    public void onRender3D(Render3DEvent event) {
        for (BlockPos pos : bedrockholes) {
            if (bedrockFlat.getValue()) {
                RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getValue(), bedrockOutlineGreen.getValue(), bedrockOutlineBlue.getValue(), bedrockOutlineAlpha.getValue())), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxAlpha.getValue(), true);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getValue(), bedrockOutlineGreen.getValue(), bedrockOutlineBlue.getValue(), bedrockOutlineAlpha.getValue())), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxAlpha.getValue(), true);
            }
        }
        for (BlockPos pos : obsidianholes) {
            if (obsidianFlat.getValue()) {
                RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getValue(), obsidianOutlineGreen.getValue(), obsidianOutlineBlue.getValue(), obsidianOutlineAlpha.getValue())), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxAlpha.getValue(), true);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getValue(), obsidianOutlineGreen.getValue(), obsidianOutlineBlue.getValue(), obsidianOutlineAlpha.getValue())), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxAlpha.getValue(), true);
            }
        }
        if (updates > updateDelay.getValue() && holes.getValue()) {
            obsidianholes.clear();
            bedrockholes.clear();
            findHoles();
        }
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        i = 0;
        if (this.orbs.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityXPOrb) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) bottlesred.getValue() / 255.0f, (float) bottlesgreen.getValue() / 255.0f, (float) bottlesblue.getValue() / 255.0f, (float) bottlesalpha.getValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(bottlesred.getValue(), bottlesgreen.getValue(), bottlesblue.getValue(), bottlesalpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.bottles.getValue()) {
            i = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityExpBottle) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) o_red.getValue() / 255.0f, (float) o_green.getValue() / 255.0f, (float) o_blue.getValue() / 255.0f, (float) o_alpha.getValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(o_red.getValue(), o_green.getValue(), o_blue.getValue(), o_alpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
    }

    public void onRender2D(Render2DEvent event) {
        if (renderPerformance.getValue()) {
            renderer.drawStringWithShadow(ChatFormatting.WHITE + "ESP " + ChatFormatting.DARK_GRAY + "[" + ChatFormatting.GRAY + updates + " | " + mc.getDebugFPS() + ChatFormatting.DARK_GRAY + "]", 0, 10, 0);
        }
    }

    public void findHoles() {
        assert (ESP.mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(ESP.mc.renderViewEntity.posX, ESP.mc.renderViewEntity.posY, ESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if(updates > updateDelay.getValue()) {
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                           bedrockholes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                          obsidianholes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                            bedrockholes.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                            obsidianholes.add(pos);
                            obsidianholes.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                            bedrockholes.add(pos.west());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                            obsidianholes.add(pos);
                            obsidianholes.add(pos.west());
                        }
                    }
                }
            }
        }
    }
}

