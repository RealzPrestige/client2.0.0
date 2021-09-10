package client.modules.visual;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class PopChams extends Module {

    public static PopChams INSTANCE = new PopChams();

    public int fade;
    public PopChams() {
        super("PopChams", "Op module.", Category.VISUAL);
    }

    public void onTotemPop(EntityPlayer entityPlayer) {
        if(mc.world.getEntityByID(1) != null) {
            mc.world.removeEntityFromWorld(1);
        }
        fade = 255;
        if (fade > 0) {
            GameProfile profile = new GameProfile(UUID.fromString("12cbdfad-33b7-4c07-aeac-01766e609482"), "PopCham");
            EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, profile);
            player.copyLocationAndAnglesFrom(entityPlayer);
            player.rotationYawHead = entityPlayer.rotationYawHead;
            mc.world.addEntityToWorld(1, player);
        } else if (fade <= 0){
            mc.world.removeEntityFromWorld(1);
        }
    }
    public void onTick(){
        if(fade > 0){
            fade = fade -2;
        }
    }
}
