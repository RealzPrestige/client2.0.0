package client.modules.movement;

import client.modules.Module;
import client.util.Timer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "", Category.MOVEMENT);
    }
    Timer timer = new Timer();
    public void onUpdate() {
        if (NoFall.fullNullCheck()) {
            return;
        }
        timer.setCurrentMS();
        if (mc.player.fallDistance > 4.0F) {
            if (timer.hasDelayRun(800)) {
                final CPacketPlayer.Position outOfBoundsPosition = new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 420.69, mc.player.posZ, mc.player.onGround);
                mc.player.connection.sendPacket(outOfBoundsPosition);
                mc.player.connection.sendPacket(new CPacketConfirmTeleport());
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + 0.0622, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY - 420.69, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                timer.setLastMS();
                return;
            }
        }
    }
}
