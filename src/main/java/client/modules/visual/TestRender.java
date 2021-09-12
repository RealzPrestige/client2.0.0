package client.modules.visual;

import client.events.Render2DEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.PlayerUtil;
import client.util.RenderUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class TestRender extends Module {
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    int redda;
    int grenna;
    int blunna;
    public TestRender(){
        super("TestRender", "", Category.VISUAL);
    }

    public void onEnable(){
        redda = 255;
        grenna = 255;
        blunna = 255;
    }

    public void onTick(){
        
        /**RED**/
        if(redda > red.getCurrentState()){
            redda = redda - (red.getCurrentState() > 200 ? red.getCurrentState() / 32 : red.getCurrentState() > 150 ? red.getCurrentState() / 16 : red.getCurrentState() > 100 ? red.getCurrentState() / 8 : red.getCurrentState() > 50 ? red.getCurrentState() / 4 : red.getCurrentState());
        } else if(redda < red.getCurrentState()) {
            redda = redda + (red.getCurrentState() > 200 ? red.getCurrentState() / 32 : red.getCurrentState() > 150 ? red.getCurrentState() / 16 : red.getCurrentState() > 100 ? red.getCurrentState() / 8 : red.getCurrentState() > 50 ? red.getCurrentState() / 4 : red.getCurrentState());
        }
        if(redda == red.getCurrentState()){
            return;
        }

        /**GREEN**/
        if(grenna > green.getCurrentState()){
            grenna = grenna - (green.getCurrentState() > 200 ? green.getCurrentState() / 32 : green.getCurrentState() > 150 ? green.getCurrentState() / 16 : green.getCurrentState() > 100 ? green.getCurrentState() / 8 : green.getCurrentState() > 50 ? green.getCurrentState() / 4 : green.getCurrentState());
        } else if(grenna < green.getCurrentState()) {
            grenna = grenna + (green.getCurrentState() > 200 ? green.getCurrentState() / 32 : green.getCurrentState() > 150 ? green.getCurrentState() / 16 : green.getCurrentState() > 100 ? green.getCurrentState() / 8 : green.getCurrentState() > 50 ? green.getCurrentState() / 4 : green.getCurrentState());
        }
        if(grenna == green.getCurrentState()){
            return;
        }

        /**BLUE**/
        if(blunna > blue.getCurrentState()){
            blunna = blunna - (blue.getCurrentState() > 200 ? blue.getCurrentState() / 32 : blue.getCurrentState() > 150 ? blue.getCurrentState() / 16 : blue.getCurrentState() > 100 ? blue.getCurrentState() / 8 : blue.getCurrentState() > 50 ? blue.getCurrentState() / 4 : blue.getCurrentState());
        } else if(blunna < blue.getCurrentState()) {
            blunna = blunna + (blue.getCurrentState() > 200 ? blue.getCurrentState() / 32 : blue.getCurrentState() > 150 ? blue.getCurrentState() / 16 : blue.getCurrentState() > 100 ? blue.getCurrentState() / 8 : blue.getCurrentState() > 50 ? blue.getCurrentState() / 4 : blue.getCurrentState());
        }
        if(blunna == blue.getCurrentState()){
            return;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event){
        BlockPos pos = PlayerUtil.getPlayerPos().down();
        RenderUtil.drawBoxESP(pos, new Color(redda, grenna, blunna), true, new Color(redda, grenna, blunna), 1, true, true,  120, true);
    }

    @Override
    public void onRender2D(Render2DEvent event){
        renderer.drawString("Red = " + redda, 0, 100, -1, true);
        renderer.drawString("Green = " + grenna, 0, 110, -1, true);
        renderer.drawString("Blue = " + blunna, 0, 120, -1, true);
    }
}
