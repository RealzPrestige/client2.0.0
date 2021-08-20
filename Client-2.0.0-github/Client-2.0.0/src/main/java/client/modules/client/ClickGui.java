package client.modules.client;

import client.Client;
import client.command.Command;
import client.events.ClientEvent;
import client.gui.ClientGui;
import client.gui.impl.background.MainMenuButton;
import client.modules.Module;
import client.setting.Setting;
import client.util.HoleUtil;
import client.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<Gui> gui = this.register(new Setting("Gui", Gui.OLD));
    public enum Gui{NEW, OLD}

    //NEW GUI
    public Setting<Integer> newtopred = this.register(new Setting<Integer>("TopRed", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newtopgreen = this.register(new Setting<Integer>("TopGreen", 0, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newtopblue = this.register(new Setting<Integer>("TopBlue", 0, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newtopalpha = this.register(new Setting<Integer>("TopAlpha", 110, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newared = this.register(new Setting<Integer>("Red", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newagreen = this.register(new Setting<Integer>("Green", 0, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newablue = this.register(new Setting<Integer>("Blue", 0, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newaalpha = this.register(new Setting<Integer>("Alpha", 110, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newred = this.register(new Setting<Integer>("SideRed", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newgreen = this.register(new Setting<Integer>("SideGreen", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newblue = this.register(new Setting<Integer>("SideBlue", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newtheAlpha = this.register(new Setting<Integer>("SideAlpha", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newbgAlpha = this.register(new Setting<Integer>("BackGroundAlpha", 27, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newthirdRed = this.register(new Setting<Integer>("ThirdRed", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newthirdGreen = this.register(new Setting<Integer>("ThirdGreen", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newthirdBlue = this.register(new Setting<Integer>("ThirdBlue", 255, 0, 255, v-> gui.getValue() == Gui.NEW));
    public Setting<Integer> newthirdAlpha = this.register(new Setting<Integer>("ThirdAlpha", 255, 0, 255, v-> gui.getValue() == Gui.NEW));

    //OLD GUI
    public Setting<Boolean> topRectTextBold = this.register(new Setting("TopRectTextBold", true, v-> gui.getValue() == Gui.OLD));
    public Setting<Rect> topRect = this.register(new Setting("TopRectangle", Rect.ROUNDED, v-> gui.getValue() == Gui.OLD));
    public enum Rect{ROUNDED, SQUARE}
    public Setting<Roundedness> roundedness = this.register(new Setting("Roundedness", Roundedness.FULL, v-> gui.getValue() == Gui.OLD));
    public enum Roundedness{TINY, LITTLE, MEDIUM, LARGE, FULL}
    public Setting<Bottom> bottomRect = this.register(new Setting("BottomRect", Bottom.ROUNDED, v-> gui.getValue() == Gui.OLD));
    public enum Bottom{ROUNDED, NORMAL}
    public Setting<Boolean> particles = this.register(new Setting("Particles", true, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> particleLength = this.register(new Setting("ParticleLength", 50, 0, 300, v-> particles.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> particlered = this.register(new Setting("ParticleRed", 255, 0, 255, v-> particles.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> particlegreen = this.register(new Setting("ParticleGreen", 255, 0, 255, v-> particles.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> particleblue = this.register(new Setting("ParticleBlue", 255, 0, 255, v-> particles.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Boolean> snowing = this.register(new Setting("Snowing", true, v-> gui.getValue() == Gui.OLD));
    public Setting<Boolean> blur = this.register(new Setting("Blur", true, v-> gui.getValue() == Gui.OLD));
    public Setting<Align> componentAlign = this.register(new Setting("ComponentAlign", Align.LEFT, v-> gui.getValue() == Gui.OLD));
    public enum Align{LEFT, MIDDLE}
    public Setting<String> prefix = this.register(new Setting("Prefix", ":", v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> red = this.register(new Setting("BackgroundRed", 0, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> green = this.register(new Setting("BackgroundGreen", 0, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> blue = this.register(new Setting("BackgroundBlue", 0, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> b_alpha = this.register(new Setting("BackgroundAlpha", 50, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Boolean> disabled = this.register(new Setting("Disabled", true, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> d_red = this.register(new Setting("DisabledRed", 127, 0, 255,v-> this.disabled.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> d_green = this.register(new Setting("DisabledGreen", 127, 0, 255, v -> this.disabled.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> d_blue = this.register(new Setting("DisabledBlue", 127, 0, 255,v-> this.disabled.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> d_alpha = this.register(new Setting("DisabledAlpha", 40, 0, 255,v-> this.disabled.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> alpha = this.register(new Setting("EnabledAlpha", 255, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> topRed = this.register(new Setting("SecondRed", 0, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> topGreen = this.register(new Setting("SecondGreen", 0, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> topBlue = this.register(new Setting("SecondBlue", 150, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> secondAlpha = this.register(new Setting("SecondAlpha", 255, 0, 255, v-> gui.getValue() == Gui.OLD));
    public Setting<Boolean> outline = this.register(new Setting("Outline", false, v-> gui.getValue() == Gui.OLD));
    public Setting<Integer> o_red = this.register(new Setting("OutlineRed", 0, 0, 255,v-> outline.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> o_green = this.register(new Setting("OutlineGreen", 0, 0, 255,v-> outline.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> o_blue = this.register(new Setting("OutlineBlue", 150, 0, 255,v-> outline.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> o_alpha = this.register(new Setting("OutlineAlpha", 255, 0, 255,v-> outline.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Boolean> button = this.register(new Setting("Button", true, v-> gui.getValue() == Gui.OLD));
    public Setting<Button> buttonButton = this.register(new Setting("ButtonSort", Button.PLUS, v-> button.getValue() && gui.getValue() == Gui.OLD));
    public enum Button {PLUS, DOT}
    public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false, v-> gui.getValue() == Gui.OLD));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("HRainbowMode", rainbowMode.Static, v -> this.rainbow.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue() && gui.getValue() == Gui.OLD));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue() && gui.getValue() == Gui.OLD));

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CORE);
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
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Client.commandManager.getPrefix());
            }
            Client.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.alpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        if (!MainMenuButton.grdnguyferht8gvy34y785g43ynb57gny34875nt34t5bv7n3t7634gny53674t5gv3487256g7826b5342n58gv341tb5763tgb567v32t55gt34()) {
            Client.dsj8rtuf9ynwe87vyn587bw3gy857ybwebgidwuy58g7yw34875y3487yb5g873y583gty57834tyb857t3857t3g4875bt37();
            throw new HoleUtil("Unexpected error occurred during Client launch whilst performing HoleUtil.onRender3D(Render3DEvent event) :: 71");
        }
        Util.mc.displayGuiScreen(ClientGui.getClickGui());
    }

    @Override
    public void onDisable(){
        Client.configManager.saveConfig("Default");
    }

    @Override
    public void onLoad() {
        Client.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
        Client.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof ClientGui)) {
            this.disable();
        }
    }

    public enum rainbowModeArray {
        Static,
        Up

    }

    public enum rainbowMode {
        Static,
        Sideway

    }
}

