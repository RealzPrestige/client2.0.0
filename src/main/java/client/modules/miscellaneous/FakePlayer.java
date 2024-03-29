package client.modules.miscellaneous;

import client.events.TotemPopEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.UUID;

public class FakePlayer extends Module {
    private static FakePlayer INSTANCE = new FakePlayer();
    public Setting<Boolean> copyInv = this.register(new Setting("Copy Inventory", true));
    public Setting<Boolean> moving = this.register(new Setting("Moving", false));
    public Setting<Integer> motion = this.register(new Setting("Motion", 2, -5, 5, v-> moving.getCurrentState()));

    public FakePlayer() {
        super("FakePlayer", "Spawns a fake Entity for testing.", Category.MISC);
        this.setInstance();
    }

    public static FakePlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakePlayer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
    private final int entityId = -420;

    public void onDisable() {
        if (fullNullCheck()) return;
        mc.world.removeEntityFromWorld(entityId);
    }

    
    public void onUpdate() {
        if (moving.getCurrentState()) {
            if (fullNullCheck()) return;
            GameProfile profile = new GameProfile(UUID.fromString("12cbdfad-33b7-4c07-aeac-01766e609482"), "FakePlayer");
            EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, profile);
            player.setLocationAndAngles(mc.player.posX + player.motionX + motion.getCurrentState(), mc.player.posY + player.motionY, mc.player.posZ + player.motionZ + motion.getCurrentState(), 90, 90);
            player.rotationYawHead = mc.player.rotationYawHead;
            if ( this.copyInv.getCurrentState( ) ) {
                player.inventory.copyInventory(mc.player.inventory);
            }
            mc.world.addEntityToWorld(entityId, player);
            return;
        }
    }
    public void onEnable(){
        if(!moving.getCurrentState()){
            if (fullNullCheck()) return;
            GameProfile profile = new GameProfile(UUID.fromString("12cbdfad-33b7-4c07-aeac-01766e609482"), "FakePlayer");
            EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, profile);
            player.copyLocationAndAnglesFrom(mc.player);
            player.rotationYawHead = mc.player.rotationYawHead;
            if ( this.copyInv.getCurrentState( ) ) {
                player.inventory.copyInventory(mc.player.inventory);
            }
            mc.world.addEntityToWorld(entityId, player);
            return;
        }
    }
}

