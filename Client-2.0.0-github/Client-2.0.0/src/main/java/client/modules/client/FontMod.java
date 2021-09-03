package client.modules.client;

import client.Client;
import client.command.Command;
import client.events.ClientEvent;
import client.modules.Module;
import client.gui.impl.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FontMod
        extends Module {
    private static FontMod INSTANCE = new FontMod();
    public Setting<String> fontName = this.register( new Setting <> ( "FontName: " , "Dialog" ));
    public Setting<Boolean> antiAlias = this.register( new Setting <> ( "AntiAlias" , true ));
    public Setting<Boolean> fractionalMetrics = this.register( new Setting <> ( "Metrics" , true ));
    public Setting<Integer> fontSize = this.register( new Setting <> ( "Size" , 17 , 12 , 30 ));
    public Setting<Integer> fontStyle = this.register( new Setting <> ( "Style" , 0 , 0 , 3 ));
    private boolean reloadFont = false;

    public FontMod() {
        super("CustomFont", "Tweaks the way your font looks.", Module.Category.CORE);
        this.setInstance();
    }

    public static FontMod getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontMod();
        }
        return INSTANCE;
    }

    public static boolean checkFont(String font, boolean message) {
        String[] fonts;
        for (String s : fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!message && s.equals(font)) {
                return true;
            }
            if (!message) continue;
            Command.sendMessage(s);
        }
        return false;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !FontMod.checkFont(setting.getPlannedValue().toString(), false)) {
                Command.sendMessage(ChatFormatting.RED + "That font doesnt exist.");
                event.setCanceled(true);
                return;
            }
            this.reloadFont = true;
        }
    }

    @Override
    public void onTick() {
        if (this.reloadFont) {
            Client.textManager.init(false);
            this.reloadFont = false;
        }
    }
}

