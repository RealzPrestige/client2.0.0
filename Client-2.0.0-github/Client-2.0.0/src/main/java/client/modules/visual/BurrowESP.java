package client.modules.visual;

import client.Client;
import client.events.Render3DEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.EntityUtil;
import client.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BurrowESP extends Module {
    private final Setting<Boolean> name = this.register(new Setting<>("Name", true));
    private final Setting<Settings> setting = this.register(new Setting<>("Setting", Settings.ECHEST));
    //ECHEST
    private final Setting<Boolean> e_box = new Setting<>("Box", true);
    private final Setting<Integer> e_boxRed = this.register(new Setting<>("BoxRed", 255, 0, 255, v -> e_box.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_boxGreen = this.register(new Setting<>("BoxGreen", 255, 0, 255, v -> e_box.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_boxBlue = this.register(new Setting<>("BoxBlue", 255, 0, 255, v -> e_box.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_boxAlpha = this.register(new Setting<>("BoxAlpha", 127, 0, 255, v -> e_box.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Boolean> e_outline = this.register(new Setting<>("Outline", true, v -> this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Float> e_outlineWidth = this.register(new Setting<>("OutlineWidth", 1f, 0f, 5f, v -> this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Boolean> e_cOutline = this.register(new Setting<>("CustomOutline", true,v -> this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_outlineRed = this.register(new Setting<>("OutlineRed", 255, 0, 255, v -> e_outline.getCurrentState() && e_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_outlineGreen = this.register(new Setting<>("OutlineGreen", 255, 0, 255, v -> e_outline.getCurrentState() && e_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_outlineBlue = this.register(new Setting<>("OutlineBlue", 255, 0, 255, v -> e_outline.getCurrentState() && e_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    private final Setting<Integer> e_outlineAlpha = this.register(new Setting<>("OutlineAlpha", 255, 0, 255, v -> e_outline.getCurrentState() && e_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.ECHEST));
    //OBSIDIAN
    private final Setting<Boolean> o_box = new Setting<>("Box", true);
    private final Setting<Integer> o_boxRed = this.register(new Setting<>("BoxRed", 255, 0, 255, v -> o_box.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_boxGreen = this.register(new Setting<>("BoxGreen", 255, 0, 255, v -> o_box.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_boxBlue = this.register(new Setting<>("BoxBlue", 255, 0, 255, v -> o_box.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_boxAlpha = this.register(new Setting<>("BoxAlpha", 127, 0, 255, v -> o_box.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Boolean> o_outline = this.register(new Setting<>("Outline", true,v -> this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Float> o_outlineWidth = this.register(new Setting<>("OutlineWidth", 1f, 0f, 5f,v -> this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Boolean> o_cOutline = this.register(new Setting<>("CustomOutline", true,v -> this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_outlineRed = this.register(new Setting<>("OutlineRed", 255, 0, 255, v -> o_outline.getCurrentState() && o_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_outlineGreen = this.register(new Setting<>("OutlineGreen", 255, 0, 255, v -> o_outline.getCurrentState() && o_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_outlineBlue = this.register(new Setting<>("OutlineBlue", 255, 0, 255, v -> o_outline.getCurrentState() && o_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Setting<Integer> o_outlineAlpha = this.register(new Setting<>("OutlineAlpha", 255, 0, 255, v -> o_outline.getCurrentState() && o_cOutline.getCurrentState() && this.setting.getCurrentState() == Settings.OBSIDIAN));
    private final Map<EntityPlayer, BlockPos> burrowedPlayers = new HashMap<>();

    public BurrowESP() {
        super("BurrowESP", "Shows info about target",Category.VISUAL);
    }

    @Override
    public void onEnable() {
        burrowedPlayers.clear();
    }
    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        this.burrowedPlayers.clear();
        this.getPlayers();
    }
    @Override
    public void onRender3D(Render3DEvent event) {
        if (!this.burrowedPlayers.isEmpty()) {
            this.burrowedPlayers.forEach((key, value) -> {
                renderBurrowedBlock(value);
                if (this.name.getCurrentState())
                    RenderUtil.drawText(value, key.getGameProfile().getName());
            });
        }
    }

    private void renderBurrowedBlock(BlockPos pos) {
        if(mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST) {
            RenderUtil.drawBoxESP(pos, new Color(this.e_boxRed.getCurrentState(), this.e_boxGreen.getCurrentState(), this.e_boxBlue.getCurrentState(), this.e_boxAlpha.getCurrentState()), true, new Color(this.e_outlineRed.getCurrentState(), this.e_outlineGreen.getCurrentState(), this.e_outlineBlue.getCurrentState(), this.e_outlineAlpha.getCurrentState()), this.e_outlineWidth.getCurrentState(), this.e_outline.getCurrentState(), this.e_box.getCurrentState(), this.e_boxAlpha.getCurrentState(), true);
        }
        if(mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
            RenderUtil.drawBoxESP(pos, new Color(this.o_boxRed.getCurrentState(), this.o_boxGreen.getCurrentState(), this.o_boxBlue.getCurrentState(), this.o_boxAlpha.getCurrentState()), true, new Color(this.o_outlineRed.getCurrentState(), this.o_outlineGreen.getCurrentState(), this.o_outlineBlue.getCurrentState(), this.o_outlineAlpha.getCurrentState()), this.o_outlineWidth.getCurrentState(), this.o_outline.getCurrentState(), this.o_box.getCurrentState(), this.o_boxAlpha.getCurrentState(), true);
        }
    }
    private void getPlayers() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player || Client.friendManager.isFriend(player.getName()) || !EntityUtil.isLiving(player)) continue;
            if (this.isBurrowed(player)) {
                this.burrowedPlayers.put(player, new BlockPos(player.posX, player.posY, player.posZ));
            }
        }
    }
    private boolean isBurrowed(EntityPlayer player) {
        BlockPos pos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY+0.2), Math.floor(player.posZ));
        return  mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST ||
                mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }
    public enum Settings {
        OBSIDIAN,
        ECHEST
    }
}