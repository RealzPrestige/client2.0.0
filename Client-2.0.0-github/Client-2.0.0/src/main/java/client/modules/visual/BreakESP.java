package client.modules.visual;

import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.*;
import java.util.Objects;

public class BreakESP extends Module {
    public Setting<Boolean> box = register(new Setting<>("Box", true));
    public Setting<Boolean> outline = register(new Setting<>("Outline", true));
    public Setting<Integer> red = register(new Setting<>("Red", 0, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 0, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255));

    String string;
    public BreakESP(){
        super("BreakESP", "Shows where spots are being broken.", Category.VISUAL);
    }

    @Override
    public void onRender3D(Render3DEvent event){
        mc.renderGlobal.damagedBlocks.forEach(((integer, destroyBlockProgress) -> {
            RenderUtil.drawBoxESP(destroyBlockProgress.getPosition(), new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState()), true, new Color(red.getCurrentState(), green.getCurrentState(), blue.getCurrentState(), alpha.getCurrentState()),1, outline.getCurrentState(), box.getCurrentState(), alpha.getCurrentState(), true);
            RenderUtil.drawText(destroyBlockProgress.getPosition(), Objects.requireNonNull(mc.world.getEntityByID(integer)).getName() + " " + string + (destroyBlockProgress.getPartialBlockDamage() * 12.5) + "%");
        }));
    }

    public void onUpdate(){
        mc.renderGlobal.damagedBlocks.forEach(((integer, destroyBlockProgress) -> {
        if(destroyBlockProgress.getPartialBlockDamage() * 12.5 <= 25){
                string = ChatFormatting.GREEN + "";
        } else if(destroyBlockProgress.getPartialBlockDamage() * 12.5 <= 50 && destroyBlockProgress.getPartialBlockDamage() * 12.5 >= 25){
            string = ChatFormatting.YELLOW + "";
        } else if(destroyBlockProgress.getPartialBlockDamage() * 12.5 <= 75 && destroyBlockProgress.getPartialBlockDamage() * 12.5 >= 50){
            string = ChatFormatting.GOLD + "";
        } else if(destroyBlockProgress.getPartialBlockDamage() * 12.5 <= 100 && destroyBlockProgress.getPartialBlockDamage() * 12.5 >= 75){
            string = ChatFormatting.RED +"";
        }
        }));
    }
}
