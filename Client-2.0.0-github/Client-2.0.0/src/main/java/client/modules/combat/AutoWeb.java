package client.modules.combat;

import client.Client;
import client.command.Command;
import client.events.Render3DEvent;
import client.modules.Module;
import client.modules.client.ClickGui;
import client.gui.impl.setting.Setting;
import client.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class AutoWeb extends Module {
    public static boolean isPlacing = false;
    private final Setting<Integer> delay = this.register( new Setting <> ( "Delay" , 10 , 0 , 250 ));
    private final Setting<Integer> blocksPerPlace = this.register( new Setting <> ( "BlocksPerTick" , 8 , 1 , 30 ));
    private final Setting<Boolean> packet = this.register( new Setting <> ( "PacketPlace" , false ));
    private final Setting<Boolean> offhand = this.register( new Setting <> ( "Offhand" , false ));
    private final Setting<Boolean> disable = this.register( new Setting <> ( "AutoDisable" , false ));
    private final Setting<Boolean> rotate = this.register( new Setting <> ( "Rotate" , false ));
    private final Setting<Boolean> raytrace = this.register( new Setting <> ( "Raytrace" , false ));
    private final Setting<Boolean> lowerbody = this.register( new Setting <> ( "Feet" , true ));
    private final Setting<Boolean> upperBody = this.register( new Setting <> ( "Face" , false ));
    private final Setting<Boolean> render = this.register( new Setting <> ( "Render" , false ));
    public Setting<Boolean> box = this.register( new Setting <> ( "Box" , false , v -> this.render.getCurrentState( ) ));
    private final Setting<Integer> red = this.register( new Setting <> ( "Red" , 0 , 0 , 255 , v -> this.box.getCurrentState( ) ));
    private final Setting<Integer> green = this.register( new Setting <> ( "Green" , 255 , 0 , 255 , v -> this.box.getCurrentState( ) ));
    private final Setting<Integer> blue = this.register( new Setting <> ( "Blue" , 0 , 0 , 255 , v -> this.box.getCurrentState( ) ));
    public Setting<Boolean> Rainbow = this.register( new Setting <> ( "Rainbow" , false , v -> this.box.getCurrentState( ) ));
    private final Setting<Integer> alpha = this.register( new Setting <> ( "Alpha" , 255 , 0 , 255 , v -> this.box.getCurrentState( ) ));
    private final Setting<Integer> boxAlpha = this.register( new Setting <> ( "BoxAlpha" , 125 , 0 , 255 , v -> this.box.getCurrentState( ) ));
    public Setting<Boolean> outline = this.register( new Setting <> ( "Outline" , false , v -> this.render.getCurrentState( ) ));
    private final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", 0 , 0 , 255 , v -> this.outline.getCurrentState()));
    private final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", 0 , 0 , 255 , v -> this.outline.getCurrentState()));
    private final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", 255 , 0 , 255 , v -> this.outline.getCurrentState()));
    public Setting<Boolean> cRainbow = this.register( new Setting <> ( "OL-Rainbow" , false , v -> this.outline.getCurrentState( ) ));
    private final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", 255 , 0 , 255 , v -> this.outline.getCurrentState()));
    private final Setting<Float> lineWidth = this.register( new Setting <> ( "LineWidth" , 1.0f , 0.1f , 5.0f , v -> this.outline.getCurrentState( ) ));

    private final Timer timer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private BlockPos startPos = null;
    private BlockPos renderPos = null;

    public AutoWeb() {
        super("AutoWeb", "Traps other players in webs.", Module.Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (AutoWeb.fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedBlockPos(AutoWeb.mc.player);
        this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
    }

    @Override
    public void onTick() {
        this.doTrap();
    }

    @Override
    public String hudInfoString() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    @Override
    public void onDisable() {
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }

    private void doTrap() {
        if (this.check()) {
            return;
        }
        this.doWebTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void doWebTrap() {
        List<Vec3d> placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }

    private List<Vec3d> getPlacements() {
        ArrayList<Vec3d> list = new ArrayList <> ( );
        Vec3d baseVec = this.target.getPositionVector();
        if ( this.lowerbody.getCurrentState( ) ) {
            list.add(baseVec);
        }
        if ( this.upperBody.getCurrentState( ) ) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(AutoWeb.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoWeb.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (Vec3d vec3d3 : list) {
            BlockPos position = new BlockPos(vec3d3);
            renderPos = position;
            int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getCurrentState());
            if (placeability != 3 && placeability != 1) continue;
            this.placeBlock(position);
        }
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (this.isOff()) {
            return true;
        }
        if ( this.disable.getCurrentState( ) && !this.startPos.equals(EntityUtil.getRoundedBlockPos(AutoWeb.mc.player))) {
            this.disable();
            return true;
        }
        if (obbySlot == -1) {
            Command.sendMessage("" + this.getDisplayName() + " " + ChatFormatting.RED + "No Webs in hotbar disabling...");
            this.toggle();
            return true;
        }
        if (AutoWeb.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoWeb.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
        }
        this.switchItem(true);
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(10.0);
        return this.target == null || !this.timer.passedMs( this.delay.getCurrentState( ) );
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : AutoWeb.mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range) || player.isInWeb || Client.speedManager.getPlayerSpeed(player) > 30.0)
                continue;
            if (target == null) {
                target = player;
                distance = AutoWeb.mc.player.getDistanceSq(player);
                continue;
            }
            if (!(AutoWeb.mc.player.getDistanceSq(player) < distance)) continue;
            target = player;
            distance = AutoWeb.mc.player.getDistanceSq(player);
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getCurrentState() && AutoWeb.mc.player.getDistanceSq(pos) <= MathUtil.square(6.0) && this.switchItem(false)) {
            isPlacing = true;
            int originalSlot = AutoWeb.mc.player.inventory.currentItem;
            int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
            if (webSlot == -1) {
                this.toggle();
            }
                AutoWeb.mc.player.inventory.currentItem = webSlot == -1 ? webSlot : webSlot;
                AutoWeb.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlock(pos, offhand.getCurrentState() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getCurrentState(), this.packet.getCurrentState(), this.isSneaking);
                AutoWeb.mc.player.inventory.currentItem = originalSlot;
                AutoWeb.mc.playerController.updateController();
            didPlace = true;
            ++placements;
        }
    }

    private boolean switchItem(boolean back) {
        boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, InventoryUtil.Switch.SILENT, BlockWeb.class);
        this.switchedItem = value[0];
        return value[1];
    }
    @Override
    public void onLogout() {
        this.disable();
    }
    @Override
    public void onRender3D(Render3DEvent event) {
        if ( this.render.getCurrentState( ) ) {
            RenderUtil.drawBoxESP(renderPos, Rainbow.getCurrentState() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getCurrentState()) : new Color(this.red.getCurrentState(), this.green.getCurrentState(), this.blue.getCurrentState(), this.alpha.getCurrentState()), this.outline.getCurrentState(), cRainbow.getCurrentState() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getCurrentState()) : new Color(this.cRed.getCurrentState(), this.cGreen.getCurrentState(), this.cBlue.getCurrentState(), this.cAlpha.getCurrentState()), this.lineWidth.getCurrentState( ) , this.outline.getCurrentState(), this.box.getCurrentState(), this.boxAlpha.getCurrentState(), true);
        }
    }
}

