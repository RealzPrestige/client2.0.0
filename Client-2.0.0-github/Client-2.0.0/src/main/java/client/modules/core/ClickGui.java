package client.modules.core;

import client.Client;
import client.events.ClientEvent;
import client.gui.ClientGui;
import client.gui.impl.setting.Setting;
import client.modules.Module;
import client.util.Util;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public int yaw;
    public Setting<Gui> gui = this.register(new Setting("Gui", Gui.OLD));
    //NEW GUI
    public Setting<Boolean> chamsViewer = this.register(new Setting<>("ChamsViewer", false, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Boolean> logo = this.register(new Setting<>("Logo", true, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Boolean> newBlur = this.register(new Setting<>("Blur", true, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newtopred = this.register(new Setting<>("TopRed", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newtopgreen = this.register(new Setting<>("TopGreen", 0, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newtopblue = this.register(new Setting<>("TopBlue", 0, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newtopalpha = this.register(new Setting<>("TopAlpha", 110, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newared = this.register(new Setting<>("Red", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newagreen = this.register(new Setting<>("Green", 0, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newablue = this.register(new Setting<>("Blue", 0, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newaalpha = this.register(new Setting<>("Alpha", 110, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newred = this.register(new Setting<>("SideRed", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newgreen = this.register(new Setting<>("SideGreen", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newblue = this.register(new Setting<>("SideBlue", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newtheAlpha = this.register(new Setting<>("SideAlpha", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newbgred = this.register(new Setting<>("BackGroundRed", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newbggreen = this.register(new Setting<>("BackGroundGreen", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newbgblue = this.register(new Setting<>("BackGroundBlue", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newbgAlpha = this.register(new Setting<>("BackGroundAlpha", 27, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newthirdRed = this.register(new Setting<>("ThirdRed", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newthirdGreen = this.register(new Setting<>("ThirdGreen", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newthirdBlue = this.register(new Setting<>("ThirdBlue", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> newthirdAlpha = this.register(new Setting<>("ThirdAlpha", 255, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> integerRed = this.register(new Setting<>("IntegerRed", 30, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> integerGreen = this.register(new Setting<>("IntegerGreen", 30, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> integerBlue = this.register(new Setting<>("IntegerBlue", 30, 0, 255, v -> gui.getCurrentState() == Gui.NEW));
    public Setting<Integer> integerAlpha = this.register(new Setting<>("IntegerAlpha", 120, 0, 255, v -> gui.getCurrentState() == Gui.NEW));

    //OLD GUI
    public Setting<Boolean> topRectTextBold = this.register(new Setting("TopRectTextBold", true, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Rect> topRect = this.register(new Setting("TopRectangle", Rect.ROUNDED, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Roundedness> roundedness = this.register(new Setting("Roundedness", Roundedness.FULL, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Bottom> bottomRect = this.register(new Setting("BottomRect", Bottom.ROUNDED, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> particles = this.register(new Setting("Particles", true, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> particleLength = this.register(new Setting("ParticleLength", 50, 0, 300, v -> particles.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> particlered = this.register(new Setting("ParticleRed", 255, 0, 255, v -> particles.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> particlegreen = this.register(new Setting("ParticleGreen", 255, 0, 255, v -> particles.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> particleblue = this.register(new Setting("ParticleBlue", 255, 0, 255, v -> particles.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> snowing = this.register(new Setting("Snowing", true, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> blur = this.register(new Setting("Blur", true, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Align> componentAlign = this.register(new Setting("ComponentAlign", Align.LEFT, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<String> prefix = this.register(new Setting("Prefix", ":", v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> red = this.register(new Setting("BackgroundRed", 0, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> green = this.register(new Setting("BackgroundGreen", 0, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> blue = this.register(new Setting("BackgroundBlue", 0, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> b_alpha = this.register(new Setting("BackgroundAlpha", 50, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> disabled = this.register(new Setting("Disabled", true, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> d_red = this.register(new Setting("DisabledRed", 127, 0, 255, v -> this.disabled.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> d_green = this.register(new Setting("DisabledGreen", 127, 0, 255, v -> this.disabled.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> d_blue = this.register(new Setting("DisabledBlue", 127, 0, 255, v -> this.disabled.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> d_alpha = this.register(new Setting("DisabledAlpha", 40, 0, 255, v -> this.disabled.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> alpha = this.register(new Setting("EnabledAlpha", 255, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> topRed = this.register(new Setting("SecondRed", 0, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> topGreen = this.register(new Setting("SecondGreen", 0, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> topBlue = this.register(new Setting("SecondBlue", 150, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> secondAlpha = this.register(new Setting("SecondAlpha", 255, 0, 255, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> outline = this.register(new Setting("Outline", false, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> o_red = this.register(new Setting("OutlineRed", 0, 0, 255, v -> outline.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> o_green = this.register(new Setting("OutlineGreen", 0, 0, 255, v -> outline.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> o_blue = this.register(new Setting("OutlineBlue", 150, 0, 255, v -> outline.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> o_alpha = this.register(new Setting("OutlineAlpha", 255, 0, 255, v -> outline.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> button = this.register(new Setting("Button", true, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<Button> buttonButton = this.register(new Setting("ButtonSort", Button.PLUS, v -> button.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false, v -> gui.getCurrentState() == Gui.OLD));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("HRainbowMode", rainbowMode.Static, v -> this.rainbow.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", 240, 0, 600, v -> this.rainbow.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getCurrentState() && gui.getCurrentState() == Gui.OLD));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getCurrentState() && gui.getCurrentState() == Gui.OLD));

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui that contains all modules.", Module.Category.CORE);
        setBind(Keyboard.KEY_O);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Client.commandManager.setPrefix(this.prefix.getPlannedValue());
            }
            Client.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.alpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(ClientGui.getClickGui());
    }

    @Override
    public void onDisable() {
        Client.configManager.saveConfig("Default");
    }

    @Override
    public void onLoad() {
        Client.colorManager.setColor(this.red.getCurrentState(), this.green.getCurrentState(), this.blue.getCurrentState(), this.alpha.getCurrentState());
        Client.commandManager.setPrefix(this.prefix.getCurrentState());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ClientGui)) {
            this.disable();
        }
        ++yaw;
    }

    public enum Gui {NEW, OLD}


    public enum Rect {ROUNDED, SQUARE}

    public enum Roundedness {TINY, LITTLE, MEDIUM, LARGE, FULL}

    public enum Bottom {ROUNDED, NORMAL}

    public enum Align {LEFT, MIDDLE}

    public enum Button {PLUS, DOT}

    public enum rainbowMode {
        Static,
        Sideway

    }
}

