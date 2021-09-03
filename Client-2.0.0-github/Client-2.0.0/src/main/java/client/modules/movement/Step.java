package client.modules.movement;

import client.modules.Module;
import client.gui.impl.setting.Setting;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer;

public class Step extends Module {
    public Setting<Boolean> vanilla = this.register( new Setting <> ( "Vanilla" , false ));
    public Setting<Integer> stepHeight = this.register(new Setting<Object>("Height", 2 , 1 , 4 , v -> ! this.vanilla.getCurrentState( ) ));
    private final double[] oneblockPositions = new double[]{0.42, 0.75};
    private final double[] twoblockPositions = new double[]{0.4, 0.75, 0.5, 0.41, 0.83, 1.16, 1.41, 1.57, 1.58, 1.42};
    private final double[] futurePositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    final double[] twoFiveOffset = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    private final double[] fourBlockPositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43, 2.78, 2.63, 2.51, 2.9, 3.21, 3.45, 3.43};
    private double[] selectedPositions = new double[0];
    private int packets;
    private static Step instance;

    public Step() {
        super("Step", "Allows you to travel up walls of 2 high.", Module.Category.MOVEMENT);
        instance = this;
    }

    public static Step getInstance() {
        if (instance == null) {
            instance = new Step();
        }
        return instance;
    }

    @Override
    public void onToggle() {
        Step.mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if ( this.vanilla.getCurrentState( ) ) {
            Step.mc.player.stepHeight = this.stepHeight.getCurrentState().floatValue();
            return;
        }
        switch (this.stepHeight.getCurrentState()) {
            case 1: {
                this.selectedPositions = this.oneblockPositions;
                break;
            }
            case 2: {
                this.selectedPositions = false ? this.twoblockPositions : this.futurePositions;
                break;
            }
            case 3: {
                this.selectedPositions = this.twoFiveOffset;
            }
            case 4: {
                this.selectedPositions = this.fourBlockPositions;
            }
        }
        if (Step.mc.player.collidedHorizontally && Step.mc.player.onGround) {
            ++this.packets;
        }
        if (Step.mc.player.onGround && !Step.mc.player.isInsideOfMaterial(Material.WATER) && !Step.mc.player.isInsideOfMaterial(Material.LAVA) && Step.mc.player.collidedVertically && Step.mc.player.fallDistance == 0.0f && !Step.mc.gameSettings.keyBindJump.pressed && Step.mc.player.collidedHorizontally && !Step.mc.player.isOnLadder() && (this.packets > this.selectedPositions.length - 2 || this.packets > 0 )) {
            for (double position : this.selectedPositions) {
                Step.mc.player.connection.sendPacket( new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + position, Step.mc.player.posZ, true) );
            }
            Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + this.selectedPositions[this.selectedPositions.length - 1], Step.mc.player.posZ);
            this.packets = 0;
        }
    }
    @Override
    public String getDisplayInfo() {
        if (this.vanilla.getCurrentState()){
            return "Vanilla";
        } else {
            return "NCP";
        }
    }
}