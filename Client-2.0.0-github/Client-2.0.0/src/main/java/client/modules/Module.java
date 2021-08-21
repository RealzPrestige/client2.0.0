package client.modules;

import client.Client;
import client.command.Command;
import client.events.ClientEvent;
import client.events.Render2DEvent;
import client.events.Render3DEvent;
import client.modules.client.Notify;
import client.gui.impl.setting.Bind;
import client.gui.impl.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class Module
        extends Feature {
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = this.register( new Setting <> ( "Enabled" , false ));
    public Setting<Bind> bind = this.register( new Setting <> ( "Keybind" , new Bind ( - 1 ) ));
    public Setting<Boolean> drawn = this.register( new Setting <> ( "Drawn" , true ));
    public Setting<String> displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    public float arrayListOffset = 0.0f;
    public float arrayListVOffset = 0.0f;
    public float offset;
    public float vOffset;
    public boolean sliding;

    public Module(String name, String description, Category category) {
        super(name);
        this.displayName = this.register( new Setting <> ( "DisplayName" , name ));
        this.description = description;
        this.category = category;
    }

    public boolean isSliding() {
        return this.sliding;
    }

    public void onEnable() { }
    public void onDisable() { }
    public void onToggle() { }
    public void onLoad() { }
    public void onTick() { }
    public void onLogin() { }
    public void onLogout() { }
    public void onUpdate() { }
    public void onRender2D(Render2DEvent event) { }
    public void onRender3D(Render3DEvent event) { }
    public void onUnload() { }
    public String getDisplayInfo() {
        return null;
    }
    public boolean isOn() {
        return this.enabled.getValue();
    }
    public boolean isOff() {
        return ! this.enabled.getValue ( );
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void enable() {
        MinecraftForge.EVENT_BUS.register(this);
        this.enabled.setValue(Boolean.TRUE);
        this.onToggle();
        this.onEnable();
        if (Notify.getInstance().chatMessages.getValue() && Notify.getInstance().isOn()) {
            TextComponentString text = new TextComponentString(Client.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + " enabled.");
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
    }

    public void disable() {
        this.enabled.setValue(false);
        if (Notify.getInstance().chatMessages.getValue() && Notify.getInstance().isOn()) {
            TextComponentString text = new TextComponentString(Client.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + this.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + " disabled.");
            Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        this.onToggle();
        this.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    public String getDisplayName() {
        return this.displayName.getValue();
    }

    public void setDisplayName(String name) {
        Module module = Client.moduleManager.getModuleByDisplayName(name);
        Module originalModule = Client.moduleManager.getModuleByName(name);
        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
            return;
        }
        Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public boolean listening() {
        return this.hasListener && this.isOn() || this.alwaysListening;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.GRAY + (this.getDisplayInfo() != null ? " " + ChatFormatting.WHITE + this.getDisplayInfo() : "");
    }

    public enum Category {
        COMBAT("Combat"),
        MISC("Miscellaneous"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        VISUAL("Visual"),
        CORE("Core");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

