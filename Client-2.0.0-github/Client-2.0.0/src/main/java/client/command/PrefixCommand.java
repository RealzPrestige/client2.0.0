package client.command;

import client.Client;
import com.mojang.realmsclient.gui.ChatFormatting;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Client.commandManager.getPrefix());
            return;
        }
        Client.commandManager.setPrefix(commands[0]);
    }
}

