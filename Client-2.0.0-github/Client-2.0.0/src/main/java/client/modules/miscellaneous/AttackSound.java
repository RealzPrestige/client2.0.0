package client.modules.miscellaneous;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;

public class AttackSound extends Module {

    public ResourceLocation osu = new ResourceLocation("textures/osu.wav");
    public ResourceLocation hitmarker = new ResourceLocation("textures/hit.wav");

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.HITMARKER));
    public enum Mode{HITMARKER, OSU}

    public AttackSound() {
        super("AttackSound", "Plays a sound whenever you his an entity.",Category.MISC);
        }

    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent event) {
        if (!event.getEntity().equals(this.mc.player)) {
            return;
        }
        switch (mode.getCurrentState()) {
            case HITMARKER: {
                playSoundFile(hitmarker);
                break;
            }
            case OSU: {
                playSoundFile(osu);
                break;
            }
        }
    }

    public static void playSoundFile(final ResourceLocation resourceLocation) {
        try {
            final InputStream sound = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
            final AudioStream as = new AudioStream(sound);
            AudioPlayer.player.start(as);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
