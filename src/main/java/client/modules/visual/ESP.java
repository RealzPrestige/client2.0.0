package client.modules.visual;

import client.events.ClientEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import client.util.EntityUtil;
import client.util.RenderUtil;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
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
    public Setting<Integer> range = register(new Setting<>("X-Range", 0, 1, 20, v-> holes.getCurrentState()));
    public Setting<Integer> rangeY = register(new Setting<>("Y-Range", 0, 1, 20, v-> holes.getCurrentState()));
    public Setting<Integer> updateDelay = register(new Setting<>("UpdateDelay", 1, 0, 30, v-> holes.getCurrentState()));

    public Setting<Boolean> bedrockBox = register(new Setting<>("BedrockBox", true, v-> holes.getCurrentState()));
    public Setting<Boolean> bedrockFlat = register(new Setting<>("BedrockFlat", true, v-> holes.getCurrentState()));
    public Setting<Integer> bedrockBoxRed = register(new Setting<>( "BedrockBoxRed", 0, 0, 255, v-> holes.getCurrentState() && bedrockBox.getCurrentState()));
    public Setting<Integer> bedrockBoxGreen = register(new Setting<>("BedrockBoxGreen", 255, 0, 255, v-> holes.getCurrentState() && bedrockBox.getCurrentState()));
    public Setting<Integer> bedrockBoxBlue = register(new Setting<>("BedrockBoxBlue", 0, 0, 255, v-> holes.getCurrentState() && bedrockBox.getCurrentState()));
    public Setting<Integer> bedrockBoxAlpha = register(new Setting<>("BedrockBoxAlpha", 120, 0, 255, v-> holes.getCurrentState() && bedrockBox.getCurrentState()));
    public Setting<Boolean> bedrockOutline = register(new Setting<>("BedrockOutline", true, v-> holes.getCurrentState()));
    public Setting<Integer> bedrockOutlineRed = register(new Setting<>( "BedrockOutlineRed", 0, 0, 255, v-> holes.getCurrentState() && bedrockOutline.getCurrentState()));
    public Setting<Integer> bedrockOutlineGreen = register(new Setting<>("BedrockOutlineGreen", 255, 0, 255, v-> holes.getCurrentState() && bedrockOutline.getCurrentState()));
    public Setting<Integer> bedrockOutlineBlue = register(new Setting<>("BedrockOutlineBlue", 0, 0, 255, v-> holes.getCurrentState() && bedrockOutline.getCurrentState()));
    public Setting<Integer> bedrockOutlineAlpha = register(new Setting<>("BedrockOutlineAlpha", 255, 0, 255, v-> holes.getCurrentState() && bedrockOutline.getCurrentState()));
    public Setting<Integer> bedrockOutlineLineWidth = register(new Setting<>("BedrockOutlineLineWidth", 1, 0.1, 5, v-> holes.getCurrentState() && bedrockOutline.getCurrentState()));

    public Setting<Boolean> obsidianBox = register(new Setting<>("ObsidianBox", true, v-> holes.getCurrentState()));
    public Setting<Boolean> obsidianFlat = register(new Setting<>("ObsidianFlat", false, v-> holes.getCurrentState()));
    public Setting<Integer> obsidianBoxRed = register(new Setting<>( "ObsidianBoxRed", 255, 0, 255, v-> holes.getCurrentState() && obsidianBox.getCurrentState()));
    public Setting<Integer> obsidianBoxGreen = register(new Setting<>("ObsidianBoxGreen", 0, 0, 255, v-> holes.getCurrentState() && obsidianBox.getCurrentState()));
    public Setting<Integer> obsidianBoxBlue = register(new Setting<>("ObsidianBoxBlue", 0, 0, 255, v-> holes.getCurrentState() && obsidianBox.getCurrentState()));
    public Setting<Integer> obsidianBoxAlpha = register(new Setting<>("ObsidianBoxAlpha", 120, 0, 255, v-> holes.getCurrentState() && obsidianBox.getCurrentState()));
    public Setting<Boolean> obsidianOutline = register(new Setting<>("ObsidianOutline", true, v-> holes.getCurrentState()));
    public Setting<Integer> obsidianOutlineRed = register(new Setting<>( "ObsidianOutlineRed", 255, 0, 255, v-> holes.getCurrentState() && obsidianOutline.getCurrentState()));
    public Setting<Integer> obsidianOutlineGreen = register(new Setting<>("ObsidianOutlineGreen", 0, 0, 255, v-> holes.getCurrentState() && obsidianOutline.getCurrentState()));
    public Setting<Integer> obsidianOutlineBlue = register(new Setting<>("ObsidianOutlineBlue", 0, 0, 255, v-> holes.getCurrentState() && obsidianOutline.getCurrentState()));
    public Setting<Integer> obsidianOutlineAlpha = register(new Setting<>("ObsidianOutlineAlpha", 255, 0, 255, v-> holes.getCurrentState() && obsidianOutline.getCurrentState()));
    public Setting<Integer> obsidianOutlineLineWidth = register(new Setting<>("obsidianOutlineLineWidth", 1, 0.1, 5, v-> holes.getCurrentState() && obsidianOutline.getCurrentState()));

    public Setting<Boolean> bottles = register(new Setting<>("Bottles", true));
    public Setting<Integer> bottlesred = register(new Setting<>( "BottlesRed", 255, 0, 255, v -> this.bottles.getCurrentState( ) ));
    public Setting<Integer> bottlesgreen = register(new Setting<>("BottlesGreen", 255, 0, 255, v -> this.bottles.getCurrentState( ) ));
    public Setting<Integer> bottlesblue = register(new Setting<>("BottlesBlue", 255, 0, 255, v -> this.bottles.getCurrentState( ) ));
    public Setting<Integer> bottlesalpha = register(new Setting<>("BottlesAlpha", 150, 0, 255, v -> this.bottles.getCurrentState( ) ));

    public Setting<Boolean> orbs = register(new Setting<>("Orbs", true));
    public Setting<Integer> o_red = register(new Setting<>("OrbsRed", 255, 0, 255, v -> this.orbs.getCurrentState( ) ));
    public Setting<Integer> o_green = register(new Setting<>("OrbsGreen", 255, 0, 255, v -> this.orbs.getCurrentState( ) ));
    public Setting<Integer> o_blue = register(new Setting<>("OrbsBlue", 255, 0, 255, v -> this.orbs.getCurrentState( ) ));
    public Setting<Integer> o_alpha = register(new Setting<>("OrbsAlpha", 150, 0, 255, v -> this.orbs.getCurrentState( ) ));

    public ESP() {
    super("ESP", "Draws boxes on your screen.", Category.VISUAL);
    }

    public void onTick() {
        if (holes.getCurrentState()) {
            if (updates > updateDelay.getCurrentState()) {
                updates = 0;
            } else {
                ++updates;
            }
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if(!holes.getCurrentState()) {
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
            if (bedrockFlat.getCurrentState()) {
                RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getCurrentState(), bedrockBoxGreen.getCurrentState(), bedrockBoxBlue.getCurrentState(), bedrockBoxAlpha.getCurrentState())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getCurrentState(), bedrockOutlineGreen.getCurrentState(), bedrockOutlineBlue.getCurrentState(), bedrockOutlineAlpha.getCurrentState())), bedrockOutlineLineWidth.getCurrentState(), bedrockOutline.getCurrentState(), bedrockBox.getCurrentState(), bedrockBoxAlpha.getCurrentState(), true);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getCurrentState(), bedrockBoxGreen.getCurrentState(), bedrockBoxBlue.getCurrentState(), bedrockBoxAlpha.getCurrentState())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getCurrentState(), bedrockOutlineGreen.getCurrentState(), bedrockOutlineBlue.getCurrentState(), bedrockOutlineAlpha.getCurrentState())), bedrockOutlineLineWidth.getCurrentState(), bedrockOutline.getCurrentState(), bedrockBox.getCurrentState(), bedrockBoxAlpha.getCurrentState(), true);
            }
        }
        for (BlockPos pos : obsidianholes) {
            if (obsidianFlat.getCurrentState()) {
                RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getCurrentState(), obsidianBoxGreen.getCurrentState(), obsidianBoxBlue.getCurrentState(), obsidianBoxAlpha.getCurrentState())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getCurrentState(), obsidianOutlineGreen.getCurrentState(), obsidianOutlineBlue.getCurrentState(), obsidianOutlineAlpha.getCurrentState())), obsidianOutlineLineWidth.getCurrentState(), obsidianOutline.getCurrentState(), obsidianBox.getCurrentState(), obsidianBoxAlpha.getCurrentState(), true);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getCurrentState(), obsidianBoxGreen.getCurrentState(), obsidianBoxBlue.getCurrentState(), obsidianBoxAlpha.getCurrentState())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getCurrentState(), obsidianOutlineGreen.getCurrentState(), obsidianOutlineBlue.getCurrentState(), obsidianOutlineAlpha.getCurrentState())), obsidianOutlineLineWidth.getCurrentState(), obsidianOutline.getCurrentState(), obsidianBox.getCurrentState(), obsidianBoxAlpha.getCurrentState(), true);
            }
        }
        if (updates > updateDelay.getCurrentState() && holes.getCurrentState()) {
            obsidianholes.clear();
            bedrockholes.clear();
            findHoles();
        }
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        i = 0;
        if (this.orbs.getCurrentState()) {
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
                RenderGlobal.renderFilledBox(bb, (float) bottlesred.getCurrentState() / 255.0f, (float) bottlesgreen.getCurrentState() / 255.0f, (float) bottlesblue.getCurrentState() / 255.0f, (float) bottlesalpha.getCurrentState() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(bottlesred.getCurrentState(), bottlesgreen.getCurrentState(), bottlesblue.getCurrentState(), bottlesalpha.getCurrentState()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.bottles.getCurrentState()) {
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
                RenderGlobal.renderFilledBox(bb, (float) o_red.getCurrentState() / 255.0f, (float) o_green.getCurrentState() / 255.0f, (float) o_blue.getCurrentState() / 255.0f, (float) o_alpha.getCurrentState() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(o_red.getCurrentState(), o_green.getCurrentState(), o_blue.getCurrentState(), o_alpha.getCurrentState()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
    }

    public void findHoles() {
        assert (ESP.mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(ESP.mc.renderViewEntity.posX, ESP.mc.renderViewEntity.posY, ESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getCurrentState(); x < playerPos.getX() + this.range.getCurrentState(); ++x) {
            for (int z = playerPos.getZ() - this.range.getCurrentState(); z < playerPos.getZ() + this.range.getCurrentState(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getCurrentState(); y > playerPos.getY() - this.rangeY.getCurrentState(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if(updates > updateDelay.getCurrentState()) {
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
    public String hudInfoString(){
        return updates + " | " + Minecraft.getDebugFPS();
    }
}

