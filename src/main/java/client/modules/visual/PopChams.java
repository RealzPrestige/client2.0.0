package client.modules.visual;

import client.Client;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import java.util.UUID;

public class PopChams extends Module {
    public static PopChams INSTANCE = new PopChams();
    public Setting<PopSpeed> popSpeed = register(new Setting<>("PopSpeed", PopSpeed.SLOW));
    public enum PopSpeed{SLOW, SLOWMEDIUM, MEDIUM, MEDIUMFAST,  FAST}
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<StartAlpha> startAlpha = register(new Setting<>("StartAlpha", StartAlpha.LOW));
    public enum StartAlpha{LOW, LOWMEDIUM, MEDIUM, MEDIUMHIGH,  HIGH}

    public PopChams() {
        super("PopChams", "Draws Fakeplayers when and where somebody pops.", Category.VISUAL);
        this.setInstance();
    }

    public static PopChams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopChams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    public void onTotemPop(EntityPlayer entityPlayer) {
        if (mc.world.getEntityByID(6900) != null) {
            mc.world.removeEntityFromWorld(6900);
        }
            GameProfile profile = new GameProfile(UUID.fromString("12cbdfad-33b7-4c07-aeac-01766e609482"), "Pop");
            EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, profile);
            player.copyLocationAndAnglesFrom(entityPlayer);
            player.rotationYawHead = entityPlayer.rotationYawHead;
            mc.world.addEntityToWorld(6900, player);
    }

    public void onTick(){
        if(Client.eventManager.popAlpha <= 0){
            mc.world.removeEntityFromWorld(6900);
           }
    }

    public void onEnable(){
        if(Client.friendManager.isFriend("Pop")){
            return;
        } else {
            Client.friendManager.addFriend("Pop");
        }
    }
}
