package client.manager;

import client.events.Render2DEvent;
import client.events.Render3DEvent;
import client.gui.ClientGui;
import client.gui.impl.background.MenuToggler;
import client.modules.Feature;
import client.modules.Module;
import client.modules.combat.*;
import client.modules.core.*;
import client.modules.miscellaneous.*;
import client.modules.movement.*;
import client.modules.player.*;
import client.modules.visual.*;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager
        extends Feature {
    public ArrayList<Module> moduleList = new ArrayList();
    public List<Module> sortedModules = new ArrayList<>();

    public void init() {

        //CLIENT
        moduleList.add(new ClickGui());
        moduleList.add(new FontMod());
        moduleList.add(new Hud());
        moduleList.add(new Notify());
        moduleList.add(new RPC());
        moduleList.add(new MenuToggler());

        //COMBAT
        moduleList.add(new AutoArmor());
        moduleList.add(new Holefiller());
        moduleList.add(new Offhand());
        moduleList.add(new Surround());
        moduleList.add(new AutoWeb());
        moduleList.add(new Flatten());
        moduleList.add(new AutoCrystal());
        moduleList.add(new PistonAura());
        moduleList.add(new AutoTrap());
        moduleList.add(new Aura());
        moduleList.add(new Criticals());
        moduleList.add(new AntiCity());
        moduleList.add(new ObiAssist());

        //MISCELLANEOUS
        moduleList.add(new FakePlayer());
        moduleList.add(new AutoRespawn());
        moduleList.add(new NoEntityTrace());
        moduleList.add(new MCFriends());
        moduleList.add(new Spammer());
        moduleList.add(new TotemPopCounter());
        moduleList.add(new ChatModifications());
        moduleList.add(new PingSpoofer());
        moduleList.add(new TabTweaks());
        moduleList.add(new MiddleClickPearl());
        moduleList.add(new ChorusPredict());
        moduleList.add(new NoBreakAnimation());

        //MOVEMENT
        moduleList.add(new ReverseStep());
        moduleList.add(new Sprint());
        moduleList.add(new YPort());
        moduleList.add(new Step());
        moduleList.add(new Phase());
        moduleList.add(new Velocity());
        moduleList.add(new NoSlow());
        moduleList.add(new ElytraFlight());
        moduleList.add(new Jesus());
        moduleList.add(new Strafe());
        moduleList.add(new NoFall());
        moduleList.add(new FastSwim());
        moduleList.add(new AntiAim());
        moduleList.add(new AutoWalk());

        //PLAYER
        moduleList.add(new KeyEXP());
        moduleList.add(new Burrow());
        moduleList.add(new FastPlace());
        moduleList.add(new HotbarRefill());
        moduleList.add(new Quiver());
        moduleList.add(new Speedmine());
        moduleList.add(new AutoEnderChest());
        moduleList.add(new Interactions());
        moduleList.add(new Freecam());
        moduleList.add(new AntiRotate());
        moduleList.add(new Blink());
        moduleList.add(new XCarry());
        moduleList.add(new YawLock());
        moduleList.add(new AutoMine());
        moduleList.add(new AutoSilentChorus());

        //VISUAL
        moduleList.add(new CrystalChanger());
        moduleList.add(new BurrowESP());
        moduleList.add(new ThirdPerson());
        moduleList.add(new Viewmodel());
        moduleList.add(new SwingAnimations());
        moduleList.add(new ShulkerViewer());
        moduleList.add(new PrestigeChams());
        moduleList.add(new NameTags());
        moduleList.add(new CityESP());
        moduleList.add(new ESP());
        moduleList.add(new Chams());
        moduleList.add(new ViewTweaks());
        moduleList.add(new PearlRender());
        moduleList.add(new NoRender());
        moduleList.add(new poo());

    }

    public Module getModuleByName(String name) {
        for (Module module : this.moduleList) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.moduleList) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.moduleList) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList <> ( );
        for (Module module : this.moduleList) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }
    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList <> ( );
        this.moduleList.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.moduleList.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void onLogout() {
        this.moduleList.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.moduleList.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.moduleList.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.moduleList.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.moduleList) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof ClientGui) {
            return;
        }
        this.moduleList.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}

