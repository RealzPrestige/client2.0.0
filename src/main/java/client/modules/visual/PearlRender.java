package client.modules.visual;

import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PearlRender extends Module {

    public HashMap<UUID, List<Vec3d>> pearlPos = new HashMap<>();
    public HashMap<UUID, Double> removeWait = new HashMap<>();

    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Double> lineWidth = register(new Setting<>("LineWidth", 3.0, 0.0, 10.0));

    public PearlRender() {
        super("PearlRender", "Draws a line where pearls are thrown", Category.VISUAL);
    }

    @Override
    public void onUpdate(){
        UUID pearlPos = null;
        for(UUID uuid : removeWait.keySet()){
            if(removeWait.get(uuid) <= 0){
                this.pearlPos.remove(uuid);
                pearlPos = uuid;
            }else {
                removeWait.replace(uuid, removeWait.get(uuid) - 0.05);
            }
        }
        if(pearlPos != null){
            removeWait.remove(pearlPos);
        }

        for(Entity e : mc.world.getLoadedEntityList()){
            if(!(e instanceof EntityEnderPearl))continue;
            if(!this.pearlPos.containsKey(e.getUniqueID())){
                this.pearlPos.put(e.getUniqueID(), new ArrayList<>(Collections.singletonList(e.getPositionVector())));
                this.removeWait.put(e.getUniqueID(), 0.1);
            }else {
                this.removeWait.replace(e.getUniqueID(), 0.1);
                List<Vec3d> v = this.pearlPos.get(e.getUniqueID());
                v.add(e.getPositionVector());
            }
        }

    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(pearlPos.isEmpty()){
            return;
        }
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth.getCurrentState().floatValue());
        for(UUID uuid : pearlPos.keySet()){
            if(pearlPos.get(uuid).size() <= 2){
                continue;
            }
            GL11.glBegin(1);
            for (int i = 1; i < pearlPos.get(uuid).size(); ++i) {
                Color color = new Color(ColorUtil.toARGB(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), 255));
                GL11.glColor3d(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

                List<Vec3d> pos = pearlPos.get(uuid);
                GL11.glVertex3d(pos.get(i).x - mc.getRenderManager().viewerPosX,pos.get(i).y - mc.getRenderManager().viewerPosY, pos.get(i).z - mc.getRenderManager().viewerPosZ);
                GL11.glVertex3d(pos.get(i - 1).x - mc.getRenderManager().viewerPosX,pos.get(i - 1).y - mc.getRenderManager().viewerPosY, pos.get(i - 1).z - mc.getRenderManager().viewerPosZ);
            }
            GL11.glEnd();
        }

        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
