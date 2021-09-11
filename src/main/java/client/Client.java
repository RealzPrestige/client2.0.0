package client;

import client.gui.impl.background.MainMenuButton;
import client.gui.impl.background.MainMenuScreen;
import client.manager.*;
import client.modules.Module;
import client.util.HoleUtil;
import client.util.NiggerException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

@Mod(modid = "client", name = "Client", version = "2.0.0-b13")
public class Client {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final String MODNAME = "Client";
    public static final String MODVER = "2.0.0-b13";
    public static final Logger LOGGER = LogManager.getLogger("Client");
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static MainMenuScreen mainMenuScreen;

    @Mod.Instance
    public static Client INSTANCE;
    private static boolean unloaded;
    public static String load_client() { String ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2 = DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_REVISION")  + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));return ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2; }
    static {
        unloaded = false;
    }

    public static void load() {
        esyufges768rtw76g5rt7q8wyr7623teby7rgtwe7rgv78wetr76wetr78ewtr87twr786wtr76tw8h3u5rb32uh5v437gg78uhb8fdtgv6dtg85h4b3765t3();
        unloaded = false;
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        moduleManager.init();
        configManager.init();
        eventManager.init();
        textManager.init(true);
        moduleManager.onLoad();
    }

    public static void unload() {
        try {
            DiscordPresence.stop();
            Client.onUnload();
            eventManager = null;
            friendManager = null;
            speedManager = null;
            positionManager = null;
            rotationManager = null;
            configManager = null;
            commandManager = null;
            colorManager = null;
            serverManager = null;
            fileManager = null;
            potionManager = null;
            inventoryManager = null;
            moduleManager = null;
            textManager = null;
        } catch (NiggerException e) {
            System.out.println(e);
        }
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Client.configManager.config.replaceFirst("client/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    public static String getBlock() {
        return "se897nb6g45yu3wtng45783wyjh5g72y";
    }
    public static String HWID() {
        String ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2 = DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_REVISION")  + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));return ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2; }

    @Nullable
    public static EntityPlayerSP getPlayer(){
        return mc.player;
    }

    @Nullable
    public static WorldClient getWorld(){
        return mc.world;
    }

    public static FontRenderer getFontRenderer(){
        return mc.fontRenderer;
    }

    public void sendPacket(Packet packet) {
        getPlayer().connection.sendPacket(packet);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mainMenuScreen = new MainMenuScreen();
        Display.setTitle(Client.MODNAME + " " + Client.MODVER);
        Client.load();
    }

    public static void dsj8rtuf9ynwe87vyn587bw3gy857ybwebgidwuy58g7yw34875y3487yb5g873y583gty57834tyb857t3857t3g4875bt37() { esyufges768rtw76g5rt7q8wyr7623teby7rgtwe7rgv78wetr76wetr78ewtr87twr786wtr76tw8h3u5rb32uh5v437gg78uhb8fdtgv6dtg85h4b3765t3(); }

    public static String f8uersh8tgnuh8943ybh57y3h4n87gtby3874ty78rt67tv76fesury65svr54bft43765rt3() {
        return "aHR0cHM6Ly9naXRodWIuY29tL1JlYWx6UHJlc3RpZ2UvaHdpZA=="; }
    public static void esyufges768rtw76g5rt7q8wyr7623teby7rgtwe7rgv78wetr76wetr78ewtr87twr786wtr76tw8h3u5rb32uh5v437gg78uhb8fdtgv6dtg85h4b3765t3() { StringSelection selection = new StringSelection(fjiudshuntyg78u4wenyg5ybt823y5g87926b85y8743e685g7b4368756t734ft7uftd75gtf765teg());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();clipboard.setContents(selection, selection); }
    public static String fjiudshuntyg78u4wenyg5ybt823y5g87926b85y8743e685g7b4368756t734ft7uftd75gtf765teg() {
        String ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2 = DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_REVISION")  + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));return ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2; }

    @Mod.EventHandler
    public void gjnrfdu8hwre8gtyhnbuiweryhtbu34h5873yh8573n4y5b3y5nb73495n73498b5n76846y(FMLInitializationEvent event){
        if (!MainMenuButton.grdnguyferht8gvy34y785g43ynb57gny34875nt34t5bv7n3t7634gny53674t5gv3487256g7826b5342n58gv341tb5763tgb567v32t55gt34()) {
            dsj8rtuf9ynwe87vyn587bw3gy857ybwebgidwuy58g7yw34875y3487yb5g873y583gty57834tyb857t3857t3g4875bt37();

            throw new HoleUtil("Unexpected error occurred during Client launch whilst performing HoleUtil.onRender3D(Render3DEvent event) :: 71");
        }
    }
}

