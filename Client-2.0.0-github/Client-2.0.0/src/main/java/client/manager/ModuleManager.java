package client.manager;

import client.events.Render2DEvent;
import client.events.Render3DEvent;
import client.gui.ClientGui;
import client.gui.impl.background.MenuToggler;
import client.modules.Feature;
import client.modules.Module;
import client.modules.client.*;
import client.modules.combat.*;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ModuleManager
        extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<>();

    public void init() {

        //CLIENT
        modules.add(new ClickGui());
        modules.add(new FontMod());
        modules.add(new Hud());
        modules.add(new Notify());
        modules.add(new RPC());
        modules.add(new MenuToggler());

        //COMBAT
        modules.add(new AutoArmor());
        modules.add(new Holefiller());
        modules.add(new Offhand());
        modules.add(new Surround());
        modules.add(new AutoWeb());
        modules.add(new Flatten());
        modules.add(new AutoCrystal());
        modules.add(new PistonAura());
        modules.add(new AutoTrap());
        modules.add(new Aura());
        modules.add(new Criticals());
        modules.add(new AntiCity());

        //MISCELLANEOUS
        modules.add(new FakePlayer());
        modules.add(new AutoRespawn());
        modules.add(new NoEntityTrace());
        modules.add(new MCFriends());
        modules.add(new Spammer());
        modules.add(new TotemPopCounter());
        modules.add(new ChatModifications());
        modules.add(new PingSpoofer());
        modules.add(new TabTweaks());
        modules.add(new MiddleClickPearl());

        //MOVEMENT
        modules.add(new ReverseStep());
        modules.add(new Sprint());
        modules.add(new YPort());
        modules.add(new Step());
        modules.add(new Phase());
        modules.add(new DirectionalSpeed());
        modules.add(new Velocity());
        modules.add(new NoSlow());
        modules.add(new ElytraFlight());
        modules.add(new Jesus());
        modules.add(new Strafe());
        modules.add(new NoFall());
        modules.add(new FastSwim());

        //PLAYER
        modules.add(new KeyEXP());
        modules.add(new Burrow());
        modules.add(new FastPlace());
        modules.add(new HotbarRefill());
        modules.add(new Quiver());
        modules.add(new Speedmine());
        modules.add(new AutoEnderChest());
        modules.add(new Interactions());
        modules.add(new Freecam());
        modules.add(new AntiRotate());
        modules.add(new Blink());

        //VISUAL
        modules.add(new CrystalChanger());
        modules.add(new ExpESP());
        modules.add(new FullBright());
        modules.add(new BurrowESP());
        modules.add(new ThirdPerson());
        modules.add(new Viewmodel());
        modules.add(new AntiFog());
        modules.add(new SwingAnimations());
        modules.add(new ShulkerViewer());
        modules.add(new PearlRender());
        modules.add(new PrestigeChams());
        modules.add(new NameTags());
        modules.add(new CityESP());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList <> ( );
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList <> ( );
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList <> ( );
        this.modules.forEach(module -> {
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
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof ClientGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    private class Animation
            extends Thread {
        public Module module;
        public float offset;
        public float vOffset;
        ScheduledExecutorService service;

        public Animation() {
            super("Animation");
            this.service = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        public void start() {
            System.out.println("fgbnrdshugb7gnw47rhnw4875tbg6234yg765tb32yn438y7bg76t5bt6432634rtb6df5t3n76j235t animation thread.");
            this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
        }
    }
}

