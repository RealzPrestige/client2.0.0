package client.modules.miscellaneous;

import client.modules.Module;
import client.gui.impl.setting.Setting;
import client.util.FileUtil;
import client.util.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Spammer extends Module {
    public Setting<Double> delay;
    public Setting<Boolean> greentext;
    public Setting<Boolean> random;
    private final Timer timer;
    private static final List<String> spamMessages;
    private static final Random rnd;

    public Spammer() {
        super("Spammer", "Spams stuff.", Category.MISC);
        this.delay = (Setting<Double>)this.register(new Setting<>("Delay", 6.0, 0.1, 20.0));
        this.greentext = (Setting<Boolean>)this.register(new Setting<>("Green", false));
        this.random = (Setting<Boolean>)this.register(new Setting<>("Random", false));
        this.timer = new Timer();
    }

    @Override
    public void onLoad() {
        this.readSpamFile();
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        this.readSpamFile();
    }

    @Override
    public void onLogin() {
        if(this.isEnabled()) {
            this.disable();
        }
        this.readSpamFile();
    }

    @Override
    public void onLogout() {
        if(this.isEnabled()) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        Spammer.spamMessages.clear();
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        if (!this.timer.passedS(this.delay.getValue())) {
            return;
        }
        if (Spammer.spamMessages.size() > 0) {
            String messageOut;
            if (this.random.getValue()) {
                final int index = Spammer.rnd.nextInt(Spammer.spamMessages.size());
                messageOut = Spammer.spamMessages.get(index);
                Spammer.spamMessages.remove(index);
            }
            else {
                messageOut = Spammer.spamMessages.get(0);
                Spammer.spamMessages.remove(0);
            }
            Spammer.spamMessages.add(messageOut);
            if (this.greentext.getValue()) {
                messageOut = "> " + messageOut;
            }
            Spammer.mc.player.connection.sendPacket(new CPacketChatMessage(messageOut.replaceAll("§", "")));
        }
        this.timer.reset();
    }

    private void readSpamFile() {
        final List<String> fileInput = FileUtil.readTextFileAllLines("client/util/Spammer.txt");
        final Iterator<String> i = fileInput.iterator();
        Spammer.spamMessages.clear();
        while (i.hasNext()) {
            final String s = i.next();
            if (!s.replaceAll("\\s", "").isEmpty()) {
                Spammer.spamMessages.add(s);
            }
        }
        if (Spammer.spamMessages.size() == 0) {
            Spammer.spamMessages.add("f");
        }
    }

    static {
        spamMessages = new ArrayList<>();
        rnd = new Random();
    }
}
