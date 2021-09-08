package client.modules.visual;

import client.events.Render2DEvent;
import client.events.TotemPopEvent;
import client.modules.Module;
import client.modules.miscellaneous.FakePlayer;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.UUID;

public class poo extends Module {
    private static poo INSTANCE = new poo();
    public int fadeAlpha;
    public poo(){
        super("Poo", "Big shit", Category.VISUAL);
        this.setInstance();
    }

    public static poo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new poo();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onPop(EntityPlayer ent) {
        Entity entity;
        if (mc.world.getEntityByID(ent.getEntityId()) != null && (entity = mc.world.getEntityByID(ent.getEntityId())) instanceof EntityPlayer) {
           fadeAlpha = 255;
            EntityPlayer player = (EntityPlayer)entity;
            EntityOtherPlayerMP fakeEntity = new EntityOtherPlayerMP(mc.world, player.getGameProfile());
            fakeEntity.copyLocationAndAnglesFrom(player);
            fakeEntity.rotationYawHead = player.rotationYawHead;
            fakeEntity.prevRotationYawHead = player.rotationYawHead;
            fakeEntity.rotationYaw = player.rotationYaw;
            fakeEntity.prevRotationYaw = player.rotationYaw;
            fakeEntity.rotationPitch = player.rotationPitch;
            fakeEntity.prevRotationPitch = player.rotationPitch;
            fakeEntity.cameraYaw = fakeEntity.rotationYaw;
            fakeEntity.cameraPitch = fakeEntity.rotationPitch;
            if(fadeAlpha == 0){
                mc.world.removeEntityFromWorld(fakeEntity.getEntityId());
            }
        }
    }

    public void onTick(){
        if(fadeAlpha > 0) {
            --fadeAlpha;
        }
    }
}
