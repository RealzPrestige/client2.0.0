package client.modules.miscellaneous;

import client.events.ChorusEvent;
import client.events.Render3DEvent;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.ColorUtil;
import client.util.RenderUtil;
import client.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;


/**
 *
 * @Author Perry
 * 26/08/2021
 *
 */

public class ChorusPredict extends Module {
    private final Setting<Integer> time = this.register(new Setting<>("Duration", 500, 50, 3000));
    private final Setting<Boolean> box = this.register(new Setting<>("Box", true));
    private final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    private final Setting<Integer> boxR = this.register(new Setting<>("BoxR", 255, 0, 255, v -> this.box.getCurrentState()));
    private final Setting<Integer> boxG = this.register(new Setting<>("BoxG", 255, 0, 255, v -> this.box.getCurrentState()));
    private final Setting<Integer> boxB = this.register(new Setting<>("BoxB", 255, 0, 255, v -> this.box.getCurrentState()));
    private final Setting<Integer> boxA = this.register(new Setting<>("BoxA", 120, 0, 255, v -> this.box.getCurrentState()));
    private final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getCurrentState()));
    private final Setting<Integer> outlineR = this.register(new Setting<>("OutlineR", 255, 0, 255, v -> this.outline.getCurrentState()));
    private final Setting<Integer> outlineG = this.register(new Setting<>("OutlineG", 255, 0, 255, v -> this.outline.getCurrentState()));
    private final Setting<Integer> outlineB = this.register(new Setting<>("OutlineB", 255, 0, 255, v -> this.outline.getCurrentState()));
    private final Setting<Integer> outlineA = this.register(new Setting<>("OutlineA", 255, 0, 255, v -> this.outline.getCurrentState()));
    private final Timer timer = new Timer();
    private double x;
    private double y;
    private double z;

    public ChorusPredict() {
        super("ChorusPredict", "Predicts where a chorus goes and draws a box there.", Category.MISC);
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
    @SubscribeEvent
    public void onChorus(ChorusEvent event) {
        this.x = event.getChorusX();
        this.y = event.getChorusY();
        this.z = event.getChorusZ();
        this.timer.reset();
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        if (timer.passedMs(time.getCurrentState())) return;
        AxisAlignedBB pos = RenderUtil.interpolateAxis(new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3));
        if (this.outline.getCurrentState()) {
            RenderUtil.drawBlockOutline(pos, new Color(this.outlineR.getCurrentState(), this.outlineG.getCurrentState(), this.outlineB.getCurrentState(), this.outlineA.getCurrentState()), this.lineWidth.getCurrentState());
        }
        if (this.box.getCurrentState()) {
            RenderUtil.drawFilledBox(pos, ColorUtil.toRGBA(this.boxR.getCurrentState(), this.boxG.getCurrentState(), this.boxB.getCurrentState(), this.boxA.getCurrentState()));
        }
    }
}