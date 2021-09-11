package client.modules.miscellaneous;

import client.modules.Module;
import client.modules.core.Notify;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;

public class TotemPopCounter extends Module {
    private static TotemPopCounter INSTANCE = new TotemPopCounter();

    public TotemPopCounter() {
        super("TotemPopCounter", "Counts enemy pops and announces in chat.", Category.MISC);
        this.setInstance();
    }

    public static HashMap<String, Integer> TotemPopContainer = new HashMap();

    public static TotemPopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TotemPopCounter();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopContainer.containsKey(player.getName())) {
            int totemCount = TotemPopContainer.get(player.getName());
            TotemPopContainer.remove(player.getName());
            if (totemCount == 1) {
                    int id = 0;
                    for (char character : player.getName().toCharArray()) {
                        id += character;
                        id *= 10;
                    }
                    mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Notify.getInstance().getCommandMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem."), id);
            } else {
                    int id = 0;
                    for (char character : player.getName().toCharArray()) {
                        id += character;
                        id *= 10;
                    }
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Notify.getInstance().getCommandMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems."), id);
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (fullNullCheck()) {
            return;
        }
        if (mc.player.equals(player)) {
            return;
        }
        int totemCount = 1;
        if (TotemPopContainer.containsKey(player.getName())) {
            totemCount = TotemPopContainer.get(player.getName());
            TotemPopContainer.put(player.getName(), ++totemCount);
        } else {
            TotemPopContainer.put(player.getName(), totemCount);
        }
        if (totemCount == 1) {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Notify.getInstance().getCommandMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem."), id);
        } else {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Notify.getInstance().getCommandMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems."), id);
            }
    }
}
